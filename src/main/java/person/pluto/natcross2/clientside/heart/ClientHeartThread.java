package person.pluto.natcross2.clientside.heart;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.clientside.ClientControlThread;

@Slf4j
public class ClientHeartThread implements IClientHeartThread, Runnable {

    private Thread myThread = null;

    private boolean isAlive = false;

    private ClientControlThread clientControlThread;

    private long heartIntervalSeconds = 10L;
    private int tryReclientCount = 10;

    public ClientHeartThread(ClientControlThread clientControlThread) {
        this.clientControlThread = clientControlThread;
    }

    @Override
    public void run() {
        Integer failCount = 0;

        while (isAlive) {
            try {
                TimeUnit.SECONDS.sleep(heartIntervalSeconds);
            } catch (InterruptedException e) {
                this.cancel();
                return;
            }
            try {
                log.debug("send urgent data to {}", clientControlThread.getListenServerPort());
                clientControlThread.sendUrgentData();
                failCount = 0;
            } catch (Exception e) {
                log.warn("{} 心跳异常，即将重新连接", clientControlThread.getListenServerPort());
                clientControlThread.stopClient();

                if (isAlive) {
                    failCount++;
                    try {
                        boolean createControl = clientControlThread.createControl();
                        if (createControl) {
                            clientControlThread.start();
                            log.info("重新建立连接 {} 成功，在第 {} 次", clientControlThread.getListenServerPort(), failCount);
                            continue;
                        }
                    } catch (Exception reClientException) {
                        log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + failCount + " 次",
                                reClientException);
                    }

                    log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + failCount + " 次");

                    if (failCount >= tryReclientCount) {
                        log.error("尝试重新连接 {} 超过最大次数，尝试关闭客户端", clientControlThread.getListenServerPort());
                        this.cancel();
                        clientControlThread.cancell();
                        log.info("尝试重新连接 {} 超过最大次数，关闭客户端成功", clientControlThread.getListenServerPort());
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        this.isAlive = true;
        if (myThread == null || !myThread.isAlive()) {
            myThread = new Thread(this);
            myThread.start();
        }
    }

    @Override
    public void cancel() {
        this.isAlive = false;
        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
        }
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public void setHeartIntervalSeconds(long heartIntervalSeconds) {
        this.heartIntervalSeconds = heartIntervalSeconds;
    }

    public void setTryReclientCount(int tryReclientCount) {
        this.tryReclientCount = tryReclientCount;
    }

}
