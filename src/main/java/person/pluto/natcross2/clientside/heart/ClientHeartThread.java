package person.pluto.natcross2.clientside.heart;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.executor.NatcrossExecutor;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * <p>
 * 心跳检测线程
 * </p>
 *
 * @author Pluto
 * @since 2021-04-25 17:53:44
 */
@Slf4j
public class ClientHeartThread implements IClientHeartThread, Runnable {

    private final ClientControlThread clientControlThread;

    private volatile boolean isAlive = false;

    @Setter
    @Getter
    private long heartIntervalSeconds = 10L;
    @Setter
    @Getter
    private int tryRecipientCount = 10;

    private volatile ScheduledFuture<?> scheduledFuture;

    private int failCount = 0;

    public ClientHeartThread(ClientControlThread clientControlThread) {
        this.clientControlThread = clientControlThread;
    }

    @Override
    public void run() {
        ClientControlThread clientControlThread = this.clientControlThread;
        if (clientControlThread.isCancelled() || !this.isAlive()) {
            this.cancel();
        }

        log.debug("send client heart data to {}", clientControlThread.getListenServerPort());
        try {
            clientControlThread.sendHeartTest();
            this.failCount = 0;

            return;
        } catch (Exception e) {
            log.warn("{} 心跳异常", clientControlThread.getListenServerPort());
            clientControlThread.stopClient();
        }
        if (!this.isAlive) {
            return;
        }

        this.failCount++;

        boolean createControl = false, logFlag = true;
        try {
            createControl = clientControlThread.createControl();
        } catch (Exception reClientException) {
            log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + this.failCount + " 次",
                    reClientException);
            logFlag = false;
        }

        if (createControl) {
            log.info("重新建立连接 {} 成功，在第 {} 次", clientControlThread.getListenServerPort(), this.failCount);

            this.failCount = 0;
            return;
        }

        if (logFlag) {
            log.warn("重新建立连接" + clientControlThread.getListenServerPort() + "失败第 " + this.failCount + " 次");
        }

        if (this.failCount >= this.tryRecipientCount) {
            log.error("尝试重新连接 {} 超过最大次数，关闭客户端", clientControlThread.getListenServerPort());
            clientControlThread.cancell();
            this.cancel();
        }
    }

    @Override
    public synchronized void start() {
        this.isAlive = true;

        ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
        if (Objects.isNull(scheduledFuture) || scheduledFuture.isCancelled()) {
            this.failCount = 0;
            this.scheduledFuture = NatcrossExecutor.scheduledClientHeart(this, this.heartIntervalSeconds);
        }
    }

    @Override
    public void cancel() {
        if (!this.isAlive) {
            return;
        }
        this.isAlive = false;

        ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
        if (Objects.nonNull(scheduledFuture) && !scheduledFuture.isCancelled()) {
            this.scheduledFuture = null;
            scheduledFuture.cancel(false);
        }
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

}
