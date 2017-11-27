package framework.configuration;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.kohsuke.graphviz.Edge;
import org.kohsuke.graphviz.Graph;

import container.ExecutionEnvironment;
import container.Queue;
import container.graph.Action;
import container.graph.NodeGraph;
import framework.basic.Element;
import framework.basic.RuntimeInfo;
import framework.component.Component;
import framework.connector.Connector;
import middleware.services.naming.CNamingServer;
import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.DeadlockFreeAssertion;
import uk.ac.ox.cs.fdr.Event;
import uk.ac.ox.cs.fdr.Machine;
import uk.ac.ox.cs.fdr.Node;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.Transition;
import uk.ac.ox.cs.fdr.TransitionList;
import utils.Utils;

public class Configuration {
	private String name;
	private DirectedGraph<Element, StructureEdge> structure;
	private boolean isAdaptive;
	static int nodeID = 0;
	static int destinationID = 0;
	
	public Configuration(String n, boolean isAdaptive) {
		this.name = n;
		this.setStructure(new DefaultDirectedGraph<>(StructureEdge.class));
		this.isAdaptive = isAdaptive;
	}

	public void check() {
		structuralChecking(); // TODO
	}

	public void structuralChecking() {
		// TODO
		return;
	}

	public void connect(Component c1, Connector t, Component c2) {
		connectStructure(c1, t);
		connectStructure(t, c2);
	}

	public void connectStructure(Element from, Element to) {
		this.structure.addVertex(from);
		this.structure.addVertex(to);
		this.structure.addEdge(from, to);
	}

	public void configure(ExecutionEnvironment env) {
		Element from, to;
		DirectedGraph<Integer, ActionEdge> fromBehaviourGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		DirectedGraph<Integer, ActionEdge> toBehaviourGraph = new DefaultDirectedGraph<>(ActionEdge.class);
				
		// -------- create runtime graphs
		for (StructureEdge edgeTemp : env.getConf().getStructure().edgeSet()) {
			from = (Element) edgeTemp.getS();			
			to = (Element) edgeTemp.getT();
			
			fromBehaviourGraph = createRuntimeGraph(from);		
			from.getSemantics().setGraph(fromBehaviourGraph);
			from.setRuntimeInfo(new RuntimeInfo(env));
			toBehaviourGraph = createRuntimeGraph(to);
			to.setRuntimeInfo(new RuntimeInfo(env));
			to.getSemantics().setGraph(toBehaviourGraph);
		}
	}
	
	public void OLDconfigure(ExecutionEnvironment env) {
		Element from, to;
		HashMap<String, HashMap<String, String>> relabelMap = new HashMap<String, HashMap<String, String>>();
		DirectedGraph<Integer, ActionEdge> fromBehaviourGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		DirectedGraph<Integer, ActionEdge> toBehaviourGraph = new DefaultDirectedGraph<>(ActionEdge.class);

		// ---------- create relabel maps
		for (StructureEdge edge : this.getStructure().edgeSet()) {
			from = this.getStructure().getEdgeSource(edge);
			to = this.getStructure().getEdgeTarget(edge);
			HashMap<String, String> tempMapFrom = new HashMap<String, String>();
			HashMap<String, String> tempMapTo = new HashMap<String, String>();

			// from
			if (relabelMap.containsKey(from.getIdentification().getName()))
				tempMapFrom = relabelMap.get(from.getIdentification().getName());

			boolean endFrom = false;
			int partnerNumber = 1;
			String keyFrom = "";
			String valueFrom = "";
			while (!endFrom) {
				keyFrom = "e" + partnerNumber;
				if (tempMapFrom.containsKey(keyFrom)) {
					partnerNumber++;
				} else {
					if (tempMapFrom.containsValue(from.getIdentification().getName()))
						valueFrom = to.getIdentification().getName();
					else
						valueFrom = from.getIdentification().getName();
					endFrom = true;
				}
			}
			tempMapFrom.put(keyFrom, valueFrom);
			relabelMap.put(from.getIdentification().getName(), tempMapFrom);

			// to
			if (relabelMap.containsKey(to.getIdentification().getName()))
				tempMapTo = relabelMap.get(to.getIdentification().getName());

			boolean endTo = false;
			partnerNumber = 1;
			String keyTo = "";
			String valueTo = valueFrom;
			while (!endTo) {
				keyTo = "e" + partnerNumber;
				if (tempMapTo.containsKey(keyTo)) {
					partnerNumber++;
				} else {
					if (tempMapTo.containsValue(valueTo))
						valueTo = to.getIdentification().getName();
					endTo = true;
				}
			}
			tempMapTo.put(keyTo, valueTo);
			relabelMap.put(to.getIdentification().getName(), tempMapTo);
		}

		// update relabel maps of elements
		HashMap<String, String> tempHashMap = new HashMap<String, String>();
		for (StructureEdge edge : this.getStructure().edgeSet()) {
			tempHashMap = relabelMap.get(((Element) edge.getS()).getIdentification().getName());
			((Element) edge.getS()).getSemantics().getRuntimeBehaviour().setLabelsMap(tempHashMap);
			tempHashMap = relabelMap.get(((Element) edge.getT()).getIdentification().getName());
			((Element) edge.getT()).getSemantics().getRuntimeBehaviour().setLabelsMap(tempHashMap);
		}

		// -------- create runtime graphs
		for (StructureEdge edgeTemp : env.getConf().getStructure().edgeSet()) {
			from = (Element) edgeTemp.getS();
			to = (Element) edgeTemp.getT();

			fromBehaviourGraph = createRuntimeGraph(from);
			from.getSemantics().setGraph(fromBehaviourGraph);
			toBehaviourGraph = createRuntimeGraph(to);
			to.getSemantics().setGraph(toBehaviourGraph);
		}
	}

	public void replaceElement(String oldElementName, String newElementName, String newElementType) {
		Element newElement = null;

		try {
			Class<?> newElementClass;
			String newElementClassName = Utils.CLASS_PACKAGE + "." + newElementType;
			newElementClass = Class.forName(newElementClassName);
			newElement = (Element) newElementClass.newInstance();
			newElement.getIdentification().setName(newElementName);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// replace vertices
		DirectedGraph<Element, StructureEdge> tempStructure = new DefaultDirectedGraph<>(StructureEdge.class);
		Iterator<Element> vertices = this.getStructure().vertexSet().iterator();
		while (vertices.hasNext()) {
			Element element = vertices.next();
			if (element.getIdentification().getName().contains(oldElementName))
				tempStructure.addVertex(newElement);
			else
				tempStructure.addVertex(element);
		}

		// replace edges
		Iterator<StructureEdge> edges = this.getStructure().edgeSet().iterator();
		while (edges.hasNext()) {
			StructureEdge edge = edges.next();
			Element from = (Element) edge.getS();
			Element to = (Element) edge.getT();
			if (from.getIdentification().getName().contains(oldElementName))
				tempStructure.addEdge(newElement, to);
			else if (to.getIdentification().getName().contains(oldElementName))
				tempStructure.addEdge(from, newElement);
			else
				tempStructure.addEdge(from, to);
		}
		this.setStructure(tempStructure);
	}
	
	public Graph createRuntime(Element e) {
		Graph runtimeGraph = new Graph();
		
		return runtimeGraph;
	}
	
	public DirectedGraph<Integer, ActionEdge> createRuntimeGraph(Element e) {
		//DirectedGraph<Integer, ActionEdge> runtimeGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		DirectedGraph<Integer, ActionEdge> runtimeGraph = new DirectedMultigraph<>(ActionEdge.class);
		
		nodeID = 0;
		destinationID = 0;
		
		Machine machine;
		ArrayList<Action> actions = new ArrayList<>();
		ArrayList<NodeGraph> nodegraphs = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		Session session = new Session();
		session.loadFile(Utils.CSP_DIR + "/" + "conf.csp");
		
		for(Assertion assertion: session.assertions()) {	
			assertion.execute(null);
			
			if(assertion.toString().contains(e.getIdentification().getName().toUpperCase())) {
				
				if(assertion instanceof DeadlockFreeAssertion) {
					machine = ((DeadlockFreeAssertion) assertion).machine();
					Node node = machine.rootNode();
					
					for(Transition transition: machine.transitions(node)) {
						describeTransitions(e, runtimeGraph, machine, session, node, transition, nodes, nodegraphs, actions, true);
					}
					
					createGraph(machine, session, actions);
				}
			}
		}
				
		//adjusts action's queues to keep action, pre*action and pos*action the
		// same
		
		Iterator<ActionEdge> it = runtimeGraph.edgeSet().iterator();
		
		String previousAction = "XXXXX";
		String currentAction  = "XXXXX";
		Queue tempQueue = null;
		while (it.hasNext()) {
			ActionEdge edge = it.next();
			String a = edge.getAction();

			previousAction = currentAction;
			if (a.contains("i_")) {
				currentAction = a.substring(a.indexOf(".") + 1, a.length()).toLowerCase();
			}else{
				currentAction = a.substring(a.indexOf(".") + 1, a.lastIndexOf(".")).toLowerCase();
			}

			if (currentAction.contains("i_pre"))
				tempQueue = edge.getQueue();
			else if (currentAction.contains("i_pos"))
				edge.setQueue(tempQueue);
			else if (previousAction.contains(currentAction))
				edge.setQueue(tempQueue);
			else
				tempQueue = edge.getQueue();
		}
		return runtimeGraph;
	}
	
	private void createGraph(Machine machine, Session session, ArrayList<Action> actions) {
		
		Graph g = new Graph();
		
		g.id(session.machineName(machine).toString());

		for (Action action : actions) {
		
			String event = action.getEvent().toString();

			Edge e = new Edge(new org.kohsuke.graphviz.Node().id(String.valueOf(action.getFrom().getId())),
					new org.kohsuke.graphviz.Node()
							.id(String.valueOf(action.getTo().getId()) + " [ label=\"" + event + "\"];"));
			g.edge(e);
		}
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(Utils.CSP_DIR + "/" + session.machineName(machine).toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		g.writeTo(out);
	}

	private void describeTransitions(Element e, DirectedGraph<Integer, ActionEdge> runtimeGraph, Machine machine, Session session, Node node, Transition transition,
			ArrayList<Node> nodes, ArrayList<NodeGraph> nodegraphs, ArrayList<Action> actions, boolean recurse) {
		
		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();
		
		nodeID = nodeID + 1;
		destinationID = nodeID + 1;

		if (node.equals(machine.rootNode())) {
			nodeID = 0;
			destinationID = nodeID + 1;
		}

		if (destination.equals(machine.rootNode())) {
			destinationID = 0;
		}
		
		//System.out.println(node + " -> " + event + " -> " + destination);
		
		TransitionList childList = machine.transitions(destination);
		if (childList.isEmpty()) {
			recurse = false;
		}
		
		if(!nodes.contains(node)) {
			nodes.add(node);
		}

		if (nodes.contains(destination)) {
			recurse = false;
		}
		
		NodeGraph src = new NodeGraph(nodeID, node);
		if (!nodes.contains(node)) {
			nodes.add(node);
			nodegraphs.add(src);
		}

		NodeGraph dest = new NodeGraph(destinationID, destination);
		if (!nodes.contains(destination)) {
			nodes.add(destination);
			nodegraphs.add(dest);
		}

		for (NodeGraph nodegraph : nodegraphs) {
			if (nodegraph.getNode().equals(node)) {
				src = nodegraph;
			}

			if (nodegraph.getNode().equals(destination)) {
				dest = nodegraph;
				
			}
		}
		
		actions.add(new Action(src, event, dest));

		//System.out.println(src.getId() + " -> " + e.getIdentification().getName() + "." + event + " -> " + dest.getId());
		
		runtimeGraph.addVertex(src.getId());
		runtimeGraph.addVertex(dest.getId());
		runtimeGraph.addEdge(src.getId(), dest.getId(), new ActionEdge(e.getIdentification().getName() + "." + event, new Queue()));
			
		if (recurse) {
			for (Transition child : machine.transitions(destination)) {
				describeTransitions(e, runtimeGraph, machine, session, destination, child, nodes, nodegraphs, actions, true);
			}
		}
	}

	public DirectedGraph<Integer, ActionEdge> OLDcreateRuntimeGraph(Element e) {
		DirectedGraph<Integer, ActionEdge> runtimeGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		int count = 0;
		boolean hasAction = true;
		String nextAction;
		String remainingBehaviour = e.getSemantics().getStandardBehaviour().getActions();

		while (hasAction) {
			nextAction = (e.getIdentification().getName() + "."
					+ remainingBehaviour.substring(0, remainingBehaviour.indexOf(Utils.PREFIX_ACTION))).trim();
			runtimeGraph.addVertex(count);
			runtimeGraph.addVertex(count + 1);
			runtimeGraph.addEdge(count, count + 1, new ActionEdge(nextAction, new Queue()));
			
			remainingBehaviour = remainingBehaviour.substring(
					remainingBehaviour.indexOf(Utils.PREFIX_ACTION) + Utils.PREFIX_ACTION.length(),
					remainingBehaviour.length());
			if (remainingBehaviour.indexOf(Utils.PREFIX_ACTION) == -1) {
				hasAction = false;
				runtimeGraph.addEdge(count + 1, 0, new ActionEdge(
						(e.getIdentification().getName() + "." + remainingBehaviour).trim(), new Queue())); // last
			} else {
				count++;
				nextAction = remainingBehaviour.substring(0, remainingBehaviour.indexOf(Utils.PREFIX_ACTION)).trim();
			}
		}

		// adjusts action's queues to keep action, pre*action and pos*action the
		// same
		Iterator<ActionEdge> it = runtimeGraph.edgeSet().iterator();

		while (it.hasNext()) {
			ActionEdge edge = it.next();
			String edgeAction = edge.getAction();
			if (edgeAction.contains("i_PreInvR") || edgeAction.contains("i_PreTerR") || edgeAction.contains("i_PreInvP")
					|| edgeAction.contains("i_PreTerP")) {
				ActionEdge nextEdge = it.next();
				edge.setQueue(nextEdge.getQueue());
			} else if (edgeAction.contains("invR") || edgeAction.contains("terR") || edgeAction.contains("invP")
					|| edgeAction.contains("terP")) {
				if (it.hasNext()) {
					ActionEdge nextEdge = it.next();
					String nextEdgeAction = nextEdge.getAction();

					if (nextEdgeAction.contains("i_PosInvR") || nextEdgeAction.contains("i_PosTerR")
							|| nextEdgeAction.contains("i_PosInvP") || nextEdgeAction.contains("i_PosTerP")) {
						nextEdge.setQueue(edge.getQueue());
					}
				}
			}
		}
		return runtimeGraph;
	}

	public void printStructure() {
		Iterator<Element> itElements;
		Iterator<StructureEdge> itEdges;
		Element element;
		StructureEdge edge;

		// print configuration information
		System.out.println("Configuration: " + this.name);

		// print components
		// elements = this.s.vertexSet();

		System.out.print("Components: ");
		itElements = this.structure.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Component)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print connectors
		System.out.print("\nConnectors: ");
		itElements = this.structure.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Connector)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print attachments
		System.out.println("\nAttachments: ");
		itEdges = this.structure.edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			System.out.print("(" + ((Element) edge.getS()).getIdentification().getName() + ","
					+ ((Element) edge.getT()).getIdentification().getName() + ")" + "\n");
		}
	}

	public String getConfName() {
		return this.name;
	}

	public boolean hasNamingService() {
		boolean foundComponent = false;
		Iterator<Element> itVertices = this.structure.vertexSet().iterator();
		Element componentTemp = null;

		while (!foundComponent && itVertices.hasNext()) {
			componentTemp = itVertices.next();
			if (componentTemp instanceof CNamingServer)
				foundComponent = true;
		}
		return foundComponent;
	}

	public boolean hasElement(String elementName) {
		boolean foundElement = false;
		
		Iterator<Element> itVertices = this.structure.vertexSet().iterator();
		Element elementTemp;

		while (!foundElement && itVertices.hasNext()) {
			elementTemp = itVertices.next();
			if (elementTemp.getIdentification().getName().trim().contains(elementName))
				foundElement = true;
		}
		return foundElement;
	}

	public boolean isAdaptive() {
		return isAdaptive;
	}

	public void setAdaptive(boolean isAdaptive) {
		this.isAdaptive = isAdaptive;
	}

	public void setStructure(DirectedGraph<Element, StructureEdge> structure) {
		this.structure = structure;
	}

	public DirectedGraph<Element, StructureEdge> getStructure() {
		return structure;
	}
}
