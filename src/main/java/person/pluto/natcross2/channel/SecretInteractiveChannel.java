package person.pluto.natcross2.channel;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Key;

import com.alibaba.fastjson.JSONObject;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;
import lombok.Getter;
import lombok.Setter;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.SecretInteractiveModel;
import person.pluto.natcross2.utils.AESUtil;

/**
 * 
 * <p>
 * InteractiveModel 加密型通道，AES加密
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:16:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SecretInteractiveChannel extends SocketChannel<InteractiveModel, InteractiveModel> {

	@Exclude
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private JsonChannel channel;

	/**
	 * 签名混淆key
	 */
	private String tokenKey;
	/**
	 * aes密钥
	 */
	private Key aesKey;
	/**
	 * 超时时间，毫秒
	 */
	private Long overtimeMills = 5000L;

	public SecretInteractiveChannel() {
		this.channel = new JsonChannel();
	}

	public SecretInteractiveChannel(Socket socket) throws IOException {
		this.channel = new JsonChannel(socket);
	}

	@Override
	public InteractiveModel read() throws Exception {
		JSONObject read = this.channel.read();
		SecretInteractiveModel secretInteractiveModel = read.toJavaObject(SecretInteractiveModel.class);
		if (Math.abs(System.currentTimeMillis() - secretInteractiveModel.getTimestamp()) > this.overtimeMills) {
			throw new IllegalStateException("超时");
		}
		boolean checkAutograph = secretInteractiveModel.checkAutograph(this.tokenKey);
		if (!checkAutograph) {
			throw new IllegalStateException("签名错误");
		}
		secretInteractiveModel.decryptMsg(this.aesKey);
		return secretInteractiveModel;
	}

	/**
	 * 统一数据转换方法，使 {@link #write(InteractiveModel)} 与
	 * {@link #writeAndFlush(InteractiveModel)} 转换结果保持一致
	 *
	 * @param value
	 * @return
	 * @throws Exception
	 * @author Pluto
	 * @since 2021-04-26 16:44:14
	 */
	private Object valueConvert(InteractiveModel value) throws Exception {
		SecretInteractiveModel secretInteractiveModel = new SecretInteractiveModel(value);
		secretInteractiveModel.setCharset(this.getCharset().name());
		secretInteractiveModel.fullMessage(this.aesKey, this.tokenKey);
		return secretInteractiveModel;
	}

	@Override
	public void write(InteractiveModel value) throws Exception {
		this.channel.write(this.valueConvert(value));
	}

	@Override
	public void flush() throws Exception {
		this.channel.flush();
	}

	@Override
	public void writeAndFlush(InteractiveModel value) throws Exception {
		this.channel.writeAndFlush(this.valueConvert(value));
	}

	/**
	 * 获取charset
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:18:58
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

	/**
	 * 使用base64格式设置aes密钥
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:19:07
	 * @param aesKey
	 */
	public void setBaseAesKey(String aesKey) {
		this.aesKey = AESUtil.createKeyByBase64(aesKey);
	}

}
