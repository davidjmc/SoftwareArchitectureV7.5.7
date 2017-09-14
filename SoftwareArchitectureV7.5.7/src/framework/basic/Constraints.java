package framework.basic;

import java.util.HashMap;

public class Constraints {
	private HashMap<String,String> invPPartner;
	private HashMap<String,String> terPPartner;
	private HashMap<String,String> invRPartner;
	private HashMap<String,String> terRPartner;
	
	public Constraints(){
		this.invPPartner = new HashMap<String,String>();
		this.terPPartner = new HashMap<String,String>();
		this.invRPartner = new HashMap<String,String>();
		this.terRPartner = new HashMap<String,String>();
	}
	
	public HashMap<String,String> getInvPPartner() {
		return invPPartner;
	}
	public void setInvPPartner(HashMap<String,String> invPPartner) {
		this.invPPartner = invPPartner;
	}
	public HashMap<String,String> getTerPPartner() {
		return terPPartner;
	}
	public void setTerPPartner(HashMap<String,String> terPPartner) {
		this.terPPartner = terPPartner;
	}
	public HashMap<String,String> getInvRPartner() {
		return invRPartner;
	}
	public void setInvRPartner(HashMap<String,String> invRPartner) {
		this.invRPartner = invRPartner;
	}
	public HashMap<String,String> getTerRPartner() {
		return terRPartner;
	}
	public void setTerRPartner(HashMap<String,String> terRPartner) {
		this.terRPartner = terRPartner;
	}
}
