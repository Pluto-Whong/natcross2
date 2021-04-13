package person.pluto.natcross2.executor;

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
	 * 默认执行方法
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-13 10:05:32
	 */
	public void execute(Runnable runnable);

	/**
	 * 执行服务监听新端口任务
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-08 15:03:20
	 */
	default public void executeServerListenAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 指定控制端口新端口任务
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-08 17:51:37
	 */
	default public void executeClientServiceAccept(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 执行 客户端消息任务
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-09 12:21:47
	 */
	default public void executeClientMessageProc(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 执行 隧道 线程
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-09 13:21:11
	 */
	default public void executePassway(Runnable runnable) {
		execute(runnable);
	}

	/**
	 * 执行nio容器中的方法
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-13 10:04:17
	 */
	default public void executeNioAction(Runnable runnable) {
		execute(runnable);
	}

}
