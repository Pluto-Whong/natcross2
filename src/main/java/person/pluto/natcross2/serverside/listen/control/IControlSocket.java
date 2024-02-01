package person.pluto.natcross2.serverside.listen.control;

import person.pluto.natcross2.serverside.listen.IServerListen;

/**
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
     * @return
     */
    boolean isValid();

    /**
     * 发送隧道等待状态
     *
     * @param socketPartKey
     * @return
     */
    boolean sendClientWait(String socketPartKey);

    /**
     * 关闭
     */
    void close();

    /**
     * 替换关闭
     * <p>
     * 适配多客户端模式，若是替换关闭则可能不进行关闭 <br>
     * 若是传统1对1模式，则等价调用 {@link #close()}
     *
     * @since 2.3
     */
    default void replaceClose() {
        this.close();
    }

    /**
     * 开启接收线程
     * <p>
     * 实现的类需要自己进行幂等性处理
     */
    void startRecv();

    /**
     * 设置控制的监听线程
     *
     * @param serverListen
     */
    void setServerListen(IServerListen serverListen);

}
