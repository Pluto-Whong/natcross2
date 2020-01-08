package person.pluto.natcross2.serverside.listen.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.SimpleSocketPart;
import person.pluto.natcross2.channel.InteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.ClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.ControlSocket;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;

/**
 * <p>
 * 简单的交互、隧道；监听服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:53:17
 */
@NoArgsConstructor
public class SimpleListenServerConfig implements IListenServerConfig {

    private Integer listenPort;

    @Setter
    @Getter
    private Long invaildMillis = 60000L;
    @Setter
    @Getter
    private Long clearInterval = 10L;

    private Charset charset = StandardCharsets.UTF_8;

    public SimpleListenServerConfig(Integer listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public Integer getListenPort() {
        return this.listenPort;
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
        return socketPart;
    }

    @Override
    public Charset getCharset() {
        return this.charset;
    }

    /**
     * 设置字符集
     * 
     * @author Pluto
     * @since 2020-01-08 16:53:53
     * @param charset
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
