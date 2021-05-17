package person.pluto.natcross2.api.socketpart;

import java.io.IOException;
import java.net.Socket;

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
	private int streamCacheSize = 8192;

	private volatile boolean canceled = false;

	public SecretSocketPart(IBelongControl belongThread) {
		super(belongThread);
	}

	@Override
	public void cancel() {
		if (this.canceled) {
			return;
		}
		this.canceled = true;
		this.isAlive = false;

		log.debug("socketPart {} will cancel", this.socketPartKey);

		SecretPassway noToSecretPassway;
		if ((noToSecretPassway = this.noToSecretPassway) != null) {
			this.noToSecretPassway = null;
			noToSecretPassway.cancel();
		}
		SecretPassway secretToNoPassway;
		if ((secretToNoPassway = this.secretToNoPassway) != null) {
			this.secretToNoPassway = null;
			secretToNoPassway.cancel();
		}

		Socket recvSocket;
		if ((recvSocket = this.recvSocket) != null) {
			this.recvSocket = null;
			try {
				recvSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 监听端口 关闭异常", socketPartKey);
			}
		}

		Socket sendSocket;
		if ((sendSocket = this.sendSocket) != null) {
			this.sendSocket = null;
			try {
				sendSocket.close();
			} catch (IOException e) {
				log.debug("socketPart [{}] 发送端口 关闭异常", socketPartKey);
			}
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
			SecretPassway noToSecretPassway = this.noToSecretPassway = new SecretPassway();
			noToSecretPassway.setBelongControl(this);
			noToSecretPassway.setMode(Mode.noToSecret);
			noToSecretPassway.setRecvSocket(this.recvSocket);
			noToSecretPassway.setSendSocket(this.sendSocket);
			noToSecretPassway.setStreamCacheSize(getStreamCacheSize());
			noToSecretPassway.setSecret(this.secret);

			SecretPassway secretToNoPassway = this.secretToNoPassway = new SecretPassway();
			secretToNoPassway.setBelongControl(this);
			secretToNoPassway.setMode(Mode.secretToNo);
			secretToNoPassway.setRecvSocket(this.sendSocket);
			secretToNoPassway.setSendSocket(this.recvSocket);
			secretToNoPassway.setSecret(this.secret);

			noToSecretPassway.start();
			secretToNoPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}

	/**
	 * 停止
	 *
	 * @author Pluto
	 * @since 2021-04-26 16:42:38
	 */
	public void stop() {
		this.cancel();

		IBelongControl belong;
		if ((belong = this.belongThread) != null) {
			this.belongThread = null;
			belong.noticeStop();
		}
	}

	@Override
	public void noticeStop() {
		this.stop();
	}

}
