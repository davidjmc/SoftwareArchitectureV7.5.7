package container.csp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import container.ExecutionEnvironment;
import container.FDRAdapter;
import framework.basic.Element;
import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import utils.Utils;

public class CSPSpecification {
	private String dataTypeExp;
	private String typedChannelsExp;
	private String untypedChannelsExp;
	private String processesExp;
	private String compositeExp;
	private String assertionsExp;
	public ExecutionEnvironment env;
	private ArrayList<String> assertions = new ArrayList<String>();

	public CSPSpecification(ExecutionEnvironment e) {
		this.dataTypeExp = new String("datatype PROCNAMES = ");
		this.typedChannelsExp = new String("channel ");
		this.untypedChannelsExp = new String("channel ");
		this.processesExp = new String("");
		this.compositeExp = new String("P1 = ");
		this.assertionsExp = new String(e.getParameters().get("csp-deadlock-assertion").toString());
		this.env = e;
	}
		
	public void create() {
		String[] processAlphabet;
		String processName;
		String processBehaviour;
		HashMap<String, Element> processes = new HashMap<String, Element>();
		Set<String> typedChannelSet = new TreeSet<String>();
		Set<String> untypedChannelSet = new TreeSet<String>();
		Set<String> dataTypeSet = new TreeSet<String>();
		Set<String> components = new TreeSet<String>();
		Set<String> connectors = new TreeSet<String>();
		Configuration conf = env.getConf();

		for (Element element : conf.getStructure().vertexSet()) {
			processName = element.getIdentification().getName().toUpperCase();
			//assertions.add(processName);

			if (element instanceof Component)
				components.add(processName);
			if (element instanceof Connector)
				connectors.add(processName);

			// datatypes
			if (!dataTypeSet.contains(processName))
				dataTypeSet.add(processName.toLowerCase());

			// processes
			processBehaviour = element.getSemantics().getRuntimeBehaviour().getActions();
			processes.put(processName, element);

			// channels
			processAlphabet = processBehaviour.split(env.getParameters().get("csp-prefix-action").toString());
			for (String event : processAlphabet) {
				event = event.trim();
				if (!event.contains("i_")) {
					event = event.substring(0, event.indexOf("."));
					if (!typedChannelSet.contains(event))
						typedChannelSet.add(event);
				} else if (!untypedChannelSet.contains(event))
					untypedChannelSet.add(event);
			}
		}

		// data set expression
		Iterator<String> itDataType = dataTypeSet.iterator();
		while (itDataType.hasNext()) {
			this.dataTypeExp = this.dataTypeExp + itDataType.next() + " | ";
		}
		this.dataTypeExp = this.dataTypeExp.substring(0, this.dataTypeExp.lastIndexOf("|"));

		// channel expressions
		// -- untyped channels
		Iterator<String> itUntypedChannels = untypedChannelSet.iterator();
		while (itUntypedChannels.hasNext()) {
			String channelName = itUntypedChannels.next();
			this.untypedChannelsExp = this.untypedChannelsExp + channelName + ",";
		}
		this.untypedChannelsExp = this.untypedChannelsExp.substring(0, this.untypedChannelsExp.lastIndexOf(","));

		// -- typed channels
		Iterator<String> itTypedChannels = typedChannelSet.iterator();
		while (itTypedChannels.hasNext()) {
			String channelName = itTypedChannels.next();
			this.typedChannelsExp = this.typedChannelsExp + channelName + ",";
		}
		this.typedChannelsExp = this.typedChannelsExp.substring(0, this.typedChannelsExp.lastIndexOf(","));

		// --- process expression
		HashMap<String, String> newBehaviours = new HashMap<String, String>();
		ArrayList<String> lastActions = new ArrayList<String>();
		
		for (String process : processes.keySet()) {
			Element tempElement = processes.get(process);
			String behaviour = tempElement.getSemantics().getRuntimeBehaviour().getActions();
	
			//String[] actions = behaviour.split(Utils.PREFIX_ACTION);
			
			if(behaviour.contains(Utils.CHOICE_ACTION)) {
				ArrayList<String> choices = new ArrayList<>(
						Arrays.asList(behaviour.split("\\[]")));
				
				Iterator<String> choice = choices.iterator();
				while(choice.hasNext()) {
					ArrayList<String> actions = new ArrayList<>(
							Arrays.asList(choice.next().split(Utils.PREFIX_ACTION)));
					lastActions.add((String) actions.get(actions.size()-1));
				}
				
			}
			
			String[] actions = behaviour.split(Utils.REGEX_ACTION);
			if (tempElement instanceof Component) {
				for (String action : actions) {
					if (!action.contains("i_")) {
						String value = process.toLowerCase();
						String pair = action.substring(action.indexOf(".") + 1, action.length());
						behaviour = behaviour.replace(pair, value);
					}
				}
			} else {
				for (String action : actions) {
					if (!action.contains("i_")) {
						String key = process.toLowerCase() + "."
								+ action.substring(action.indexOf(".") + 1, action.length());
						String value = env.getExecutionManager().geteMaps().get(key);
						String pair = key.substring(key.indexOf(".") + 1, key.length());
						behaviour = behaviour.replace(pair, value);
					}
				}
			}
			newBehaviours.putIfAbsent(process, behaviour);
		}
				
		
		
		for (String process : newBehaviours.keySet()) {
			
			if(newBehaviours.get(process).contains(Utils.CHOICE_ACTION)) {
				ArrayList<String> choices = new ArrayList<>(
						Arrays.asList(newBehaviours.get(process).split("\\[]")));
				this.processesExp = this.processesExp + process + " = ";
				
				int i = 0;
				for(String choice: choices) {
					i = i+1;
					if(choice.contains(Utils.PREFIX_ACTION)) {
						this.processesExp = this.processesExp + 
								choice.substring(0, choice.lastIndexOf("->")) + 
								"->" + "(" + choice.substring(choice.lastIndexOf("->")+2) + " -> " + process;
					} else {
						this.processesExp = this.processesExp + choice + " -> " + process;
					}
					
					if(choices.size() != i) {
						this.processesExp = this.processesExp + Utils.CHOICE_ACTION;
					}
				}
				
				this.processesExp = this.processesExp + ")" + "\n";
			} else {
				this.processesExp = this.processesExp + process + " = " + newBehaviours.get(process) + " -> " + process;
				this.processesExp = this.processesExp + "\n";
			}
		}

		// composite Exp
		// --- components
		Iterator<String> itComponents = components.iterator();
		this.compositeExp = this.compositeExp + "(";
		while (itComponents.hasNext())
			this.compositeExp = this.compositeExp + itComponents.next() + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ") \n";

		String syncEventExp = createSyncEventExp(typedChannelSet);
		this.compositeExp = this.compositeExp + syncEventExp + "\n";
		this.compositeExp = this.compositeExp + "(";

		// --- connectors
		String relabellingEvents = createRelabelling(typedChannelSet);
		Iterator<String> itConnectors = connectors.iterator();
		while (itConnectors.hasNext())
			this.compositeExp = this.compositeExp + itConnectors.next() + relabellingEvents + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ")\n";

	}

	public void createOld() {
		String[] processAlphabet;
		String processName;
		String processBehaviour;
		HashMap<String, String> processes = new HashMap<String, String>();
		Set<String> typedChannelSet = new TreeSet<String>();
		Set<String> untypedChannelSet = new TreeSet<String>();
		Set<String> dataTypeSet = new TreeSet<String>();
		Set<String> components = new TreeSet<String>();
		Set<String> connectors = new TreeSet<String>();
		Configuration conf = env.getConf();

		for (Element element : conf.getStructure().vertexSet()) {
			processName = element.getIdentification().getName().toUpperCase();

			if (element instanceof Component)
				components.add(processName);
			if (element instanceof Connector)
				connectors.add(processName);

			// datatypes
			if (!dataTypeSet.contains(processName))
				dataTypeSet.add(processName.toLowerCase());

			// processes
			processBehaviour = element.getSemantics().getRuntimeBehaviour().getActions();
			processes.put(processName, processBehaviour);

			// channels
			processAlphabet = processBehaviour.split(env.getParameters().get("csp-prefix-action").toString());
			for (String event : processAlphabet) {
				event = event.trim();
				if (!event.contains("i_")) {
					event = event.substring(0, event.indexOf("."));
					if (!typedChannelSet.contains(event))
						typedChannelSet.add(event);
				} else if (!untypedChannelSet.contains(event))
					untypedChannelSet.add(event);
			}
		}

		// data set expression
		Iterator<String> itDataType = dataTypeSet.iterator();
		while (itDataType.hasNext()) {
			this.dataTypeExp = this.dataTypeExp + itDataType.next() + " | ";
		}
		this.dataTypeExp = this.dataTypeExp.substring(0, this.dataTypeExp.lastIndexOf("|"));

		// channel expressions
		// -- untyped channels
		Iterator<String> itUntypedChannels = untypedChannelSet.iterator();
		while (itUntypedChannels.hasNext()) {
			String channelName = itUntypedChannels.next();
			this.untypedChannelsExp = this.untypedChannelsExp + channelName + ",";
		}
		this.untypedChannelsExp = this.untypedChannelsExp.substring(0, this.untypedChannelsExp.lastIndexOf(","));

		// -- typed channels
		Iterator<String> itTypedChannels = typedChannelSet.iterator();
		while (itTypedChannels.hasNext()) {
			String channelName = itTypedChannels.next();
			this.typedChannelsExp = this.typedChannelsExp + channelName + ",";
		}
		this.typedChannelsExp = this.typedChannelsExp.substring(0, this.typedChannelsExp.lastIndexOf(","));

		// --- process expression
		HashMap<String, String> newBehaviours = new HashMap<String, String>();
		for (String process : processes.keySet()) {
			String behaviour = processes.get(process);
			String[] actions = behaviour.split(Utils.PREFIX_ACTION);
			for (String action : actions) {
				if (!action.contains("i_")) {
					String key = process.toLowerCase() + "."
							+ action.substring(action.indexOf(".") + 1, action.length());
					String value = env.getExecutionManager().geteMaps().get(key);
					String pair = key.substring(key.indexOf(".") + 1, key.length());
					behaviour = behaviour.replace(pair, value);
				}
			}
			newBehaviours.putIfAbsent(process, behaviour);
		}

		for (String process : newBehaviours.keySet()) {
			this.processesExp = this.processesExp + process + " = " + newBehaviours.get(process) + " -> " + process;
			this.processesExp = this.processesExp + "\n";
		}

		// composite Exp
		// --- components
		Iterator<String> itComponents = components.iterator();
		this.compositeExp = this.compositeExp + "(";
		while (itComponents.hasNext())
			this.compositeExp = this.compositeExp + itComponents.next() + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ") \n";

		String syncEventExp = createSyncEventExp(typedChannelSet);
		this.compositeExp = this.compositeExp + syncEventExp + "\n";
		this.compositeExp = this.compositeExp + "(";

		// --- connectors
		String relabellingEvents = createRelabelling(typedChannelSet);
		Iterator<String> itConnectors = connectors.iterator();
		while (itConnectors.hasNext())
			this.compositeExp = this.compositeExp + itConnectors.next() + relabellingEvents + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ")\n";

	}

	public void save() {
		String cspFileName = env.getConf().getConfName() + ".csp";
		String cspFullFileName = env.getParameters().get("csp-dir") + "/" + cspFileName;

		PrintWriter writer;
		try {
			writer = new PrintWriter(cspFullFileName, "UTF-8");

			writer.println(this.dataTypeExp);
			writer.println("");
			writer.println(this.untypedChannelsExp);
			writer.println("");
			writer.println(this.typedChannelsExp + " : PROCNAMES");
			writer.println("");
			writer.println(this.processesExp);
			writer.println("");
			writer.println(this.compositeExp);
			for(String assertion: assertions) {
				writer.println("assert " + assertion + " :[deadlock free]");
			}
			writer.println(this.assertionsExp);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createSyncEventExp(Set<String> typedChannels) {
		String r = new String();
		Iterator<String> itTypedChannels = typedChannels.iterator();

		r = "[|{|";
		while (itTypedChannels.hasNext())
			r = r + itTypedChannels.next() + ",";

		r = r.substring(0, r.lastIndexOf(",")) + "|}|]";

		return r;

	}

	public String createRelabelling(Set<String> typedChannels) {
		String r = new String();
		String typedChannel;
		Iterator<String> itTypedChannels = typedChannels.iterator();

		r = "[[";
		while (itTypedChannels.hasNext()) {
			typedChannel = itTypedChannels.next();
			switch (typedChannel) {
			case "invP":
				r = r + typedChannel + "<-invR" + ",";
				break;
			case "invR":
				r = r + typedChannel + "<-invP" + ",";
				break;
			case "terP":
				r = r + typedChannel + "<-terR" + ",";
				break;
			case "terR":
				r = r + typedChannel + "<-terP" + ",";
				break;
			}
		}

		r = r.substring(0, r.lastIndexOf(",")) + "]]";
		return r;
	}

	public void check() {
		FDRAdapter fdr = new FDRAdapter();

		// invoke FDR
		//fdr.check(env.getConf().getConfName());
		
		//david
		fdr.check(env);
		
		return;
	}
	
	public String getProcessesExp() {
		return processesExp;
	}

	public void setProcessesExp(String processesExp) {
		this.processesExp = processesExp;
	}

	public String getAssertionsExp() {
		return assertionsExp;
	}

	public void setAssertionsExp(String assertionsExp) {
		this.assertionsExp = assertionsExp;
	}

	public String getTypedChannelsExp() {
		return typedChannelsExp;
	}

	public void setTypedChannelsExp(String typedChannelsExp) {
		this.typedChannelsExp = typedChannelsExp;
	}

	public String getUntypedChannelsExp() {
		return untypedChannelsExp;
	}

	public void setUntypedChannelsExp(String untypedChannelsExp) {
		this.untypedChannelsExp = untypedChannelsExp;
	}

	public String getCompositeExp() {
		return compositeExp;
	}

	public void setCompositeExp(String compositeExp) {
		this.compositeExp = compositeExp;
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public void setEnv(ExecutionEnvironment env) {
		this.env = env;
	}

}
