package middleware.services.naming;

import framework.configuration.Configuration;
import framework.connector.TRequestReply;
import middleware.distribution.invoker.CInvoker;
import middleware.distribution.marshaller.CMarshaller;
import middleware.infrastructure.CServerRequestHandler;
import middleware.services.naming.CNamingServer;

public class NamingConf extends Configuration {

	public NamingConf(String name, boolean isAdaptive) {
		this.name = name;
		this.setAdaptive(isAdaptive);
		
		CNamingServer namingServer = new CNamingServer("namingserver");
		CServerRequestHandler srh = new CServerRequestHandler("srh");
		CMarshaller marshaller = new CMarshaller("marshaller");
		CInvoker invoker = new CInvoker("invoker");

		TRequestReply t0 = new TRequestReply("t0");
		TRequestReply t1 = new TRequestReply("t1");
		TRequestReply t2 = new TRequestReply("t2");

		connect(srh,t0,invoker);
		connect(invoker, t1, marshaller);
		connect(invoker, t2, namingServer);

		srh.setSemantics("invP[remote] ; invR[t0] ; terR[t0] ; terP[remote]");
		t0.setSemantics("invP[t0] ; invR[invoker] ; terR[invoker] ; terP[t0]");
		
		invoker.setSemantics(
				"invP[invoker] ; invR[t1] ; terR[t1] ; invR[t2] ; terR[t2] ; invR[t1] ; terR[t1] ; terP[invoker]");
		t1.setSemantics("invP[t1] ; invR[marshaller] ; terR[marshaller] ; terP[t1]");
		
		marshaller.setSemantics("invP[marshaller] ; terP[marshaller]");
		t2.setSemantics("invP[t2]; invR[namingserver] ; terR[namingserver]; terP[t2]");
		
		namingServer.setSemantics("invP[namingserver] ; terP[namingserver]");
	}
}