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
	 * 执行服务监听新端口任务
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-08 15:03:20
	 */
	void executeServerListenAccept(Runnable runnable);

	/**
	 * 指定控制端口新端口任务
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-08 17:51:37
	 */
	void executeClientServiceAccept(Runnable runnable);

	/**
	 * 执行 客户端消息任务
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-09 12:21:47
	 */
	void executeClientMessageProc(Runnable runnable);

	/**
	 * 执行 隧道 线程
	 * 
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-09 13:21:11
	 */
	void executePassway(Runnable runnable);

}
