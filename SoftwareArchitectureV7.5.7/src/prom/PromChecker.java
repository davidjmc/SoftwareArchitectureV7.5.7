package prom;

import java.util.ArrayList;
import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.ltlchecker.RuleModel;

import ee.tkasekamp.ltlminer.LTLMiner;
import ee.tkasekamp.ltlminer.util.XLogReader;

public class PromChecker implements IChecker {

	public boolean check(String xesFile, Object obj) throws Exception {
		boolean result = false;
		PromProperty property = (PromProperty) obj;
		
		// check property in the xes file
		XLog xLog = XLogReader.openLog(xesFile);

		switch (property.getName().trim()) {
		case "always_when_A_then_eventually_B":
			result = always_when_A_then_eventually_B(xLog, property.getParameters().get(0),
					property.getParameters().get(1));
		case "eventually_activity_A_then_B":
			result = eventually_activity_A_then_B(xLog, property.getParameters().get(0),
					property.getParameters().get(1));
			break;
		case "eventually_activity_A_then_B_then_C":
			result = eventually_activity_A_then_B_then_C(xLog, property.getParameters().get(0),
					property.getParameters().get(1), property.getParameters().get(2));
			break;
		case "eventually_activity_A_next_B_next_C":
			result = eventually_activity_A_next_B_next_C(xLog, property.getParameters().get(0),
					property.getParameters().get(1), property.getParameters().get(2));
			break;
		case "eventually_activity_A":
			result = eventually_activity_A(xLog, property.getParameters().get(0));
			break;
		default:
			System.out.println("Property inexistent!!");
			System.exit(0);
		}
		return result;
	}

	// property
	public static boolean always_when_A_then_eventually_B(XLog xLog, String eventA, String eventB) throws Exception {
		StringBuilder formula = new StringBuilder(1000);
		ArrayList<RuleModel> result = null;
		LTLMiner miner = new LTLMiner();

		formula.append("formula always_when_A_then_eventually_B(A: activity, B:activity) :=  { }" + "[]((activity==\""
				+ eventA + "\" /\\ <>(activity==\"" + eventB + "\")));");
		result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula.toString())), 0.0);

		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}

	public static boolean resultAnalyse(ArrayList<RuleModel> result) {
		double sumTotal = 0;

		for (int idx = 0; idx < result.size(); idx++)
			sumTotal = sumTotal + result.get(idx).getCoverage();

		if (sumTotal / result.size() > 0.9)
			return true;
		else
			return false;
	}

	// property
	public static boolean eventually_activity_A_then_B(XLog xLog, String eventA, String eventB) throws Exception {
		StringBuilder formula = new StringBuilder(1000);
		ArrayList<RuleModel> result = null;
		LTLMiner miner = new LTLMiner();

		formula.append("formula eventually_activity_A_then_B(A: activity, B:activity) :=  { }" + "<>((activity==\""
				+ eventA + "\" /\\ <>(activity==\"" + eventB + "\")));");
		result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula.toString())), 0.0);

		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}

	// property
	public static boolean eventually_activity_A_then_B_then_C(XLog xLog, String eventA, String eventB, String eventC)
			throws Exception {
		StringBuilder formula = new StringBuilder(1000);

		formula.append("formula eventually_activity_A_then_B_then_C(A: activity,B:activity,C:activity) :=  { }"
				+ "<>((activity==\"" + eventA + "\" /\\ <>( (activity==\"" + eventB + "\"/\\ <>(activity==\"" + eventC
				+ "\")))));");
		LTLMiner miner = new LTLMiner();
		ArrayList<RuleModel> result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula.toString())), 0.0);

		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}

	// property
	public static boolean eventually_activity_A_next_B_next_C(XLog xLog, String eventA, String eventB, String eventC)
			throws Exception {
		StringBuilder formula = new StringBuilder(1000);

		formula.append("formula eventually_activity_A_next_B_next_C(A: activity,B:activity,C:activity) :=  { }"
				+ "<>((activity==\"" + eventA + "\" /\\ _O( (activity==\"" + eventB + "\"/\\ _O(activity==\"" + eventC
				+ "\")))));");
		LTLMiner miner = new LTLMiner();
		ArrayList<RuleModel> result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula.toString())), 0.0);

		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}

	// property
	public static boolean eventually_activity_A(XLog xLog, String eventA) throws Exception {
		String formula = new String();

		formula = "formula eventually_activity_A(A: activity) := { }" + "<>(activity == \"" + eventA.trim() + "\");";
		LTLMiner miner = new LTLMiner();

		ArrayList<RuleModel> result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula)), 0.0);

		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}

	// property
	public boolean eventually_activity_A_or_eventually_B(XLog xLog, String eventA, String eventB) throws Exception {
		String formula = "formula eventually_activity_A_or_eventually_B( A: activity , B: activity ) :=  { }"
				+ "!( (<>(activity == A) \\/ <>(activity == B) ));";
		LTLMiner miner = new LTLMiner();

		ArrayList<RuleModel> result = miner.mine(xLog, new ArrayList<>(Arrays.asList(formula)), 0.0);
		if (result.size() == 0) // in the case the file has not events
			return true;
		else
			return resultAnalyse(result);
	}
}