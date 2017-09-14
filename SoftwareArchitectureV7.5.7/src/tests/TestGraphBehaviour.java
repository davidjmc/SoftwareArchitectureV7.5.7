package tests;

import java.util.Random;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import utils.Utils;

class ActionEdge {
	private String action;

	public ActionEdge(String a) {
		this.action = a;
	}

	public void setAction(String a) {
		this.action = a;
	}

	public String getAction() {
		return this.action;
	}
}

public class TestGraphBehaviour {

	interface Action {
		void action();
	}

	public static void main(String[] args) {
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);
		 String behaviour = "invP" + Utils.PREFIX_ACTION + 
				 "invR" + Utils.PREFIX_ACTION+
				 "terR" + Utils.PREFIX_ACTION+
				 "terP";
		 
		graph = expToGraph(behaviour); 

		//graph = createGraph();
		executeGraph(graph);
	}

	public static void executeGraph(DirectedGraph<Integer, ActionEdge> graph) {
		Set<ActionEdge> nextEdges;
		int nextVertex = 0;
		ActionEdge nextEdge = null;

		while (true) {
			nextEdges = graph.outgoingEdgesOf(nextVertex);
			if (nextEdges.size() > 1)
				nextEdge = selectEdge(nextEdges);
			else if (nextEdges.size() == 1) {
				nextEdge = nextEdges.iterator().next();
			}
			execute(nextEdge.getAction());
			nextVertex = graph.getEdgeTarget(nextEdge);
		}
	}

	public static ActionEdge selectEdge(Set<ActionEdge> edges) {
		Object[] x = edges.toArray();

		return (ActionEdge) x[new Random().nextInt(edges.size())];
	}

	public static DirectedGraph<Integer, ActionEdge> createGraph() {
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);

		for (int idx = 0; idx < 8; idx++)
			graph.addVertex(idx);

		graph.addEdge(0, 1, new ActionEdge("i_PreInvR"));
		graph.addEdge(1, 2, new ActionEdge("invR.e1"));
		graph.addEdge(2, 3, new ActionEdge("i_PosInvR"));
		graph.addEdge(3, 4, new ActionEdge("i_PreTerR"));
		graph.addEdge(4, 5, new ActionEdge("terR.e1"));
		graph.addEdge(5, 0, new ActionEdge("i_PosTerR"));
		//graph.addEdge(0, 7, new ActionEdge("stop"));

		return graph;
	}

	public static DirectedGraph<Integer, ActionEdge> expToGraph(String exp) {
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);
		String [] actions = exp.split(Utils.PREFIX_ACTION);

		// add vertices
		for(int idx = 0; idx < actions.length; idx++){
			graph.addVertex(idx);
		}
		
		// add edges
		for(int idx = 0; idx < actions.length - 1; idx++){
			int source = idx;
			int target = idx +1;
			graph.addEdge(source, target, new ActionEdge(actions[idx]));
		}
		graph.addEdge(actions.length-1, 0,new ActionEdge(actions[actions.length-1]));
		return graph;
	}

	public static void execute(String s, Action a) {
		a.action();
	}

	public static void execute(String a) {
		if (a.contains("i_"))
			execute(a, () -> System.out.println(a));
		else if (a.contains("stop"))
			execute(a, () -> System.exit(0));
		else
			execute(a, () -> System.out.println(a));
	}
}
