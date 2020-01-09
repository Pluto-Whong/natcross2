package person.pluto.natcross2.serverside.listen.config;

import java.net.ServerSocket;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;

/**
 * 
 * <p>
 * 穿透监听服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:51:04
 */
public interface IListenServerConfig {

    /**
     * 获取监听的端口
     * 
     * @author Pluto
     * @since 2020-01-08 16:51:17
     * @return
     */
    Integer getListenPort();

    /**
     * 新建无效端口处理线程
     * 
     * @author Pluto
     * @since 2020-01-08 16:51:26
     * @param serverListenThread
     * @return
     */
    IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread);

    /**
     * 创建隧道伙伴
     * 
     * @author Pluto
     * @since 2020-01-08 16:51:41
     * @param serverListenThread
     * @return
     */
    AbsSocketPart newSocketPart(ServerListenThread serverListenThread);

    /**
     * 获取字符集
     * 
     * @author Pluto
     * @since 2020-01-08 16:51:57
     * @return
     */
    Charset getCharset();

    /**
     * 新建控制器
     * 
     * @author Pluto
     * @since 2020-01-08 16:52:05
     * @param socketChannel
     * @param config
     * @return
     */
    IControlSocket newControlSocket(SocketChannel<?, ?> socketChannel, JSONObject config);

    /**
     * 创建监听端口
     * 
     * @author wangmin1994@qq.com
     * @since 2020-01-09 13:24:13
     * @return
     * @throws Exception
     */
    default public ServerSocket createServerSocket() throws Exception {
        return new ServerSocket(this.getListenPort());
    }
}
