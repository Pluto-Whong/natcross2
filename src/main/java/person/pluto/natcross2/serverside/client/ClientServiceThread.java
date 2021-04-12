package person.pluto.natcross2.serverside.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.serverside.client.config.IClientServiceConfig;

/**
 * <p>
 * 客户端服务进程
 * </p>
 *
 * @author Pluto
 * @since 2019-07-05 10:53:33
 */
@Slf4j
public final class ClientServiceThread implements Runnable {

	private Thread myThread = null;

	private boolean isAlive = false;
	private ServerSocket listenServerSocket;

	private IClientServiceConfig<?, ?> config;

	public ClientServiceThread(IClientServiceConfig<?, ?> config) {
		this.config = config;
	}

	@Override
	public void run() {
		Selector selector = null;

		ServerSocketChannel channel = listenServerSocket.getChannel();

		if (Objects.nonNull(channel)) {
			try {
				selector = Selector.open();

				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_ACCEPT);
			} catch (IOException e) {
				channel = null;
			}
		}

		while (isAlive) {
			try {
				if (Objects.isNull(selector)) {
					Socket listenSocket = listenServerSocket.accept();
					procMethod(listenSocket);
				} else {
					int select = selector.select();
					if (select <= 0) {
						continue;
					}

					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

					for (; iterator.hasNext();) {
						SelectionKey key = iterator.next();
						iterator.remove();

						if (!key.isValid() || !key.isAcceptable()) {
							continue;
						}
						SocketChannel accept = channel.accept();
						procMethod(accept.socket());
					}
				}
			} catch (Exception e) {
				log.warn("客户端服务进程 轮询等待出现异常", e);
			}
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
				config.getClientServiceAdapter().procMethod(listenSocket);
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
	public void start() throws Exception {
		log.info("client service [{}] starting ...", this.config.getListenPort());
		this.isAlive = true;
		if (myThread == null || !myThread.isAlive()) {
			myThread = new Thread(this);
			myThread.setName("client-server-" + this.formatInfo());

			try {
				// 启动时配置，若启动失败则执行cancell并再次抛出异常让上级处理
				listenServerSocket = config.createServerSocket();
			} catch (Exception e) {
				log.error("create client ServerSocket[{}] faild!", config.getListenPort());
				this.cancell();
				throw e;
			}

			myThread.start();
			log.info("client service [{}] start success", this.config.getListenPort());
		} else {
			log.warn("client service [{}] is started", this.config.getListenPort());
		}
	}

	/**
	 * 退出
	 *
	 * @author Pluto
	 * @since 2019-07-18 18:32:03
	 */
	public void cancell() {
		log.info("client service [{}] will cancell", this.config.getListenPort());

		isAlive = false;

		if (listenServerSocket != null) {
			try {
				listenServerSocket.close();
				listenServerSocket = null;
			} catch (IOException e) {
				log.warn("监听端口关闭异常", e);
			}
		}

		if (myThread != null) {
			myThread.interrupt();
			myThread = null;
		}

		log.info("client service [{}] cancell success", this.config.getListenPort());
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
