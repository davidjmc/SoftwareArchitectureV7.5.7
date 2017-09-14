package adaptation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import container.ExecutionEnvironment;
import prom.Converter;
import prom.PromChecker;
import prom.PromProperty;

public class Analyser {
	private static ExecutionEnvironment env;

	public Analyser(ExecutionEnvironment e) {
		Analyser.env = e;
	}

	public AnalysisStatus analyse(String dataFile, ArrayList<String> args) {
		PromChecker promChecker = new PromChecker();
		boolean isSatisfied = false;
		ArrayList<String> checkArgs = new ArrayList<String>();
		ArrayList<String> converterArgs = new ArrayList<String>();
		String property;
		AnalysisStatus analysisStatus = new AnalysisStatus();

		switch (env.getParameters().get("dynamic-verification-tool").toString()) {
		case "prom":
			// configure converter parameters
			converterArgs.add(args.get(4));
			converterArgs.add(args.get(5));
			converterArgs.add(args.get(6));
			converterArgs.add(args.get(7));

			// convert "data" file into a "xes" files
			String xesFile = new String(dataFile.replace(".data", ".xes"));
			Converter.dataToXes(dataFile, xesFile, converterArgs);

			// configure check parameters
			property = args.get(0);
			checkArgs.add(args.get(1)); // a
			checkArgs.add(args.get(2)); // b
			checkArgs.add(args.get(3)); // c
			PromProperty promProperty = new PromProperty(property, args);

			try {
				isSatisfied = promChecker.check(xesFile, promProperty);
				analysisStatus.setAdaptationNecessary(isSatisfied);

				// remove files
				Path pathDataFile = Paths.get(dataFile);
				Path pathXESFile = Paths.get(xesFile);
				Files.deleteIfExists(pathDataFile);
				Files.deleteIfExists(pathXESFile);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Verification tool not supported");
			System.exit(0);
			break;
		}
		return analysisStatus;
	}
}
