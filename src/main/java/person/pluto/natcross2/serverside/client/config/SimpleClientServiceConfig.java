package person.pluto.natcross2.serverside.client.config;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NoArgsConstructor;
import person.pluto.natcross2.channel.InteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.client.adapter.DefaultReadAheadPassValueAdapter;
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
@NoArgsConstructor
public class SimpleClientServiceConfig implements IClientServiceConfig<InteractiveModel, InteractiveModel> {

	private Integer listenPort;
	private IClientServiceAdapter clientServiceAdapter = new DefaultReadAheadPassValueAdapter(this);
	private Charset charset = StandardCharsets.UTF_8;

	public SimpleClientServiceConfig(Integer listenPort) {
		this.listenPort = listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	@Override
	public Integer getListenPort() {
		return listenPort;
	}

	@Override
	public ServerSocket createServerSocket() throws Exception {
		ServerSocketChannel openServerSocketChannel = SelectorProvider.provider().openServerSocketChannel();
		openServerSocketChannel.bind(new InetSocketAddress(this.getListenPort()));
		return openServerSocketChannel.socket();
//		return new ServerSocket(this.getListenPort());
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
