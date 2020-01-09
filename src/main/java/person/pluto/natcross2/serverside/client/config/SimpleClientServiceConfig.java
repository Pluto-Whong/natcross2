package person.pluto.natcross2.serverside.client.config;

import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.pluto.natcross2.channel.InteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.client.adapter.IClientServiceAdapter;

/**
 * 
 * <p>
 * 简单交互的客户端服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:45:47
 */
public class SimpleClientServiceConfig implements IClientServiceConfig<InteractiveModel, InteractiveModel> {

    private final Integer listenPort;
    private IClientServiceAdapter clientServiceAdapter;
    private Charset charset = StandardCharsets.UTF_8;

    public SimpleClientServiceConfig(Integer listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public Integer getListenPort() {
        return listenPort;
    }

    @Override
    public ExecutorService newProcExecutorService() {
        return Executors.newCachedThreadPool();
    }

    /**
     * 设置适配器
     * 
     * @author Pluto
     * @since 2020-01-09 16:19:16
     * @param clientServiceAdapter
     */
    public void setClientServiceAdapter(IClientServiceAdapter clientServiceAdapter) {
        this.clientServiceAdapter = clientServiceAdapter;
    }

    @Override
    public IClientServiceAdapter getClientServiceAdapter() {
        return clientServiceAdapter;
    }

    @Override
    public SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newSocketChannel(Socket listenSocket)
            throws Exception {
        InteractiveChannel channel = new InteractiveChannel();
        channel.setSocket(listenSocket);
        channel.setCharset(charset);
        return channel;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    /**
     * 设置字符编码
     * 
     * @author Pluto
     * @since 2020-01-08 16:46:06
     * @param charset
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
