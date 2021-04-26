package person.pluto.natcross2;

import person.pluto.natcross2.CommonConstants.ListenDest;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.config.AllSecretInteractiveClientConfig;
import person.pluto.natcross2.clientside.config.HttpRouteClientConfig;
import person.pluto.natcross2.clientside.config.InteractiveClientConfig;
import person.pluto.natcross2.clientside.config.SecretInteractiveClientConfig;
import person.pluto.natcross2.model.HttpRoute;

/**
 * 
 * <p>
 * 客户端，放在内网侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-09 16:26:44
 */
public class ClientApp {

	public static void main(String[] args) throws Exception {
//		simple();
//		secret();
//		secretAll();
		secretHttpRoute();
	}

	/**
	 * http路由
	 * 
	 * 默认使用交互加密、数据不加密的策略
	 * 
	 * @author Pluto
	 * @since 2020-04-24 11:42:18
	 * @throws Exception
	 */
	public static void secretHttpRoute() throws Exception {
		HttpRoute[] routes = new HttpRoute[] {
				//
				HttpRoute.of("localhost", "127.0.0.1", 8080),
				//
				HttpRoute.of(true, "127.0.0.1", "127.0.0.1", 8080),
				//
		};

		for (ListenDest model : CommonConstants.listenDestArray) {
			SecretInteractiveClientConfig baseConfig = new SecretInteractiveClientConfig();

			// 设置服务端IP和端口
			baseConfig.setClientServiceIp(CommonConstants.serviceIp);
			baseConfig.setClientServicePort(CommonConstants.servicePort);
			// 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
			baseConfig.setListenServerPort(model.listenPort);

			// 设置交互密钥和签名key
			baseConfig.setBaseAesKey(CommonConstants.aesKey);
			baseConfig.setTokenKey(CommonConstants.tokenKey);

			HttpRouteClientConfig config = new HttpRouteClientConfig(baseConfig);
			config.addRoute(routes);

			new ClientControlThread(config).createControl();
		}
	}

	/**
	 * 交互、隧道都进行加密
	 * 
	 * @author Pluto
	 * @since 2020-01-08 17:29:54
	 * @throws Exception
	 */
	public static void secretAll() throws Exception {
		for (ListenDest model : CommonConstants.listenDestArray) {
			AllSecretInteractiveClientConfig config = new AllSecretInteractiveClientConfig();

			// 设置服务端IP和端口
			config.setClientServiceIp(CommonConstants.serviceIp);
			config.setClientServicePort(CommonConstants.servicePort);
			// 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
			config.setListenServerPort(model.listenPort);
			// 设置要暴露的目标IP和端口
			config.setDestIp(model.destIp);
			config.setDestPort(model.destPort);

			// 设置交互密钥和签名key
			config.setBaseAesKey(CommonConstants.aesKey);
			config.setTokenKey(CommonConstants.tokenKey);
			// 设置隧道交互密钥
			config.setBasePasswayKey(CommonConstants.aesKey);

			new ClientControlThread(config).createControl();
		}
	}

	/**
	 * 交互加密，即交互验证
	 * 
	 * @author Pluto
	 * @since 2020-01-08 17:30:13
	 * @throws Exception
	 */
	public static void secret() throws Exception {
		for (ListenDest model : CommonConstants.listenDestArray) {
			SecretInteractiveClientConfig config = new SecretInteractiveClientConfig();

			// 设置服务端IP和端口
			config.setClientServiceIp(CommonConstants.serviceIp);
			config.setClientServicePort(CommonConstants.servicePort);
			// 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
			config.setListenServerPort(model.listenPort);
			// 设置要暴露的目标IP和端口
			config.setDestIp(model.destIp);
			config.setDestPort(model.destPort);

			// 设置交互密钥和签名key
			config.setBaseAesKey(CommonConstants.aesKey);
			config.setTokenKey(CommonConstants.tokenKey);

			new ClientControlThread(config).createControl();
		}
	}

	/**
	 * 无加密、无验证
	 * 
	 * @author Pluto
	 * @since 2020-01-08 17:30:22
	 * @throws Exception
	 */
	public static void simple() throws Exception {
		for (ListenDest model : CommonConstants.listenDestArray) {
			InteractiveClientConfig config = new InteractiveClientConfig();

			// 设置服务端IP和端口
			config.setClientServiceIp(CommonConstants.serviceIp);
			config.setClientServicePort(CommonConstants.servicePort);
			// 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
			config.setListenServerPort(model.listenPort);
			// 设置要暴露的目标IP和端口
			config.setDestIp(model.destIp);
			config.setDestPort(model.destPort);

			new ClientControlThread(config).createControl();
		}
	}

}
