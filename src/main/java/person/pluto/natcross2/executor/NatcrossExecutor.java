package person.pluto.natcross2.executor;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * <p>
 * 线程执行器
 * <p>
 * 主要是为了统一位置，方便管理
 * </p>
 *
 * @author Pluto
 * @since 2021-04-08 14:37:52
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NatcrossExecutor {

	private static volatile IExecutor INSTANCE = new SimpleExecutor();

	public static void shutdown() {
		INSTANCE.shutdown();
	}

	/**
	 * 重设执行器
	 *
	 * @param executor
	 * @author Pluto
	 * @since 2021-04-08 14:59:04
	 */
	public static void resetExecutor(IExecutor executor) {
		if (Objects.nonNull(INSTANCE)) {
			try {
				INSTANCE.shutdown();
			} catch (Exception e) {
				//
			}
		}
		INSTANCE = executor;
	}

	/**
	 * 服务监听线程任务执行器
	 * <p>
	 * For {@link person.pluto.natcross2.serverside.listen.ServerListenThread}
	 *
	 * @param runnable
	 * @author Pluto
	 * @since 2021-04-26 16:49:08
	 */
	public static void executeServerListenAccept(Runnable runnable) {
		INSTANCE.executeServerListenAccept(runnable);
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
	public static void executeClientServiceAccept(Runnable runnable) {
		INSTANCE.executeClientServiceAccept(runnable);
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
	public static void executeClientMessageProc(Runnable runnable) {
		INSTANCE.executeClientMessageProc(runnable);
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
	public static void executePassway(Runnable runnable) {
		INSTANCE.executePassway(runnable);
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
	public static void executeNioAction(Runnable runnable) {
		INSTANCE.executeNioAction(runnable);
	}

}
