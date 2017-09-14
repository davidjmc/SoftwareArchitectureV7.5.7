package applications.distributed.calculator;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CCalculatorServer extends CServer {
	private static Queue invPToTerPQueue = new Queue();

	public CCalculatorServer(String name) {
		super(name);
	}

	public CCalculatorServer() {
		super("");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Request inRequest = new Request();
		Reply outReply;
		int p1, p2;

		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();
			switch (inRequest.getOp()) {
			case "add":
				p1 = (int) inRequest.getArgs().get(0);
				p2 = (int) inRequest.getArgs().get(1);
				outReply = new Reply(p1 + p2);
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
