package person.pluto.natcross2.api.passway;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * <p>
 * 隧道通用线程池
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:53:53
 */
public final class PasswayControl {

    /**
     ** 隧道线程使用，若不使用全局也可以的
     */
    private static ExecutorService passwayExecutorService = Executors.newCachedThreadPool();

    /**
     * 将隧道线程加入线程池
     * 
     * @author Pluto
     * @since 2020-01-08 15:54:15
     * @param runnable
     */
    public static void executePassway(Runnable runnable) {
        passwayExecutorService.execute(runnable);
    }

    /**
     * 设置隧道线程池
     * 
     * @author Pluto
     * @since 2020-01-08 15:54:22
     * @param executorService
     */
    public static void setPasswayExecutorService(ExecutorService executorService) {
        if (PasswayControl.passwayExecutorService != null) {
            PasswayControl.passwayExecutorService.shutdown();
        }
        PasswayControl.passwayExecutorService = executorService;
    }

    /**
     * 立即关闭
     * 
     * @author Pluto
     * @since 2020-01-10 09:46:08
     */
    public static void closeNow() {
        if (PasswayControl.passwayExecutorService != null) {
            PasswayControl.passwayExecutorService.shutdownNow();
        }
        PasswayControl.passwayExecutorService = null;
    }

}
