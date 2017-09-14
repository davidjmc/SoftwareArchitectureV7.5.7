package middleware.basic;

import java.util.ArrayList;

import middleware.distribution.clientproxy.ClientProxy;

public class Invocation {
	private String operationName;
	private ArrayList<Object> parameters = new ArrayList<Object>();
	private ClientProxy clientProxy;

	public Invocation() {
	};

	// TEST ONLY
	public Invocation(String operationName, Object p1){
		this.operationName = operationName;
		this.parameters.add(p1);
	}
	
	public Invocation(String operationName, ArrayList<Object> parameters, ClientProxy clientProxy) {
		this.operationName = operationName;
		this.parameters = parameters;
		this.setClientProxy(clientProxy);
	};

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String serviceName) {
		this.operationName = serviceName;
	}

	public ArrayList<Object> getParameters() {
		return this.parameters;
	}

	public void setParameters(ArrayList<Object> p) {
		for (Object m : p) {
			this.parameters.add(m);
		}
	}

	public ClientProxy getClientProxy() {
		return clientProxy;
	}

	public void setClientProxy(ClientProxy clientProxy) {
		this.clientProxy = clientProxy;
	}
}