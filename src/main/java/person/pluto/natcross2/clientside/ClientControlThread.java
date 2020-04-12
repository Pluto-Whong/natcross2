package person.pluto.natcross2.clientside;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.clientside.adapter.IClientAdapter;
import person.pluto.natcross2.clientside.config.IClientConfig;
import person.pluto.natcross2.clientside.heart.IClientHeartThread;

/**
 * <p>
 * 客户端控制服务
 * </p>
 *
 * @author Pluto
 * @since 2019-07-05 10:53:33
 */
@Slf4j
public final class ClientControlThread implements Runnable, IBelongControl {

    private Thread myThread = null;

    private boolean isAlive = false;

    private Map<String, AbsSocketPart> socketPartMap = new HashMap<>();

    private IClientConfig<?, ?> config;

    private IClientHeartThread clientHeartThread;
    private IClientAdapter<?, ?> clientAdapter;

    public ClientControlThread(IClientConfig<?, ?> config) {
        this.config = config;

    }

    /**
     * 触发控制服务
     *
     * @author Pluto
     * @since 2019-07-18 19:02:15
     * @return
     * @throws Exception
     */
    public boolean createControl() throws Exception {
        if (this.clientAdapter == null) {
            this.clientAdapter = this.config.newCreateControlAdapter(this);
        }

        boolean flag = this.clientAdapter.createControl();

        if (!flag) {
            this.stopClient();
            return false;
        }

        this.start();
        return true;
    }

    @Override
    public void run() {
        while (isAlive) {
            try {
                // 使用适配器代理执行
                this.clientAdapter.waitMessage();
            } catch (Exception e) {
                log.warn("client control [{}] to server is exception,will stopClient",
                        this.config.getListenServerPort());
                this.stopClient();
            }
        }
    }

    @Override
    public boolean stopSocketPart(String socketPartKey) {
        log.debug("stopSocketPart[{}]", socketPartKey);
        AbsSocketPart socketPart = socketPartMap.get(socketPartKey);
        if (socketPart == null) {
            return false;
        }
        socketPart.cancel();
        socketPartMap.remove(socketPartKey);
        return true;
    }

    /**
     ** 启动
     * 
     * @author Pluto
     * @since 2020-01-07 16:13:26
     */
    public void start() {
        this.isAlive = true;
        if (myThread == null || !myThread.isAlive()) {

            if (this.clientHeartThread == null || !this.clientHeartThread.isAlive()) {
                this.clientHeartThread = this.config.newClientHeartThread(this);
                if (this.clientHeartThread != null) {
                    this.clientHeartThread.start();
                }
            }

            myThread = new Thread(this);
            myThread.start();
        }
    }

    /**
     ** 停止客户端监听
     *
     * @author Pluto
     * @since 2019-07-19 09:24:41
     */
    public void stopClient() {
        isAlive = false;

        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
        }
    }

    /**
     ** 全部退出
     *
     * @author Pluto
     * @since 2019-07-19 09:19:43
     */
    public void cancell() {

        stopClient();

        if (this.clientHeartThread != null) {
            try {
                this.clientHeartThread.cancel();
            } catch (Exception e) {
                // do no thing
            }
            this.clientHeartThread = null;
        }

        if (clientAdapter != null) {
            try {
                clientAdapter.close();
            } catch (Exception e) {
                // do no thing
            }
            this.clientAdapter = null;
        }

        Set<String> keySet = socketPartMap.keySet();
        String[] array = keySet.toArray(new String[keySet.size()]);

        for (String key : array) {
            stopSocketPart(key);
        }

    }

    /**
     ** 服务端监听的端口
     * 
     * @author Pluto
     * @since 2020-01-07 16:13:47
     * @return
     */
    public Integer getListenServerPort() {
        return config.getListenServerPort();
    }

    /**
     ** 重设目标端口
     * 
     * @author Pluto
     * @since 2020-01-07 16:14:06
     * @param destIp
     * @param destPort
     */
    public void setDestIpPort(String destIp, Integer destPort) {
        this.config.setDestIpPort(destIp, destPort);
    }

    /**
     ** 检测是否还活着
     * 
     * @author Pluto
     * @since 2020-01-07 16:14:21
     * @return
     */
    public boolean isAlive() {
        if (isAlive) {
            return true;
        }
        return false;
    }

    /**
     * 发送心跳测试
     * 
     * @author Pluto
     * @since 2020-01-07 15:54:47
     * @throws Exception
     */
    public void sendUrgentData() throws Exception {
        // 无需判空，空指针异常也是异常
        this.clientAdapter.sendUrgentData();
    }

    /**
     * 设置隧道伙伴
     * 
     * @author Pluto
     * @since 2020-01-08 16:35:06
     * @param socketPartKey
     * @param socketPart
     */
    public void putSocketPart(String socketPartKey, AbsSocketPart socketPart) {
        this.socketPartMap.put(socketPartKey, socketPart);
    }

}
