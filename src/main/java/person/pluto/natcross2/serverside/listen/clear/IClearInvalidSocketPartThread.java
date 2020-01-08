package person.pluto.natcross2.serverside.listen.clear;

import person.pluto.natcross2.serverside.listen.ServerListenThread;

/**
 * 
 * <p>
 * 清理无效端口 线程
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:50:09
 */
public interface IClearInvalidSocketPartThread extends Runnable {

    /**
     * 设置附属的穿透线程
     * 
     * @author Pluto
     * @since 2020-01-08 16:50:22
     * @param serverListenThread
     */
    void setServerListenThread(ServerListenThread serverListenThread);

    /**
     * 启动
     * 
     * @author Pluto
     * @since 2020-01-08 16:50:46
     */
    void start();

    /**
     * 退出
     * 
     * @author Pluto
     * @since 2020-01-08 16:50:51
     */
    void cancel();

}
