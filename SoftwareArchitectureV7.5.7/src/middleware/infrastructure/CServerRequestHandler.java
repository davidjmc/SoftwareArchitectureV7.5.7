package middleware.infrastructure;

import java.util.concurrent.TimeUnit;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import utils.Utils;

public class CServerRequestHandler extends CClient {
	protected int localPort;
	protected int remotePort = 99999;
	protected String remoteHost = "";

	public CServerRequestHandler(String name) {
		super(name);
	}

	public CServerRequestHandler() {
		super("");
	}

	public CServerRequestHandler(String name, int lp) {
		super(name);
		this.localPort = lp;
	}

	@Override
	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		byte[] msg;

		// receive through execution environment
		try {
			msg = (byte[]) unit.getEnv().getCommunicationManager().getIn().getQueue().poll(Utils.QUEUE_WAITING_TIME,TimeUnit.MILLISECONDS);
			if (msg != null)
				local.getQueue().offer(new SAMessage(msg));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		byte[] outMessage = null;

		// send through execution environment
		outMessage = (byte[]) take(local, unit);
		if (outMessage != null)
			unit.getEnv().getCommunicationManager().getOut().getQueue().offer(outMessage);

		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
