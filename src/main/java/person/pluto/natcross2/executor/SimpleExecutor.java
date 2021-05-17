package person.pluto.natcross2.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 线程执行器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-09 12:46:36
 */
public class SimpleExecutor implements IExecutor {

	private ExecutorService executor = Executors.newCachedThreadPool();

	private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void shutdown() {
		this.executor.shutdownNow();
		this.scheduledExecutor.shutdownNow();
	}

	@Override
	public void execute(Runnable runnable) {
		this.executor.execute(runnable);
	}

	@Override
	public ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds) {
		return this.scheduledExecutor.scheduleWithFixedDelay(runnable, delaySeconds, delaySeconds, TimeUnit.SECONDS);
	}
	
	@Override
	public ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds) {
		return this.scheduledExecutor.scheduleWithFixedDelay(runnable, delaySeconds, delaySeconds, TimeUnit.SECONDS);
	}

}
