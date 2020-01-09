package person.pluto.natcross2;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.lang3.StringUtils;

import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.client.ClientServiceThread;
import person.pluto.natcross2.serverside.client.adapter.ReadAheadPassValueAdapter;
import person.pluto.natcross2.serverside.client.config.SecretSimpleClientServiceConfig;
import person.pluto.natcross2.serverside.client.config.SimpleClientServiceConfig;
import person.pluto.natcross2.serverside.client.handler.InteractiveProcessHandler;
import person.pluto.natcross2.serverside.client.process.ClientConnectProcess;
import person.pluto.natcross2.serverside.client.process.ClientControlProcess;
import person.pluto.natcross2.serverside.listen.ListenServerControl;
import person.pluto.natcross2.serverside.listen.config.SimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.serversocket.ICreateServerSocket;
import person.pluto.natcross2.serverside.listen.config.AllSecretSimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.SecretSimpleListenServerConfig;

public class ServerApp {

    public static String aesKey = "0PMudFSqJ9WsQrTC60sva9sJAV4PF5iOBjKZW17NeF4=";
    public static String tokenKey = "tokenKey";

    // 你的p12格式的证书路径
    private static String sslKeyStorePath = System.getenv("sslKeyStorePath");
    // 你的证书密码
    private static String sslKeyStorePassword = System.getenv("sslKeyStorePassword");

    public static ICreateServerSocket createServerSocket;

    public static void main(String[] args) throws Exception {

        // 如果需要HTTPS协议的支持，则填写sslKeyStorePath、sslKeyStorePassword或在环境变量中定义
        if (StringUtils.isNoneBlank(sslKeyStorePath, sslKeyStorePassword)) {
            createServerSocket = new ICreateServerSocket() {
                @Override
                public ServerSocket createServerSocket(int listenPort) throws Exception {
                    KeyStore kstore = KeyStore.getInstance("PKCS12");
                    kstore.load(new FileInputStream(sslKeyStorePath), sslKeyStorePassword.toCharArray());
                    KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("sunx509");
                    keyFactory.init(kstore, sslKeyStorePassword.toCharArray());

                    SSLContext ctx = SSLContext.getInstance("TLSv1.2");
                    ctx.init(keyFactory.getKeyManagers(), null, null);

                    SSLServerSocketFactory serverSocketFactory = ctx.getServerSocketFactory();

                    return serverSocketFactory.createServerSocket(listenPort);
                }
            };
        }

//        simple();
        secret();
//        secretAll();
    }

    /**
     * 交互、隧道都进行加密
     * 
     * @author Pluto
     * @since 2020-01-08 17:29:26
     * @throws Exception
     */
    public static void secretAll() throws Exception {
        SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(10010);
        config.setBaseAesKey(aesKey);
        config.setTokenKey(tokenKey);

        // 设置适配器
        ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> adapter = new ReadAheadPassValueAdapter<>(config);

        // 设置适配器需要处理的处理器
        InteractiveProcessHandler handler = new InteractiveProcessHandler();
        // 处理器处理方法
        handler.addLast(new ClientControlProcess());
        handler.addLast(new ClientConnectProcess());
        adapter.addLast(handler);
        // 设置处理器完成 !

        config.setClientServiceAdapter(adapter);
        // 设置适配器完成!

        ClientServiceThread clientServiceThread = new ClientServiceThread(config);

        clientServiceThread.start();

        AllSecretSimpleListenServerConfig listengConfig = new AllSecretSimpleListenServerConfig(8081);
        listengConfig.setBaseAesKey(aesKey);
        listengConfig.setTokenKey(tokenKey);
        // 隧道和交互的密钥使用同一个
        listengConfig.setBasePasswayKey(aesKey);

        listengConfig.setCreateServerSocket(createServerSocket);

        ListenServerControl.createNewListenServer(listengConfig);
    }

    /**
     * 交互加密，即交互验证
     * 
     * @author Pluto
     * @since 2020-01-08 17:28:54
     * @throws Exception
     */
    public static void secret() throws Exception {
        SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(10010);
        config.setBaseAesKey(aesKey);
        config.setTokenKey(tokenKey);

        // 设置适配器
        ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> adapter = new ReadAheadPassValueAdapter<>(config);

        // 设置适配器需要处理的处理器
        InteractiveProcessHandler handler = new InteractiveProcessHandler();
        // 处理器处理方法
        handler.addLast(new ClientControlProcess());
        handler.addLast(new ClientConnectProcess());
        adapter.addLast(handler);
        // 设置处理器完成 !

        config.setClientServiceAdapter(adapter);
        // 设置适配器完成!

        ClientServiceThread clientServiceThread = new ClientServiceThread(config);

        clientServiceThread.start();

        SecretSimpleListenServerConfig listengConfig = new SecretSimpleListenServerConfig(8081);
        listengConfig.setBaseAesKey(aesKey);
        listengConfig.setTokenKey(tokenKey);

        listengConfig.setCreateServerSocket(createServerSocket);

        ListenServerControl.createNewListenServer(listengConfig);
    }

    /**
     * 无加密、无验证
     * 
     * @author Pluto
     * @since 2020-01-08 17:29:02
     * @throws Exception
     */
    public static void simple() throws Exception {
        SimpleClientServiceConfig config = new SimpleClientServiceConfig(10010);

        // 设置适配器
        ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> adapter = new ReadAheadPassValueAdapter<>(config);

        // 设置适配器需要处理的处理器
        InteractiveProcessHandler handler = new InteractiveProcessHandler();
        // 处理器处理方法
        handler.addLast(new ClientControlProcess());
        handler.addLast(new ClientConnectProcess());
        adapter.addLast(handler);
        // 设置处理器完成 !

        config.setClientServiceAdapter(adapter);
        // 设置适配器完成!

        ClientServiceThread clientServiceThread = new ClientServiceThread(config);

        clientServiceThread.start();

        SimpleListenServerConfig listengConfig = new SimpleListenServerConfig(8081);

        listengConfig.setCreateServerSocket(createServerSocket);

        ListenServerControl.createNewListenServer(listengConfig);
    }

}
