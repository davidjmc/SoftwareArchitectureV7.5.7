package adaptation;

import container.ExecutionEnvironment;
import utils.Utils;

public class Planner {
	private ExecutionEnvironment env;

	public Planner(ExecutionEnvironment e) {
		this.env = e;
	}

	public AdaptationPlan selectPlan(AnalysisStatus status) {
		AdaptationPlan plan = null;

		switch (status.getCode()) {
		case 000: // no adaptation needed
			break;
		case Utils.REPLACE_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add(new Action("replace"));
			plan.getElements().add(new Element("server"));
			plan.getElements().add("server1");
			plan.getElements().add("CEchoServer1");
			break;
		case Utils.ADD_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("add");
			plan.getElements().add("client");
			plan.getElements().add("t0");
			plan.getElements().add("server2");
			plan.getElements().add("CEchoServer2");
			break;
		case Utils.REPLACE_BEHAVIOUR: // TODO
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("updateBehaviour");
			plan.getElements().add("server1");
			break;
		case Utils.REMOVE_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("remove");
			plan.getElements().add("sender1");
			break;
		}
		return plan;
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public void setEnv(ExecutionEnvironment env) {
		this.env = env;
	}
}
