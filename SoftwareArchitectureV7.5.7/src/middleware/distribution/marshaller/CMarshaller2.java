package middleware.distribution.marshaller;

import java.io.IOException;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CMarshaller2 extends CServer {
	private MarshallerImpl marshaller = new MarshallerImpl();
	private static Queue invPToTerPQueue = new Queue();

	public CMarshaller2(String name) {
		super(name);
	}

	public CMarshaller2() {
		super("");
	}

	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Object msgToBeMarshalled = null;
		Object unMarshalledMsg = null;
		byte[] marshalledMsg = null;
		byte[] msgToBeUnmarshalled = null;
		Request inRequest = new Request();
		Reply outReply;

		inMessage = (SAMessage) take(local, unit);

		if (inMessage != null) {
			inRequest = (Request) inMessage.getContent();

			switch (inRequest.getOp()) {
			case "marshall":
				msgToBeMarshalled = (Object) inRequest.getArgs().get(0);
				try {
					marshalledMsg = marshaller.marshall(msgToBeMarshalled);
					outReply = new Reply((Object) marshalledMsg);
					outMessage = new SAMessage(outReply);
					invPToTerPQueue.getQueue().offer(outMessage);
				} catch (IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case "unmarshall":
				msgToBeUnmarshalled = (byte[]) inRequest.getArgs().get(0);
				unMarshalledMsg = marshaller.unmarshall(msgToBeUnmarshalled);
				outReply = new Reply((Object) unMarshalledMsg);
				outMessage = new SAMessage(outReply);

				invPToTerPQueue.getQueue().offer(outMessage);
				break;
			default:
				System.out.println("ERROR: Operation '" + inRequest.getOp() + "' not implemented by Marshaller");
				System.exit(0);
				break;
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