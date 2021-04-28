package person.pluto.natcross2.executor;

import java.util.concurrent.ScheduledFuture;

/**
 * 
 * <p>
 * 执行器实现
 * </p>
 *
 * @author Pluto
 * @since 2021-04-08 14:38:23
 */
public interface IExecutor {

	/**
	 * 关闭
	 *
	 * @throws Exception
	 * @author Pluto
	 * @since 2021-04-15 10:27:42
	 */
	public void shutdown();

	/**
	 * 默认执行方法
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-13 10:05:32
	 */
	public void execute(Runnable runnable);

	/**
	 * 服务监听线程任务执行器
	 * <p>
	 * For {@link person.pluto.natcross2.serverside.listen.ServerListenThread}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:49:08
	 */
	default public void executeServerListenAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 客户端监听线程任务执行器
	 * <p>
	 * For {@link person.pluto.natcross2.serverside.client.ClientServiceThread}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:49:28
	 */
	default public void executeClientServiceAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 客户端消息处理任务执行器
	 * <p>
	 * For
	 * {@link person.pluto.natcross2.clientside.adapter.InteractiveSimpleClientAdapter#waitMessage()}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:49:48
	 */
	default public void executeClientMessageProc(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 隧道线程执行器
	 * <p>
	 * For {@link person.pluto.natcross2.api.passway.*}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:50:40
	 */
	default public void executePassway(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * nio事件任务执行器
	 * <p>
	 * For {@link person.pluto.natcross2.nio.NioHallows#run()}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:50:56
	 */
	default public void executeNioAction(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 心跳检测定时循环任务执行
	 * 
	 * @param runnable
	 * @param delaySeconds
	 * @author Pluto
	 * @since 2021-04-28 14:13:47
	 */
	public ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds);

	/**
	 * 服务监听清理无效socket对
	 * 
	 * @param runnable
	 * @param delaySeconds
	 * @return
	 * @author Pluto
	 * @since 2021-04-28 15:22:11
	 */
	public ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds);

}
