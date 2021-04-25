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
		this.channel = new LengthChannel();
	}

	public StringChannel(Socket socket) throws IOException {
		this.channel = new LengthChannel(socket);
	}

	@Override
	public String read() throws Exception {
		byte[] read = this.channel.read();
		return new String(read, this.charset);
	}

	private byte[] valueConvert(String value) {
		return value.getBytes(this.charset);
	}

	@Override
	public void write(String value) throws Exception {
		this.channel.write(this.valueConvert(value));
	}

	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	@Override
	public void writeAndFlush(String value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:22:06
	 * @return
	 */
	public Charset getCharset() {
		return this.charset;
	}

	@Override
	public void setCharset(Charset charset) {
		this.charset = charset;
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
