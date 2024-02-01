package person.pluto.natcross2.clientside.adapter;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.interactive.ServerWaitModel;

import java.time.LocalDateTime;

/**
 * <p>
 * 客户端适配器
 * </p>
 *
 * @param <R> 处理的对象
 * @param <W> 可写的对象
 * @author Pluto
 * @since 2020-01-08 16:22:43
 */
public interface IClientAdapter<R, W> {

    /**
     * 请求建立控制器
     *
     * @return
     * @throws Exception
     */
    boolean createControlChannel() throws Exception;

    /**
     * 请求建立隧道连接
     *
     * @param serverWaitModel
     * @return
     */
    boolean clientConnect(ServerWaitModel serverWaitModel);

    /**
     * 等待消息处理
     *
     * @throws Exception
     */
    void waitMessage() throws Exception;

    /**
     * 关闭
     *
     * @throws Exception
     */
    void close() throws Exception;

    /**
     * 向控制器发送心跳
     *
     * @throws Exception
     */
    void sendHeartTest() throws Exception;

    /**
     * 获取服务端最后心跳测试/回复的时间
     *
     * @return
     */
    LocalDateTime obtainServerHeartLastRecvTime();

    /**
     * 重设服务端最后心跳测试/回复时间
     * <p>
     * 取 {@link LocalDateTime#now()} 为 设定值
     *
     * @return
     */
    default LocalDateTime resetServerHeartLastRecvTime() {
        return this.resetServerHeartLastRecvTime(LocalDateTime.now());
    }

    /**
     * 重设服务端最后心跳测试/回复时间
     *
     * @param time 自有设置
     * @return
     */
    LocalDateTime resetServerHeartLastRecvTime(LocalDateTime time);

    /**
     * 获取socket读写通道
     *
     * @return
     */
    SocketChannel<? extends R, ? super W> getSocketChannel();

}
