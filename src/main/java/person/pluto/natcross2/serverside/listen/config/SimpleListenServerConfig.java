package person.pluto.natcross2.serverside.listen.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.SimpleSocketPart;
import person.pluto.natcross2.channel.InteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.ClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.ControlSocket;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;
import person.pluto.natcross2.serverside.listen.recv.ClientHeartHandler;
import person.pluto.natcross2.serverside.listen.recv.CommonReplyHandler;
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
		if (this.createServerSocket == null) {
//			ServerSocketChannel openServerSocketChannel = SelectorProvider.provider().openServerSocketChannel();
//			openServerSocketChannel.bind(new InetSocketAddress(this.getListenPort()));
//			return openServerSocketChannel.socket();
			return new ServerSocket(this.getListenPort());
		} else {
			return this.createServerSocket.createServerSocket(this.getListenPort());
		}
	}

	/**
	 * 创建controlSocket使用channel
	 * 
	 * @author Pluto
	 * @since 2020-04-15 13:19:49
	 * @param socket
	 * @return
	 */
	protected SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newControlSocketChannel(
			Socket socket) {
		InteractiveChannel interactiveChannel;
		try {
			interactiveChannel = new InteractiveChannel(socket);
		} catch (IOException e) {
			return null;
		}
		return interactiveChannel;
	}

	@Override
	public IControlSocket newControlSocket(Socket socket, JSONObject config) {
		SocketChannel<? extends InteractiveModel, ? super InteractiveModel> controlSocketChannel = this
				.newControlSocketChannel(socket);
		ControlSocket controlSocket = new ControlSocket(controlSocketChannel);
		controlSocket.addRecvHandler(CommonReplyHandler.INSTANCE);
		controlSocket.addRecvHandler(ClientHeartHandler.INSTANCE);
		return controlSocket;
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
