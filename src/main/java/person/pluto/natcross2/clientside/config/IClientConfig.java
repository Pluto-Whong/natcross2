package person.pluto.natcross2.clientside.config;

import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.clientside.heart.IClientHeartThread;

/**
 * 
 * <p>
 * 客户端配置接口
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:30:04
 * @param <R> 通道读取的类型
 * @param <W> 通道写入的类型
 */
public interface IClientConfig<R, W> {

    /**
     * 获取服务端IP
     * 
     * @author Pluto
     * @since 2020-01-08 08:56:10
     * @return
     */
    String getClientServiceIp();

    /**
     * 获取服务端端口
     * 
     * @author Pluto
     * @since 2020-01-08 08:56:24
     * @return
     */
    Integer getClientServicePort();

    /**
     * 对应的监听端口
     * 
     * @author Pluto
     * @since 2020-01-08 08:56:31
     * @return
     */
    Integer getListenServerPort();

    /**
     * 目标IP
     * 
     * @author Pluto
     * @since 2020-01-08 08:56:56
     * @return
     */
    String getDestIp();

    /**
     * 目标端口
     * 
     * @author Pluto
     * @since 2020-01-08 08:57:03
     * @return
     */
    Integer getDestPort();

    /**
     * 设置目标IP
     * 
     * @author Pluto
     * @since 2020-01-08 08:57:16
     * @param destIp
     */
    void setDestIpPort(String destIp, Integer destPort);

    /**
     * 新建心跳测试线程
     * 
     * @author Pluto
     * @since 2020-01-08 09:03:09
     * @param clientControlThread
     * @return
     */
    IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread);

    /**
     * 新建适配器
     * 
     * @author Pluto
     * @since 2020-01-08 09:03:21
     * @param clientControlThread
     * @return
     */
    IClientAdapter<R, W> newCreateControlAdapter(ClientControlThread clientControlThread);

    /**
     * 新建与服务端的交互线程
     * 
     * @author Pluto
     * @since 2020-01-08 09:03:50
     * @return
     */
    SocketChannel<? extends R, ? super W> newClientChannel();

    /**
     * 创建新的socketPart
     * 
     * @author Pluto
     * @since 2020-01-08 13:47:17
     * @return
     */
    AbsSocketPart newSocketPart(ClientControlThread clientControlThread);

}
