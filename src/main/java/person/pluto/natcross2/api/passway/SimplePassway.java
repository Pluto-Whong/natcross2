package person.pluto.natcross2.api.passway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;

/**
 * <p>
 * 简单的隧道，无任何处理，只从输入侧原样输出到输出侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:58:11
 */
@Slf4j
public class SimplePassway implements Runnable {

    private boolean alive = false;

    /**
     * 所属对象，完成后通知
     */
    @Setter
    private IBelongControl belongControl;

    /**
     * 缓存大小
     */
    @Setter
    private int streamCacheSize = 4096;

    @Setter
    private Socket recvSocket;
    @Setter
    private Socket sendSocket;

    @Override
    public void run() {
        try (InputStream inputStream = recvSocket.getInputStream();
                OutputStream outputStream = sendSocket.getOutputStream()) {
            int len = -1;
            byte[] arrayTemp = new byte[streamCacheSize];

            while (alive && (len = inputStream.read(arrayTemp)) > 0) {
                outputStream.write(arrayTemp, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.debug("one InputToOutputThread closed");
        }

        // 传输完成后退出
        this.cancell();
        if (belongControl != null) {
            belongControl.noticeStop();
        }
    }

    /**
     * 判断是否有效
     * 
     * @author Pluto
     * @since 2020-01-08 15:59:13
     * @return
     */
    public boolean isValid() {
        return alive;
    }

    /**
     * 退出
     * 
     * @author Pluto
     * @since 2020-01-08 15:59:19
     */
    public void cancell() {
        this.alive = false;

        try {
            this.recvSocket.close();
            this.sendSocket.close();
        } catch (IOException e) {
            // do no thing
        }
    }

    /**
     * 启动
     * 
     * @author Pluto
     * @since 2020-01-08 16:01:02
     */
    public void start() {
        if (!this.alive) {
            this.alive = true;
            PasswayControl.executePassway(this);
        }
    }

}
