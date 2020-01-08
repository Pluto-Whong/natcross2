package person.pluto.natcross2.channel;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

import person.pluto.natcross2.model.InteractiveModel;

/**
 * 
 * <p>
 * InteractiveModel 模式读写
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:11:50
 */
public class InteractiveChannel extends SocketChannel<InteractiveModel, InteractiveModel> {

    /**
     * 实际通道
     */
    private JsonChannel channle;

    public InteractiveChannel() {
        this.channle = new JsonChannel();
    }

    public InteractiveChannel(Socket socket) throws IOException {
        this.channle = new JsonChannel(socket);
    }

    @Override
    public InteractiveModel read() throws Exception {
        JSONObject read = channle.read();
        return read.toJavaObject(InteractiveModel.class);
    }

    @Override
    public void write(InteractiveModel value) throws Exception {
        channle.write(value);
    }

    @Override
    public void flush() throws Exception {
        channle.flush();
    }

    @Override
    public void writeAndFlush(InteractiveModel value) throws Exception {
        this.write(value);
        this.flush();
    }

    /**
     * 获取charset
     * 
     * @author Pluto
     * @since 2020-01-08 16:12:33
     * @return
     */
    public Charset getCharset() {
        return channle.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        channle.setCharset(charset);
    }

    @Override
    public Socket getSocket() {
        return channle.getSocket();
    }

    @Override
    public void setSocket(Socket socket) throws IOException {
        channle.setSocket(socket);
    }

    @Override
    public void closeSocket() throws IOException {
        channle.closeSocket();
    }

}
