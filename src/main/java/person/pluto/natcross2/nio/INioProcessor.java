package person.pluto.natcross2.nio;

import java.nio.channels.SelectionKey;

/**
 * <p>
 * nio 执行器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-12 17:51:37
 */
@FunctionalInterface
public interface INioProcessor {

    /**
     * 需要执行的方法
     *
     * @param key
     */
    void process(SelectionKey key);

}
