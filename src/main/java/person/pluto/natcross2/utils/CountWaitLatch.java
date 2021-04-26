package person.pluto.natcross2.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 
 * <p>
 * 可增 计数 门闩
 * </p>
 *
 * @author Pluto
 * @since 2021-04-14 14:19:35
 */
public class CountWaitLatch {
	/**
	 * Synchronization control For CountWaitLatch. Uses AQS state to represent
	 * count.
	 */
	private static final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 4982264981922014374L;

		Sync(int count) {
			setState(count);
		}

		int getCount() {
			return getState();
		}

		protected int tryAcquireShared(int acquires) {
			if (acquires == 0) {
				return (getState() == 0) ? acquires : -1;
			}

			for (;;) {
				int c = getState();
				int nextc = c + acquires;
				if (compareAndSetState(c, nextc))
					return acquires;
			}
		}

		protected boolean tryReleaseShared(int releases) {
			// Decrement count; signal when transition to zero
			for (;;) {
				int c = getState();
				if (c == 0)
					return false;
				int nextc = c - releases;
				if (compareAndSetState(c, nextc))
					return nextc == 0;
			}
		}
	}

	private final Sync sync;

	/**
	 * count == 0
	 */
	public CountWaitLatch() {
		this(0);
	}

	/**
	 * @param count >=0
	 */
	public CountWaitLatch(int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0");
		this.sync = new Sync(count);
	}

	/**
	 * 等待释放
	 *
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(0);
	}

	/**
	 * 等待释放
	 *
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireSharedNanos(0, unit.toNanos(timeout));
	}

	/**
	 * count++
	 */
	public void countUp() {
		sync.acquireShared(1);
	}

	/**
	 * --count >= 0
	 */
	public void countDown() {
		sync.releaseShared(1);
	}

	/**
	 * count >= 0
	 */
	public long getCount() {
		return sync.getCount();
	}

	@Override
	public String toString() {
		return super.toString() + "[Count = " + sync.getCount() + "]";
	}
}
