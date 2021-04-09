package person.pluto.natcross2.api.socketpart;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.passway.SecretPassway;
import person.pluto.natcross2.api.passway.SecretPassway.Mode;
import person.pluto.natcross2.api.secret.ISecret;

/**
 * 
 * <p>
 * 加密-无加密socket对
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:05:25
 */
@Slf4j
public class SecretSocketPart extends AbsSocketPart implements IBelongControl {

	@Getter
	@Setter
	private ISecret secret;

	private SecretPassway noToSecretPassway;
	private SecretPassway secretToNoPassway;

	@Getter
	@Setter
	private int streamCacheSize = 4096;

	public SecretSocketPart(IBelongControl belongThread) {
		super(belongThread);
	}

	@Override
	public boolean isValid() {
		if (super.isValid()) {
			if (noToSecretPassway == null || !noToSecretPassway.isValid() || secretToNoPassway == null
					|| !secretToNoPassway.isValid()) {
				return false;
			}
			return isAlive;
		}

		return false;
	}

	@Override
	public void cancel() {
		log.debug("socketPart {} will cancel", this.socketPartKey);
		this.isAlive = false;
		if (recvSocket != null) {
			try {
				recvSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 监听端口 关闭异常", socketPartKey);
			}
			recvSocket = null;
		}

		if (sendSocket != null) {
			try {
				sendSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 发送端口 关闭异常", socketPartKey);
			}
			sendSocket = null;
		}

		if (noToSecretPassway != null) {
			noToSecretPassway.cancell();
			noToSecretPassway = null;
		}
		if (secretToNoPassway != null) {
			secretToNoPassway.cancell();
			secretToNoPassway = null;
		}

		log.debug("socketPart {} is cancelled", this.socketPartKey);
	}

	@Override
	public boolean createPassWay() {
		if (this.isAlive) {
			return true;
		}
		this.isAlive = true;
		try {
			// 主要面向服务端-客户端过程加密
			noToSecretPassway = new SecretPassway();
			noToSecretPassway.setBelongControl(this);
			noToSecretPassway.setMode(Mode.noToSecret);
			noToSecretPassway.setRecvSocket(recvSocket);
			noToSecretPassway.setSendSocket(sendSocket);
			noToSecretPassway.setStreamCacheSize(getStreamCacheSize());
			noToSecretPassway.setSecret(secret);

			secretToNoPassway = new SecretPassway();
			secretToNoPassway.setBelongControl(this);
			secretToNoPassway.setMode(Mode.secretToNo);
			secretToNoPassway.setRecvSocket(sendSocket);
			secretToNoPassway.setSendSocket(recvSocket);
			secretToNoPassway.setSecret(secret);

			noToSecretPassway.start();
			secretToNoPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}

	public void stop() {
		this.cancel();
		if (belongThread != null) {
			belongThread.stopSocketPart(socketPartKey);
		}
		belongThread = null;
	}

}
