package middleware.infrastructure;

import java.util.concurrent.TimeUnit;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Request;
import utils.MyError;
import utils.Utils;

public class CClientRequestHandler extends CServer {
	public CClientRequestHandler(String name) {
		super(name);
	}

	public CClientRequestHandler() {
		super("");
	}

	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		Request inRequest = new Request();
		SAMessage inMessage = new SAMessage();

		// send through the environment
		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();

			switch (inRequest.getOp()) {
			case "send":
				byte[] msg = (byte[]) inRequest.getArgs().get(0);
				//unit.getEnv().getCommunicationManager().getOut().getQueue().put(msg);
				unit.getEnv().getCommunicationManager().getOut().getQueue().offer(msg);
				here(Thread.currentThread().getStackTrace()[1].getMethodName());
				break;
			default:
				new MyError("Operation '" + inRequest.getOp() + "' is not implemented by CRH", Utils.FATAL_ERROR)
						.print();
				break;
			}
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreTerP(Queue local, ExecutionUnit unit) {
		byte[] msg = null;

		try {
			//msg = (byte[]) unit.getEnv().getCommunicationManager().getIn().getQueue().take();
			msg = (byte[]) unit.getEnv().getCommunicationManager().getIn().getQueue().poll(Utils.QUEUE_WAITING_TIME,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		if (msg != null) {
			local.getQueue().offer(new SAMessage(msg));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
