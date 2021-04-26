package person.pluto.natcross2.api.socketpart;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.IHttpRouting;
import person.pluto.natcross2.api.passway.SimplePassway;
import person.pluto.natcross2.model.HttpRoute;
import person.pluto.natcross2.utils.Tools;

/**
 * <p>
 * http路由socket对
 * </p>
 *
 * @author Pluto
 * @since 2020-04-23 16:57:28
 */
@Slf4j
public class HttpRouteSocketPart extends SimpleSocketPart {

	private static final Charset httpCharset = Charset.forName("ISO-8859-1");

	// 这里的 : 好巧不巧的 0x20 位是1，可以利用一波
	private static final byte colonByte = ':';
	private static final byte[] hostMatcher = new byte[] { 'h', 'o', 's', 't', colonByte };
	private static final int colonIndex = hostMatcher.length - 1;

	private final IHttpRouting httpRouting;

	public HttpRouteSocketPart(IBelongControl belongThread, IHttpRouting httpRouting) {
		super(belongThread);
		this.httpRouting = httpRouting;
	}

	/**
	 * 选择路由并连接至目标
	 * 
	 * @author Pluto
	 * @since 2020-04-24 11:01:24
	 * @throws Exception
	 */
	protected void routeHost() throws Exception {
		HttpRoute willConnect = null;

		InputStream inputStream = new BufferedInputStream(sendSocket.getInputStream());

		// 缓存数据，不能我们处理了就不给实际应用
		ByteArrayOutputStream headerBufferStream = new ByteArrayOutputStream(1024);

		// 临时输出列，用于读取一整行后进行字符串判断
		ByteArrayOutputStream lineBufferStream = new ByteArrayOutputStream();

		for (int flag = 0, lineCount = 0, matchFlag = 0;; lineCount++) {
			// 依次读取
			int read = inputStream.read();
			lineBufferStream.write(read);

			if (read < 0) {
				break;
			}

			// 记录换行状态
			if (read == '\r' || read == '\n') {
				flag++;
			} else {
				flag = 0;
				if (
				// 这里matchFlag与lineCount不相等的频次比例较大，先比较
				matchFlag == lineCount
						// 肯定要小于了呀
						&& lineCount < hostMatcher.length
						// 如果是冒号的位置，需要完全相等
						&& (matchFlag == colonIndex ? read == hostMatcher[matchFlag]
								// 大写转小写，说好的可以利用 : 0x20 位是1 的特性呢😭
								: (read | 0x20) == hostMatcher[matchFlag])) {
					matchFlag++;
				}
			}

			// 如果大于等于4则就表示http头结束了
			if (flag >= 4) {
				break;
			}

			// 等于2表示一行结束了，需要进行处理
			if (flag == 2) {
				boolean isHostLine = (matchFlag == hostMatcher.length);

				// for循环特性，设置-1，营造line为0
				lineCount = -1;
				matchFlag = 0;

				// 省去一次toByteArray拷贝的可能
				lineBufferStream.writeTo(headerBufferStream);

				if (isHostLine) {
					byte[] byteArray = lineBufferStream.toByteArray();
					// 重置行输出流
					lineBufferStream.reset();

					int left, right;
					for (left = right = hostMatcher.length; right < byteArray.length; right++) {
						if (byteArray[left] == ' ') {
							// 左边先去掉空白，去除期间right不用判断
							left++;
						} else if (byteArray[right] == colonByte || byteArray[right] == ' ' || byteArray[right] == '\r'
								|| byteArray[right] == '\n') {
							// right位置到left位置必有字符，遇到空白或 : 则停下，与left中间的组合为host地址
							break;
						}
					}

					// 将缓存中的数据进行字符串化，根据http标准，字符集为 ISO-8859-1
					String host = new String(byteArray, left, right - left, httpCharset);

					willConnect = this.httpRouting.pickEffectiveRoute(host);

					break;
				} else {
					// 重置临时输出流
					lineBufferStream.reset();
				}
			}

		}

		// 将最后残留的输出
		lineBufferStream.writeTo(headerBufferStream);

		Socket recvSocket = this.recvSocket;

		InetSocketAddress destAddress = new InetSocketAddress(willConnect.getDestIp(), willConnect.getDestPort());
		recvSocket.connect(destAddress);

		OutputStream outputStream = recvSocket.getOutputStream();
		headerBufferStream.writeTo(outputStream);

		// emmm.... 用bufferedStream每次read不用单字节从硬件缓存里读呀，快了些呢，咋地了，不就是再拷贝一次嘛！
		Tools.streamCopy(inputStream, outputStream);

		// flush的原因，不排除这里全部读完了，导致缓存中没有数据，那即使创建了passway也不会主动flush而是挂在那里，防止遇到lazy的自动刷新特性
		outputStream.flush();
	}

	@Override
	public boolean createPassWay() {
		if (this.isAlive) {
			return true;
		}
		this.isAlive = true;
		try {
			routeHost();

			SimplePassway outToInPassway = this.outToInPassway = new SimplePassway();
			outToInPassway.setBelongControl(this);
			outToInPassway.setSendSocket(this.sendSocket);
			outToInPassway.setRecvSocket(this.recvSocket);
			outToInPassway.setStreamCacheSize(getStreamCacheSize());

			SimplePassway inToOutPassway = this.inToOutPassway = new SimplePassway();
			inToOutPassway.setBelongControl(this);
			inToOutPassway.setSendSocket(this.recvSocket);
			inToOutPassway.setRecvSocket(this.sendSocket);
			inToOutPassway.setStreamCacheSize(getStreamCacheSize());

			outToInPassway.start();
			inToOutPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}

}
