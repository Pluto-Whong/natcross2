package person.pluto.natcross2.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * 执行器暂存
 * </p>
 *
 * @author Pluto
 * @since 2021-04-13 09:53:54
 */
@Data
@AllArgsConstructor(staticName = "of")
public class ProcesserHolder {

	private SelectableChannel channel;

	private int interestOps;

	private INioProcesser processer;

	/**
	 * 执行事件的任务
	 *
	 * @param key
	 * @author Pluto
	 * @since 2021-04-26 16:35:36
	 */
	public void proccess(SelectionKey key) {
		this.processer.proccess(key);

		if (!NioHallows.reRegisterByKey(key, this.interestOps)) {
			NioHallows.release(this.channel);
		}
	}
}
