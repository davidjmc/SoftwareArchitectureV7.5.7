package applications.distributed.echo;

import java.util.ArrayList;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CEchoClient extends CClient {

	public CEchoClient(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		ArrayList<Object> args = new ArrayList<Object>();
		args.add("[ECHOED MESSAGE " + this.getIdentification().getName() + "]");
		Request request = new Request("echo", args);

		local.getQueue().offer((SAMessage) new SAMessage(request));
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		Reply reply = new Reply();
		SAMessage inMessage = new SAMessage();

		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			reply = (Reply) inMessage.getContent();
			System.out.println(this.getClass() + " [" + this.getIdentification().getName() + "]  : " + reply.getR());
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
