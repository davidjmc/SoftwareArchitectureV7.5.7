package middleware.basic;

import java.net.UnknownHostException;

import middleware.distribution.clientproxy.ClientProxy;

public class RemoteObject {
	private ClientProxy clientProxy;

	public RemoteObject() throws UnknownHostException {
	}

	public ClientProxy getClientProxy() {
		return clientProxy;
	}

	public void setClientProxy(ClientProxy clientProxy) {
		this.clientProxy = clientProxy;
	}

}
