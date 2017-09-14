package framework.basic;

import java.util.ArrayList;

import utils.Utils;

public class RequiredInterface extends Interface{

	public RequiredInterface() {
	}

	public RequiredInterface(int type){	
	if (type == Utils.INTERFACE_TWO_WAY){
			this.id = this.hashCode();
			this.ports = new ArrayList<Port>();
			this.ports.add(new Port("invR" + this.id, Utils.PORT_OUT));
			this.ports.add(new Port("terR" + this.id, Utils.PORT_IN));
			this.setAvailable(true);
	}
	if (type == Utils.INTERFACE_ONE_WAY){
		this.id = this.hashCode();
		this.ports = new ArrayList<Port>();
		this.ports.add(new Port("invR" + this.id, Utils.PORT_OUT));
		this.setAvailable(true);
	}
}	
	public RequiredInterface(int type, ArrayList<Port> ports) {
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
