package person.pluto.natcross2.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * 
 * <p>
 * 无归类的工具集
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:56:35
 */
public final class Tools {

	/**
	 * integer 转换为 byte[]
	 *
	 * @param source
	 * @return
	 * @author Pluto
	 * @since 2019-12-05 11:20:53
	 */
	public static byte[] intToBytes(int source) {
		return new byte[] { (byte) ((source >> 24) & 0xFF), (byte) ((source >> 16) & 0xFF),
				(byte) ((source >> 8) & 0xFF), (byte) (source & 0xFF) };
	}

	/**
	 * byte[] 转 integer
	 *
	 * @param byteArr
	 * @return
	 * @author Pluto
	 * @since 2019-12-05 11:21:07
	 */
	public static int bytes2int(byte[] byteArr) {
		int count = 0;

		for (int i = 0; i < 4; ++i) {
			count <<= 8;
			count |= byteArr[i] & 255;
		}

		return count;
	}

	/**
	 * 拷贝数据流
	 *
	 * @param inputStream
	 * @param outputStream
	 * @return
	 * @throws IOException
	 * @author Pluto
	 * @since 2021-04-08 13:53:12
	 */
	public static int streamCopy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] temp = new byte[8192];
		int sum = 0;
		int len;
		while ((len = inputStream.read(temp)) > 0) {
			sum += len;
			outputStream.write(temp, 0, len);

			if (inputStream.available() <= 0) {
				break;
			}
		}
		return sum;
	}

	public static int channelWrite(WritableByteChannel channel, ByteBuffer buffer) throws IOException {
		int sum = 0;
		while (buffer.hasRemaining()) {
			sum += channel.write(buffer);
		}
		return sum;
	}

}
