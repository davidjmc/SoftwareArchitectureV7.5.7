package tests;

class Task implements Runnable {
	volatile boolean running = true;
	volatile boolean paused = false;
	Thread thread;

	public Task() {
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public void pause() {
		thread.interrupt();
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void run() {
		while (running) {
			while (!Thread.interrupted()) {
				System.out.println("running");
			}
			while (paused) {
				System.out.println("paused");
			}
		}
	}
}

class Manager implements Runnable {
	private Task task;
	private Thread thread;

	public Manager(Task t) {
		this.task = t;
	}

	public void stop() {
		this.task.stop();
	}

	public void pause() {
		task.pause();
		thread.interrupt();
	}

	public void resume() {
		this.task.resume();
	}

	public void start() {
		thread = new Thread(task);
		thread.start();
	}

	public void run() {
		start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pause();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resume();
	}
}

public class TestThreadShutdown {

	public static void main(String[] args) {
		Task task = new Task();

		try {
			task.start();
			while (true) {
				Thread.sleep(10);
				task.pause();
				Thread.sleep(10);
				task.resume();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}