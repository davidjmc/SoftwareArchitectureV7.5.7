package prom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Converter {

	public static void dataToXes(String dataFile, String xesFile,ArrayList<String> args) {
		FileWriter xesFileWriter = null;
		BufferedReader dataFileReader = null;
		StringBuilder xesXML = new StringBuilder(1000);
		String[] splitLine = null;
		int traceID = 0;
		int state = 0;
		
		// read "data" file
		try {
			dataFileReader = new BufferedReader(new FileReader(dataFile));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Map<String, String> map = new TreeMap<String, String>();
		String line = null;
		String[] fields;

		try {
			while ((line = dataFileReader.readLine()) != null) {
				fields = line.split(";");
				map.put(fields[3], line);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			dataFileReader.close();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		try {
			xesFileWriter = new FileWriter(xesFile);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		xesXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\">"
				+ "<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>"
				+ "<extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>"
				+ "<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>"
				+ "<classifier name=\"Event Name\" keys=\"concept:name\"/>"
				+ "<classifier name=\"(Event Name AND Lifecycle transition)\" keys=\"concept:name lifecycle:transition\"/>"
				+ "<string key=\"concept:name\" value=\"events.csv\"/>");

		try {
			xesFileWriter.write(xesXML.toString());
			xesFileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String val : map.values()) {
			splitLine = val.split(";");
			xesXML.setLength(0);
			switch (state) {
			case 0:
				if (splitLine[0].contains(args.get(0)) && splitLine[1].contains(args.get(2))
						&& splitLine[2].contains("start")) {
					xesXML.append("<trace>" + "<string key=\"concept:name\" value=\"" + traceID + "\"/>");
					xesXML.append("<event>" + "<string key=\"concept:name\" value=\"" + splitLine[0] + "_"
							+ splitLine[1] + "\"/>" + "<string key=\"lifecycle:transition\" value=\"" + splitLine[2]
							+ "\"/>" + "\"/>" + "<date key=\"time:timestamp\" value=\"" + splitLine[3] + "\"/>"
							+ "</event>");
					traceID++;
					state = 1;
				}
				break;
			case 1:
				xesXML.append("<event>" + "<string key=\"concept:name\" value=\"" + splitLine[0] + "_" + splitLine[1]
						+ "\"/>" + "<string key=\"lifecycle:transition\" value=\"" + splitLine[2] + "\"/>" + "\"/>"
						+ "<date key=\"time:timestamp\" value=\"" + splitLine[3] + "\"/>" + "</event>");
				if (splitLine[0].contains(args.get(1)) && splitLine[1].contains(args.get(3))
						&& splitLine[2].contains("complete")) {
					xesXML.append("</trace>");
					state = 0;
				}
				break;
			}
			try {
				xesFileWriter.write(xesXML.toString());
				xesFileWriter.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		xesXML.setLength(0);
		if (state == 1) // trace was not complete
			xesXML.append("</trace>");

		xesXML.append("</log>");
		try {
			xesFileWriter.write(xesXML.toString());
			xesFileWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
