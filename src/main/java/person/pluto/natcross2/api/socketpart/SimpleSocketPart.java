package person.pluto.natcross2.api.socketpart;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.api.passway.SimplePassway;

/**
 * 
 * <p>
 * socket匹配对
 * </p>
 *
 * @author Pluto
 * @since 2019-07-12 08:36:30
 */
@Slf4j
public class SimpleSocketPart extends AbsSocketPart implements IBelongControl {

    private SimplePassway outToInPassway;
    private SimplePassway inToOutPassway;

    @Getter
    @Setter
    private long invaildMillis = 60000L;
    @Getter
    @Setter
    private int streamCacheSize = 4096;

    public SimpleSocketPart(IBelongControl belongThread) {
        super(belongThread);
    }

    @Override
    public boolean isValid() {
        if (isAlive) {
            if (outToInPassway == null || !outToInPassway.isValid() || inToOutPassway == null
                    || !inToOutPassway.isValid()) {
                return false;
            }
            return isAlive;
        }

        long millis = Duration.between(createTime, LocalDateTime.now()).toMillis();
        return millis < invaildMillis;
    }

    /**
     ** 停止，并告知上层处理掉
     *
     * @author Pluto
     * @since 2019-07-11 17:04:52
     */
    public void stop() {
        this.cancel();
        if (belongThread != null) {
            belongThread.stopSocketPart(socketPartKey);
        }
        belongThread = null;
    }

    @Override
    public void cancel() {
        log.debug("socketPart {} will cancel", this.socketPartKey);
        this.isAlive = false;
        if (recvSocket != null) {
            try {
                recvSocket.close();
            } catch (IOException e) {
                log.debug("socketPart [{}] 监听端口 关闭异常", socketPartKey);
            }
            recvSocket = null;
        }

        if (sendSocket != null) {
            try {
                sendSocket.close();
            } catch (IOException e) {
                log.debug("socketPart [{}] 发送端口 关闭异常", socketPartKey);
            }
            sendSocket = null;
        }

        if (outToInPassway != null) {
            outToInPassway.cancell();
            outToInPassway = null;
        }
        if (inToOutPassway != null) {
            inToOutPassway.cancell();
            inToOutPassway = null;
        }
        log.debug("socketPart {} is cancelled", this.socketPartKey);
    }

    @Override
    public boolean createPassWay() {
        if (this.isAlive) {
            return true;
        }
        this.isAlive = true;
        try {
            outToInPassway = new SimplePassway();
            outToInPassway.setBelongControl(this);
            outToInPassway.setRecvSocket(recvSocket);
            outToInPassway.setSendSocket(sendSocket);
            outToInPassway.setStreamCacheSize(getStreamCacheSize());

            inToOutPassway = new SimplePassway();
            inToOutPassway.setBelongControl(this);
            inToOutPassway.setRecvSocket(sendSocket);
            inToOutPassway.setSendSocket(recvSocket);
            inToOutPassway.setStreamCacheSize(getStreamCacheSize());

            outToInPassway.start();
            inToOutPassway.start();
        } catch (Exception e) {
            log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
            this.stop();
            return false;
        }
        return true;
    }

    @Override
    public void noticeStop() {
        this.stop();
    }

}
