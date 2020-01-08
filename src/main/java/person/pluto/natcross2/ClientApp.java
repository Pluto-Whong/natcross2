package person.pluto.natcross2;

import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.config.AllSecretInteractiveClientConfig;
import person.pluto.natcross2.clientside.config.InteractiveClientConfig;
import person.pluto.natcross2.clientside.config.SecretInteractiveClientConfig;

public class ClientApp {

    public static void main(String[] args) throws Exception {
//        simple();
//        secret();
        secretAll();
    }

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
