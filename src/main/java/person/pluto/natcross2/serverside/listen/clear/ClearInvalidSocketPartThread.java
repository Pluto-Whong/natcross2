package person.pluto.natcross2.serverside.listen.clear;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.serverside.listen.ServerListenThread;

/**
 * 
 * <p>
 * 清理无效端口
 * </p>
 *
 * @author Pluto
 * @since 2019-08-21 12:59:03
 */
@Slf4j
@NoArgsConstructor
public class ClearInvalidSocketPartThread implements IClearInvalidSocketPartThread {

	private volatile Thread myThread = null;

	private volatile boolean isAlive = false;

	private ServerListenThread serverListenThread;

	/**
	 * 清理间隔
	 */
	@Setter
	@Getter
	private Long clearIntervalSeconds = 10L;

	public ClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		this.setServerListenThread(serverListenThread);
	}

	@Override
	public void run() {
		while (this.isAlive) {
			this.serverListenThread.clearInvaildSocketPart();

			try {
				TimeUnit.SECONDS.sleep(this.clearIntervalSeconds);
			} catch (InterruptedException e) {
				// do no thing
			}
		}
	}

	@Override
	public void start() {
		this.isAlive = true;
		if (this.myThread == null || !this.myThread.isAlive()) {
			this.myThread = new Thread(this);
			this.myThread.setName("clear-invalid-socket-part-" + serverListenThread.formatInfo());
			this.myThread.start();
		}
		log.info("ClearInvalidSocketPartThread for [{}] started !", this.serverListenThread.getListenPort());
	}

	@Override
	public void cancel() {
		this.isAlive = false;
		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}
		log.info("ClearInvalidSocketPartThread for [{}] cancell !", this.serverListenThread.getListenPort());
	}

	@Override
	public void setServerListenThread(ServerListenThread serverListenThread) {
		this.serverListenThread = serverListenThread;
	}

}
