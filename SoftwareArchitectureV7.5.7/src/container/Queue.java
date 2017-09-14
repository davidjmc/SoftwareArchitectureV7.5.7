package container;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import utils.Utils;

public class Queue {
	private volatile BlockingQueue<Object> queue;
	private final Semaphore semaphore = new Semaphore(1, true);

	public Queue() {
		this.setQueue(new ArrayBlockingQueue<Object>(Utils.BLOCKING_QUEUE_MAX_SIZE));
	}

	public Queue(int size) {
		this.setQueue(new ArrayBlockingQueue<Object>(size));
	}

	public synchronized BlockingQueue<Object> getQueue() {
		return queue;
	}

	public synchronized void setQueue(BlockingQueue<Object> queue) {
		try {
			semaphore.acquire();
			this.queue = queue;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
