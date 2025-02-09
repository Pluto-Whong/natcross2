package person.pluto.natcross2.clientside.config;

import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.clientside.heart.IClientHeartThread;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.spi.SelectorProvider;

/**
 * <p>
 * 客户端配置接口
 * </p>
 *
 * @param <R> 通道读取的类型
 * @param <W> 通道写入的类型
 * @author Pluto
 * @since 2020-01-08 16:30:04
 */
public interface IClientConfig<R, W> {

    /**
     * 获取服务端IP
     *
     * @return
     */
    String getClientServiceIp();

    /**
     * 获取服务端端口
     *
     * @return
     */
    Integer getClientServicePort();

    /**
     * 对应的监听端口
     *
     * @return
     */
    Integer getListenServerPort();

    /**
     * 目标IP
     *
     * @return
     */
    String getDestIp();

    /**
     * 目标端口
     *
     * @return
     */
    Integer getDestPort();

    /**
     * 设置目标IP
     *
     * @param destIp
     */
    void setDestIpPort(String destIp, Integer destPort);

    /**
     * 新建心跳测试线程
     *
     * @param clientControlThread
     * @return
     */
    IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread);

    /**
     * 新建适配器
     *
     * @param clientControlThread
     * @return
     */
    IClientAdapter<R, W> newCreateControlAdapter(ClientControlThread clientControlThread);

    /**
     * 新建与服务端的交互线程
     *
     * @return
     */
    SocketChannel<? extends R, ? super W> newClientChannel();

    /**
     * 创建新的socketPart
     *
     * @return
     */
    AbsSocketPart newSocketPart(ClientControlThread clientControlThread);

    /**
     * 创建目标端口
     *
     * @return
     * @throws Exception
     */
    default Socket newDestSocket() throws Exception {
        java.nio.channels.SocketChannel openSocketChannel = SelectorProvider.provider().openSocketChannel();
        openSocketChannel.connect(new InetSocketAddress(this.getDestIp(), this.getDestPort()));
        return openSocketChannel.socket();
//		return new Socket(this.getDestIp(), this.getDestPort());
    }

}
