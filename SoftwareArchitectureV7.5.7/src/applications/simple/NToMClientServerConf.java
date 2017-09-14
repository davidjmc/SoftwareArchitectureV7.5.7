package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TNTo1RequestReply;
import framework.connector.TRequestReply;
import middleware.distribution.requestor.CRequestor;

public class NToMClientServerConf {

	public Configuration configure(){
		Configuration conf = new Configuration("NToMClientServerConf",false);
		
		// components
		Component echoClient1 = new CEchoClient("client1");
		Component echoClient2 = new CEchoClient("client2");
		Component echoClient3 = new CEchoClient("client3");

		Component server1 = new CServer1("server1");
		Component server2 = new CServer2("server2");
		Component requestor = new CRequestor("requestor");

		// connectors
		Connector t0 = new TNTo1RequestReply("t0",3);
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");

		// behaviors
		echoClient1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client1]]"));
		echoClient2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client2]]"));
		echoClient3.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client3]]"));
		
		t1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t1]]","[[e2<-server1]]"));
		t2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-t2]]","[[e2<-server2]]"));
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e0<-requestor]]","[[e1<-client1]]","[[e2<-client2]]","[[e3<-client3]]"));
		
		requestor.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-requestor]]","[[e2<-t1]]","[[e3<-t2]]"));		
		server1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server1]]"));
		server2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server2]]"));

		// attachments
		conf.connect(echoClient1, t0, requestor);
		conf.connect(echoClient2, t0, requestor);
		conf.connect(echoClient3, t0, requestor);
		conf.connect(requestor, t1, server1);
		conf.connect(requestor, t2, server2);
		
		return conf;
	}
}