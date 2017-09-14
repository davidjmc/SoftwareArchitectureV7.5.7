package prom;

import java.util.ArrayList;

public abstract class Property {
	private String name = null;
	private ArrayList<String> parameters = new ArrayList<String>();
	
	public Property (String name,ArrayList<String> parameters){
		this.name = name;
		this.parameters = parameters;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getParameters() {
		return parameters;
	}
	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}

}
