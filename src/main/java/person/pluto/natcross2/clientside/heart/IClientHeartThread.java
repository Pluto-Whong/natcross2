package person.pluto.natcross2.clientside.heart;

/**
 * 
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
     * @author Pluto
     * @since 2020-01-08 16:33:15
     * @return
     */
    boolean isAlive();

    /**
     * 退出
     * 
     * @author Pluto
     * @since 2020-01-08 16:33:24
     */
    void cancel();

    /**
     * 开始
     * 
     * @author Pluto
     * @since 2020-01-08 16:33:29
     */
    void start();

}
