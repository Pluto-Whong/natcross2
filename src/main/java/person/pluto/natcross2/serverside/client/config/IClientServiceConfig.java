package person.pluto.natcross2.serverside.client.config;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.client.adapter.IClientServiceAdapter;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * <p>
 * 客户端服务配置
 * </p>
 *
 * @param <R> 交互通道读取的类
 * @param <W> 交互通道可写的类
 * @author Pluto
 * @since 2020-01-08 16:43:17
 */
public interface IClientServiceConfig<R, W> {

    /**
     * 监听端口
     *
     * @return
     */
    Integer getListenPort();

    /**
     * 创建监听端口
     *
     * @return
     * @throws Exception
     */
    ServerSocket createServerSocket() throws Exception;

    /**
     * 客户端适配器
     *
     * @return
     */
    IClientServiceAdapter getClientServiceAdapter();

    /**
     * 交互通道
     *
     * @param listenSocket
     * @return
     * @throws Exception
     */
    SocketChannel<? extends R, ? super W> newSocketChannel(Socket listenSocket) throws Exception;

    /**
     * 字符集
     *
     * @return
     */
    Charset getCharset();

}
