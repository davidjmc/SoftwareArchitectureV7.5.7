package container.graph;

import uk.ac.ox.cs.fdr.Event;

public class Action {
	
	NodeGraph from;
	NodeGraph to;
	Event event;

	public Action(NodeGraph node, Event event, NodeGraph destination) {
		this.from = node;
		this.event = event;
		this.to = destination;

	}

	public NodeGraph getFrom() {
		return from;
	}

	public void setFrom(NodeGraph from) {
		this.from = from;
	}

	public NodeGraph getTo() {
		return to;
	}

	public void setTo(NodeGraph to) {
		this.to = to;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
