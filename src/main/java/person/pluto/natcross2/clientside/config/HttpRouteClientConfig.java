package person.pluto.natcross2.clientside.config;

import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.HttpRouteSocketPart;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.clientside.adapter.InteractiveSimpleClientAdapter;
import person.pluto.natcross2.clientside.heart.IClientHeartThread;
import person.pluto.natcross2.model.HttpRoute;
import person.pluto.natcross2.model.InteractiveModel;

/**
 * 
 * <p>
 * http路由客户端配置
 * </p>
 *
 * @author Pluto
 * @since 2020-04-24 10:09:46
 */
@Slf4j
public class HttpRouteClientConfig extends InteractiveClientConfig {

    private InteractiveClientConfig baseConfig;

    private HttpRoute masterRoute = null;
    private LinkedHashMap<String, HttpRoute> routeMap = new LinkedHashMap<>();

    private ReentrantLock routeLock = new ReentrantLock();

    public HttpRouteClientConfig() {
        this.baseConfig = new InteractiveClientConfig();
    }

    public HttpRouteClientConfig(InteractiveClientConfig baseConfig) {
        this.baseConfig = baseConfig;
    }

    /**
     * 预设置
     * 
     * @author Pluto
     * @since 2020-04-24 11:37:35
     * @param masterRoute
     * @param routeMap
     */
    public void presetRoute(HttpRoute masterRoute, LinkedHashMap<String, HttpRoute> routeMap) {
        routeLock.lock();
        this.masterRoute = masterRoute;
        this.routeMap = routeMap;
        routeLock.unlock();
    }

    /**
     * 增加路由
     * 
     * @author Pluto
     * @since 2020-04-24 10:42:34
     * @param httpRoutes
     */
    public void addRoute(HttpRoute... httpRoutes) {
        if (httpRoutes == null || httpRoutes.length < 1) {
            return;
        }
        routeLock.lock();
        try {
            if (Objects.isNull(masterRoute)) {
                masterRoute = httpRoutes[0];
            }
            for (HttpRoute model : httpRoutes) {
                routeMap.put(model.getHost(), model);
                if (model.isMaster()) {
                    masterRoute = model;
                }
            }
        } finally {
            routeLock.unlock();
        }
    }

    /**
     * 清理路由
     * 
     * @author Pluto
     * @since 2020-04-24 10:43:05
     * @param hosts
     */
    public void clearRoute(String... hosts) {
        if (Objects.isNull(hosts) || hosts.length < 1) {
            return;
        }

        routeLock.lock();
        try {
            for (String host : hosts) {
                routeMap.remove(host);
                if (StringUtils.equals(masterRoute.getHost(), host)) {
                    masterRoute = null;
                }
            }

            if (Objects.isNull(masterRoute)) {
                Collection<HttpRoute> values = routeMap.values();
                Iterator<HttpRoute> iterator = values.iterator();
                if (iterator.hasNext()) {
                    // 先将第一个设置为主路由，再遍历所有，如果是主标志则设置为主，以最后的主为准
                    masterRoute = iterator.next();

                    while (iterator.hasNext()) {
                        HttpRoute model = iterator.next();
                        if (model.isMaster()) {
                            masterRoute = model;
                        }
                    }
                } else {
                    log.warn("{}:{} 路由是空的，若需要重新设置，请使用preset进行设置", this.getClientServiceIp(),
                            this.getClientServicePort());
                }
            }
        } finally {
            routeLock.unlock();
        }

    }

    @Override
    public void setDestIpPort(String destIp, Integer destPort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread) {
        return this.baseConfig.newClientHeartThread(clientControlThread);
    }

    @Override
    public IClientAdapter<InteractiveModel, InteractiveModel> newCreateControlAdapter(
            ClientControlThread clientControlThread) {
        InteractiveSimpleClientAdapter simpleClientAdapter = new InteractiveSimpleClientAdapter(clientControlThread,
                this);
        return simpleClientAdapter;
    }

    @Override
    public SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
        return this.baseConfig.newClientChannel();
    }

    @Override
    public AbsSocketPart newSocketPart(ClientControlThread clientControlThread) {
        HttpRouteSocketPart httpRouteSocketPart = new HttpRouteSocketPart(clientControlThread);
        httpRouteSocketPart.setStreamCacheSize(this.getStreamCacheSize());

        routeLock.lock();
        httpRouteSocketPart.presetRoute(masterRoute, routeMap);
        routeLock.unlock();

        return httpRouteSocketPart;
    }

    @Override
    public Socket newDestSocket() throws Exception {
        return new Socket();
    }

    @Override
    public String getClientServiceIp() {
        return baseConfig.getClientServiceIp();
    }

    @Override
    public void setClientServiceIp(String clientServiceIp) {
        baseConfig.setClientServiceIp(clientServiceIp);
    }

    @Override
    public Integer getClientServicePort() {
        return baseConfig.getClientServicePort();
    }

    @Override
    public void setClientServicePort(Integer clientServicePort) {
        baseConfig.setClientServicePort(clientServicePort);
    }

    @Override
    public Integer getListenServerPort() {
        return baseConfig.getListenServerPort();
    }

    @Override
    public void setListenServerPort(Integer listenServerPort) {
        baseConfig.setListenServerPort(listenServerPort);
    }

    @Override
    public String getDestIp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDestIp(String destIp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getDestPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDestPort(Integer destPort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Charset getCharset() {
        return baseConfig.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        baseConfig.setCharset(charset);
    }

    @Override
    public int getStreamCacheSize() {
        return baseConfig.getStreamCacheSize();
    }

    @Override
    public void setStreamCacheSize(int streamCacheSize) {
        baseConfig.setStreamCacheSize(streamCacheSize);
    };

}
