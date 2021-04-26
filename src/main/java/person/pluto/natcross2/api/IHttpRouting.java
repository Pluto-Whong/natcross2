package person.pluto.natcross2.api;

import person.pluto.natcross2.model.HttpRoute;

/**
 * <p>
 * http 路由器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-26 08:54:44
 */
public interface IHttpRouting {

	/**
	 * 根据host获取路由
	 *
	 * @param host
	 * @return
	 * @author Pluto
	 * @since 2021-04-26 08:57:10
	 */
	public HttpRoute pickRouteByHost(String host);

	/**
	 * 获取默认路由
	 *
	 * @return
	 * @author Pluto
	 * @since 2021-04-26 08:57:21
	 */
	public HttpRoute pickMasterRoute();

	/**
	 * 获取有效路由
	 * <p>
	 * 等价执行 {@link #pickRouteByHost(String)} ，若无对应路由则返回 {@link #pickMasterRoute()}
	 *
	 * @param host
	 * @return
	 * @author Pluto
	 * @since 2021-04-26 08:57:32
	 */
	public HttpRoute pickEffectiveRoute(String host);

}
