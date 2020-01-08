package person.pluto.natcross2.serverside.listen.control;

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

}
