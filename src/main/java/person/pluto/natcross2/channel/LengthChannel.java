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

	private final ReentrantLock readLock = new ReentrantLock(true);
	private final ReentrantLock writerLock = new ReentrantLock(true);

	private byte[] lenBytes = new byte[4];

	public LengthChannel() {
	}

	public LengthChannel(Socket socket) throws IOException {
		this.setSocket(socket);
	}

	@Override
	public byte[] read() throws Exception {
		ReentrantLock readLock = this.readLock;
		byte[] lenBytes = this.lenBytes;

		readLock.lock();
		try {
			int offset = 0;

			InputStream is = getInputSteam();

			int len;
			for (; offset < lenBytes.length;) {
				len = is.read(lenBytes, offset, lenBytes.length - offset);
				if (len < 0) {
					// 如果-1，提前关闭了，又没有获得足够的数据，那么就抛出异常
					throw new IOException("Insufficient byte length[" + lenBytes.length + "] when io closed");
				}
				offset += len;
			}

			int length = Tools.bytes2int(lenBytes);

			offset = 0;
			byte[] b = new byte[length];
			for (; offset < length;) {
				len = is.read(b, offset, length - offset);
				if (len < 0) {
					// 如果-1，提前关闭了，又没有获得足够的数据，那么就抛出异常
					throw new IOException("Insufficient byte length[" + length + "] when io closed");
				}
				offset += len;
			}
			return b;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void write(byte[] value) throws Exception {
		ReentrantLock writerLock = this.writerLock;

		writerLock.lock();
		try {
			java.nio.channels.SocketChannel socketChannel;
			if (Objects.nonNull(socketChannel = this.socketChannel)) {
				Tools.channelWrite(socketChannel, ByteBuffer.wrap(Tools.intToBytes(value.length)));
				Tools.channelWrite(socketChannel, ByteBuffer.wrap(value));
			} else {
				OutputStream os = getOutputStream();
				os.write(Tools.intToBytes(value.length));
				os.write(value);
			}
		} finally {
			writerLock.unlock();
		}
	}

	@Override
	public void flush() throws Exception {
		ReentrantLock writerLock = this.writerLock;

		writerLock.lock();
		try {
			getOutputStream().flush();
		} finally {
			writerLock.unlock();
		}
	}

	@Override
	public void writeAndFlush(byte[] value) throws Exception {
		ReentrantLock writerLock = this.writerLock;

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
		return this.socket;
	}

	@Override
	public void setSocket(Socket socket) throws IOException {
		if (Objects.nonNull(this.socket)) {
			throw new UnsupportedOperationException("socket cannot be set repeatedly");
		}

		this.socket = socket;

		this.socketChannel = socket.getChannel();

		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
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
		InputStream inputStream;
		if ((inputStream = this.inputStream) == null) {
			inputStream = this.inputStream = this.socket.getInputStream();
		}
		return inputStream;
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
		OutputStream outputStream;
		if ((outputStream = this.outputStream) == null) {
			outputStream = this.outputStream = this.getSocket().getOutputStream();
		}
		return outputStream;
	}

}
