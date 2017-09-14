package applications.distributed.calculator;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.invoker.CInvoker;
import middleware.distribution.marshaller.CMarshaller;
import middleware.infrastructure.CServerRequestHandler;
import utils.Utils;

public class MiddlewareCalculatorServerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("MiddlewareCalculatorServerConf",false); // server1 -> server2
		
		// components
		Component invoker = new CInvoker("invoker");    
		Component marshaller = new CMarshaller("marshaller");
		Component srh = new CServerRequestHandler("srh",Utils.CALCULATOR_SERVICE_PORT);
		Component server = new CCalculatorServer("server");

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