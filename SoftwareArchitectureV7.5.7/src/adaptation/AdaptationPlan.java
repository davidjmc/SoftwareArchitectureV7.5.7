package adaptation;

import java.util.ArrayList;

public class AdaptationPlan {
	private String id;
	private ArrayList<Action> actions = new ArrayList<Action>();

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
