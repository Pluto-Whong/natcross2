package person.pluto.natcross2.model;

import lombok.Data;

/**
 * <p>
 * http路由表
 * </p>
 *
 * @author Pluto
 * @since 2020-04-24 09:31:51
 */
@Data
public class HttpRoute {

    public static HttpRoute of(String host, String destIp, Integer destPort) {
        return HttpRoute.of(false, host, destIp, destPort);
    }

    public static HttpRoute of(boolean master, String host, String destIp, Integer destPort) {
        HttpRoute model = new HttpRoute();
        model.setMaster(master);
        model.setHost(host);
        model.setDestIp(destIp);
        model.setDestPort(destPort);
        return model;
    }

    // 主路由，如果是多个则会去队列最后设置的那个
    private boolean master;

    // 请求时的域名host
    private String host;

    // 目标IP
    private String destIp;

    // 目标端口
    private Integer destPort;

}
