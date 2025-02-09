package person.pluto.natcross2.serverside.listen.config;

import com.alibaba.fastjson.JSONObject;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
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
     * @return
     */
    Integer getListenPort();

    /**
     * 新建无效端口处理线程
     *
     * @param serverListenThread
     * @return
     */
    IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread);

    /**
     * 创建隧道伙伴
     *
     * @param serverListenThread
     * @return
     */
    AbsSocketPart newSocketPart(ServerListenThread serverListenThread);

    /**
     * 获取字符集
     *
     * @return
     */
    Charset getCharset();

    /**
     * 新建控制器
     *
     * @param socket
     * @param config
     * @return
     */
    IControlSocket newControlSocket(Socket socket, JSONObject config);

    /**
     * 创建监听端口
     *
     * @return
     * @throws Exception
     */
    ServerSocket createServerSocket() throws Exception;
}
