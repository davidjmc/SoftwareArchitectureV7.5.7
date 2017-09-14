package applications.distributed.calculator;

import java.util.ArrayList;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;
import middleware.distribution.clientproxy.ClientProxy;

public class CNamingClient extends CClient {

	public CNamingClient(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local) {
		ArrayList<Object> args = new ArrayList<Object>();

		args.add("Calculator");
		args.add(new ClientProxy());
		Request request = new Request("bind", args);

		try {
			local.getQueue().put((SAMessage) new SAMessage(request));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void i_PosTerR(Queue local) {
		Reply reply = new Reply();

		try {
			reply = (Reply) ((SAMessage) local.getQueue().take()).getContent();
			reply.getR();
			 System.out.println(this.getClass() + "["+this.getIdentification().getName()+"] : " + reply.getR());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
