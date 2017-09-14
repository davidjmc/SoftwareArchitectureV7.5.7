package applications.distributed.calculator;

import java.util.ArrayList;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import middleware.basic.Request;
import middleware.distribution.clientproxy.CClientProxy;
import utils.MyError;
import utils.Utils;

public class CCalculatorClientProxy extends CClientProxy {
	private Queue invPToInvRQueue = new Queue();
	private Queue terRToTerPQueue = new Queue();
	private String host = "localhost";
	private int port = Utils.CALCULATOR_SERVICE_PORT; // TODO

	public CCalculatorClientProxy(String name) {
		super(name);
	}

	public CCalculatorClientProxy() {
		super("");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		Request inRequest = new Request();
		Request outRequest = null;
		ArrayList<Object> args = new ArrayList<Object>();

		inMessage = (SAMessage) take(local, unit);

		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();
			switch (inRequest.getOp()) {
			case "add":
				args.add(inRequest);
				args.add(host);
				args.add(port);
				outRequest = new Request("invoke", args);
				break;
			default:
				new MyError("Operation '" + inRequest.getOp() + "' is not implemented by " + this.getClass(),
						Utils.FATAL_ERROR).print();
				break;
			}
			invPToInvRQueue.getQueue().offer(new SAMessage(outRequest));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		takePut(invPToInvRQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {
		takePut(terRToTerPQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		takePut(local, terRToTerPQueue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
