package person.pluto.natcross2.serverside.listen.clear;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.executor.NatcrossExecutor;
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
public class ClearInvalidSocketPartThread implements IClearInvalidSocketPartThread {

	private final ServerListenThread serverListenThread;

	private ScheduledFuture<?> scheduledFuture;

	/**
	 * 清理间隔
	 */
	@Setter
	@Getter
	private long clearIntervalSeconds = 10L;

	public ClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		this.serverListenThread = serverListenThread;
	}

	@Override
	public void run() {
		this.serverListenThread.clearInvaildSocketPart();
	}

	@Override
	public synchronized void start() {
		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.isNull(scheduledFuture) || scheduledFuture.isCancelled()) {
			this.scheduledFuture = NatcrossExecutor.scheduledClearInvalidSocketPart(this, this.clearIntervalSeconds);
		}

		log.info("ClearInvalidSocketPartThread for [{}] started !", this.serverListenThread.getListenPort());
	}

	@Override
	public void cancel() {
		ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
		if (Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
			this.scheduledFuture = null;
			scheduledFuture.cancel(false);
		}

		log.info("ClearInvalidSocketPartThread for [{}] cancell !", this.serverListenThread.getListenPort());
	}

}
