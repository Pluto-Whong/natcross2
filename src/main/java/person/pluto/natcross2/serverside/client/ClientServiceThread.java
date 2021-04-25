package person.pluto.natcross2.serverside.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.nio.INioProcesser;
import person.pluto.natcross2.nio.NioHallows;
import person.pluto.natcross2.serverside.client.config.IClientServiceConfig;
import person.pluto.natcross2.utils.Assert;

/**
 * <p>
 * 客户端服务进程
 * </p>
 *
 * @author Pluto
 * @since 2019-07-05 10:53:33
 */
@Slf4j
public final class ClientServiceThread implements Runnable, INioProcesser {

	private volatile Thread myThread = null;

	private volatile boolean isAlive = false;
	private volatile boolean canceled = false;
	private ServerSocket listenServerSocket;

	private final IClientServiceConfig<?, ?> config;

	public ClientServiceThread(IClientServiceConfig<?, ?> config) throws Exception {
		this.config = config;

		// 启动时配置，若启动失败则执行cancell并再次抛出异常让上级处理
		this.listenServerSocket = config.createServerSocket();

		log.info("client service [{}] is created!", this.config.getListenPort());
	}

	@Override
	public void run() {
		while (this.isAlive) {
			try {
				Socket listenSocket = this.listenServerSocket.accept();
				this.procMethod(listenSocket);
			} catch (Exception e) {
				log.warn("客户端服务进程 轮询等待出现异常", e);
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
			this.procMethod(accept.socket());
		} catch (IOException e) {
			log.warn("客户端服务进程 轮询等待出现异常", e);
			this.cancel();
		}
	}

	/**
	 * 处理客户端发来的消息
	 *
	 * @author Pluto
	 * @since 2020-01-03 14:46:36
	 * @param listenSocket
	 */
	public void procMethod(Socket listenSocket) {
		NatcrossExecutor.executeClientServiceAccept(() -> {
			try {
				this.config.getClientServiceAdapter().procMethod(listenSocket);
			} catch (Exception e) {
				log.error("处理socket异常", e);
				try {
					listenSocket.close();
				} catch (IOException sce) {
					log.warn("处理新socket时报错，并关闭socket异常", e);
				}
			}
		});
	}

	/**
	 * 启动
	 *
	 * @author Pluto
	 * @since 2020-01-03 14:05:59
	 */
	public void start() {
		Assert.state(this.canceled == false, "已退出，不得重新启动");

		log.info("client service [{}] starting ...", this.config.getListenPort());
		this.isAlive = true;

		ServerSocketChannel channel = this.listenServerSocket.getChannel();
		if (Objects.nonNull(channel)) {
			if (!channel.isRegistered() || (channel.validOps() & SelectionKey.OP_ACCEPT) == 0) {
				try {
					NioHallows.register(channel, SelectionKey.OP_ACCEPT, this);
				} catch (IOException e) {
					log.error("register clientService channel[{}] faild!", config.getListenPort());
					this.cancel();
					throw new RuntimeException("nio注册时异常", e);
				}
			}
		} else {
			if (this.myThread == null || !this.myThread.isAlive()) {
				this.myThread = new Thread(this);
				this.myThread.setName("client-server-" + this.formatInfo());
				this.myThread.start();
			}
		}

		log.info("client service [{}] start success", this.config.getListenPort());
	}

	/**
	 * 退出
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:32:03
	 */
	public void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;

		log.info("client service [{}] will cancell", this.config.getListenPort());

		this.isAlive = false;

		ServerSocket listenServerSocket;
		if ((listenServerSocket = this.listenServerSocket) != null) {
			this.listenServerSocket = null;

			NioHallows.release(listenServerSocket.getChannel());

			try {
				listenServerSocket.close();
			} catch (IOException e) {
				log.warn("监听端口关闭异常", e);
			}
		}

		Thread myThread = this.myThread;
		if ((myThread = this.myThread) != null) {
			myThread = this.myThread;
			myThread.interrupt();
		}

		log.info("client service [{}] cancell success", this.config.getListenPort());
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
	 * 获取监听端口
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:32:40
	 * @return
	 */
	public Integer getListenPort() {
		return this.config.getListenPort();
	}

	/**
	 * 格式化为短小的可识别信息
	 * 
	 * @author Pluto
	 * @since 2020-04-15 14:17:41
	 * @return
	 */
	public String formatInfo() {
		return String.valueOf(this.getListenPort());
	}

}
