package container;

import utils.Utils;

public class EndPointType {
	private int type;

	public EndPointType(int type) {
		this.type = type;
	}

	public String getTypeName(){
		if (this.type == Utils.ENDPOINT_IN)
			return "IN";
		else
			return "OUT";
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
