package container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.jgrapht.DirectedGraph;

import container.csp.CSPSpecification;
import framework.basic.Element;
import framework.configuration.ActionEdge;
import framework.configuration.StructureEdge;

public class ExecutionManager {
	private volatile ExecutionEnvironment env;
	private volatile List<ExecutionUnit> executionUnits = Collections.synchronizedList(new ArrayList<ExecutionUnit>());
	private volatile ConcurrentHashMap<String, Queue> queues = new ConcurrentHashMap<String, Queue>();
	private volatile ConcurrentHashMap<String, String> actionMaps = new ConcurrentHashMap<String, String>();
	private volatile ConcurrentHashMap<String, String> eMaps = new ConcurrentHashMap<String, String>();
	private final Semaphore semaphore = new Semaphore(1, true);

	public synchronized void setExecutionUnits(List<ExecutionUnit> unit) {
		try {
			semaphore.acquire();
			this.executionUnits = unit;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ExecutionManager(ExecutionEnvironment e) {
		this.setEnv(e);
	}

	public synchronized List<ExecutionUnit> getexecutionUnits() {
		return this.executionUnits;
	}

	public ExecutionManager(ArrayList<ExecutionUnit> t) {
		this.executionUnits = t;
	}

	public List<ExecutionUnit> getThreads() {
		return this.executionUnits;
	}

	public void configure() {
		this.env.getConf().configure(this.env);
		createExecutionUnits(this.env);
		createQueues(this.env, 1);
	}

	public void execute() {
		for (ExecutionUnit unit : this.executionUnits) {
			final Thread t = new Thread(unit);
			t.start();
		}
	}

	public synchronized void createQueues(ExecutionEnvironment e, int remove) {
		DirectedGraph<Element, StructureEdge> structure = e.getConf().getStructure();
		ConcurrentHashMap<String, String> tempEmaps = new ConcurrentHashMap<String, String>();
		ConcurrentHashMap<String, String> tempActionMaps = new ConcurrentHashMap<String, String>();
		ConcurrentHashMap<String, Queue> tempQueues = new ConcurrentHashMap<String, Queue>();

		// generate maps of partners, e.g., e1 -> invoker
		for (Element vertice : structure.vertexSet()) {
			Set<StructureEdge> incomingEdges = structure.incomingEdgesOf(vertice);
			Set<StructureEdge> outgoingEdges = structure.outgoingEdgesOf(vertice);
			Iterator<StructureEdge> itIncoming = incomingEdges.iterator();
			Iterator<StructureEdge> itOutgoing = outgoingEdges.iterator();
			int count = 1;

			while (itIncoming.hasNext()) {
				StructureEdge tempEdge = itIncoming.next();
				String tempNameS = ((Element) tempEdge.getS()).getIdentification().getName();
				String tempNameT = ((Element) tempEdge.getT()).getIdentification().getName();
				tempEmaps.put(tempNameT + ".e" + count, tempNameS);
				count++;
			}
			while (itOutgoing.hasNext()) {
				StructureEdge tempEdge = itOutgoing.next();
				String tempNameS = ((Element) tempEdge.getS()).getIdentification().getName();
				String tempNameT = ((Element) tempEdge.getT()).getIdentification().getName();
				tempEmaps.put(tempNameS + ".e" + count, tempNameT);
				count++;
			}
		}

		// generate maps of actions, e.g., client.invP.e1 -> client.invP.t0 and
		// create queues, e.g., client.invP.t0 -> queue1
		for (Element vertice : structure.vertexSet()) {
			for (ActionEdge edgeAction : vertice.getSemantics().getGraph().edgeSet()) {
				String action = edgeAction.getAction();
				if (!action.contains("i_")) {
					String key = action.substring(0, action.indexOf(".")) + "."
							+ action.substring(action.lastIndexOf(".") + 1, action.length());

					String to = action.substring(action.lastIndexOf(".") + 1, action.length());
					String coreAction = action.substring(action.indexOf(".") + 1, action.lastIndexOf("."));

					if (coreAction.contains("invR")) {
						String newTo = tempEmaps.get(key);
						String newAction = action.replace(to, newTo);

						String fromPair = newAction.substring(newAction.lastIndexOf(".") + 1, newAction.length());
						String toPair = newAction.substring(0, newAction.indexOf("."));
						String actionPair = fromPair + ".invP." + toPair;

						tempActionMaps.put(action, newAction);
						Queue queue1 = new Queue();
						if (tempQueues.containsKey(actionPair))
							tempQueues.put(newAction, tempQueues.get(actionPair));
						else
							tempQueues.put(newAction, queue1);
					}

					if (coreAction.contains("invP")) {
						String newTo = tempEmaps.get(key);
						String newAction = action.replace(to, newTo);

						String fromPair = newAction.substring(newAction.lastIndexOf(".") + 1, newAction.length());
						String toPair = newAction.substring(0, newAction.indexOf("."));
						String actionPair = fromPair + ".invR." + toPair;

						tempActionMaps.put(action, newAction);
						Queue queue1 = new Queue();
						if (tempQueues.containsKey(actionPair)) {
							tempQueues.put(newAction, tempQueues.get(actionPair));
						} else
							tempQueues.put(newAction, queue1);
					}

					if (coreAction.contains("terR")) {
						String newTo = tempEmaps.get(key);
						String newAction = action.replace(to, newTo);

						String fromPair = newAction.substring(newAction.lastIndexOf(".") + 1, newAction.length());
						String toPair = newAction.substring(0, newAction.indexOf("."));
						String actionPair = fromPair + ".terP." + toPair;

						tempActionMaps.put(action, newAction);
						Queue queue1 = new Queue();
						if (tempQueues.containsKey(actionPair)) {
							tempQueues.put(newAction, tempQueues.get(actionPair));
						} else
							tempQueues.put(newAction, queue1);
					}
					if (coreAction.contains("terP")) {
						String newTo = tempEmaps.get(key);
						String newAction = action.replace(to, newTo);

						String fromPair = newAction.substring(newAction.lastIndexOf(".") + 1, newAction.length());
						String toPair = newAction.substring(0, newAction.indexOf("."));
						String actionPair = fromPair + ".terR." + toPair;

						tempActionMaps.put(action, newAction);
						Queue queue1 = new Queue();
						if (tempQueues.containsKey(actionPair)) {
							tempQueues.put(newAction, tempQueues.get(actionPair));
						} else
							tempQueues.put(newAction, queue1);
					}
				}
			}
		}

		e.getExecutionManager().setQueues(tempQueues);
		e.getExecutionManager().setActionMaps(tempActionMaps);
		e.getExecutionManager().seteMaps(tempEmaps);
	}

	public synchronized boolean hasComponent(String elementName) {
		boolean foundComponent = false;

		for (ExecutionUnit executionUnit : executionUnits) {
			if (executionUnit.getElement().getIdentification().getName().contains(elementName)) {
				foundComponent = true;
				break;
			}
		}
		return foundComponent;
	}

	public synchronized void createExecutionUnits(ExecutionEnvironment env) {
		ExecutionUnit newExecutionUnit;
		Iterator<Element> itVertices = env.getConf().getStructure().vertexSet().iterator();

		while (itVertices.hasNext()) {
			newExecutionUnit = new ExecutionUnit((Element) itVertices.next(), env);
			this.executionUnits.add(newExecutionUnit);
		}
	}

	public synchronized void pause(ExecutionEnvironment env, String element) {		
		for (ExecutionUnit unit : env.getExecutionManager().getexecutionUnits()) {
			if (unit.getElement().getIdentification().getName().contains(element) || element.contains("all"))
				unit.pause();
		}
	}
	
	public synchronized void resume(ExecutionEnvironment env, String element) {
		for (ExecutionUnit unit : env.getExecutionManager().getexecutionUnits()) {
			if (unit.getElement().getIdentification().getName().contains(element) || element.contains("all")) {
				unit.resume();
			}
		}
	}

	public synchronized ExecutionEnvironment getEnv() {
		return env;
	}

	public synchronized void setEnv(ExecutionEnvironment env) {
		try {
			semaphore.acquire();
			this.env = env;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized ConcurrentHashMap<String, Queue> getQueues() {
		return queues;
	}

	public synchronized void setQueues(ConcurrentHashMap<String, Queue> queues) {
		try {
			semaphore.acquire();
			this.queues = queues;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized ConcurrentHashMap<String, String> getActionMaps() {
		return actionMaps;
	}

	public synchronized void setActionMaps(ConcurrentHashMap<String, String> actionMaps) {
		try {
			semaphore.acquire();
			this.actionMaps = actionMaps;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized ConcurrentHashMap<String, String> geteMaps() {
		return eMaps;
	}

	public synchronized void seteMaps(ConcurrentHashMap<String, String> eMaps) {
		try {
			semaphore.acquire();
			this.eMaps = eMaps;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
