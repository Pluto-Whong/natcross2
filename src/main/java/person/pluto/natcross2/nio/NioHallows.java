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

	private volatile Selector selector;
	private final Object selectorLock = new Object();

	private Map<SelectableChannel, ProcesserHolder> chanelProcesserMap = new ConcurrentHashMap<>();

	@Setter
	@Getter
	private long selectTimeout = 100L;
	@Setter
	@Getter
	private long wakeupSleepNanos = 1000000L;

	private boolean alive = false;

	private CountWaitLatch countWaitLatch = new CountWaitLatch();

	public Selector getSelector() throws IOException {
		if (Objects.isNull(selector)) {
			synchronized (selectorLock) {
				// 二次校验
				if (Objects.isNull(selector)) {
					selector = Selector.open();
					this.start();
				}
			}
		}

		return selector;
	}

	public Selector getWakeupSelector() throws IOException {
		return this.getSelector().wakeup();
	}

	public void register0(SelectableChannel channel, int ops, INioProcesser proccesser) throws IOException {
		Objects.requireNonNull(channel, "channel non null");
		try {
			chanelProcesserMap.put(channel, ProcesserHolder.of(channel, ops, proccesser));
			channel.configureBlocking(false);

			countWaitLatch.countUp();
			// 这里有个坑点，如果在select中，这里会被阻塞
			channel.register(getWakeupSelector(), ops);
		} catch (Throwable e) {
			chanelProcesserMap.remove(channel);
			throw e;
		} finally {
			countWaitLatch.countDown();
		}
	}

	public void release0(SelectableChannel channel) {
		if (Objects.isNull(channel)) {
			return;
		}
		chanelProcesserMap.remove(channel);

		SelectionKey key = channel.keyFor(selector);

		if (Objects.nonNull(key)) {
			key.cancel();
		}
	}

	@Override
	public void run() {
		for (; alive;) {
			try {
				// 采用有期限的监听，以免线程太快，没有来的及注册，就永远阻塞在那里了
				int select = getSelector().select(this.getSelectTimeout());
				if (select <= 0) {
					// 给注册事务一个时间，如果等待时间太长（可能需要注入的太多），就跳出再去获取新事件，防止饿死
					try {
						countWaitLatch.await(wakeupSleepNanos, TimeUnit.NANOSECONDS);
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
		if (myThread == null || !myThread.isAlive()) {
			myThread = new Thread(this);
			myThread.setName("nio-hallows");

			this.alive = true;
			myThread.start();

			log.info("NioHallows is started!");
		}
	}

	public void cancel() {
		log.info("NioHallows cancell");

		this.alive = false;

		if (selector != null) {
			try {
				selector.close();
				selector = null;
			} catch (IOException e) {
			}
		}

		if (myThread != null) {
			myThread.interrupt();
			myThread = null;
		}
	}

}
