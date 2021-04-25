package person.pluto.natcross2.clientside.heart;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.clientside.ClientControlThread;

/**
 * 
 * <p>
 * 心跳检测线程
 * </p>
 *
 * @author Pluto
 * @since 2021-04-25 17:53:44
 */
@Slf4j
public class ClientHeartThread implements IClientHeartThread, Runnable {

	private volatile Thread myThread = null;

	private volatile boolean isAlive = false;

	private ClientControlThread clientControlThread;

	private long heartIntervalSeconds = 10L;
	private int tryReclientCount = 10;

	public ClientHeartThread(ClientControlThread clientControlThread) {
		this.clientControlThread = clientControlThread;
	}

	@Override
	public void run() {
		ClientControlThread clientControlThread = this.clientControlThread;

		int failCount = 0;

		while (this.isAlive) {
			try {
				TimeUnit.SECONDS.sleep(this.heartIntervalSeconds);
			} catch (InterruptedException e) {
				this.cancel();
				return;
			}
			try {
				log.debug("send urgent data to {}", clientControlThread.getListenServerPort());
				clientControlThread.sendUrgentData();
				failCount = 0;
			} catch (Exception e) {
				log.warn("{} 心跳异常，即将重新连接", clientControlThread.getListenServerPort());
				this.clientControlThread.stopClient();

				if (this.isAlive) {
					failCount++;
					try {
						boolean createControl = clientControlThread.createControl();
						if (createControl) {
							clientControlThread.start();
							log.info("重新建立连接 {} 成功，在第 {} 次", clientControlThread.getListenServerPort(), failCount);
							continue;
						}
					} catch (Exception reClientException) {
						log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + failCount + " 次",
								reClientException);
					}

					log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + failCount + " 次");

					if (failCount >= this.tryReclientCount) {
						log.error("尝试重新连接 {} 超过最大次数，尝试关闭客户端", clientControlThread.getListenServerPort());
						this.cancel();
						clientControlThread.cancell();
						log.info("尝试重新连接 {} 超过最大次数，关闭客户端成功", clientControlThread.getListenServerPort());
					}
				}
			}
		}
	}

	@Override
	public void start() {
		this.isAlive = true;
		if (this.myThread == null || !this.myThread.isAlive()) {
			this.myThread = new Thread(this);
			this.myThread.setName("client-heart-" + clientControlThread.formatInfo());
			this.myThread.start();
		}
	}

	@Override
	public void cancel() {
		this.isAlive = false;

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}
	}

	@Override
	public boolean isAlive() {
		return this.isAlive;
	}

	public void setHeartIntervalSeconds(long heartIntervalSeconds) {
		this.heartIntervalSeconds = heartIntervalSeconds;
	}

	public void setTryReclientCount(int tryReclientCount) {
		this.tryReclientCount = tryReclientCount;
	}

}
