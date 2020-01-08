package person.pluto.natcross2.serverside.client.config;

import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.client.adapter.IClientServiceAdapter;

/**
 * 
 * <p>
 * 客户端服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:43:17
 * @param <R> 交互通道读取的类
 * @param <W> 交互通道可写的类
 */
public interface IClientServiceConfig<R, W> {

    /**
     * 监听端口
     * 
     * @author Pluto
     * @since 2020-01-08 16:43:51
     * @return
     */
    Integer getListenPort();

    /**
     * 执行使用的线程池
     * 
     * @author Pluto
     * @since 2020-01-08 16:43:59
     * @return
     */
    ExecutorService newProcExecutorService();

    /**
     * 客户端适配器
     * 
     * @author Pluto
     * @since 2020-01-08 16:44:14
     * @return
     */
    IClientServiceAdapter getClientServiceAdapter();

    /**
     * 交互通道
     * 
     * @author Pluto
     * @since 2020-01-08 16:44:23
     * @param listenSocket
     * @return
     * @throws Exception
     */
    SocketChannel<? extends R, ? super W> newSocketChannel(Socket listenSocket) throws Exception;

    /**
     * 字符集
     * 
     * @author Pluto
     * @since 2020-01-08 16:44:31
     * @return
     */
    Charset getCharset();

}
