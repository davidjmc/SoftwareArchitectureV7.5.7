package framework.configuration;

import org.jgrapht.graph.DefaultEdge;

import container.Queue;

public class BehaviourEdge extends DefaultEdge {
	private String portFrom;
	private String portTo;
	private Queue queue;

	private static final long serialVersionUID = 1L;

	public BehaviourEdge() {
		this.portFrom = new String();
		this.portTo = new String();
		this.queue = new Queue();
	}

	public void setS(Object s) {
		this.setSource(s);
	}

	public void setT(Object t) {
		this.setSource(t);
	}

	public Object getS() {
		return this.getSource();
	}

	public Object getT() {
		return this.getTarget();
	}

	public String getPortFrom() {
		return portFrom;
	}

	public void setPortFrom(String portFrom) {
		this.portFrom = portFrom;
	}

	public String getPortTo() {
		return portTo;
	}

	public void setPortTo(String portTo) {
		this.portTo = portTo;
	}

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}
}
