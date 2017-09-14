package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.requestor.CRequestor;

public class OneToNClientServerConf {
	
	public Configuration configure(){
		Configuration conf = new Configuration("OneToNClientServerConf",false);

		// components
		Component echoClient = new CEchoClient("client");
		Component server1    = new CServer1("server1");
		Component server2    = new CServer2("server2");
		Component requestor  = new CRequestor("requestor");

		// connectors
		Connector t0 = new TRequestReply("t0");
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");

		// behaviors
		echoClient.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]"));
		server1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server1]]"));
		server2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server2]]"));
		
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-requestor]]"));
		t1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t1]]","[[e2<-server1]]"));
		t2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t2]]","[[e2<-server2]]"));

		requestor.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-requestor]]","[[e2<-t1]]","[[e3<-t2]]"));

		// attachments
		conf.connect(echoClient, t0, requestor);
		conf.connect(requestor, t1, server1);
		conf.connect(requestor, t2, server2);
		
		return conf;
	}
}