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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.executor.NatcrossExecutor;

/**
 * <p>
 * nio 容器
 * </p>
 *
 * @author Pluto
 * @since 2021-04-13 09:25:51
 */
@Slf4j
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
	private Object selectorLock = new Object();

	private Map<SelectableChannel, ProcesserHolder> chanelProcesserMap = new ConcurrentHashMap<>();

	@Setter
	@Getter
	private long selectTimeout = 100L;
	@Setter
	@Getter
	private long wakeupSleepNanos = 1000L;

	private boolean alive;

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
			// 这里有个坑点，如果在select中，这里会被阻塞
			channel.register(getWakeupSelector(), ops);
		} catch (Throwable e) {
			chanelProcesserMap.remove(channel);
			throw e;
		}
	}

	public void release0(SelectableChannel channel) {
		if (Objects.isNull(channel)) {
			return;
		}
		chanelProcesserMap.remove(channel);
	}

	@Override
	public void run() {
		for (; alive;) {
			try {
				// 采用有期限的监听，以免线程太快，没有来的及注册，就永远阻塞在那里了
				int select = getSelector().select(this.getSelectTimeout());
				if (select <= 0) {
					Iterator<SelectionKey> iterator = getSelector().selectedKeys().iterator();
					for (; iterator.hasNext();) {
						iterator.remove();
					}
					// 稍微休息下，给注册事务一个时间
					TimeUnit.NANOSECONDS.sleep(wakeupSleepNanos);
					continue;
				}

				Iterator<SelectionKey> iterator = getSelector().selectedKeys().iterator();
				for (; iterator.hasNext();) {
					SelectionKey key = iterator.next();

					ProcesserHolder processerHolder = chanelProcesserMap.get(key.channel());
					if (Objects.isNull(processerHolder)) {
						key.interestOps(0);
						iterator.remove();
						continue;
					} else {
						key.interestOps(key.interestOps() & ~processerHolder.getInterestOps());
						iterator.remove();
					}

					NatcrossExecutor.executeNioAction(() -> {
						processerHolder.proccess(key);
					});
				}

			} catch (IOException | InterruptedException e) {
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
			selector.wakeup();
			selector = null;
		}

		if (myThread != null) {
			myThread.interrupt();
			myThread = null;
		}
	}

}
