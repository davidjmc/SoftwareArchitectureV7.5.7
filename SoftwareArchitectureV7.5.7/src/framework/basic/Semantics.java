package framework.basic;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import framework.configuration.ActionEdge;
import utils.MyError;
import utils.Utils;

public class Semantics {
	private Behaviour standardBehaviour;
	private Behaviour runtimeBehaviour;
	private DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);
	private final Semaphore semaphore = new Semaphore(1, true);

	public Semantics() {
		this.setStandardBehaviour(new Behaviour());
		this.setRuntimeBehaviour(new Behaviour());
	}

	public Semantics(Behaviour s) {
		this.setStandardBehaviour(s);
		this.setRuntimeBehaviour(s);
		this.setGraph(this.createGraph());
	}

	public Semantics(String actions) {
		if (checkSyntax(actions)) {
			this.setStandardBehaviour(actions.replace(" ", ""));
			this.setRuntimeBehaviour(actions.replace(" ", ""));
			this.setGraph(this.createGraph());
		} else
			new MyError("Behaviour has a syntax error!!", Utils.FATAL_ERROR).print();
	}

	public boolean checkSyntax(String behaviour) {
		boolean r = true;
		String[] actions = behaviour.split("\\->|\\[]");
		//String[] actions = behaviour.split("->");
		Set<String> possibleActions = new TreeSet<String>();

		for (String a : Utils.SET_OF_ACTIONS)
			possibleActions.add(a);

		// behaviour is empty
		if (behaviour.isEmpty())
			r = false;

		// check if the actions have the correct name
		for (String a : actions) {
			if (a.contains("."))
				a = a.substring(0, a.indexOf("."));

			if (!possibleActions.contains(a.trim()))
				r = false;
		}
		return r;
	}

	public DirectedGraph<Integer, ActionEdge> createGraph() {
		String exp = this.getRuntimeBehaviour().getActions();
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);
		String[] actions = exp.split(Utils.PREFIX_ACTION);

		// add vertices
		for (int idx = 0; idx < actions.length; idx++) {
			graph.addVertex(idx);
		}

		// add edges
		for (int idx = 0; idx < actions.length - 1; idx++) {
			int source = idx;
			int target = idx + 1;
			graph.addEdge(source, target, new ActionEdge(actions[idx]));
		}
		graph.addEdge(actions.length - 1, 0, new ActionEdge(actions[actions.length - 1]));
		return graph;
	}

	public void relabelGraphActions(Element element) {
		for (ActionEdge edge : this.getGraph().edgeSet()) {
			{
				String action = edge.getAction();
				if (action.contains("i_Pre")) {
					// String mainAction = action.substring(5, 6).toLowerCase()
					// + action.substring(6, action.length());
					String mainAction = action.substring(5, 6).toLowerCase() + action.substring(6, 8);
					int nextVertex = element.getSemantics().getGraph().getEdgeTarget(edge);
					Set<ActionEdge> nextEdges = element.getSemantics().getGraph().outgoingEdgesOf(nextVertex);
					for (ActionEdge nextEdge : nextEdges) {
						if (nextEdge.getAction().contains(mainAction)) {
							String oldLabel = nextEdge.getAction().substring(nextEdge.getAction().indexOf("."),
									nextEdge.getAction().length());
							String newLabel = element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
							String newAction = action + newLabel;
							edge.setAction(newAction);
						}
					}
				} else if (action.contains("i_Pos")) {
					// String mainAction = action.substring(5, 6).toLowerCase()
					// + action.substring(6, action.length());
					String mainAction = action.substring(5, 6).toLowerCase() + action.substring(6, 8);
					int previousVertex = element.getSemantics().getGraph().getEdgeSource(edge);
					Set<ActionEdge> previousEdges = element.getSemantics().getGraph().incomingEdgesOf(previousVertex);
					for (ActionEdge previousEdge : previousEdges) {
						if (previousEdge.getAction().contains(mainAction)) {
							String newLabel = previousEdge.getAction().substring(previousEdge.getAction().indexOf("."),
									previousEdge.getAction().length());
							String newAction = action + newLabel;
							edge.setAction(newAction);
						}
					}

				} else if (!action.contains("i_")) {
					String oldLabel = action.substring(action.indexOf("."), action.length());
					String newLabel = element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
					edge.setAction(action.replace(oldLabel, newLabel));
				}
			}
		}
	}

	public Behaviour getStandardBehaviour() {
		return standardBehaviour;
	}

	public void setStandardBehaviour(String standardBehaviour) {
		this.standardBehaviour = new Behaviour(standardBehaviour);
	}

	public void setStandardBehaviour(Behaviour standardBehaviour) {
		this.standardBehaviour = standardBehaviour;
	}

	public Behaviour getRuntimeBehaviour() {
		return runtimeBehaviour;
	}

	public void setRuntimeBehaviour(Behaviour runtimeBehaviour) {
		this.runtimeBehaviour = runtimeBehaviour;
	}

	public void setRuntimeBehaviour(String runtimeBehaviour) {
		this.runtimeBehaviour = new Behaviour(runtimeBehaviour);
	}

	public synchronized DirectedGraph<Integer, ActionEdge> getGraph() {
		return this.graph;
	}

	public synchronized void setGraph(DirectedGraph<Integer, ActionEdge> graph) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.graph = graph;
		semaphore.release();
	}
}
