package applications.simple;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TOneWay;

public class SenderReceiverConf {

	public Configuration configure() {
		Configuration conf = new Configuration("SenderReceiverConf",true);

		// components
		Component sender = new CEchoSender("sender");
		Component receiver = new CEchoReceiver1("receiver1");

		// connectors
		Connector t0 = new TOneWay("t0");

		// attachments
		conf.connect(sender, t0, receiver);

		return conf;
	}
}