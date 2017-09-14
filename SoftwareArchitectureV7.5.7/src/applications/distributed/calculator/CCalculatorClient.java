package applications.distributed.calculator;

import java.util.ArrayList;
import java.util.Random;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;
import utils.Utils;

public class CCalculatorClient extends CClient {
	private long startTime;
	private long totalTime;
	private int count = 0;

	public CCalculatorClient(String name) {
		super(name);
	}

	public CCalculatorClient() {
		super("");
	}

	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		ArrayList<Object> args = new ArrayList<Object>();
		//double val = new Random().nextGaussian() * 10 + 100; // mean=100
																// stddv=10
		//int millisDelay = (int) Math.round(val);

		//try {
		//	Thread.currentThread();
		//	Thread.sleep(millisDelay);
		//} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//}

//		if (count == Utils.NUMBER_OF_INVOCATIONS) {
	//		float r = (float) totalTime / (Utils.NUMBER_OF_INVOCATIONS * 1000000);
		//	System.out.printf("Mean Duration Time: %.4f (ms)", r);
			//System.exit(0);
		//}
		//count++;

		//args.add(count);
		//args.add(count);
		args.add(1);
		args.add(3);
		Request request = new Request("add", args);
		local.getQueue().offer((SAMessage) new SAMessage(request));

		// start timer
		//startTime = System.nanoTime();
	}

	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		Reply reply = new Reply();

		totalTime = totalTime + (System.nanoTime() - startTime);

		reply = (Reply) ((SAMessage) take(local, unit)).getContent();
		//reply.getR();
		//System.out.println(this.getClass() + "[" + this.getIdentification().getName() + "] : " + reply.getR());
	}
}
