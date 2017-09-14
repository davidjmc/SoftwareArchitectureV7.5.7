package applications.distributed.generic;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.invoker.CInvoker;
import middleware.distribution.marshaller.CMarshaller;
import middleware.distribution.marshaller.CMarshaller1;
import middleware.infrastructure.CServerRequestHandler;
import utils.Utils;

public class MiddlewareGenericServerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("MiddlewareGenericServerConf",false); // server1 -> server2
		
		// components
		Component invoker = new CInvoker("invoker");    
		Component marshaller = new CMarshaller1("marshaller1");
		Component srh = new CServerRequestHandler("srh",Utils.GENERIC_SERVICE_PORT);
		Component server = new CGenericServer("server");

		// connectors
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");
		Connector t3 = new TRequestReply("t3");

		// attachments
		conf.connect(srh,t1,invoker);
		conf.connect(invoker, t2, marshaller);
		conf.connect(invoker, t3, server);
		
		return conf;
	}
}
