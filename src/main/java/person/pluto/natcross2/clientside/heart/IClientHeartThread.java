package person.pluto.natcross2.clientside.heart;

/**
 * <p>
 * 心跳测试线程
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:33:03
 */
public interface IClientHeartThread {

    /**
     * 是否还活着
     *
     * @return
     */
    boolean isAlive();

    /**
     * 退出
     */
    void cancel();

    /**
     * 开始
     */
    void start();

}
