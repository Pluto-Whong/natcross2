package person.pluto.natcross2.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import person.pluto.natcross2.utils.Tools;

/**
 * 
 * <p>
 * 长度限定读写通道
 * </p>
 *
 * @author Pluto
 * @since 2021-04-08 12:42:38
 */
public class LengthChannel extends SocketChannel<byte[], byte[]> {

	private Socket socket;
	private java.nio.channels.SocketChannel socketChannel;

	private InputStream inputStream;
	private OutputStream outputStream;

	private ReentrantLock readLock = new ReentrantLock(true);
	private ReentrantLock writerLock = new ReentrantLock(true);

	private byte[] lenBytes = new byte[4];

	public LengthChannel() {
	}

	public LengthChannel(Socket socket) throws IOException {
		this.setSocket(socket);
	}

	@Override
	public byte[] read() throws Exception {
		readLock.lock();
		try {
			int offset = 0;

			InputStream is = getInputSteam();

			for (; offset < lenBytes.length;) {
				offset += is.read(lenBytes, offset, lenBytes.length - offset);
			}
			int length = Tools.bytes2int(lenBytes);

			offset = 0;
			byte[] b = new byte[length];
			for (; offset < length;) {
				offset += is.read(b, offset, length - offset);
			}
			return b;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void write(byte[] value) throws Exception {
		writerLock.lock();
		try {
			if (Objects.nonNull(this.socketChannel)) {
				int length = value.length;
				this.socketChannel.write(ByteBuffer.wrap(Tools.intToBytes(length)));
				this.socketChannel.write(ByteBuffer.wrap(value));
			} else {
				OutputStream os = getOutputStream();
				int length = value.length;
				os.write(Tools.intToBytes(length));
				os.write(value);
			}
		} finally {
			writerLock.unlock();
		}
	}

	@Override
	public void flush() throws Exception {
		writerLock.lock();
		try {
			getOutputStream().flush();
		} finally {
			writerLock.unlock();
		}
	}

	@Override
	public void writeAndFlush(byte[] value) throws Exception {
		writerLock.lock();
		try {
			this.write(value);
			this.flush();
		} finally {
			writerLock.unlock();
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;

		this.socketChannel = this.socket.getChannel();

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
			this.inputStream = this.socket.getInputStream();
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
			this.outputStream = this.getSocket().getOutputStream();
		}
		return this.outputStream;
	}

}
