package container.graph;

import uk.ac.ox.cs.fdr.Node;

public class NodeGraph {
	
	int id;
	Node node;

	public NodeGraph(int id, Node node) {
		super();
		this.id = id;
		this.node = node;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
