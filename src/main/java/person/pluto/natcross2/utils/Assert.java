package person.pluto.natcross2.utils;

/**
 * <p>
 * 断言
 * </p>
 *
 * @author Pluto
 * @since 2021-04-13 13:46:51
 */
public final class Assert {

    private Assert() {
    }

    /**
     * 状态判断
     *
     * @param expression
     * @param message
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
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
