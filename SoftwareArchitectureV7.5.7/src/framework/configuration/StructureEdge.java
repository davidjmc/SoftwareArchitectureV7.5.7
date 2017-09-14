package framework.configuration;

import org.jgrapht.graph.DefaultEdge;

public class StructureEdge extends DefaultEdge {
	private static final long serialVersionUID = 1L;

	public Object getS(){
		return this.getSource();
	}
	
	public Object getT(){
		return this.getTarget();
	}
}
