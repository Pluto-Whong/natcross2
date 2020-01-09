package person.pluto.natcross2.serverside.client.handler;

import person.pluto.natcross2.serverside.client.process.ClientConnectProcess;
import person.pluto.natcross2.serverside.client.process.ClientControlProcess;

public class DefaultInteractiveProcessHandler extends InteractiveProcessHandler {

    public DefaultInteractiveProcessHandler() {
        this.addLast(new ClientControlProcess());
        this.addLast(new ClientConnectProcess());
    }

}
