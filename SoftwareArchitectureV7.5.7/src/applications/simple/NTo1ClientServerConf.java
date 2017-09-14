package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TNTo1RequestReply;

public class NTo1ClientServerConf {

	public Configuration configure(){
		Configuration conf = new Configuration("NTo1ClientServerConf",false);

		// components
		Component echoClient1 = new CEchoClient("client1");
		Component echoClient2 = new CEchoClient("client2");
		Component echoClient3 = new CEchoClient("client3");
		Component echoServer = new CEchoServer1("server");

		// connectors
		Connector t0 = new TNTo1RequestReply("t0",3);

		// behaviors
		echoClient1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client1]]"));
		echoClient2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client2]]"));
		echoClient3.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client3]]"));
		
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e0<-server]]","[[e1<-client1]]","[[e2<-client2]]","[[e3<-client3]]"));
		echoServer.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server]]"));

		// attachments
		conf.connect(echoClient1, t0, echoServer);
		conf.connect(echoClient2, t0, echoServer);
		conf.connect(echoClient3, t0, echoServer);
		
		return conf;
	}
}