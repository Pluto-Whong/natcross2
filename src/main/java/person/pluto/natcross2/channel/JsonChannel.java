package person.pluto.natcross2.channel;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>
 * json方式读写
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:13:02
 */
public class JsonChannel extends SocketChannel<JSONObject, Object> {

    /**
     * 实际通道
     */
    private StringChannel channle;

    public JsonChannel() {
        channle = new StringChannel();
    }

    public JsonChannel(Socket socket) throws IOException {
        this.channle = new StringChannel(socket);
    }

    @Override
    public JSONObject read() throws Exception {
        String read = channle.read();
        return JSON.parseObject(read);
    }

    @Override
    public void write(Object value) throws Exception {
        String string = null;
        if (value instanceof JSONAware) {
            string = ((JSONAware) value).toJSONString();
        } else {
            string = JSON.toJSONString(value);
        }
        channle.write(string);
    }

    @Override
    public void flush() throws Exception {
        channle.flush();
    }

    @Override
    public void writeAndFlush(Object value) throws Exception {
        this.write(value);
        this.flush();
    }

    /**
     * 获取charset
     * 
     * @author Pluto
     * @since 2020-01-08 16:13:32
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
