package person.pluto.natcross2.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.executor.NatcrossExecutor;
import person.pluto.natcross2.utils.CountWaitLatch;

/**
 * <p>
 * nio 容器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-13 09:25:51
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NioHallows implements Runnable {

	public static final NioHallows INSTANCE = new NioHallows();

	public static void register(SelectableChannel channel, int ops, INioProcesser proccesser) throws IOException {
		INSTANCE.register0(channel, ops, proccesser);
	}

	public static void release(SelectableChannel channel) {
		INSTANCE.release0(channel);
	}

	private volatile Thread myThread = null;

	private volatile boolean alive = false;

	private volatile Selector selector;
	private final Object selectorLock = new Object();

	private final CountWaitLatch countWaitLatch = new CountWaitLatch();

	private final Map<SelectableChannel, ProcesserHolder> chanelProcesserMap = new ConcurrentHashMap<>();

	@Setter
	@Getter
	private long selectTimeout = 100L;
	@Setter
	@Getter
	private long wakeupSleepNanos = 1000000L;

	public Selector getSelector() throws IOException {
		if (Objects.isNull(this.selector)) {
			synchronized (this.selectorLock) {
				// 二次校验
				if (Objects.isNull(this.selector)) {
					this.selector = Selector.open();
					this.start();
				}
			}
		}

		return this.selector;
	}

	public Selector getWakeupSelector() throws IOException {
		return this.getSelector().wakeup();
	}

	public void register0(SelectableChannel channel, int ops, INioProcesser proccesser) throws IOException {
		Objects.requireNonNull(channel, "channel non null");
		try {
			this.chanelProcesserMap.put(channel, ProcesserHolder.of(channel, ops, proccesser));
			channel.configureBlocking(false);

			this.countWaitLatch.countUp();
			// 这里有个坑点，如果在select中，这里会被阻塞
			channel.register(this.getWakeupSelector(), ops);
		} catch (Throwable e) {
			this.chanelProcesserMap.remove(channel);
			throw e;
		} finally {
			this.countWaitLatch.countDown();
		}
	}

	public void release0(SelectableChannel channel) {
		if (Objects.isNull(channel)) {
			return;
		}
		this.chanelProcesserMap.remove(channel);

		SelectionKey key = channel.keyFor(this.selector);

		if (Objects.nonNull(key)) {
			key.cancel();
		}
	}

	@Override
	public void run() {
		CountWaitLatch countWaitLatch = this.countWaitLatch;
		Map<SelectableChannel, ProcesserHolder> chanelProcesserMap = this.chanelProcesserMap;

		for (; this.alive;) {
			try {
				// 采用有期限的监听，以免线程太快，没有来的及注册，就永远阻塞在那里了
				int select = getSelector().select(this.getSelectTimeout());
				if (select <= 0) {
					// 给注册事务一个时间，如果等待时间太长（可能需要注入的太多），就跳出再去获取新事件，防止饿死
					try {
						countWaitLatch.await(this.wakeupSleepNanos, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						log.warn("selector wait register timeout");
					}
					continue;
				}

				Iterator<SelectionKey> iterator = getSelector().selectedKeys().iterator();
				for (; iterator.hasNext();) {
					SelectionKey key = iterator.next();
					iterator.remove();
					key.interestOps(0);

					ProcesserHolder processerHolder = chanelProcesserMap.get(key.channel());
					if (Objects.isNull(processerHolder)) {
						key.cancel();
						continue;
					}

					NatcrossExecutor.executeNioAction(() -> {
						processerHolder.proccess(key);
					});
				}

			} catch (IOException e) {
				log.error("NioHallows run exception", e);
			}
		}
	}

	public void start() {
		this.alive = true;
		if (this.myThread == null || !this.myThread.isAlive()) {
			this.myThread = new Thread(this);
			this.myThread.setName("nio-hallows");
			this.myThread.start();

			log.info("NioHallows is started!");
		}
	}

	public void cancel() {
		log.info("NioHallows cancell");

		this.alive = false;

		Selector selector;
		if ((selector = this.selector) != null) {
			this.selector = null;
			try {
				selector.close();
			} catch (IOException e) {
			}
		}

		Thread myThread;
		if ((myThread = this.myThread) != null) {
			this.myThread = null;
			myThread.interrupt();
		}
	}

}
