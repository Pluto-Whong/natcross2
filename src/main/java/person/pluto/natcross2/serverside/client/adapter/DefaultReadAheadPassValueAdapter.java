package person.pluto.natcross2.serverside.client.adapter;

import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.serverside.client.config.IClientServiceConfig;
import person.pluto.natcross2.serverside.client.handler.DefaultInteractiveProcessHandler;

public class DefaultReadAheadPassValueAdapter extends ReadAheadPassValueAdapter<InteractiveModel, InteractiveModel> {

    public DefaultReadAheadPassValueAdapter(IClientServiceConfig<InteractiveModel, InteractiveModel> config) {
        super(config);
        this.addLast(new DefaultInteractiveProcessHandler());
    }

}
