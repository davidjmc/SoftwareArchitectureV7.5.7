package container;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import adaptation.AdaptationManager;
import container.csp.CSPSpecification;
import framework.configuration.Configuration;
import utils.Utils;

public class ExecutionEnvironment {
	private Configuration conf;
	private AdaptationManager adaptationManager;
	private volatile ExecutionManager executionManager;
	private CommunicationManager communicationManager;
	private int portExecutionEnvironment;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private final Semaphore semaphore = new Semaphore(1, true);

	public ExecutionEnvironment(Object conf) {
		this.conf = (Configuration) conf;
		this.adaptationManager = new AdaptationManager(this);
		this.executionManager = new ExecutionManager(this);
		this.communicationManager = new CommunicationManager(this);
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);
		this.configureParameters();
	}

	public ExecutionEnvironment() {
		this.configureParameters();
		this.adaptationManager = new AdaptationManager(this);
		this.executionManager = new ExecutionManager(this);
		this.communicationManager = new CommunicationManager(this);
	}

	public void configureParameters() {
		parameters.put("prom-delay-first-checking", Utils.DELAY_FIRST_CHECKING);
		parameters.put("prom-delay-between-checking", Utils.DELAY_BETWEEN_CHECKING);
		//parameters.put("csp-prefix-action", "->");
		parameters.put("csp-prefix-action", "\\->|\\[]");
		//parameters.put("csp-dir", "/Users/nsr/Dropbox/research/specification/csp");
		parameters.put("csp-dir", "/home/david/David/Doutorado/csp/orig");
		parameters.put("prom-property", "eventually_activity_A_then_B");
		parameters.put("prom-property-parameter-a", "client_i_PreInvR");
		parameters.put("prom-property-parameter-b", "server_i_PosInvP");
		parameters.put("prom-property-parameter-c", "");
		parameters.put("csp-deadlock-assertion", "assert P1 :[deadlock free]");
		parameters.put("prom-begin-element", "client");
		parameters.put("prom-end-element", "server");
		parameters.put("prom-begin-action", "i_PreInvR");
		parameters.put("prom-end-action", "i_PosInvP");
		parameters.put("dynamic-verification-tool", "prom");
		parameters.put("general-generate-log", false);
	}

	public void deploy(Object conf) {
		this.conf = (Configuration) conf;
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);

		// initialize managers
		this.adaptationManager = new AdaptationManager(this);
		this.executionManager = new ExecutionManager(this);
		this.communicationManager = new CommunicationManager(this);
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);

		// configure parameters
		this.configureParameters();		
		
		CSPSpecification csp = new CSPSpecification(this);
		//csp.cr();
		
		// configure execution environment
		executionManager.configure();
	
		// behavioral check
		
		csp.create();
		csp.save();
		csp.check();
		
		// structural check // TODO
		this.conf.check();

		// execute managers
		new Thread(communicationManager).start();;
		executionManager.execute();
		if (this.conf.isAdaptive()) {
			adaptationManager.execute();
		}
	}

	public CommunicationManager getCommunicationManager() {
		return this.communicationManager;
	}

	public synchronized Configuration getConf() {
		return this.conf;
	}

	public synchronized void setConf(Configuration c) {
		try {
			semaphore.acquire();
			this.conf = c;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getPortExecutionEnvironment() {
		return this.portExecutionEnvironment;
	}

	public synchronized ExecutionManager getExecutionManager() {
		return this.executionManager;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public AdaptationManager getAdaptationManager() {
		return adaptationManager;
	}

	public synchronized void setAdaptationManager(AdaptationManager adaptationManager) {
		try {
			semaphore.acquire();
			this.adaptationManager = adaptationManager;
			semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
