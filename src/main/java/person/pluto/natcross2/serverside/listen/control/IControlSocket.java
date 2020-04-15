package person.pluto.natcross2.serverside.listen.control;

import person.pluto.natcross2.serverside.listen.ServerListenThread;

/**
 * 
 * <p>
 * 控制端口接口
 * </p>
 *
 * @author Pluto
 * @since 2020-01-07 09:52:51
 */
public interface IControlSocket {

    /**
     * 是否有效
     * 
     * @author Pluto
     * @since 2020-01-08 16:54:13
     * @return
     */
    boolean isValid();

    /**
     * 发送隧道等待状态
     * 
     * @author Pluto
     * @since 2020-01-08 16:54:18
     * @param socketPartKey
     * @return
     */
    boolean sendClientWait(String socketPartKey);

    /**
     * 关闭
     * 
     * @author Pluto
     * @since 2020-01-08 16:54:40
     */
    void close();

    /**
     * 开启接收线程
     * 
     * @author Pluto
     * @since 2020-04-15 11:36:44
     */
    void startRecv();

    /**
     * 设置控制的监听线程
     * 
     * @author Pluto
     * @since 2020-04-15 13:10:25
     * @param serverListenThread
     */
    void setServerListen(ServerListenThread serverListenThread);

}
