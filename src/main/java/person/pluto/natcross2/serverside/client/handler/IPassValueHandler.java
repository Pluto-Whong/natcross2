package person.pluto.natcross2.serverside.client.handler;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.common.Optional;
import person.pluto.natcross2.serverside.client.adapter.PassValueNextEnum;

/**
 * <p>
 * 传值方式客户端是配置的处理接口
 * </p>
 *
 * @param <R>
 * @param <W>
 * @author Pluto
 * @since 2020-01-08 16:47:40
 */
public interface IPassValueHandler<R, W> {

    /**
     * 处理方法
     *
     * @param socketChannel 交互通道
     * @param optional      可以重设值
     * @return
     */
    PassValueNextEnum proc(SocketChannel<? extends R, ? super W> socketChannel, Optional<? extends R> optional);

}
