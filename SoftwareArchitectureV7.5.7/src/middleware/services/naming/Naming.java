package middleware.services.naming;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import middleware.distribution.clientproxy.ClientProxy;

public interface Naming {

	public void bind(String serviceName, ClientProxy clientProxy) throws UnknownHostException, IOException, Throwable;
	public ClientProxy lookup(String serviceName) throws UnknownHostException, IOException, Throwable;
	public ArrayList<String> list() throws UnknownHostException, IOException, Throwable;

}
