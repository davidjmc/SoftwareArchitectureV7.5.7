package tests;

import uk.ac.ox.cs.fdr.*;

public class TestFDR {

	public static void main(String argv[]) {
		int returnCode;
		Session session = new Session();
		
		session.loadFile("/Users/nsr/Dropbox/research/specification/csp/client-server.csp");

		for (Assertion assertion : session.assertions()) {
			assertion.execute(null);
			System.out.println(assertion.toString() + " " + (assertion.passed() ? "Passed" : "Failed"));
		}
	}
}
