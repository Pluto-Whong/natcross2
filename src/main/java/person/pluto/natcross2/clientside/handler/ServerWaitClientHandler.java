package person.pluto.natcross2.clientside.handler;

import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.interactive.ServerWaitModel;

/**
 * <p>
 * 心跳检测
 * </p>
 *
 * @author Pluto
 * @since 2020-04-15 13:02:09
 */
public class ServerWaitClientHandler implements IClientHandler<InteractiveModel, InteractiveModel> {

    public static final ServerWaitClientHandler INSTANCE = new ServerWaitClientHandler();

    @Override
    public boolean proc(InteractiveModel model,
            IClientAdapter<? extends InteractiveModel, ? super InteractiveModel> clientAdapter) throws Exception {
        InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
        if (!InteractiveTypeEnum.SERVER_WAIT_CLIENT.equals(interactiveTypeEnum)) {
            return false;
        }

        ServerWaitModel serverWaitModel = model.getData().toJavaObject(ServerWaitModel.class);
        clientAdapter.clientConnect(serverWaitModel);

        return true;
    }

}
