package prom;

import java.util.ArrayList;
import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.ltlchecker.RuleModel;

import ee.tkasekamp.ltlminer.LTLMiner;
import ee.tkasekamp.ltlminer.util.XLogReader;
import utils.UtilsConf;

public class UsageTest {

	public static void main(String[] args) {

		String formula = "formula notCoExistence( A: activity , B: activity ) :=  { }"
				+ "!( (<>(activity == \"ServerRequestHandler_receive\") /\\ <>(activity == \"NamingImpl_lookup\") ));";
		LTLMiner miner = new LTLMiner();
		XLog log = null;
		try {
			log = XLogReader.openLog(UtilsConf.PROM_XES_DIR + "/" + UtilsConf.PROM_XES_FILE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<RuleModel> result = miner.mine(log, new ArrayList<>(Arrays.asList(formula)), 0.0);
		// assertEquals(42, result.size());
		System.out.println("Result: "+result.get(0).isSatisfied());
		for (RuleModel rule : result) {
			System.out.println(rule.getCoverage() + " " + rule.getLtlRule()+" "+rule.isSatisfied());
		}
	}
}
