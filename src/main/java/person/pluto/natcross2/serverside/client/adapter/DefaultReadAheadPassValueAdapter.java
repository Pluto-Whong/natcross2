package person.pluto.natcross2.serverside.client.adapter;

import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.client.config.IClientServiceConfig;
import person.pluto.natcross2.serverside.client.handler.DefaultInteractiveProcessHandler;

/**
 * 
 * <p>
 * 默认的预读后处理适配器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-26 17:06:25
 */
public class DefaultReadAheadPassValueAdapter extends ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> {

	public DefaultReadAheadPassValueAdapter(IClientServiceConfig<InteractiveModel, InteractiveModel> config) {
		super(config);
		this.addLast(new DefaultInteractiveProcessHandler());
	}

}
