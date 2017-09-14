package container;

import framework.basic.Element;

public class EndPoint {
	private Element element;
	private int interfaceID;
	private String portID;
	private EndPointType type;

	public int getInterfaceID() {
		return interfaceID;
	}


	public void setInterfaceID(int interfaceID) {
		this.interfaceID = interfaceID;
	}


	public String getPortID() {
		return portID;
	}


	public void setPortID(String portID) {
		this.portID = portID;
	}


	public EndPointType getType() {
		return type;
	}


	public void setType(EndPointType type) {
		this.type = type;
	}


	public Element getElement() {
		return element;
	}


	public void setElement(Element element) {
		this.element = element;
	}

}
