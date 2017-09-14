package middleware.distribution.miop;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestBody implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9198742578441430115L;
	private ArrayList<Object> parameters = new ArrayList<Object>();


	public RequestBody(ArrayList<Object> p){
		this.parameters = p;	
	}
	
	public ArrayList<Object> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Object> parameters) {
		this.parameters = parameters;
	}
}
