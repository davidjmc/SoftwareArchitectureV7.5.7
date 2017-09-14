package applications.distributed.calculator;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.invoker.CInvoker;
import middleware.distribution.marshaller.CMarshaller;
import middleware.infrastructure.CServerRequestHandler;
import middleware.services.naming.CNamingServer;
import utils.Utils;

public class MiddlewareNamingServerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("MiddlewareNamingServerConf",false); 
		
		// components
		Component invoker = new CInvoker("invoker");    
		Component marshaller = new CMarshaller("marshaller");
		Component srh = new CServerRequestHandler("srh",Utils.NAMING_SERVICE_PORT);
		Component server = new CNamingServer("naming"); 

		// connectors
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");
		Connector t3 = new TRequestReply("t3");

		// behaviors
		srh.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-srh]]"));
		t1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-srh]]","[[e2<-t1]]"));
		invoker.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t1]]","[[e2<-t2]]","[[e3<-t3]]"));
		t2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t2]]","[[e2<-marshaller]]"));
		marshaller.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-marshaller]]"));
		t3.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t3]]","[[e2<-server]]"));
		server.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server]]"));

		// attachments
		conf.connect(srh,t1,invoker);
		conf.connect(invoker, t2, marshaller);
		conf.connect(invoker, t3, server);
		
		return conf;
	}
}