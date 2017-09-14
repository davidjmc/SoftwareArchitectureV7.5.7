package applications.simple;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;

public class ClientServerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("ClientServerConf",true); // server1 -> server2

		// components
		Component echoClient = new CEchoClient("client");
		Component echoServer = new CEchoServer1("server1");

		// connectors
		Connector t0 = new TRequestReply("t0");

		// attachments
		conf.connect(echoClient, t0, echoServer);
		
		return conf;
	}
}