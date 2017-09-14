package applications.simple;

import container.ExecutionEnvironment;
import framework.configuration.Configuration;

public class Main {

	public static void main(String[] args) {
		
		//Configuration conf =  new SenderReceiverConf().configure();
		Configuration conf = new ClientServerConf().configure();
	    //Configuration conf = new NToMClientServerConf().configure();
		//Configuration conf = new NTo1SenderReceiverConf().configure();
	    //Configuration conf = new NTo1ClientServerConf().configure();
		//Configuration conf = new OneToNClientServerConf().configure();
		//Configuration conf = new ClientMarshallerConf().configure();
		ExecutionEnvironment env = new ExecutionEnvironment();		
		
		env.deploy(conf);
	}
}
