package person.pluto.natcross2;

import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.config.AllSecretInteractiveClientConfig;
import person.pluto.natcross2.clientside.config.InteractiveClientConfig;
import person.pluto.natcross2.clientside.config.SecretInteractiveClientConfig;

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

    // 穿透的目标，即要暴露在外网的内网IP
    public static final String destIp = "127.0.0.1";
    // 要暴露的内网端口
    public static final int destPort = 8080;

    public static void main(String[] args) throws Exception {
//        simple();
        secret();
//        secretAll();
    }

    /**
     * 交互、隧道都进行加密
     * 
     * @author Pluto
     * @since 2020-01-08 17:29:54
     * @throws Exception
     */
    public static void secretAll() throws Exception {
        AllSecretInteractiveClientConfig config = new AllSecretInteractiveClientConfig();

        // 设置服务端IP和端口
        config.setClientServiceIp(ServerApp.serviceIp);
        config.setClientServicePort(ServerApp.servicePort);
        // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
        config.setListenServerPort(ServerApp.listengPort);
        // 设置要暴露的目标IP和端口
        config.setDestIp(destIp);
        config.setDestPort(destPort);

        // 设置交互密钥和签名key
        config.setBaseAesKey(ServerApp.aesKey);
        config.setTokenKey(ServerApp.tokenKey);
        // 设置隧道交互密钥
        config.setBasePasswayKey(ServerApp.aesKey);

        new ClientControlThread(config).createControl();
    }

    /**
     * 交互加密，即交互验证
     * 
     * @author Pluto
     * @since 2020-01-08 17:30:13
     * @throws Exception
     */
    public static void secret() throws Exception {
        SecretInteractiveClientConfig config = new SecretInteractiveClientConfig();

        // 设置服务端IP和端口
        config.setClientServiceIp(ServerApp.serviceIp);
        config.setClientServicePort(ServerApp.servicePort);
        // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
        config.setListenServerPort(ServerApp.listengPort);
        // 设置要暴露的目标IP和端口
        config.setDestIp(destIp);
        config.setDestPort(destPort);

        // 设置交互密钥和签名key
        config.setBaseAesKey(ServerApp.aesKey);
        config.setTokenKey(ServerApp.tokenKey);

        new ClientControlThread(config).createControl();
    }

    /**
     * 无加密、无验证
     * 
     * @author Pluto
     * @since 2020-01-08 17:30:22
     * @throws Exception
     */
    public static void simple() throws Exception {
        InteractiveClientConfig config = new InteractiveClientConfig();

        // 设置服务端IP和端口
        config.setClientServiceIp(ServerApp.serviceIp);
        config.setClientServicePort(ServerApp.servicePort);
        // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
        config.setListenServerPort(ServerApp.listengPort);
        // 设置要暴露的目标IP和端口
        config.setDestIp(destIp);
        config.setDestPort(destPort);

        new ClientControlThread(config).createControl();
    }

}
