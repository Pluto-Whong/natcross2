package person.pluto.natcross2.api.passway;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.api.IBelongControl;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.nio.INioProcessor;
import person.pluto.natcross2.nio.NioHallows;
import person.pluto.natcross2.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * <p>
 * 简单的隧道，无任何处理，只从输入侧原样输出到输出侧
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:58:11
 */
@Slf4j
public class SimplePassway implements Runnable, INioProcessor {

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
    private int streamCacheSize = 8192;

    @Setter
    private Socket recvSocket;
    @Setter
    private Socket sendSocket;

    private OutputStream outputStream;
    private SocketChannel outputChannel;

    private OutputStream getOutputStream() throws IOException {
        OutputStream outputStream = this.outputStream;
        if (Objects.isNull(outputStream)) {
            outputStream = this.sendSocket.getOutputStream();
            this.outputStream = outputStream;
        }
        return outputStream;
    }

    private SocketChannel getOutputChannel() {
        SocketChannel outputChannel = this.outputChannel;
        if (Objects.isNull(outputChannel)) {
            outputChannel = this.sendSocket.getChannel();
            this.outputChannel = outputChannel;
        }
        return outputChannel;
    }

    /**
     * 向输出通道输出数据
     * <p>
     * 这里不只是为了DMA而去用DMA，而是这里有奇葩问题
     * <p>
     * 如能采用了SocketChannel，而去用outputStream的时候，不管输入输出，都会有奇怪的问题，比如输出会莫名的阻塞住
     * <p>
     * 整体就是如果能用nio的方法，但是用了bio形式都会各种什么 NullPointException、IllageSateException 等等错误
     * <p>
     *
     * @param byteBuffer
     * @throws IOException
     */
    private void write(ByteBuffer byteBuffer) throws IOException {
        SocketChannel outputChannel = this.getOutputChannel();
        if (Objects.nonNull(outputChannel)) {
            Tools.channelWrite(outputChannel, byteBuffer);
        } else {
            OutputStream outputStream = this.getOutputStream();
            outputStream.write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
            outputStream.flush();
        }
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = this.recvSocket.getInputStream();

            int len;
            byte[] arrayTemp = new byte[this.streamCacheSize];

            while (this.alive && (len = inputStream.read(arrayTemp)) > 0) {
                this.write(ByteBuffer.wrap(arrayTemp, 0, len));
            }
        } catch (IOException e) {
            // do nothing
        }

        log.debug("one InputToOutputThread closed");

        // 传输完成后退出
        this.cancel();
    }

    // ============== nio =================

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private ByteBuffer byteBuffer;

    private ByteBuffer obtainByteBuffer() {
        ByteBuffer byteBuffer = this.byteBuffer;
        if (Objects.isNull(byteBuffer)) {
            if (Objects.isNull(this.getOutputChannel())) {
                byteBuffer = ByteBuffer.allocate(this.streamCacheSize);
            } else {
                // 输入输出可以使用channel，此处则使用DirectByteBuffer，这时候才真正体现出了DMA
                byteBuffer = ByteBuffer.allocateDirect(this.streamCacheSize);
            }
            this.byteBuffer = byteBuffer;
        }
        return byteBuffer;
    }

    @Override
    public void process(SelectionKey key) {
        if (this.alive && key.isValid()) {
            ByteBuffer buffer = this.obtainByteBuffer();

            SocketChannel inputChannel = (SocketChannel) key.channel();
            try {
                int len;
                do {
                    buffer.clear();

                    len = inputChannel.read(buffer);

                    if (len > 0) {
                        buffer.flip();
                        if (buffer.hasRemaining()) {
                            this.write(buffer);
                        }
                    }

                } while (len > 0);

                // 如果不是负数，则还没有断开连接，返回继续等待
                if (len == 0) {
                    return;
                }
            } catch (IOException e) {
                //
            }
        }

        log.debug("one InputToOutputThread closed");

        this.cancel();
    }

    /**
     * 判断是否有效
     *
     * @return
     */
    public boolean isValid() {
        return this.alive;
    }

    /**
     * 退出
     */
    public void cancel() {
        if (!this.alive) {
            return;
        }
        this.alive = false;

        NioHallows.release(this.recvSocket.getChannel());

        try {
            Socket sendSocket = this.sendSocket;
            if (Objects.nonNull(sendSocket)) {
                // TCP 挥手步骤，对方调用 shutdownOutput 后等价完成 socket.close
                sendSocket.shutdownOutput();
            }
        } catch (IOException e) {
            // do nothing
        }

        IBelongControl belong;
        if ((belong = this.belongControl) != null) {
            this.belongControl = null;
            belong.noticeStop();
        }
    }

    /**
     * 启动
     */
    public void start() {
        if (this.alive) {
            return;
        }
        this.alive = true;

        SocketChannel recvChannel = this.recvSocket.getChannel();
        if (Objects.isNull(recvChannel)) {
            NatcrossExecutor.executePassway(this);
        } else {
            try {
                NioHallows.register(recvChannel, SelectionKey.OP_READ, this);
            } catch (IOException e) {
                log.error("nio register failed", e);
                this.cancel();
            }
        }
    }

}
