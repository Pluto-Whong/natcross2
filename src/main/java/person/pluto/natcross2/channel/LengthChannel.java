package person.pluto.natcross2.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
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

	private Selector selector;

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

	private byte[] read(ByteBuffer buffer) throws IOException {
		for (; buffer.position() < buffer.limit();) {
			int select = selector.select();
			if (select <= 0) {
				continue;
			}

			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

			for (; iterator.hasNext();) {
				SelectionKey key = iterator.next();
				iterator.remove();

				if (!key.isValid() || !key.isReadable()) {
					continue;
				}

				int len = -1;
				do {
					len = this.socketChannel.read(buffer);

					if (len < 0) {
						if (buffer.position() < buffer.limit()) {
							// 如果-1，提前关闭了，又没有获得足够的数据，那么就抛出异常
							throw new IOException("Insufficient byte length when io closed");
						} else {
							// 如果够了，那就够了，-1的问题交给其他系统处理，拿到东西走就行
							break;
						}
					}
				} while (len > 0 && buffer.position() < buffer.limit());

				if (buffer.position() >= buffer.limit()) {
					// 如果够了，就直接退出，剩下的是后续的交互，不要抢
					break;
				}
			}
		}

		buffer.flip();

		return buffer.array();
	}

	@Override
	public byte[] read() throws Exception {
		readLock.lock();
		try {
			if (Objects.nonNull(this.socketChannel)) {
				ByteBuffer buffer = ByteBuffer.wrap(lenBytes);

				this.read(buffer);

				int length = Tools.bytes2int(lenBytes);

				buffer = ByteBuffer.allocate(length);

				return this.read(buffer);
			} else {
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
			}
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
		this.write(value);
		this.flush();
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;

		this.socketChannel = this.socket.getChannel();
		if (Objects.nonNull(this.socketChannel)) {
			selector = Selector.open();
			this.socketChannel.configureBlocking(false);
			this.socketChannel.register(selector, SelectionKey.OP_READ);
		}

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
