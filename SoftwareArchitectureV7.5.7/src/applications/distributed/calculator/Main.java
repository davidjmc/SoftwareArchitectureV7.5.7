package applications.distributed.calculator;

import container.ExecutionEnvironment;
import framework.configuration.Configuration;

public class Main {

	public static void main(String[] args) {
		
		Configuration confClient = new MiddlewareClientConf().configure();
		Configuration confServer = new MiddlewareCalculatorServerConf().configure();
		
		ExecutionEnvironment envClient = new ExecutionEnvironment();		
		ExecutionEnvironment envServer = new ExecutionEnvironment();
		
		envServer.deploy(confServer);
		envClient.deploy(confClient);
	}
}
