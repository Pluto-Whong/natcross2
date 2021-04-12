package person.pluto.natcross2.api.passway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.executor.NatcrossExecutor;

/**
 * <p>
 * 简单的隧道，无任何处理，只从输入侧原样输出到输出侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:58:11
 */
@Slf4j
public class SimplePassway implements Runnable {

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

	private Selector inputChannelSelector;

	private OutputStream outputStream;
	private SocketChannel outputChannel;

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
		if (Objects.nonNull(outputChannel)) {
			outputChannel.write(byteBuffer);
		} else {
			outputStream.write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
			outputStream.flush();
		}
	}

	@Override
	public void run() {
		try {
			outputChannel = sendSocket.getChannel();
			outputStream = sendSocket.getOutputStream();

			if (Objects.isNull(recvSocket.getChannel())) {
				InputStream inputStream = recvSocket.getInputStream();

				int len = -1;
				byte[] arrayTemp = new byte[streamCacheSize];

				while (alive && (len = inputStream.read(arrayTemp)) > 0) {
					this.write(ByteBuffer.wrap(arrayTemp, 0, len));
				}
			} else {
				inputChannelSelector = Selector.open();

				SocketChannel inputChannel = recvSocket.getChannel();
				inputChannel.configureBlocking(false);
				inputChannel.register(inputChannelSelector, SelectionKey.OP_READ);

				ByteBuffer buffer = ByteBuffer.allocate(streamCacheSize);
				s: for (; alive;) {
					int select = inputChannelSelector.select();
					if (select <= 0) {
						continue;
					}

					Iterator<SelectionKey> iterator = inputChannelSelector.selectedKeys().iterator();

					for (; iterator.hasNext();) {
						SelectionKey key = iterator.next();
						iterator.remove();

						if (!key.isValid() || !key.isReadable()) {
							continue;
						}

						int len = -1;
						do {
							buffer.clear();

							len = inputChannel.read(buffer);

							if (len < 0) {
								break s;
							}

							buffer.flip();
							if (buffer.hasRemaining()) {
								this.write(buffer);
							}

						} while (len > 0);
					}
				}
			}
		} catch (IOException e) {
			// do nothing
		}

		log.debug("one InputToOutputThread closed");

		// 传输完成后退出
		this.cancell();
		if (belongControl != null) {
			belongControl.noticeStop();
		}
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

		try {
			this.recvSocket.close();
			this.sendSocket.close();
		} catch (IOException e) {
			// do no thing
		}

		if (Objects.nonNull(this.inputChannelSelector)) {
			this.inputChannelSelector.wakeup();
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
			NatcrossExecutor.executePassway(this);
		}
	}

}
