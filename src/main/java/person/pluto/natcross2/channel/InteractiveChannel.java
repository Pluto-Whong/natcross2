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
	private JsonChannel channel;

	public InteractiveChannel() {
		this.channel = new JsonChannel();
	}

	public InteractiveChannel(Socket socket) throws IOException {
		this.channel = new JsonChannel(socket);
	}

	@Override
	public InteractiveModel read() throws Exception {
		JSONObject read = this.channel.read();
		return read.toJavaObject(InteractiveModel.class);
	}

	@Override
	public void write(InteractiveModel value) throws Exception {
		this.channel.write(value);
	}

	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	@Override
	public void writeAndFlush(InteractiveModel value) throws Exception {
		this.channel.writeAndFlush(value);
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:12:33
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
