package applications.simple;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CReceiver;
import middleware.basic.Request;

public class CEchoReceiver1 extends CReceiver {

	public CEchoReceiver1(String name) {
		super(name);
	}

	public CEchoReceiver1() {
		super("");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		Request inRequest = new Request();

        inMessage = (SAMessage) take(local,unit);
		
		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();

			switch (inRequest.getOp()) {
			case "echo":
				System.out.println(this.getClass() + " : " + inRequest.getArgs().get(0));
				break;
			}
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
