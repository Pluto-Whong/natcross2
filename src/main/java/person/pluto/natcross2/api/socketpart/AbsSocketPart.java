package person.pluto.natcross2.api.socketpart;

import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import person.pluto.natcross2.api.IBelongControl;

/**
 * 
 * <p>
 * socketPart抽象类
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:01:56
 */
@Data
public abstract class AbsSocketPart {

	@Setter(AccessLevel.PRIVATE)
	protected volatile boolean isAlive = false;
	@Setter(AccessLevel.PRIVATE)
	protected volatile boolean canceled = false;

	@Setter(AccessLevel.PRIVATE)
	protected LocalDateTime createTime;

	/**
	 * 等待连接有效时间，ms
	 */
	@Getter
	@Setter
	private long invaildMillis = 60000L;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.PROTECTED)
	protected IBelongControl belongThread;

	protected String socketPartKey;
	/**
	 * 接受数据的socket；接受与发送的区分主要是主动发送方
	 */
	protected Socket recvSocket;
	/**
	 * 发送的socket
	 */
	protected Socket sendSocket;

	public AbsSocketPart(IBelongControl belongThread) {
		this.belongThread = belongThread;
		this.createTime = LocalDateTime.now();
	}

	/**
	 * 是否有效
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:04:53
	 * @return
	 */
	public boolean isValid() {
		if (this.canceled) {
			return false;
		}

		if (this.isAlive) {
			return true;
		}

		long millis = Duration.between(this.createTime, LocalDateTime.now()).toMillis();
		return millis < this.invaildMillis;
	}

	/**
	 * 退出
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:04:59
	 */
	public abstract void cancel();

	/**
	 * 打通隧道
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:05:04
	 * @return
	 */
	public abstract boolean createPassWay();

}
