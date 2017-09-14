package applications.distributed.echo;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.marshaller.CMarshaller;
import middleware.distribution.requestor.CRequestor;
import middleware.infrastructure.CClientRequestHandler;

public class MiddlewareClientConf {

	public Configuration configure() {
		Configuration conf = new Configuration("MiddlewareClientConf",false); 
		
		// components
		Component client = new CEchoClient("client");
		Component proxy = new CEchoClientProxy("proxy");
		Component marshaller = new CMarshaller("marshaller");
		Component requestor = new CRequestor("requestor");  
		Component crh = new CClientRequestHandler("crh");

		// connectors
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");
		Connector t3 = new TRequestReply("t3");
		Connector t4 = new TRequestReply("t4");

		// attachments
		conf.connect(client,t1,proxy);
		conf.connect(proxy,t2,requestor);
		conf.connect(requestor,t3,marshaller);
		conf.connect(requestor,t4,crh);
		
		return conf;
	}
}