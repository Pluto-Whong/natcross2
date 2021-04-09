package person.pluto.natcross2.channel;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 
 * <p>
 * 字符型通道
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:21:31
 */
public class StringChannel extends SocketChannel<String, String> {

    private LengthChannel channel;

    private Charset charset = StandardCharsets.UTF_8;

    public StringChannel() {
        channel = new LengthChannel();
    }

    public StringChannel(Socket socket) throws IOException {
        this.channel = new LengthChannel(socket);
    }

    @Override
    public String read() throws Exception {
        byte[] read = channel.read();
        return new String(read, charset);
    }

    @Override
    public void write(String value) throws Exception {
        channel.write(value.getBytes(charset));
    }

    @Override
    public void flush() throws Exception {
        channel.flush();
    }

    @Override
    public void writeAndFlush(String value) throws Exception {
        this.write(value);
        this.flush();
    }

    /**
     * 获取charset
     * 
     * @author Pluto
     * @since 2020-01-08 16:22:06
     * @return
     */
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public Socket getSocket() {
        return channel.getSocket();
    }

    @Override
    public void setSocket(Socket socket) throws IOException {
        channel.setSocket(socket);
    }

    @Override
    public void closeSocket() throws IOException {
        channel.closeSocket();
    }

}
