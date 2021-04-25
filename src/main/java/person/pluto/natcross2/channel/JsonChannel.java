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
		this.channel = new StringChannel();
	}

	public JsonChannel(Socket socket) throws IOException {
		this.channel = new StringChannel(socket);
	}

	@Override
	public JSONObject read() throws Exception {
		String read = this.channel.read();
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
		this.channel.write(this.valueConvert(value));
	}

	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	@Override
	public void writeAndFlush(Object value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:13:32
	 * @return
	 */
	public Charset getCharset() {
		return this.channel.getCharset();
	}

	@Override
	public void setCharset(Charset charset) {
		this.channel.setCharset(charset);
	}

	@Override
	public Socket getSocket() {
		return this.channel.getSocket();
	}

	@Override
	public void setSocket(Socket socket) throws IOException {
		this.channel.setSocket(socket);
	}

	@Override
	public void closeSocket() throws IOException {
		this.channel.closeSocket();
	}

}
