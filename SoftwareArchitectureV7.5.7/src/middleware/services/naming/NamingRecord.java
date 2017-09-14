package middleware.services.naming;

import middleware.distribution.clientproxy.ClientProxy;

public class NamingRecord {
	private String serviceName;
	private ClientProxy clientProxy;

	public NamingRecord(final String serviceName, final ClientProxy clientProxy) {
		this.setClientProxy(clientProxy);
		this.setServiceName(serviceName);
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public ClientProxy getClientProxy() {
		return clientProxy;
	}

	public void setClientProxy(ClientProxy clientProxy) {
		this.clientProxy = clientProxy;
	}
}
