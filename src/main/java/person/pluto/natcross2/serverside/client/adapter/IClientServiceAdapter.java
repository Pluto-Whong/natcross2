package person.pluto.natcross2.serverside.client.adapter;

import java.net.Socket;

/**
 * 
 * <p>
 * 客户端服务适配器
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:40:35
 */
public interface IClientServiceAdapter {

    /**
     * 处理方法
     * 
     * @author Pluto
     * @since 2020-01-08 16:40:43
     * @param listenSocket
     * @throws Exception
     */
    void procMethod(Socket listenSocket) throws Exception;

}
