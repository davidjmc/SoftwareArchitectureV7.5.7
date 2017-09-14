package prom;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteProm {

	public static void executeScript() throws IOException, InterruptedException {
		String shellScriptTobeExecuted = "/Users/nsr/Dropbox/java/BamV2.0/src/prom/prom_cli.sh";
		String[] cmd = {"/bin/bash", "-c",shellScriptTobeExecuted};
		ProcessBuilder pb = new ProcessBuilder(cmd);
		File workingDirectory = new File("/Users/nsr/Dropbox/java/BamV2.0/src/prom");

		// set current directory
		pb.directory(workingDirectory);

		try {
			Process process = pb.start();
			String s = null;

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));

			// read the output from the command
			//while ((s = stdInput.readLine()) != null) {
			//	System.out.println(s);
			//}

			//read any errors from the attempted command
			//while ((s = stdError.readLine()) != null) {
			//	System.out.println(s);
			//}
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
		return;
	}

	public static void main(String[] args) throws Throwable {
		System.out.println("Check started...");
		executeScript();
		System.out.println("Check finished...");
	}
}
