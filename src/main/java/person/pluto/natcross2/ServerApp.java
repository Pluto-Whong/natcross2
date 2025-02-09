package person.pluto.natcross2;

import org.apache.commons.lang3.StringUtils;
import person.pluto.natcross2.CommonConstants.ListenDest;
import person.pluto.natcross2.serverside.client.ClientServiceThread;
import person.pluto.natcross2.serverside.client.config.SecretSimpleClientServiceConfig;
import person.pluto.natcross2.serverside.client.config.SimpleClientServiceConfig;
import person.pluto.natcross2.serverside.listen.ListenServerControl;
import person.pluto.natcross2.serverside.listen.config.AllSecretSimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.MultControlListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.SecretSimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.SimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.serversocket.ICreateServerSocket;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

/**
 * <p>
 * 服务端，放在外网侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-09 16:27:03
 */
public class ServerApp {

    // 你的p12格式的证书路径
    private static final String sslKeyStorePath = System.getenv("sslKeyStorePath");
    // 你的证书密码
    private static final String sslKeyStorePassword = System.getenv("sslKeyStorePassword");

    public static ICreateServerSocket createServerSocket;

    public static void main(String[] args) throws Exception {

        // 如果需要HTTPS协议的支持，则填写sslKeyStorePath、sslKeyStorePassword或在环境变量中定义
        if (StringUtils.isNoneBlank(sslKeyStorePath, sslKeyStorePassword)) {
            createServerSocket = listenPort -> {
                KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(Files.newInputStream(Paths.get(sslKeyStorePath)), sslKeyStorePassword.toCharArray());
                KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("sunx509");
                keyFactory.init(keystore, sslKeyStorePassword.toCharArray());

                SSLContext ctx = SSLContext.getInstance("TLSv1.2");
                ctx.init(keyFactory.getKeyManagers(), null, null);

                SSLServerSocketFactory serverSocketFactory = ctx.getServerSocketFactory();

                return serverSocketFactory.createServerSocket(listenPort);
            };
        }

//		simple();
        secret();
//		secretAll();
//		multControlSecret();
    }

    /**
     * 多客户端，控制通道加密
     */
    public static void multControlSecret() throws Exception {
        // 设置并启动客户端服务线程
        SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
        // 设置交互aes密钥和签名密钥
        config.setBaseAesKey(CommonConstants.aesKey);
        config.setTokenKey(CommonConstants.tokenKey);
        new ClientServiceThread(config).start();

        for (ListenDest model : CommonConstants.listenDestArray) {
            // 设置并启动一个穿透端口
            SecretSimpleListenServerConfig baseListengConfig = new SecretSimpleListenServerConfig(model.listenPort);
            // 设置交互aes密钥和签名密钥，这里使用和客户端服务相同的密钥，可以根据需要设置不同的
            baseListengConfig.setBaseAesKey(CommonConstants.aesKey);
            baseListengConfig.setTokenKey(CommonConstants.tokenKey);
            baseListengConfig.setCreateServerSocket(createServerSocket);

            MultControlListenServerConfig listengConfig = new MultControlListenServerConfig(baseListengConfig);

            ListenServerControl.createNewListenServer(listengConfig);
        }
    }

    /**
     * 交互、隧道都进行加密
     */
    public static void secretAll() throws Exception {
        // 设置并启动客户端服务线程
        SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
        // 设置交互aes密钥和签名密钥
        config.setBaseAesKey(CommonConstants.aesKey);
        config.setTokenKey(CommonConstants.tokenKey);
        new ClientServiceThread(config).start();

        for (ListenDest model : CommonConstants.listenDestArray) {
            AllSecretSimpleListenServerConfig listengConfig = new AllSecretSimpleListenServerConfig(model.listenPort);
            // 设置交互aes密钥和签名密钥，这里使用和客户端服务相同的密钥，可以根据需要设置不同的
            listengConfig.setBaseAesKey(CommonConstants.aesKey);
            listengConfig.setTokenKey(CommonConstants.tokenKey);
            // 设置隧道密钥
            listengConfig.setBasePasswayKey(CommonConstants.aesKey);
            listengConfig.setCreateServerSocket(createServerSocket);
            ListenServerControl.createNewListenServer(listengConfig);
        }
    }

    /**
     * 交互加密，即交互验证
     */
    public static void secret() throws Exception {
        // 设置并启动客户端服务线程
        SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
        // 设置交互aes密钥和签名密钥
        config.setBaseAesKey(CommonConstants.aesKey);
        config.setTokenKey(CommonConstants.tokenKey);
        new ClientServiceThread(config).start();

        for (ListenDest model : CommonConstants.listenDestArray) {
            // 设置并启动一个穿透端口
            SecretSimpleListenServerConfig listengConfig = new SecretSimpleListenServerConfig(model.listenPort);
            // 设置交互aes密钥和签名密钥，这里使用和客户端服务相同的密钥，可以根据需要设置不同的
            listengConfig.setBaseAesKey(CommonConstants.aesKey);
            listengConfig.setTokenKey(CommonConstants.tokenKey);
            listengConfig.setCreateServerSocket(createServerSocket);
            ListenServerControl.createNewListenServer(listengConfig);
        }
    }

    /**
     * 无加密、无验证
     */
    public static void simple() throws Exception {
        // 设置并启动客户端服务线程
        SimpleClientServiceConfig config = new SimpleClientServiceConfig(CommonConstants.servicePort);
        new ClientServiceThread(config).start();

        for (ListenDest model : CommonConstants.listenDestArray) {
            // 设置并启动一个穿透端口
            SimpleListenServerConfig listengConfig = new SimpleListenServerConfig(model.listenPort);
            listengConfig.setCreateServerSocket(createServerSocket);
            ListenServerControl.createNewListenServer(listengConfig);
        }
    }

}
