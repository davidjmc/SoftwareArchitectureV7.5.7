package framework.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

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
	//ArrayList<NodeGraph> nodegraphs = new ArrayList<>();
	//static ArrayList<container.graph.Action> actions = new ArrayList<>();

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

	public DirectedGraph<Integer, ActionEdge> createRuntimeGraph(Element e) {
		DirectedGraph<Integer, ActionEdge> runtimeGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		int count = 0;
		boolean hasAction = true;
		ArrayList<Action> actions = new ArrayList<>();
		ArrayList<NodeGraph> nodegraphs = new ArrayList<>();
		ArrayList<String> choices;
		String nextAction;
		String action;
		String act;
		String[] expressions = null;
		Machine machine;
		ArrayList<Node> nodes = new ArrayList<Node>();
		//String remainingBehaviour = e.getSemantics().getStandardBehaviour().getActions();
		
		Session session = new Session();
		session.loadFile(Utils.CSP_DIR + "/" + "conf.csp");
		
		for(Assertion assertion: session.assertions()) {
			nodeID = 0;
			destinationID = 0;
			assertion.execute(null);
			
			if(assertion.toString().contains(e.getIdentification().getName().toUpperCase())) {
				
				if(assertion instanceof DeadlockFreeAssertion) {
					machine = ((DeadlockFreeAssertion) assertion).machine();
					Node node = machine.rootNode();
					
					for(Transition transition: machine.transitions(node)) {
						describeTransitions(e, runtimeGraph, machine, session, node, transition, nodes, nodegraphs, actions, true);
					}
				}
			}
			runtimeGraph = createGraph(e, actions);
		}
		
		/*if(remainingBehaviour.contains("[]")) {
			int orig = 0;
			int dest = 1;
			int c = 1;
			boolean t = true;
			
			actions = new ArrayList<>(
					Arrays.asList(remainingBehaviour.split(Utils.PREFIX_ACTION)));
			
			while(t) {
				
				action = (e.getIdentification().getName() + "." + actions.get(orig)).trim();
				
				if(actions.get(dest).contains("[]")) {
					ArrayList<String> acts = new ArrayList<>(Arrays.asList(actions.get(dest).split("\\[]")));
					
					for(int j=0; j<acts.size(); j++) {
						//act = (e.getIdentification().getName() + "." + acts.get(j)).trim();
						runtimeGraph.addVertex(orig);
						runtimeGraph.addVertex(dest);
						runtimeGraph.addEdge(orig, dest, new ActionEdge(action, new Queue()));
						dest = dest + 1;
					} 
				} else {
					runtimeGraph.addVertex(orig);
					runtimeGraph.addVertex(dest);
					runtimeGraph.addEdge(orig, dest, new ActionEdge(action, new Queue()));
				}
				
				orig = orig + 1;
				dest = dest + 1;
				c = c + 1;
				
				if(c == actions.size()) {
					dest = 0;
					
					action = (e.getIdentification().getName() + "." + actions.get(orig)).trim();
					
					if(action.contains("[]")) {
						
						ArrayList<String> acts = new ArrayList<>(Arrays.asList(actions.get(orig).split("\\[]")));
						for(int j=0; j<acts.size(); j++) {
							act = (e.getIdentification().getName() + "." + acts.get(j)).trim();
							runtimeGraph.addVertex(orig);
							runtimeGraph.addVertex(dest);
							runtimeGraph.addEdge(orig, dest, new ActionEdge(act, new Queue()));
							orig = orig + 1;
						}
						
					}
					t = false;
				}
			}
			
			for(int i=0; i<(actions.size()-1); i++) {
				int src = i;
				int des = i+1;
				
				if(actions.get(des).contains("[]")) {
					ArrayList<String> acts = new ArrayList<>(Arrays.asList(actions.get(des).split("\\[]")));
					
					for(int j=0; j<acts.size(); j++) {
						act = (e.getIdentification().getName() + "." + acts.get(j)).trim();
						runtimeGraph.addVertex(src);
						runtimeGraph.addVertex(des);
						runtimeGraph.addEdge(src, des, new ActionEdge(act, new Queue()));
					}
				} else {
					action = (e.getIdentification().getName() + "." + actions.get(src)).trim();
					runtimeGraph.addVertex(src);
					runtimeGraph.addVertex(des);
					runtimeGraph.addEdge(src, des, new ActionEdge(action, new Queue()));
				}
				
				if(i+1 == (actions.size()-1)) {
					src = i+1;
					des = 0;
					
					if(actions.get(src).contains("[]")) {
						ArrayList<String> acts = new ArrayList<>(Arrays.asList(actions.get(src).split("\\[]")));
						for(int j=0; j<acts.size(); j++) {
							act = (e.getIdentification().getName() + "." + acts.get(j)).trim();
							runtimeGraph.addVertex(src);
							runtimeGraph.addVertex(des);
							runtimeGraph.addEdge(src, des, new ActionEdge(act, new Queue()));
						}
					} else {
						runtimeGraph.addEdge(src, des, new ActionEdge((e.getIdentification().getName() + 
								"." + actions.get(src)).trim(), new Queue()));
					}
				}
			}	
		} else {
			actions = new ArrayList<>(
					Arrays.asList(remainingBehaviour.split(Utils.PREFIX_ACTION)));
			
			for(int i=0; i<(actions.size()-1); i++) {
				int src = i;
				int des = i+1;
				action = (e.getIdentification().getName() + "." + actions.get(src)).trim();
				
				runtimeGraph.addVertex(src);
				runtimeGraph.addVertex(des);
				runtimeGraph.addEdge(src, des, new ActionEdge(action, new Queue()));			
				
				if(i+1 == (actions.size()-1)) {
					src = i+1;
					des = 0;
					runtimeGraph.addEdge(src, des, new ActionEdge((e.getIdentification().getName() + "." + actions.get(src)).trim(), new Queue()));
				}
			}
		}*/
		
		/*String[] actions = remainingBehaviour.split(Utils.PREFIX_ACTION);
		System.out.println("Quant actions: " + actions.length);
		
		
		int quant = actions.length - 1;
		
		for(int i=0; i<quant; i++) {
			int src = i;
			int des = i+1;
			action = (e.getIdentification().getName() + "." + actions[src]).trim();
			
			runtimeGraph.addVertex(src);
			runtimeGraph.addVertex(des);
			runtimeGraph.addEdge(src, des, new ActionEdge(action, new Queue()));
			System.out.println("src: " + src + " Action: " + action + " des: " + des);
			
			if(i+1 == quant) {
				src = i+1;
				des = 0;
				
				runtimeGraph.addEdge(src, des, new ActionEdge((e.getIdentification().getName() + "." + actions[src]).trim(), new Queue()));
				System.out.println("src: " + src + " Action: " + action + " des: " + des);
			} 
		}*/
			
/*	while (hasAction) {
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

		}*/

		// adjusts action's queues to keep action, pre*action and pos*action the
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

	private DirectedGraph<Integer, ActionEdge> createGraph(Element e, ArrayList<Action> actions) {
		DirectedGraph<Integer, ActionEdge> runtimeGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		
		for(Action action: actions) {
			
			runtimeGraph.addVertex(action.getFrom().getId());
			runtimeGraph.addVertex(action.getTo().getId());
			runtimeGraph.addEdge(action.getFrom().getId(), action.getTo().getId(), 
					new ActionEdge(e.getIdentification().getName() + "." + action.getEvent().toString(), new Queue()));
			
		}

		return runtimeGraph;
	}

	private void describeTransitions(Element e, DirectedGraph<Integer, ActionEdge> runtimeGraph, Machine machine, Session session, Node node, Transition transition,
			ArrayList<Node> nodes, ArrayList<NodeGraph> nodegraphs, ArrayList<Action> actions, boolean recurse) {
		
		
		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();
		
		int current = destinationID;
		nodeID = nodeID + 1;
		destinationID = nodeID + 1;

		if (node.equals(machine.rootNode())) {
			nodeID = 0;
			destinationID = nodeID + 1;
		}

		if (destination.equals(machine.rootNode())) {
			destinationID = 0;
		}
		
		System.out.println(node + " -> " + event + " -> " + destination);

		TransitionList childList = machine.transitions(destination);

		if (childList.isEmpty()) {
			recurse = false;
		}
		
	/*	if(!nodes.contains(node)) {
			nodes.add(node);
		}*/

		if (nodes.contains(destination)) {
			recurse = false;
		}
		
		System.out.println("nodeID: " + nodeID);
		NodeGraph src = new NodeGraph(nodeID, node);
		if (!nodes.contains(node)) {
			nodes.add(node);
			nodegraphs.add(src);
		}

		System.out.println("dest: " + destinationID);
		NodeGraph dest = new NodeGraph(destinationID, destination);
		if (!nodes.contains(destination)) {
			nodes.add(destination);
			nodegraphs.add(dest);
		}

		System.out.println("Current: " + current);
		for (NodeGraph nodegraph : nodegraphs) {
			if (nodegraph.getNode().equals(node)) {
				src = nodegraph;
			}

			if (nodegraph.getNode().equals(destination)) {
				dest = nodegraph;
			}
		}
		
		container.graph.Action action = new container.graph.Action(src, event, dest);
		actions.add(action);
			
		//runtimeGraph.addVertex(nodeID);
		//runtimeGraph.addVertex(destinationID);
		//runtimeGraph.addEdge(nodeID, destinationID, new ActionEdge(e.getIdentification().getName() + "." + event, new Queue()));
		
		System.out.println("cascacsa : " + childList.size());
		
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
		// it = runtimeGraph.edgeSet().iterator();
		// while (it.hasNext()) {
		// ActionEdge edge = it.next();
		// System.out.println(this.getClass() + ": " + edge.getAction() + " " +
		// edge.getQueue());
		// }
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
