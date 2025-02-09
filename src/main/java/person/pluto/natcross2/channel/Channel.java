package person.pluto.natcross2.channel;

import java.io.Closeable;
import java.nio.charset.Charset;

/**
 * <p>
 * 读写通道
 * </p>
 *
 * @param <R> 读取返回的类型
 * @param <W> 写入的类型
 * @author Pluto
 * @since 2020-01-03 15:40:28
 */
public interface Channel<R, W> extends Closeable {

    /**
     * 简单的读取方式
     *
     * @return
     * @throws Exception
     */
    R read() throws Exception;

    /**
     * 简单的写入
     *
     * @param value
     * @throws Exception
     */
    void write(W value) throws Exception;

    /**
     * 刷新
     *
     * @throws Exception
     */
    void flush() throws Exception;

    /**
     * 简单的写入并刷新
     *
     * @param value
     * @throws Exception
     */
    void writeAndFlush(W value) throws Exception;

    /**
     * 设置交互编码
     *
     * @param charset
     */
    default void setCharset(Charset charset) {
        throw new UnsupportedOperationException("不支持的操作");
    }
}
