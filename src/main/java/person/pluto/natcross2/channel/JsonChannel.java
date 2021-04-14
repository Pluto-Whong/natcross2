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
	private StringChannel channel;

	public JsonChannel() {
		channel = new StringChannel();
	}

	public JsonChannel(Socket socket) throws IOException {
		this.channel = new StringChannel(socket);
	}

	@Override
	public JSONObject read() throws Exception {
		String read = channel.read();
		return JSON.parseObject(read);
	}

	private String valueConvert(Object value) {
		String string = null;
		if (value instanceof JSONAware) {
			string = ((JSONAware) value).toJSONString();
		} else {
			string = JSON.toJSONString(value);
		}
		return string;
	}

	@Override
	public void write(Object value) throws Exception {
		channel.write(valueConvert(value));
	}

	@Override
	public void flush() throws Exception {
		channel.flush();
	}

	@Override
	public void writeAndFlush(Object value) throws Exception {
		channel.writeAndFlush(valueConvert(value));
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:13:32
	 * @return
	 */
	public Charset getCharset() {
		return channel.getCharset();
	}

	@Override
	public void setCharset(Charset charset) {
		channel.setCharset(charset);
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
