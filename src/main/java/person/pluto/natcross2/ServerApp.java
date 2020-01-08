package person.pluto.natcross2;

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
import person.pluto.natcross2.serverside.listen.config.AllSecretSimpleListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.IListenServerConfig;
import person.pluto.natcross2.serverside.listen.config.SecretSimpleListenServerConfig;

public class ServerApp {

    public static String aesKey = "0PMudFSqJ9WsQrTC60sva9sJAV4PF5iOBjKZW17NeF4=";
    public static String tokenKey = "tokenKey";

    public static void main(String[] args) throws Exception {
//        simple();
//        secret();
        secretAll();
    }

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
        listengConfig.setBasePasswayKey(aesKey);
        ListenServerControl.createNewListenServer(listengConfig);
    }

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
        ListenServerControl.createNewListenServer(listengConfig);
    }

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

        IListenServerConfig listengConfig = new SimpleListenServerConfig(8081);
        ListenServerControl.createNewListenServer(listengConfig);
    }

}
