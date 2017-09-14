package adaptation;

import java.util.ArrayList;

import framework.basic.Element;

public class Action {
	private String name;
	private ArrayList<Element> element = new ArrayList<Element>();
	
	public Action(String a){
		this.name = a;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Element> getElement() {
		return element;
	}
	public void setElement(ArrayList<Element> element) {
		this.element = element;
	}
}
