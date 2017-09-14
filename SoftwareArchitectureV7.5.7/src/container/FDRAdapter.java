package container;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import framework.basic.CheckingStatus;
import framework.basic.Element;
import framework.configuration.ActionEdge;
import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.DeadlockFreeAssertion;
import uk.ac.ox.cs.fdr.Event;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Machine;
import uk.ac.ox.cs.fdr.Node;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.Transition;
import uk.ac.ox.cs.fdr.TransitionList;
import utils.MyError;
import utils.Utils;

public class FDRAdapter {

	//david
	public void check(ExecutionEnvironment env) {
		CheckingStatus r = new CheckingStatus();
		Session session = new Session();
		
		//david
		String confName = env.getConf().getConfName();

		session.loadFile(Utils.CSP_DIR + "/" + confName + ".csp");

		try {
			for (Assertion assertion : session.assertions()) {
				assertion.execute(null);
				r.setBehaviourStatus(assertion.passed());
				
				/*if(assertion instanceof DeadlockFreeAssertion) {
					configureBehaviour(env, session, ((DeadlockFreeAssertion) assertion).machine());
				}*/
				
				if (!r.getBehaviourStatus()) {
					System.out.println(confName + " has a deadlock!");
					System.exit(0);
				}
			}
		} catch (InputFileError error) {
			System.out.println("Could not compile: " + error.toString());
		}

		return;
	}

	private void configureBehaviour(ExecutionEnvironment env, Session session, Machine machine) {
		Node node = machine.rootNode();
		int rootID = 0;
		
		DirectedGraph<Integer, ActionEdge> behaviourGraph = new DefaultDirectedGraph<>(ActionEdge.class);
		
		for(Transition transition: machine.transitions(node)) {
			describeBehaviour(session, machine, node, rootID, transition, behaviourGraph, true);
		}
	}

	private void describeBehaviour(Session session, Machine machine, Node node, int id, Transition transition,
			DirectedGraph<Integer, ActionEdge> behaviourGraph, boolean recurse) {
		int nodeID = id + 1;
		int destinationID = nodeID + 1;
		
		Event event = session.uncompileEvent(transition.event());
		Node destination = transition.destination();
		
		if(node.equals(machine.rootNode())) {
			nodeID = 0;
			destinationID = nodeID + 1;
		}
			
		
		behaviourGraph.addVertex(nodeID);
		behaviourGraph.addVertex(destinationID);
		behaviourGraph.addEdge(nodeID, destinationID, new ActionEdge(event.toString(), new Queue()));
		
		TransitionList transitions = machine.transitions(destination);
		
		for(Transition t: transitions) {
			describeBehaviour(session, machine, destination, destinationID, t, behaviourGraph, recurse);
		}

	}

}
