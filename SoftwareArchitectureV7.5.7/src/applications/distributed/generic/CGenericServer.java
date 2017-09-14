package applications.distributed.generic;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CGenericServer extends CServer {
	private static Queue invPToTerPQueue = new Queue();
	private static int serviceTime = 100;

	public CGenericServer(String name) {
		super(name);
	}

	public CGenericServer() {
		super("");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Request inRequest = new Request();
		Reply outReply;
		int p1;

		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();

			switch (inRequest.getOp()) {
			case "doSomething":
				p1 = (int) inRequest.getArgs().get(0);
				 try {
				 serviceTime = serviceTime + (int) (serviceTime*0.01);
				 Thread.currentThread();
				Thread.sleep(serviceTime);
				 } catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				 e1.printStackTrace();
				 }
				outReply = new Reply(p1);
				outMessage = new SAMessage(outReply);
				invPToTerPQueue.getQueue().offer(outMessage);
				break;
			default:
				System.out.println("Operation '" + inRequest.getOp() + "' not implemented by " + this.getClass());
				System.exit(0);
			}
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {

		takePut(invPToTerPQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
