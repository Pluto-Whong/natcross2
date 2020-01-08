package person.pluto.natcross2.channel;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * <p>
 * socket通道
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:19:51
 * @param <R> 读取返回的类型
 * @param <W> 写入的类型
 */
public abstract class SocketChannel<R, W> implements Channel<R, W> {

    /**
     * 获取socket
     * 
     * @author Pluto
     * @since 2020-01-08 16:20:28
     * @return
     */
    abstract public Socket getSocket();

    /**
     * 设置socket
     * 
     * @author Pluto
     * @since 2020-01-08 16:20:37
     * @param socket
     * @throws IOException
     */
    abstract public void setSocket(Socket socket) throws IOException;

    /**
     * 关闭socket
     * 
     * @author Pluto
     * @since 2020-01-08 16:20:45
     * @throws IOException
     */
    abstract public void closeSocket() throws IOException;

    @Override
    public void close() throws IOException {
        this.closeSocket();
    }

}
