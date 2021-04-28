package person.pluto.natcross2.serverside.client.adapter;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.common.Optional;
import person.pluto.natcross2.serverside.client.config.IClientServiceConfig;
import person.pluto.natcross2.serverside.client.handler.IPassValueHandler;

/**
 * 
 * <p>
 * 预读后处理适配器
 * </p>
 *
 * @author Pluto
 * @since 2020-01-06 08:56:22
 * @param <R>
 * @param <W>
 */
@Slf4j
public class ReadAheadPassValueAdapter<R, W> implements IClientServiceAdapter {

	/**
	 * 处理器队列
	 */
	private List<IPassValueHandler<? super R, ? extends W>> handlerList = new LinkedList<>();
	/**
	 * 客户端服务配置
	 */
	private final IClientServiceConfig<R, W> config;

	public ReadAheadPassValueAdapter(IClientServiceConfig<R, W> config) {
		this.config = config;
	}

	@Override
	public void procMethod(Socket listenSocket) throws Exception {
		// 建立交互通道
		SocketChannel<? extends R, ? super W> socketChannel;
		try {
			socketChannel = this.config.newSocketChannel(listenSocket);
		} catch (Exception e) {
			log.error("创建socket通道异常", e);
			throw e;
		}

		Optional<R> optional;
		try {
			R read = socketChannel.read();
			optional = Optional.of(read);
		} catch (Exception e) {
			log.error("读取数据异常", e);
			throw e;
		}

		boolean closeFlag = true;
		for (IPassValueHandler<? super R, ? extends W> handler : handlerList) {
			// 按照队列进行执行
			PassValueNextEnum proc = handler.proc(socketChannel, optional);

			// 只要有一个需要不关闭通道则就不得关闭
			if (!proc.isCloseFlag()) {
				closeFlag = false;
			}
			// 如果无需继续向下则退出
			if (!proc.isNextFlag()) {
				break;
			}
		}

		if (closeFlag) {
			try {
				socketChannel.closeSocket();
			} catch (IOException e) {
				log.error("关闭socket异常", e);
				return;
			}
		}

	}

	/**
	 * 添加处理器到最后一个
	 *
	 * @author Pluto
	 * @since 2020-01-06 09:06:55
	 * @param handler
	 * @return
	 */
	public ReadAheadPassValueAdapter<R, W> addLast(IPassValueHandler<? super R, ? extends W> handler) {
		this.handlerList.add(handler);
		return this;
	}

	/**
	 * 设置handlerList
	 *
	 * @author Pluto
	 * @since 2020-01-06 09:07:53
	 * @param handlerList
	 * @return
	 */
	public ReadAheadPassValueAdapter<R, W> setHandlerList(List<IPassValueHandler<? super R, ? extends W>> handlerList) {
		this.handlerList = handlerList;
		return this;
	}

	/**
	 * 获取处理器，可以自行更改
	 *
	 * @author Pluto
	 * @since 2020-01-06 09:06:28
	 * @return
	 */
	public List<IPassValueHandler<? super R, ? extends W>> getHandlerList() {
		return this.handlerList;
	}

}
