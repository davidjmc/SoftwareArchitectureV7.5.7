package applications.distributed.generic;

import java.util.ArrayList;
import java.util.Random;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;
import utils.Utils;

public class CGenericClient extends CClient {
	private long startTime;
	private long endTime;
	private long totalTime;
	private int count = 0;

	public CGenericClient(String name) {
		super(name);
	}

	public CGenericClient() {
		super("");
	}

	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		ArrayList<Object> args = new ArrayList<Object>();
		//double val = new Random().nextGaussian() * 10 + 100; // mean=100 std=10
		double val = new Random().nextGaussian() * 10 + 100; // mean=100 std=10
		int millisDelay = (int) Math.round(val);

		try {
			Thread.currentThread();
			Thread.sleep(millisDelay);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (count == Utils.NUMBER_OF_INVOCATIONS) {
			 float r = (float)
			 totalTime/(Utils.NUMBER_OF_INVOCATIONS*1000000);
			 //System.out.printf("Mean Duration Time: %.4f (ms)",r);
			System.exit(0);
		}
		count++;

		args.add(count);
		Request request = new Request("doSomething", args);
		local.getQueue().offer((SAMessage) new SAMessage(request));

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");

		// start timer
		startTime = System.nanoTime();
	}

	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		Reply reply = new Reply();
		SAMessage inMessage = new SAMessage();

		endTime = System.nanoTime();
		totalTime = totalTime + (endTime - startTime);
		float r = (float) (endTime - startTime) / (1000000);

		//System.out.printf("Duration time: %.4f\n", r);
		//System.out.printf("%.4f\n", r);

		inMessage = (SAMessage) take(local, unit);
		if (inMessage != null) {
			reply = (Reply) inMessage.getContent();
			reply.getR();
			 System.out.println(this.getClass() +
			 "["+this.getIdentification().getName()+"] : " + reply.getR());
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
