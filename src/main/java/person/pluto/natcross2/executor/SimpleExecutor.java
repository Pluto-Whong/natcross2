package person.pluto.natcross2.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	public void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	@Override
	public void executeServerListenAccept(Runnable runnable) {
		execute(runnable);
	}

	@Override
	public void executeClientServiceAccept(Runnable runnable) {
		execute(runnable);
	}

	@Override
	public void executeClientMessageProc(Runnable runnable) {
		execute(runnable);
	}

	@Override
	public void executePassway(Runnable runnable) {
		execute(runnable);
	}

}
