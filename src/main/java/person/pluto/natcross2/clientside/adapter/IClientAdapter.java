package person.pluto.natcross2.clientside.adapter;

/**
 * 
 * <p>
 * 客户端适配器
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:22:43
 * @param <R> 处理的对象
 * @param <W> 可写的对象
 */
public interface IClientAdapter<R, W> {

    /**
     * 请求建立控制器
     * 
     * @author Pluto
     * @since 2020-01-08 16:22:53
     * @return
     * @throws Exception
     */
    boolean createControl() throws Exception;

    /**
     * 处理方法
     * 
     * @author Pluto
     * @since 2020-01-08 16:23:09
     * @param read
     */
    void procMethod(R read);

    /**
     * 等待消息处理
     * 
     * @author Pluto
     * @since 2020-01-08 16:23:33
     * @throws Exception
     */
    void waitMessage() throws Exception;

    /**
     * 关闭
     * 
     * @author Pluto
     * @since 2020-01-08 16:23:42
     * @throws Exception
     */
    void close() throws Exception;

    /**
     * 向控制器发送心跳
     * 
     * @author Pluto
     * @since 2020-01-08 16:23:49
     * @throws Exception
     */
    void sendUrgentData() throws Exception;

}
