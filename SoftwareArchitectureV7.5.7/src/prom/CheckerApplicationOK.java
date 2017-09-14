package prom;

import utils.Utils;

public class CheckerApplicationOK {

	public static void main(String[] args) throws Exception {
		// define checker to be used
		PromChecker promChecker = new PromChecker();
		//CADPChecker cadpChecker = new CADPChecker();

		// data file
		String dataFile = new String(Utils.PROM_DATA_DIR + "/"
				+ Utils.PROM_DATA_FILE);
		
		// perform verification
		String[] parameters = {"Requestor_invoke"};

		PromProperty property = new PromProperty(new String(
				"eventuallyActivityA"), parameters);

		System.out.println("Data File (*.data): "+dataFile);
		System.out.println("Property          : "+property.getName());
		System.out.println("XES File (*.xes)  : "+Utils.PROM_XES_DIR + "/"+ Utils.PROM_XES_FILE);
		
		int NUMBER_OF_REPETITIONS = 1;
		long tBefore = 0, tAfter = 0;
		boolean propertySatisfied = false;
		
		tBefore = System.nanoTime();
		for (int idx = 0; idx < NUMBER_OF_REPETITIONS; idx++)
			propertySatisfied = promChecker.check(dataFile, property);
		tAfter = System.nanoTime();

		System.out.println("Property Satisfied: "+propertySatisfied);
		System.out.println("Meantime (ms): " + ((tAfter-tBefore)/(NUMBER_OF_REPETITIONS*1000000)));
	}
}
