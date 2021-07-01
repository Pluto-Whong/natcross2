package person.pluto.natcross2.serverside.listen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.common.CommonFormat;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.nio.INioProcesser;
import person.pluto.natcross2.nio.NioHallows;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.config.IListenServerConfig;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;
import person.pluto.natcross2.utils.Assert;

/**
 * <p>
 * 监听转发服务进程
 * </p>
 *
 * @author Pluto
 * @since 2019-07-05 10:53:33
 */
@Slf4j
public final class ServerListenThread implements Runnable, INioProcesser, IBelongControl, IServerListen {

	private volatile Thread myThread = null;

	private volatile boolean isAlive = false;
	private volatile boolean canceled = false;

	private final IListenServerConfig config;
	private final ServerSocket listenServerSocket;

	private final ConcurrentHashMap<String, AbsSocketPart> socketPartMap = new ConcurrentHashMap<>();

	private volatile IControlSocket controlSocket;
	private volatile IClearInvalidSocketPartThread clearInvalidSocketPartThread;

	public ServerListenThread(IListenServerConfig config) throws Exception {
		this.config = config;

		// 此处就开始占用端口，防止重复占用端口，和启动时已被占用
		this.listenServerSocket = this.config.createServerSocket();

		log.info("server listen port[{}] is created!", this.getListenPort());
	}

	@Override
	public void run() {
		while (this.isAlive) {
			try {
				Socket listenSocket = this.listenServerSocket.accept();
				this.procMethod(listenSocket);
			} catch (Exception e) {
				log.warn("监听服务[" + this.getListenPort() + "]服务异常", e);
				this.cancel();
			}
		}
	}

	@Override
	public void proccess(SelectionKey key) {
		if (!key.isValid()) {
			this.cancel();
		}

		try {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			SocketChannel accept = channel.accept();
			for (; Objects.nonNull(accept); accept = channel.accept()) {
				this.procMethod(accept.socket());
			}
		} catch (IOException e) {
			log.warn("监听服务[" + this.getListenPort() + "]服务异常", e);
			this.cancel();
		}
	}

	/**
	 * 任务执行方法
	 *
	 * @param listenSocket
	 */
	private void procMethod(Socket listenSocket) {
		NatcrossExecutor.executeServerListenAccept(() -> {
			// 如果没有控制接收socket，则取消接入，不主动关闭所有接口，防止controlSocket临时掉线，讲道理没有controlSocket也不会启动
			if (Objects.isNull(this.controlSocket)) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					// do nothing
				}
				return;
			}

			String socketPartKey = CommonFormat.generateSocketPartKey(this.getListenPort());

			AbsSocketPart socketPart = this.config.newSocketPart(this);
			socketPart.setSocketPartKey(socketPartKey);
			socketPart.setRecvSocket(listenSocket);

			this.socketPartMap.put(socketPartKey, socketPart);
			// 发送指令失败，同controlSocket为空，不使用异步执行，毕竟接口发送只能顺序，异步的方式也会被锁，等同同步
			if (!this.sendClientWait(socketPartKey)) {
				this.socketPartMap.remove(socketPartKey);
				socketPart.cancel();
				return;
			}
		});
	}

	/**
	 ** 告知客户端，有新连接
	 *
	 * @author Pluto
	 * @since 2019-07-11 15:45:14
	 * @param socketPartKey
	 */
	private boolean sendClientWait(String socketPartKey) {
		log.info("告知新连接 sendClientWait[{}]", socketPartKey);
		boolean sendClientWait = false;

		IControlSocket controlSocket = this.controlSocket;

		try {
			sendClientWait = controlSocket.sendClientWait(socketPartKey);
		} catch (Throwable e) {
			log.error("告知新连接 sendClientWait[" + socketPartKey + "] 发生未知异常", e);
			sendClientWait = false;
		}

		if (!sendClientWait) {
			log.warn("告知新连接 sendClientWait[" + socketPartKey + "] 失败");
			if (controlSocket == null || !controlSocket.isValid()) {
				// 保证control为置空状态
				this.stopListen();
			}
			return false;
		}
		return true;
	}

	/**
	 ** 启动
	 *
	 * @author Pluto
	 * @throws Exception
	 * @since 2020-01-07 09:36:30
	 */
	private void start() {
		Assert.state(this.canceled == false, "已退出，不得重新启动");
		if (this.isAlive) {
			return;
		}
		this.isAlive = true;

		log.info("server listen port[{}] starting ...", this.getListenPort());

		if (this.clearInvalidSocketPartThread == null) {
			this.clearInvalidSocketPartThread = this.config.newClearInvalidSocketPartThread(this);
			if (this.clearInvalidSocketPartThread != null) {
				this.clearInvalidSocketPartThread.start();
			}
		}

		ServerSocketChannel channel = this.listenServerSocket.getChannel();
		if (Objects.nonNull(channel)) {
			try {
				NioHallows.register(channel, SelectionKey.OP_ACCEPT, this);
			} catch (IOException e) {
				log.error("register serverListen channel[{}] faild!", config.getListenPort());
				this.cancel();
				throw new RuntimeException("nio注册时异常", e);
			}
		} else {
			Thread myThread = this.myThread;
			if (myThread == null || !myThread.isAlive()) {
				myThread = this.myThread = new Thread(this);
				myThread.setName("server-listen-" + this.formatInfo());
				myThread.start();
			}
		}

		log.info("server listen port[{}] start success!", this.getListenPort());
	}

	/**
	 ** 关停监听服务，不注销已经建立的，并置空controlSocket
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:43:43
	 */
	private synchronized void stopListen() {
		log.info("stopListen[{}]", this.getListenPort());
		this.isAlive = false;

		NioHallows.release(this.listenServerSocket.getChannel());

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}

		IControlSocket controlSocket = this.controlSocket;
		if ((controlSocket = this.controlSocket) != null) {
			this.controlSocket = null;
			try {
				controlSocket.close();
			} catch (Exception e) {
				log.debug("监听服务控制端口关闭异常", e);
			}
		}
	}

	/**
	 ** 退出
	 *
	 * @author Pluto
	 * @since 2020-01-07 09:37:00
	 */
	public synchronized void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;

		log.info("serverListen cancelling[{}]", this.getListenPort());

		ListenServerControl.remove(this.getListenPort());

		this.stopListen();

		try {
			this.listenServerSocket.close();
		} catch (Exception e) {
			// do no thing
		}

		IClearInvalidSocketPartThread clearInvalidSocketPartThread;
		if ((clearInvalidSocketPartThread = this.clearInvalidSocketPartThread) != null) {
			this.clearInvalidSocketPartThread = null;
			try {
				clearInvalidSocketPartThread.cancel();
			} catch (Exception e) {
				// do no thing
			}
		}

		String[] socketPartKeyArray = this.socketPartMap.keySet().toArray(new String[0]);
		for (String key : socketPartKeyArray) {
			this.stopSocketPart(key);
		}

		log.debug("serverListen cancel[{}] is success", this.getListenPort());
	}

	/**
	 ** 停止指定的端口
	 *
	 * @author Pluto
	 * @since 2019-07-11 16:33:10
	 * @param socketPartKey
	 * @return
	 */
	@Override
	public boolean stopSocketPart(String socketPartKey) {
		log.debug("停止接口 stopSocketPart[{}]", socketPartKey);
		AbsSocketPart socketPart = this.socketPartMap.remove(socketPartKey);
		if (socketPart == null) {
			return false;
		}
		socketPart.cancel();
		return true;
	}

	/**
	 ** 清理无效socketPart
	 *
	 * @author Pluto
	 * @since 2019-08-21 12:50:57
	 */
	public void clearInvaildSocketPart() {
		log.debug("clearInvaildSocketPart[{}]", this.getListenPort());

		ConcurrentHashMap<String, AbsSocketPart> socketPartMap = this.socketPartMap;

		Set<String> keySet = socketPartMap.keySet();
		// 被去除的时候set会变化而导致空值问题
		String[] array = keySet.toArray(new String[0]);

		for (String key : array) {
			AbsSocketPart socketPart = socketPartMap.get(key);
			if (socketPart != null && !socketPart.isValid()) {
				this.stopSocketPart(key);
			}
		}

	}

	/**
	 ** 将接受到的连接进行设置组合
	 * 
	 * @param socketPartKey
	 * @param sendSocket
	 * @return
	 */
	public boolean doSetPartClient(String socketPartKey, Socket sendSocket) {
		log.debug("接入接口 doSetPartClient[{}]", socketPartKey);
		AbsSocketPart socketPart = this.socketPartMap.get(socketPartKey);
		if (socketPart == null) {
			return false;
		}
		socketPart.setSendSocket(sendSocket);

		boolean createPassWay = socketPart.createPassWay();
		if (!createPassWay) {
			socketPart.cancel();
			this.stopSocketPart(socketPartKey);
			return false;
		}

		return true;
	}

	/**
	 ** 设置控制端口
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:46:05
	 * @param controlSocket
	 */
	public synchronized void setControlSocket(Socket socket) {
		log.info("setControlSocket[{}]", this.getListenPort());

		IControlSocket controlSocketNew = this.config.newControlSocket(socket, null);

		IControlSocket controlSocket;
		if ((controlSocket = this.controlSocket) != null) {
			this.controlSocket = null;
			try {
				controlSocket.replaceClose();
			} catch (Exception e) {
				log.debug("监听服务控制端口关闭异常", e);
			}
		}

		controlSocketNew.setServerListen(this);
		controlSocketNew.startRecv();
		this.controlSocket = controlSocketNew;
		this.start();
	}

	@Override
	public synchronized void controlCloseNotice(IControlSocket controlSocket) {
		if (Objects.equals(controlSocket, this.controlSocket)) {
			this.stopListen();
		}
	}

	/**
	 ** 获取监听端口
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:45:57
	 * @return
	 */
	public Integer getListenPort() {
		return this.config.getListenPort();
	}

	/**
	 ** 获取已建立的连接
	 *
	 * @author Pluto
	 * @since 2019-07-19 16:32:09
	 * @return
	 */
	public List<String> getSocketPartList() {
		return new LinkedList<>(this.socketPartMap.keySet());
	}

	/**
	 ** 获取socket对
	 *
	 * @author Pluto
	 * @since 2020-01-07 09:43:08
	 * @return
	 */
	public Map<String, AbsSocketPart> getSocketPartMap() {
		return this.socketPartMap;
	}

	/**
	 ** 是否激活状态
	 *
	 * @author Pluto
	 * @since 2020-01-07 09:43:28
	 * @return
	 */
	public boolean isAlive() {
		return this.isAlive;
	}

	/**
	 * 是否已退出
	 *
	 * @return
	 * @author Pluto
	 * @since 2021-04-13 13:39:11
	 */
	public boolean isCanceled() {
		return this.canceled;
	}

	/**
	 * 获取配置
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:55:41
	 * @return
	 */
	public IListenServerConfig getConfig() {
		return this.config;
	}

	public String formatInfo() {
		return String.valueOf(this.getListenPort());
	}

}
