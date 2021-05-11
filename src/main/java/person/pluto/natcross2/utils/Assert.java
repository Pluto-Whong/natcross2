package person.pluto.natcross2.utils;

/**
 * 
 * <p>
 * 断言
 * </p>
 *
 * @author Pluto
 * @since 2021-04-13 13:46:51
 */
public class Assert {

	/**
	 * 状态判断
	 *
	 * @param expression
	 * @param message
	 * @author Pluto
	 * @since 2021-04-13 13:47:17
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * 是否为true
	 *
	 * @param expression
	 * @param message
	 * @author Pluto
	 * @since 2021-04-13 13:47:24
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 是否为空
	 *
	 * @param object
	 * @param message
	 * @author Pluto
	 * @since 2021-04-13 13:47:30
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 是否非空
	 *
	 * @param object
	 * @param message
	 * @author Pluto
	 * @since 2021-04-13 13:47:38
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
