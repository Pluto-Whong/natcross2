package person.pluto.natcross2.serverside.listen.recv;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.channel.SocketChannel;
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
public class CommonReplyHandler implements IRecvHandler<InteractiveModel, InteractiveModel> {

	public static final CommonReplyHandler INSTANCE = new CommonReplyHandler();

	@Getter
	@Setter
	private IRecvHandler<InteractiveModel, InteractiveModel> handler;

	@Override
	public boolean proc(InteractiveModel model,
			SocketChannel<? extends InteractiveModel, ? super InteractiveModel> channel) throws Exception {
		InteractiveTypeEnum interactiveTypeEnum = InteractiveTypeEnum.getEnumByName(model.getInteractiveType());
		if (!InteractiveTypeEnum.COMMON_REPLY.equals(interactiveTypeEnum)) {
			return false;
		}

		if (Objects.isNull(handler)) {
			log.info("common reply: {}", model);
			return true;
		}

		return handler.proc(model, channel);
	}

}
