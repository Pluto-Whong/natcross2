package person.pluto.natcross2.clientside.handler;

import person.pluto.natcross2.clientside.adapter.IClientAdapter;

/**
 * <p>
 * 接收处理器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-15 11:13:20
 */
public interface IClientHandler<R, W> {

	boolean proc(R model, IClientAdapter<? extends R, ? super W> clientAdapter) throws Exception;

}
