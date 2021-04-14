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
		JSONObject read = channel.read();
		return read.toJavaObject(InteractiveModel.class);
	}

	@Override
	public void write(InteractiveModel value) throws Exception {
		channel.write(value);
	}

	@Override
	public void flush() throws Exception {
		channel.flush();
	}

	@Override
	public void writeAndFlush(InteractiveModel value) throws Exception {
		channel.writeAndFlush(value);
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:12:33
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
