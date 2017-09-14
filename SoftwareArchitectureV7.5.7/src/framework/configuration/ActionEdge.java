package framework.configuration;

import container.Queue;

public class ActionEdge {
	private String action;
	private Queue queue;

	public ActionEdge(String a){
		this.action = a;
	}

	public ActionEdge(String a, Queue q){
		this.action = a;
		this.queue = new Queue();
	}

	public synchronized String getAction() {
		return action;
	}

	public synchronized void setAction(String action) {
		this.action = action;
	}
	public synchronized Queue getQueue() {
		return queue;
	}
	public synchronized void setQueue(Queue queue) {
		this.queue = queue;
	}

}
