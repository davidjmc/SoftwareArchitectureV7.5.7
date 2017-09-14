package applications.simple;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CReceiver;
import middleware.basic.Request;

public class CEchoReceiver2 extends CReceiver {

	public CEchoReceiver2(String name) {
		super(name);
	}

	public CEchoReceiver2() {
		super("");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		Request inRequest = new Request();
		inMessage = (SAMessage) take(local, unit);

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
