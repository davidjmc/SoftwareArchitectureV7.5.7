package middleware.distribution.requestor;

import java.util.ArrayList;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.component.CNToMSeq;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CRequestor extends CNToMSeq {
	private Queue invPToInvR1Queue = new Queue();
	private Queue terR1ToInvR2Queue = new Queue();
	private Queue terR2ToInvR3Queue = new Queue();
	private Queue terR3ToTerPQueue = new Queue();
	private String host;
	private int port;

	public CRequestor(String name) {
		super(name, 1, 2);
		this.semantics = new Semantics("invP.e1->i_PosInvP->" + // from proxy
				"i_PreInvR1->invR.e2->i_PosInvR1->" + // to marshaller
				"i_PreTerR1->terR.e2->i_PosTerR1->" + // from marshaller
				"i_PreInvR2->invR.e3->i_PosInvR2->" + // to crh
				"i_PreTerR2->terR.e3->i_PosTerR2->" + // from crh
				"i_PreInvR3->invR.e2->i_PosInvR3->" + // to marshaller
				"i_PreTerR3->terR.e2->i_PosTerR3->" + // from marshaller
				"i_PreTerP->terP.e1->i_PosTerP"); // to clientproxy
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		takePut(local,invPToInvR1Queue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosInvR1(Queue local, ExecutionUnit unit) {
	}

	public void i_PosInvR2(Queue local, ExecutionUnit unit) {
	}

	public void i_PosInvR3(Queue local, ExecutionUnit unit) {
	}

	public void i_PreTerR1(Queue local, ExecutionUnit unit) {

	}

	public void i_PreTerR2(Queue local, ExecutionUnit unit) {

	}

	public void i_PreTerR3(Queue local, ExecutionUnit unit) {

	}

	public void i_PreInvR1(Queue local, ExecutionUnit unit) {
		SAMessage inMessage;
		Request outRequest = new Request();
		Request inRequest = new Request();
		ArrayList<Object> args = new ArrayList<Object>();

		inMessage = (SAMessage) take(invPToInvR1Queue, unit);
		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();
			outRequest.setOp("marshall");
			args.add(inRequest.getArgs().get(0)); // message to be
													// marshalled
			this.host = (String) inRequest.getArgs().get(1); // host
			this.port = (int) inRequest.getArgs().get(2); // port
			outRequest.setArgs(args);
			local.getQueue().offer(new SAMessage(outRequest));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR1(Queue local, ExecutionUnit unit) {
		takePut(local, terR1ToInvR2Queue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR2(Queue local, ExecutionUnit unit) {
		ArrayList<Object> args = new ArrayList<Object>();
		Request request = new Request();
		Reply reply = new Reply();
		SAMessage inMessage;

		inMessage = (SAMessage) take(terR1ToInvR2Queue, unit);
		if (inMessage != null) {
			reply = (Reply) inMessage.getContent();
			request.setOp("send");
			args.add(reply.getR());
			args.add(this.host);
			args.add(this.port);
			request.setArgs(args);
			local.getQueue().offer((SAMessage) new SAMessage(request));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR2(Queue local, ExecutionUnit unit) {
		takePut(local, terR2ToInvR3Queue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR3(Queue local, ExecutionUnit unit) {
		Request outRequest = new Request();
		byte[] outMessage;
		ArrayList<Object> args = new ArrayList<Object>();
		SAMessage inMessage;

		inMessage = (SAMessage) take(terR2ToInvR3Queue, unit);
		if (inMessage != null) {
			outMessage = (byte[]) inMessage.getContent();
			outRequest.setOp("unmarshall");
			args.add(outMessage); // message to be marshalled
			outRequest.setArgs(args);
			local.getQueue().offer(new SAMessage(outRequest));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR3(Queue local, ExecutionUnit unit) {
		takePut(local, terR3ToTerPQueue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {
		takePut(terR3ToTerPQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
