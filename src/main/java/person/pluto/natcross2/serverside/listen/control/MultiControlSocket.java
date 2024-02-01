package person.pluto.natcross2.serverside.listen.control;

import person.pluto.natcross2.serverside.listen.IServerListen;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <p>
 * 复合 控制socket实例
 * </p>
 *
 * @author Pluto
 * @since 2021-06-18 12:27:47
 */
public class MultiControlSocket implements IControlSocket {

    protected final LinkedList<IControlSocket> controlSockets = new LinkedList<>();

    protected IServerListen serverListen;

    /**
     * 增加控制socket
     *
     * @param controlSocket
     * @return
     */
    public synchronized boolean addControlSocket(IControlSocket controlSocket) {
        return this.controlSockets.add(controlSocket);
    }

    @Override
    public synchronized boolean isValid() {
        Iterator<IControlSocket> iterator = controlSockets.iterator();
        while (iterator.hasNext()) {
            IControlSocket controlSocket = iterator.next();
            if (controlSocket.isValid()) {
                return true;
            }
            iterator.remove();
        }
        return false;
    }

    protected synchronized IControlSocket pollControlSocket(IControlSocket lastControlSocket) {
        LinkedList<IControlSocket> controlSockets = this.controlSockets;

        if (Objects.nonNull(lastControlSocket) && !lastControlSocket.isValid()) {
            lastControlSocket.close();
            controlSockets.remove(lastControlSocket);
        }

        IControlSocket controlSocket;
        if (controlSockets.size() > 1) {
            controlSocket = controlSockets.poll();
            controlSockets.add(controlSocket);
        } else {
            controlSocket = controlSockets.peekFirst();
        }

        return controlSocket;
    }

    @Override
    public boolean sendClientWait(String socketPartKey) {
        IControlSocket controlSocket = null;
        for (; ; ) {
            controlSocket = this.pollControlSocket(controlSocket);
            if (Objects.isNull(controlSocket)) {
                return false;
            }

            boolean sendClientWait = controlSocket.sendClientWait(socketPartKey);
            if (sendClientWait) {
                return true;
            }
        }
    }

    @Override
    public synchronized void close() {
        LinkedList<IControlSocket> controlSockets = this.controlSockets;

        Iterator<IControlSocket> iterator = controlSockets.iterator();
        while (iterator.hasNext()) {
            IControlSocket controlSocket = iterator.next();
            try {
                controlSocket.close();
            } catch (Throwable e) {
                // do nothing
            }
            iterator.remove();
        }
    }

    @Override
    public synchronized void replaceClose() {
        // do nothing
    }

    @Override
    public synchronized void startRecv() {
        LinkedList<IControlSocket> controlSockets = this.controlSockets;

        for (IControlSocket controlSocket : controlSockets) {
            try {
                controlSocket.startRecv();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    private synchronized boolean removeControlSocket(IControlSocket controlSocket) {
        return controlSockets.remove(controlSocket);
    }

    @Override
    public synchronized void setServerListen(final IServerListen serverListen) {
        this.serverListen = serverListen;
        IServerListen serverListenTemp = new IServerListen() {
            @Override
            public String formatInfo() {
                return serverListen.formatInfo();
            }

            @Override
            public void controlCloseNotice(IControlSocket controlSocket) {
                MultiControlSocket.this.removeControlSocket(controlSocket);
            }
        };

        LinkedList<IControlSocket> controlSockets = this.controlSockets;

        for (IControlSocket controlSocket : controlSockets) {
            try {
                controlSocket.setServerListen(serverListenTemp);
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

}
