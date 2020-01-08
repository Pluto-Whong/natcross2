package person.pluto.natcross2.serverside.listen.control;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.interactive.ServerWaitModel;

/**
 * 
 * <p>
 * 控制socket实例
 * </p>
 *
 * @author Pluto
 * @since 2019-07-17 11:03:56
 */
public class ControlSocket implements IControlSocket {

    private Socket controlSocket;

    private SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel;

    /**
     * 锁定输出资源标志
     */
    private Lock socketLock = new ReentrantLock();

    public ControlSocket(SocketChannel<? extends InteractiveModel, ? super InteractiveModel> socketChannel) {
        this.socketChannel = socketChannel;
        this.controlSocket = this.socketChannel.getSocket();
    }

    @Override
    public boolean isValid() {
        socketLock.lock();
        try {
            if (this.controlSocket == null || !this.controlSocket.isConnected() || this.controlSocket.isClosed()) {
                return false;
            }

            try {
                this.controlSocket.sendUrgentData(0xff);
            } catch (IOException e) {
                return false;
            }

            return true;
        } finally {
            socketLock.unlock();
        }
    }

    @Override
    public void close() {
        if (socketChannel != null) {
            try {
                socketChannel.closeSocket();
            } catch (IOException e) {
                // do no thing
            }
        }

        if (controlSocket != null) {
            try {
                controlSocket.close();
            } catch (Exception e) {
                // no thing
            }
        }
    }

    @Override
    public boolean sendClientWait(String socketPartKey) {
        InteractiveModel model = InteractiveModel.of(InteractiveTypeEnum.CLIENT_WAIT,
                new ServerWaitModel(socketPartKey));

        socketLock.lock();
        try {
            socketChannel.writeAndFlush(model);
        } catch (Exception e) {
            return false;
        } finally {
            socketLock.unlock();
        }

        return true;
    }

}
