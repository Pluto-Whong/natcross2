package person.pluto.natcross2.serverside.listen.clear;

/**
 * <p>
 * 清理无效端口 线程
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:50:09
 */
public interface IClearInvalidSocketPartThread extends Runnable {

    /**
     * 启动
     */
    void start();

    /**
     * 退出
     */
    void cancel();

}
