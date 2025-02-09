package person.pluto.natcross2.serverside.client.process;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.NatcrossResultModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.enumeration.NatcrossResultEnum;
import person.pluto.natcross2.model.interactive.ClientControlModel;
import person.pluto.natcross2.serverside.listen.ListenServerControl;
import person.pluto.natcross2.serverside.listen.ServerListenThread;

/**
 * <p>
 * 请求建立控制器处理方法
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:48:43
 */
public class ClientControlProcess implements IProcess {

    public static final ClientControlProcess INSTANCE = new ClientControlProcess();

    @Override
    public boolean wouldProc(InteractiveModel recvInteractiveModel) {
        InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(
                recvInteractiveModel.getInteractiveType());
        return InteractiveTypeEnum.CLIENT_CONTROL.equals(interactiveTypeEnum);
    }

    @Override
    public boolean processMethod(SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel,
            InteractiveModel recvInteractiveModel) throws Exception {
        ClientControlModel clientControlModel = recvInteractiveModel.getData().toJavaObject(ClientControlModel.class);
        ServerListenThread serverListenThread = ListenServerControl.get(clientControlModel.getListenPort());

        if (serverListenThread == null) {
            socketChannel.writeAndFlush(
                    InteractiveModel.of(recvInteractiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
                            NatcrossResultEnum.NO_HAS_SERVER_LISTEN.toResultModel()));
            return false;
        }

        socketChannel.writeAndFlush(
                InteractiveModel.of(recvInteractiveModel.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
                        NatcrossResultModel.ofSuccess()));

        serverListenThread.setControlSocket(socketChannel.getSocket());
        return true;
    }

}
