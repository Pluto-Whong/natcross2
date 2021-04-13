package person.pluto.natcross2.api.passway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.nio.INioProcesser;
import person.pluto.natcross2.nio.NioHallows;

/**
 * <p>
 * 简单的隧道，无任何处理，只从输入侧原样输出到输出侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:58:11
 */
@Slf4j
public class SimplePassway implements Runnable, INioProcesser {

	private boolean alive = false;

	/**
	 * 所属对象，完成后通知
	 */
	@Setter
	private IBelongControl belongControl;

	/**
	 * 缓存大小
	 */
	@Setter
	private int streamCacheSize = 8192;

	@Setter
	private Socket recvSocket;
	@Setter
	private Socket sendSocket;

	private OutputStream outputStream;
	private SocketChannel outputChannel;

	private OutputStream getOutputStream() throws IOException {
		if (Objects.isNull(this.outputStream)) {
			this.outputStream = sendSocket.getOutputStream();
		}
		return this.outputStream;
	}

	private SocketChannel getSocketChannel() {
		if (Objects.isNull(this.outputChannel)) {
			this.outputChannel = sendSocket.getChannel();
		}
		return this.outputChannel;
	}

	/**
	 * 向输出通道输出数据
	 * <p>
	 * 这里不只是为了DMA而去用DMA，而是这里有奇葩问题
	 * <p>
	 * 如能采用了SocketChannel，而去用outputStream的时候，不管输入输出，都会有奇怪的问题，比如输出会莫名的阻塞住
	 * <p>
	 * 整体就是如果能用nio的方法，但是用了bio形式都会各种什么 NullPointException、IllageSateException 等等错误
	 * <p>
	 *
	 * @param byteBuffer
	 * @throws IOException
	 * @author Pluto
	 * @since 2021-04-09 16:37:33
	 */
	private void write(ByteBuffer byteBuffer) throws IOException {
		if (Objects.nonNull(this.getSocketChannel())) {
			this.getSocketChannel().write(byteBuffer);
		} else {
			this.getOutputStream().write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
			this.getOutputStream().flush();
		}
	}

	@Override
	public void run() {
		try {
			InputStream inputStream = recvSocket.getInputStream();

			int len = -1;
			byte[] arrayTemp = new byte[streamCacheSize];

			while (alive && (len = inputStream.read(arrayTemp)) > 0) {
				this.write(ByteBuffer.wrap(arrayTemp, 0, len));
			}
		} catch (IOException e) {
			// do nothing
		}

		log.debug("one InputToOutputThread closed");

		// 传输完成后退出
		this.cancell();
	}

	// ============== nio =================

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private ByteBuffer byteBuffer;

	private ByteBuffer obtainByteBuffer() {
		if (Objects.isNull(byteBuffer)) {
			byteBuffer = ByteBuffer.allocate(streamCacheSize);
		}
		return byteBuffer;
	}

	@Override
	public void proccess(SelectionKey key) {
		ByteBuffer buffer = this.obtainByteBuffer();

		SocketChannel inputChannel = (SocketChannel) key.channel();
		try {
			if (!key.isReadable()) {
				return;
			}

			int len = -1;
			do {
				buffer.clear();

				len = inputChannel.read(buffer);

				if (len > 0) {
					buffer.flip();
					if (buffer.hasRemaining()) {
						this.write(buffer);
					}
				}

			} while (len > 0);

			// 如果不是负数，则还没有断开连接，返回继续等待
			if (len >= 0) {
				return;
			}
		} catch (IOException e) {
			//
		}

		log.debug("one InputToOutputThread closed");

		this.cancell();
	}

	/**
	 * 判断是否有效
	 * 
	 * @author Pluto
	 * @since 2020-01-08 15:59:13
	 * @return
	 */
	public boolean isValid() {
		return alive;
	}

	/**
	 * 退出
	 * 
	 * @author Pluto
	 * @since 2020-01-08 15:59:19
	 */
	public void cancell() {
		this.alive = false;

		NioHallows.release(recvSocket.getChannel());

		try {
			this.recvSocket.close();
			this.sendSocket.close();
		} catch (IOException e) {
			// do no thing
		}

		if (belongControl != null) {
			IBelongControl belong = belongControl;
			belongControl = null;
			if (belong != null) {
				belong.noticeStop();
			}
		}
	}

	/**
	 * 启动
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:01:02
	 */
	public void start() {
		if (!this.alive) {
			this.alive = true;

			if (Objects.isNull(recvSocket.getChannel())) {
				NatcrossExecutor.executePassway(this);
			} else {
				try {
					NioHallows.register(recvSocket.getChannel(), SelectionKey.OP_READ, this);
				} catch (IOException e) {
					this.cancell();
					return;
				}
			}
		}
	}

}
