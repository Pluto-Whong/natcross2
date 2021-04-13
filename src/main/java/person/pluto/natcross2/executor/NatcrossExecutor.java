package person.pluto.natcross2.executor;

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

	/**
	 * 重设执行器
	 *
	 * @param executor
	 * @author Pluto
	 * @since 2021-04-08 14:59:04
	 */
	public static void resetExecutor(IExecutor executor) {
		INSTANCE = executor;
	}

	public static void executeServerListenAccept(Runnable runnable) {
		INSTANCE.executeServerListenAccept(runnable);
	}

	public static void executeClientServiceAccept(Runnable runnable) {
		INSTANCE.executeClientServiceAccept(runnable);
	}

	public static void executeClientMessageProc(Runnable runnable) {
		INSTANCE.executeClientMessageProc(runnable);
	}

	public static void executePassway(Runnable runnable) {
		INSTANCE.executePassway(runnable);
	}

	public static void executeNioAction(Runnable runnable) {
		INSTANCE.executeNioAction(runnable);
	}

}
