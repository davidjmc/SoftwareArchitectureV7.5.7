package applications.distributed.echo;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CEchoServer extends CServer {
	private static Queue invPToTerPQueue = new Queue();

	public CEchoServer(String name) {
		super(name);
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Request inRequest = new Request();
		Reply outReply;

		inMessage = (SAMessage) take(local, unit);

		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();

			switch (inRequest.getOp()) {
			case "echo":
				outReply = new Reply(inRequest.getArgs().get(0) + " >>> " + this.getIdentification().getName());
				outMessage = new SAMessage(outReply);
				invPToTerPQueue.getQueue().offer(outMessage);
				break;
			default:
				System.out.println("Operation '" + inRequest.getOp() + "' not implemented by EchoServer");
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
