package person.pluto.natcross2.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import person.pluto.natcross2.utils.Tools;

public class LengthChannel extends SocketChannel<byte[], byte[]> {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public LengthChannel() {
    }

    public LengthChannel(Socket socket) throws IOException {
        this.setSocket(socket);
    }

    @Override
    public byte[] read() throws Exception {
        InputStream is = getInputSteam();

        byte[] len = new byte[4];
        is.read(len);
        int length = Tools.bytes2int(len);

        byte[] b = new byte[length];
        is.read(b, 0, length);
        return b;
    }

    @Override
    public void write(byte[] value) throws Exception {
        OutputStream os = getOutputStream();
        int length = value.length;
        os.write(Tools.intToBytes(length));
        os.write(value);
    }

    @Override
    public void flush() throws Exception {
        getOutputStream().flush();
    }

    @Override
    public void writeAndFlush(byte[] value) throws Exception {
        this.write(value);
        this.flush();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
    }

    @Override
    public void closeSocket() throws IOException {
        this.socket.close();
    }

    /**
     * 惰性获取输入流
     * 
     * @author Pluto
     * @since 2020-01-08 16:15:32
     * @return
     * @throws IOException
     */
    private InputStream getInputSteam() throws IOException {
        if (this.inputStream == null) {
            return this.socket.getInputStream();
        }
        return this.inputStream;
    }

    /**
     * 惰性获取输出流
     * 
     * @author Pluto
     * @since 2020-01-08 16:15:48
     * @return
     * @throws IOException
     */
    private OutputStream getOutputStream() throws IOException {
        if (this.outputStream == null) {
            return this.getSocket().getOutputStream();
        }
        return this.outputStream;
    }

}
