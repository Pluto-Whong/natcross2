package person.pluto.natcross2.serverside.listen.recv;

import person.pluto.natcross2.channel.SocketChannel;

/**
 * <p>
 * 接收处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-15 11:13:20
 */
public interface IRecvHandler<R, W> {

    /**
     * 处理方法
     *
     * @param model
     * @param channel
     * @return
     * @throws Exception
     */
    boolean proc(R model, SocketChannel<? extends R, ? super W> channel) throws Exception;

}
