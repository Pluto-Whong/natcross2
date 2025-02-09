package person.pluto.natcross2.executor;

import java.util.concurrent.ScheduledFuture;

/**
 * <p>
 * 执行器实现
 * </p>
 *
 * @author Pluto
 * @since 2021-04-08 14:38:23
 */
public interface IExecutor {

    /**
     * 关闭
     *
     * @throws Exception
     */
    void shutdown();

    /**
     * 默认执行方法
     *
     * @param runnable
     */
    void execute(Runnable runnable);

    /**
     * 服务监听线程任务执行器
     * <p>
     * For {@link person.pluto.natcross2.serverside.listen.ServerListenThread}
     *
     * @param runnable
     */
    default void executeServerListenAccept(Runnable runnable) {
        execute(runnable);
    }

    /**
     * 客户端监听线程任务执行器
     * <p>
     * For {@link person.pluto.natcross2.serverside.client.ClientServiceThread}
     *
     * @param runnable
     */
    default void executeClientServiceAccept(Runnable runnable) {
        execute(runnable);
    }

    /**
     * 客户端消息处理任务执行器
     * <p>
     * For
     * {@link person.pluto.natcross2.clientside.adapter.InteractiveSimpleClientAdapter#waitMessage()}
     *
     * @param runnable
     */
    default void executeClientMessageProc(Runnable runnable) {
        execute(runnable);
    }

    /**
     * 隧道线程执行器
     * <p>
     * For {@link person.pluto.natcross2.api.passway}
     *
     * @param runnable
     */
    default void executePassway(Runnable runnable) {
        execute(runnable);
    }

    /**
     * nio事件任务执行器
     * <p>
     * For {@link person.pluto.natcross2.nio.NioHallows#run()}
     *
     * @param runnable
     */
    default void executeNioAction(Runnable runnable) {
        execute(runnable);
    }

    /**
     * 心跳检测定时循环任务执行
     *
     * @param runnable
     * @param delaySeconds
     */
    ScheduledFuture<?> scheduledClientHeart(Runnable runnable, long delaySeconds);

    /**
     * 服务监听清理无效socket对
     *
     * @param runnable
     * @param delaySeconds
     * @return
     */
    ScheduledFuture<?> scheduledClearInvalidSocketPart(Runnable runnable, long delaySeconds);

}
