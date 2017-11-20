package container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import framework.basic.Element;
import framework.configuration.ActionEdge;
import utils.MyError;
import utils.Utils;

public class ExecutionUnit implements Runnable {
	private volatile ExecutionEnvironment env;
	private volatile Element element;
	private volatile AtomicInteger state = new AtomicInteger(1);
	private volatile AtomicBoolean pausable = new AtomicBoolean(true);
	private volatile Semaphore semaphore = new Semaphore(1, true);
	private volatile int nextVertex = 0;

	public Element getElement() {
		return this.element;
	}

	public synchronized void setElement(Element e) {
		try {
			semaphore.acquire();
			this.element = e;
			semaphore.release();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public synchronized AtomicInteger getState() {
		return this.state;
	}

	public ExecutionUnit() {
	}

	public ExecutionUnit(ExecutionEnvironment e) {
		try {
			semaphore.acquire();
			this.env = e;
			semaphore.release();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public ExecutionUnit(Element e1, ExecutionEnvironment e2) {
		this.element = e1;
		this.env = e2;
	}

	public synchronized void pause() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.state.getAndSet(3);
		semaphore.release();
	}

	public synchronized void resume() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.state.getAndSet(4);
		semaphore.release();
	}

	public void run() {
		Set<ActionEdge> nextEdges;
		ActionEdge nextEdge = null;
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);
		
		// check if behavior is empty
		graph = this.element.getSemantics().getGraph();
		if (graph.vertexSet().isEmpty())
			new MyError("Behaviour graph of " + element.getIdentification().getName() + " is empty", Utils.FATAL_ERROR)
					.print();

		// execution loop
		while (!this.state.compareAndSet(99, 99)) {
			switch (this.state.get()) {
			case 1: // running
				this.state.getAndSet(2);
				break;
			case 2: // processing
				nextEdges = graph.outgoingEdgesOf(nextVertex);		
				nextEdge = nextEdges.iterator().next(); // TODO in complete CSP
				//System.out.println("NextEdge: " + nextEdge.getAction() + "nextVertex" + nextVertex);
				executeAction(nextEdge);
				nextVertex = graph.getEdgeTarget(nextEdge);
				break;
			case 3: // paused
				break;
			case 4: // resync
				this.setNextVertex(0); // move to first vertex
				graph = this.element.getSemantics().getGraph();
				this.state.getAndSet(2);
				break;
			}
		}
	}

	public Method defineMethod(String action) {
		Class<?>[] paramTypesExternal = { Queue.class, Queue.class, ExecutionUnit.class };
		Class<?>[] paramTypesInternal = { Queue.class, ExecutionUnit.class };
		Class<?> c = null;
		Method method = null;

		try {
			c = Class.forName(element.getType().getObj().getClass().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (action.contains("i_"))
			try {
				method = c.getMethod(action, paramTypesInternal);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				method = c.getMethod(action, paramTypesExternal);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return method;
	}

	public void executeAction(ActionEdge edge) {
		Method method;
		String action;
		String actionFullName;

		// transform action -> method name
		actionFullName = edge.getAction();
		if (actionFullName.contains("i_"))
			action = (actionFullName.substring(actionFullName.indexOf('.') + 1, actionFullName.length())).trim();
		else
			action = (actionFullName.substring(actionFullName.indexOf('.') + 1, actionFullName.lastIndexOf(".")))
					.trim();
		method = defineMethod(action);

		// actually invoke method
		Queue queue1, queue2;
		ConcurrentHashMap<String, Queue> q1;
		ConcurrentHashMap<String, String> q2;
		String s1, s2;

		try {
			if (action.contains("i_")) {
				Object obj = this.element.getType().getObj();
				method.invoke(obj, edge.getQueue(), this);
			} else {
				queue1 = edge.getQueue();
				q1 = this.element.getRuntimeInfo().getEnv().getExecutionManager().getQueues();
				q2 = this.element.getRuntimeInfo().getEnv().getExecutionManager().getActionMaps();
				s1 = edge.getAction();
				s2 = q2.get(s1);
				queue2 = q1.get(s2);
				Object obj = this.element.getType().getObj();
				method.invoke(obj, queue1, queue2, this);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public ExecutionEnvironment getEnv() {
		return this.env;
	}

	public void setEnv(ExecutionEnvironment e) {
		try {
			semaphore.acquire();
			this.env = e;
			semaphore.release();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return;
	}

	public synchronized int getNextVertex() {
		return nextVertex;
	}

	public synchronized void setNextVertex(int nextVertex) {
		this.nextVertex = nextVertex;
	}

	public AtomicBoolean getPausable() {
		return pausable;
	}

	public synchronized void setPausable(AtomicBoolean pausable) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.pausable = pausable;
		semaphore.release();
	}
}
