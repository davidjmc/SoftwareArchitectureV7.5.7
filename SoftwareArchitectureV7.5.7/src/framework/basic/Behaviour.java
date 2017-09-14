package framework.basic;

import java.util.HashMap;
import java.util.List;

public class Behaviour {
	private String actions;
	private HashMap<String, String> labelsMap;

	public Behaviour() {
		actions = new String("");
		labelsMap = new HashMap<String, String>();
	}

	public Behaviour(String a) {
		this.actions = new String(a);
		labelsMap = new HashMap<String, String>();
	}

	public HashMap<String,String> getLabelsMap() {
		return this.labelsMap;
	}

	public void setLabelsMap(HashMap<String,String> map) {
		this.labelsMap = map;
	}
	
	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public void relabelBehaviourActions() {
		for (String oldLabel : this.labelsMap.keySet())
			this.setActions(this.getActions().replace(oldLabel, labelsMap.get(oldLabel)));

		return;
	}

	public void relabel(List<String> listLabels) {
		String oldLabel = new String();
		String newLabel = new String();

		for (String labelMap : listLabels)
			if (!labelMap.matches("^\\[\\[\\w*<-\\w*\\]\\]$")) {
				System.out.println(this.getClass() + " ERROR: Syntax error in Relabelling operation");
				System.exit(0);
			} else {
				oldLabel = "." + labelMap.substring(labelMap.indexOf("[[") + 2, labelMap.indexOf("<-")).trim();
				newLabel = "." + labelMap.substring(labelMap.indexOf("<-") + 2, labelMap.indexOf("]]")).trim();
				this.labelsMap.put(oldLabel, newLabel);
			}
	}
}
