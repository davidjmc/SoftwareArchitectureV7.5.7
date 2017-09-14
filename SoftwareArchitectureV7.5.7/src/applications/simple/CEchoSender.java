package applications.simple;

import java.util.ArrayList;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CSender;
import middleware.basic.Request;

public class CEchoSender extends CSender {

	public CEchoSender(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		ArrayList<Object> args = new ArrayList<Object>();
		Request request;
		
		args.add("ECHOED MESSAGE " + this.getIdentification().getName() + "]");
		
		request = new Request("echo",args);
		
		local.getQueue().offer((SAMessage) new SAMessage(request));
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
