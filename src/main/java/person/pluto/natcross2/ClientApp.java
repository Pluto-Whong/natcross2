package person.pluto.natcross2;

import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.config.AllSecretInteractiveClientConfig;
import person.pluto.natcross2.clientside.config.InteractiveClientConfig;
import person.pluto.natcross2.clientside.config.SecretInteractiveClientConfig;

public class ClientApp {

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

        config.setClientServiceIp("127.0.0.1");
        config.setClientServicePort(10010);
        config.setListenServerPort(8081);
        config.setDestIp("127.0.0.1");
        config.setDestPort(8080);

        config.setBaseAesKey(ServerApp.aesKey);
        config.setTokenKey(ServerApp.tokenKey);
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

        config.setClientServiceIp("127.0.0.1");
        config.setClientServicePort(10010);
        config.setListenServerPort(8081);
        config.setDestIp("127.0.0.1");
        config.setDestPort(8080);

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

        config.setClientServiceIp("127.0.0.1");
        config.setClientServicePort(10010);
        config.setListenServerPort(8081);
        config.setDestIp("127.0.0.1");
        config.setDestPort(8080);

        new ClientControlThread(config).createControl();
    }

}
