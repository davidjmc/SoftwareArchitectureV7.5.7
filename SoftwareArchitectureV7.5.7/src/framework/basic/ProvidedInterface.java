package framework.basic;

import java.util.ArrayList;

import utils.Utils;

public class ProvidedInterface extends Interface {

	public ProvidedInterface() {
	}

	public ProvidedInterface(int type){	
		if (type == Utils.INTERFACE_TWO_WAY)
		{
			this.id = this.hashCode();
			this.ports = new ArrayList<Port>();
			this.ports.add(new Port("invP" + this.id, Utils.PORT_IN));
			this.ports.add(new Port("terP" + this.id, Utils.PORT_OUT));
			this.setAvailable(true);
		}
		if (type == Utils.INTERFACE_ONE_WAY){
			this.id = this.hashCode();
			this.ports = new ArrayList<Port>();
			this.ports.add(new Port("invP" + this.id, Utils.PORT_IN));
			this.setAvailable(true);
		}
	}
	
	public ProvidedInterface(int type, ArrayList<Port> ports) {
		this.id = this.hashCode();
		this.ports = ports;
		this.setAvailable(true);
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
}
