package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TNTo1OneWay;

public class NTo1SenderReceiverConf {

	public Configuration configure() {
		Configuration conf = new Configuration("NTo1SenderReceiverConf", true);

		// components
		Component sender1  = new CEchoSender("sender1");
		Component sender2  = new CEchoSender("sender2");
		Component sender3  = new CEchoSender("sender3");
		Component receiver = new CEchoReceiver1("receiver");

		// connectors
		Connector t0 = new TNTo1OneWay("t0", 3);

		// behaviors
		sender1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-sender1]]"));
		sender2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-sender2]]"));
		sender3.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-sender3]]"));

		t0.getSemantics().getRuntimeBehaviour().relabel(
				Arrays.asList("[[e0<-receiver]]", "[[e1<-sender1]]", "[[e2<-sender2]]", "[[e3<-sender3]]"));

		receiver.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-receiver]]"));

		// attachments
		conf.connect(sender1, t0, receiver);
		conf.connect(sender2, t0, receiver);
		conf.connect(sender3, t0, receiver);

		return conf;
	}
}