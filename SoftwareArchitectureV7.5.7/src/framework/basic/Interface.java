package framework.basic;

import java.util.ArrayList;

public abstract class Interface {
	protected int id;
	protected ArrayList<Port> ports;
	protected boolean isAvailable;
	private int syncType;
	
	public Interface(){
		this.ports = new ArrayList<Port>();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Port> getPorts() {
		return ports;
	}
	public void setPorts(ArrayList<Port> ports) {
		this.ports = ports;
	}
	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public int getSyncType() {
		return syncType;
	}
	public void setSyncType(int syncType) {
		this.syncType = syncType;
	}
}
