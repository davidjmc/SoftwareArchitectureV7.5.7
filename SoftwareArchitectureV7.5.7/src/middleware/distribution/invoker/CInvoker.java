package middleware.distribution.invoker;

import java.util.ArrayList;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.component.CNToMSeq;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CInvoker extends CNToMSeq {
	private Queue invPToInvR1Queue = new Queue();
	private Queue terR1ToInvR2Queue = new Queue();
	private Queue terR2ToInvR3Queue = new Queue();
	private Queue terR3ToTerPQueue = new Queue();

	public CInvoker(String name) {
		super(name, 1, 2);

		this.semantics = new Semantics("invP.e1->i_PosInvP->" + // from srh
				"i_PreInvR1->invR.e2->i_PosInvR1->" + // to marshaller
				"i_PreTerR1->terR.e2->i_PosTerR1->" + // from marshaller
				"i_PreInvR2->invR.e3->i_PosInvR2->" + // to server
				"i_PreTerR2->terR.e3->i_PosTerR2->" + // from server
				"i_PreInvR3->invR.e2->i_PosInvR3->" + // to marshaller
				"i_PreTerR3->terR.e2->i_PosTerR3->" + // from marshaller
				"i_PreTerP->terP.e1->i_PosTerP"); // to srh
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {

		takePut(local, invPToInvR1Queue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR1(Queue local, ExecutionUnit unit) {
		SAMessage inMessage;
		Request outRequest = new Request();
		ArrayList<Object> args = new ArrayList<Object>();

		inMessage = (SAMessage) take(invPToInvR1Queue, unit);
		if (inMessage != null) {
			outRequest.setOp("unmarshall");
			args.add(inMessage.getContent()); // message to be unmarshalled
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
		Request outRequest = new Request();
		Request inRequest = new Request();
		Reply reply = new Reply();
		SAMessage inMessage;

		inMessage = (SAMessage) take(terR1ToInvR2Queue, unit);
		if (inMessage != null) {
			reply = (Reply) inMessage.getContent();
			inRequest = (Request) reply.getR();

			args = inRequest.getArgs();

			outRequest.setOp(inRequest.getOp());
			outRequest.setArgs(args);
			local.getQueue().offer((SAMessage) new SAMessage(outRequest));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR2(Queue local, ExecutionUnit unit) {

		takePut(local, terR2ToInvR3Queue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR3(Queue local, ExecutionUnit unit) {
		SAMessage inMessage;
		Reply inReply;
		Request outRequest = new Request();
		ArrayList<Object> args = new ArrayList<Object>();

		inMessage = (SAMessage) take(terR2ToInvR3Queue, unit);
		if (inMessage != null) {
			inReply = (Reply) inMessage.getContent();
			outRequest.setOp("marshall");
			args.add(inReply.getR()); // message to be marshalled
			outRequest.setArgs(args);
			local.getQueue().offer(new SAMessage(outRequest));
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR3(Queue local, ExecutionUnit unit) {
		SAMessage inMessage;
		Reply inReply;

		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			inReply = (Reply) inMessage.getContent();
			terR3ToTerPQueue.getQueue().offer(inReply.getR());
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {

		takePut(terR3ToTerPQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosInvR1(Queue local, ExecutionUnit unit) {
	}

	public void i_PreTerR1(Queue local, ExecutionUnit unit) {
	}

	public void i_PosInvR2(Queue local, ExecutionUnit unit) {
	}

	public void i_PosInvR3(Queue local, ExecutionUnit unit) {
	}

	public void i_PreTerR2(Queue local, ExecutionUnit unit) {
	}

	public void i_PreTerR3(Queue local, ExecutionUnit unit) {
	}
}
