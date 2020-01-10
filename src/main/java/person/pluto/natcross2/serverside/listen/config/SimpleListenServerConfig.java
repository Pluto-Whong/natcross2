package person.pluto.natcross2.serverside.listen.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.SimpleSocketPart;
import person.pluto.natcross2.channel.InteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.ClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.ControlSocket;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;
import person.pluto.natcross2.serverside.listen.serversocket.ICreateServerSocket;

/**
 * <p>
 * 简单的交互、隧道；监听服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:53:17
 */
@Data
@NoArgsConstructor
public class SimpleListenServerConfig implements IListenServerConfig {

    private Integer listenPort;

    private Long invaildMillis = 60000L;
    private Long clearInterval = 10L;

    private Charset charset = StandardCharsets.UTF_8;

    private ICreateServerSocket createServerSocket;

    private int streamCacheSize = 8196;

    public SimpleListenServerConfig(Integer listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public ServerSocket createServerSocket() throws Exception {
        if (createServerSocket == null) {
            return new ServerSocket(this.getListenPort());
        } else {
            return createServerSocket.createServerSocket(this.getListenPort());
        }
    }

    @Override
    public IControlSocket newControlSocket(SocketChannel<?, ?> socketChannel, JSONObject config) {
        InteractiveChannel interactiveChannel;
        try {
            interactiveChannel = new InteractiveChannel(socketChannel.getSocket());
        } catch (IOException e) {
            return null;
        }
        return new ControlSocket(interactiveChannel);
    }

    @Override
    public IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
        ClearInvalidSocketPartThread clearInvalidSocketPartThread = new ClearInvalidSocketPartThread(
                serverListenThread);
        clearInvalidSocketPartThread.setClearIntervalSeconds(this.getClearInterval());
        return clearInvalidSocketPartThread;
    }

    @Override
    public AbsSocketPart newSocketPart(ServerListenThread serverListenThread) {
        SimpleSocketPart socketPart = new SimpleSocketPart(serverListenThread);
        socketPart.setInvaildMillis(this.getInvaildMillis());
        socketPart.setStreamCacheSize(this.getStreamCacheSize());
        return socketPart;
    }

}
