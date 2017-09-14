package middleware.distribution.miop;

import java.io.Serializable;

public class Reply implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object rep;

	public Object getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

}
