package person.pluto.natcross2.serverside.listen.control;

import person.pluto.natcross2.serverside.listen.IServerListen;

/**
 * 
 * <p>
 * 控制端口接口
 * </p>
 *
 * @author Pluto
 * @since 2020-01-07 09:52:51
 */
public interface IControlSocket {

	/**
	 * 是否有效
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:54:13
	 * @return
	 */
	boolean isValid();

	/**
	 * 发送隧道等待状态
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:54:18
	 * @param socketPartKey
	 * @return
	 */
	boolean sendClientWait(String socketPartKey);

	/**
	 * 关闭
	 * 
	 * @author Pluto
	 * @since 2020-01-08 16:54:40
	 */
	void close();

	/**
	 * 替换关闭
	 * <p>
	 * 适配多客户端模式，若是替换关闭则可能不进行关闭 <br>
	 * 若是传统1对1模式，则等价调用 {@link #close()}
	 * 
	 * @since 2.3
	 */
	default void replaceClose() {
		this.close();
	}

	/**
	 * 开启接收线程
	 * 
	 * 实现的类需要自己进行幂等性处理
	 * 
	 * @author Pluto
	 * @since 2020-04-15 11:36:44
	 */
	void startRecv();

	/**
	 * 设置控制的监听线程
	 * 
	 * @author Pluto
	 * @since 2020-04-15 13:10:25
	 * @param serverListen
	 */
	void setServerListen(IServerListen serverListen);

}
