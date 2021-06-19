package person.pluto.natcross2.clientside;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.clientside.config.IClientConfig;
import person.pluto.natcross2.clientside.heart.IClientHeartThread;

/**
 * <p>
 * 客户端控制服务
 * </p>
 *
 * @author Pluto
 * @since 2019-07-05 10:53:33
 */
@Slf4j
public final class ClientControlThread implements Runnable, IBelongControl {

	private volatile Thread myThread = null;

	private volatile boolean isAlive = false;
	@Getter
	private volatile boolean cancelled = false;

	private final Map<String, AbsSocketPart> socketPartMap = new ConcurrentHashMap<>();

	private final IClientConfig<?, ?> config;

	private volatile IClientHeartThread clientHeartThread;
	private volatile IClientAdapter<?, ?> clientAdapter;

	public ClientControlThread(IClientConfig<?, ?> config) {
		this.config = config;

	}

	/**
	 * 触发控制服务
	 *
	 * @author Pluto
	 * @since 2019-07-18 19:02:15
	 * @return
	 * @throws Exception
	 */
	public boolean createControl() throws Exception {
		this.stopClient();

		if (this.clientAdapter == null) {
			this.clientAdapter = this.config.newCreateControlAdapter(this);
		}

		boolean flag = this.clientAdapter.createControlChannel();

		if (!flag) {
			return false;
		}

		this.start();
		return true;
	}

	@Override
	public void run() {
		while (this.isAlive) {
			try {
				// 使用适配器代理执行
				this.clientAdapter.waitMessage();
			} catch (Exception e) {
				log.warn("client control [{}] to server is exception,will stopClient",
						this.config.getListenServerPort());
				this.stopClient();
			}
		}
	}

	@Override
	public boolean stopSocketPart(String socketPartKey) {
		log.debug("stopSocketPart[{}]", socketPartKey);

		AbsSocketPart socketPart = this.socketPartMap.remove(socketPartKey);
		if (socketPart == null) {
			return false;
		}
		socketPart.cancel();
		return true;
	}

	/**
	 ** 启动
	 * 
	 * @author Pluto
	 * @since 2020-01-07 16:13:26
	 */
	private void start() {
		this.isAlive = true;
		this.cancelled = false;

		Thread myThread = this.myThread;
		if (Objects.isNull(myThread) || !myThread.isAlive()) {

			IClientHeartThread clientHeartThread = this.clientHeartThread;
			if (Objects.isNull(clientHeartThread) || !clientHeartThread.isAlive()) {
				clientHeartThread = this.clientHeartThread = this.config.newClientHeartThread(this);
				if (Objects.nonNull(clientHeartThread)) {
					clientHeartThread.start();
				}
			}

			myThread = this.myThread = new Thread(this);
			myThread.setName("client-" + this.formatInfo());
			myThread.start();
		}
	}

	/**
	 ** 停止客户端监听
	 *
	 * @author Pluto
	 * @since 2019-07-19 09:24:41
	 */
	public void stopClient() {
		this.isAlive = false;

		Thread myThread = this.myThread;
		if (myThread != null) {
			this.myThread = null;
			myThread.interrupt();
		}

		IClientAdapter<?, ?> clientAdapter = this.clientAdapter;
		if (Objects.nonNull(clientAdapter)) {
			try {
				clientAdapter.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 ** 全部退出
	 *
	 * @author Pluto
	 * @since 2019-07-19 09:19:43
	 */
	public void cancell() {
		if (this.cancelled) {
			return;
		}
		this.cancelled = true;

		this.stopClient();

		IClientHeartThread clientHeartThread;
		if ((clientHeartThread = this.clientHeartThread) != null) {
			this.clientHeartThread = null;
			try {
				clientHeartThread.cancel();
			} catch (Exception e) {
				// do no thing
			}
		}

		IClientAdapter<?, ?> clientAdapter;
		if ((clientAdapter = this.clientAdapter) != null) {
			this.clientAdapter = null;
			try {
				clientAdapter.close();
			} catch (Exception e) {
				// do no thing
			}
		}

		String[] array = this.socketPartMap.keySet().toArray(new String[0]);

		for (String key : array) {
			this.stopSocketPart(key);
		}

	}

	/**
	 ** 服务端监听的端口
	 * 
	 * @author Pluto
	 * @since 2020-01-07 16:13:47
	 * @return
	 */
	public Integer getListenServerPort() {
		return this.config.getListenServerPort();
	}

	/**
	 ** 重设目标端口
	 * 
	 * @author Pluto
	 * @since 2020-01-07 16:14:06
	 * @param destIp
	 * @param destPort
	 */
	public void setDestIpPort(String destIp, Integer destPort) {
		this.config.setDestIpPort(destIp, destPort);
	}

	/**
	 ** 检测是否还活着
	 * 
	 * @author Pluto
	 * @since 2020-01-07 16:14:21
	 * @return
	 */
	public boolean isAlive() {
		return this.isAlive;
	}

	/**
	 * 发送心跳测试
	 * 
	 * @author Pluto
	 * @since 2020-01-07 15:54:47
	 * @throws Exception
	 */
	public void sendHeartTest() throws Exception {
		// 无需判空，空指针异常也是异常
		this.clientAdapter.sendHeartTest();
	}

	/**
	 * 设置隧道伙伴
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:35:06
	 * @param socketPartKey
	 * @param socketPart
	 */
	public void putSocketPart(String socketPartKey, AbsSocketPart socketPart) {
		this.socketPartMap.put(socketPartKey, socketPart);
	}

	/**
	 * 格式化信息
	 * 
	 * @author Pluto
	 * @since 2020-04-15 14:14:49
	 * @return
	 */
	public String formatInfo() {
		return String.valueOf(this.getListenServerPort());
	}

}
