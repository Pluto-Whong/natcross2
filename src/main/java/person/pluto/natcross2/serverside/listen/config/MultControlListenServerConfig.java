package person.pluto.natcross2.serverside.listen.config;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.serverside.listen.clear.IClearInvalidSocketPartThread;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;
import person.pluto.natcross2.serverside.listen.control.MultControlSocket;

/**
 * <p>
 * 多客户端；监听服务配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:53:17
 */
public class MultControlListenServerConfig implements IListenServerConfig {

	protected final IListenServerConfig baseConfig;

	protected final MultControlSocket multControlSocket = new MultControlSocket();

	public MultControlListenServerConfig(IListenServerConfig baseConfig) {
		this.baseConfig = baseConfig;
	}

	@Override
	public ServerSocket createServerSocket() throws Exception {
		return this.baseConfig.createServerSocket();
	}

	@Override
	public IControlSocket newControlSocket(Socket socket, JSONObject config) {
		IControlSocket controlSocket = this.baseConfig.newControlSocket(socket, config);
		multControlSocket.addControlSocket(controlSocket);
		return multControlSocket;
	}

	@Override
	public IClearInvalidSocketPartThread newClearInvalidSocketPartThread(ServerListenThread serverListenThread) {
		return this.baseConfig.newClearInvalidSocketPartThread(serverListenThread);
	}

	@Override
	public AbsSocketPart newSocketPart(ServerListenThread serverListenThread) {
		return this.baseConfig.newSocketPart(serverListenThread);
	}

	@Override
	public Integer getListenPort() {
		return this.baseConfig.getListenPort();
	}

	@Override
	public Charset getCharset() {
		return this.baseConfig.getCharset();
	}

}
