package person.pluto.natcross2.clientside.handler;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;

/**
 * <p>
 * 统一回复 处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-15 13:02:09
 */
@Slf4j
public class CommonReplyHandler implements IClientHandler<InteractiveModel, InteractiveModel> {

	public static final CommonReplyHandler INSTANCE = new CommonReplyHandler();

	@Getter
	@Setter
	private IClientHandler<InteractiveModel, InteractiveModel> handler;

	@Override
	public boolean proc(InteractiveModel model,
			IClientAdapter<? extends InteractiveModel, ? super InteractiveModel> clientAdapter) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.COMMON_REPLY.equals(interactiveTypeEnum)) {
			return false;
		}

		IClientHandler<InteractiveModel, InteractiveModel> handler;
		if (Objects.isNull(handler = this.handler)) {
			log.info("common reply: {}", model);
			return true;
		}

		return handler.proc(model, clientAdapter);
	}

}
