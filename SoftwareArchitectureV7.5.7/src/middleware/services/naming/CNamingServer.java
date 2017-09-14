package middleware.services.naming;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;
import middleware.distribution.clientproxy.ClientProxy;

public class CNamingServer extends CServer {
	private static Queue invPToTerPQueue = new Queue();

	public CNamingServer(String name) {
		super(name);
	}

	@Override
	public void i_PosInvP(Queue local) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Request inRequest = new Request();
		Reply outReply;
		String p1;
		ClientProxy p2;
		NamingImpl naming = new NamingImpl();

		try {
			inMessage = (SAMessage) local.getQueue().take();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		inRequest = (Request) inMessage.getContent();

		switch (inRequest.getOp()) {
		case "bind":
			p1 = (String) inRequest.getArgs().get(0);
			p2 = (ClientProxy) inRequest.getArgs().get(1);
			naming.bind(p1, p2);
			outReply = new Reply(); // TODO 
			outMessage = new SAMessage(outReply);
			try {
				invPToTerPQueue.getQueue().put(outMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "lookup":
			p1 = (String) inRequest.getArgs().get(0);
			outReply = new Reply(naming.lookup(p1));
			outMessage = new SAMessage(outReply);
			try {
				invPToTerPQueue.getQueue().put(outMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case "list":
			// TODO
		default:
			System.out.println("Operation '" + inRequest.getOp() + "' not implemented by " + this.getClass());
			System.exit(0);
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local) {

		try {
			local.getQueue().put(invPToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
