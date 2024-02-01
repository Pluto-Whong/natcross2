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
     * 获取有效路由
     *
     * @param host
     * @return
     */
    HttpRoute pickEffectiveRoute(String host);

}
