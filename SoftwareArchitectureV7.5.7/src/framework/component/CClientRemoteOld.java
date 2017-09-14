package framework.component;

import java.util.ArrayList;
import java.util.Arrays;

import container.Queue;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

// CClientRemote should be a component of type "server" 
public abstract class CClientRemoteOld extends Component {
	protected int localPort = 99999;
	protected static Queue invPToInvRQueue = new Queue();
	protected static Queue terRToTerPQueue = new Queue();

	public CClientRemoteOld(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.interfaces = Arrays.asList(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.semantics = new Semantics(
				"i_PreInvP -> invP.e1 -> i_PosInvP -> "
				+ "i_PreInvR -> invR.e2-> i_PosInvR -> "
				+ "i_PreTerR -> terR.e2 -> i_PosTerR -> "
				+ "i_PreTerP -> terP.e1 -> i_PosTerP");
	}

	public void invR(Queue local, Queue remote) {
		SAMessage inMessage = new SAMessage();
		ArrayList<Object> args = new ArrayList<Object>();

		// send through execution environment
		try {
			inMessage = (SAMessage) invPToInvRQueue.getQueue().take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		ArrayList<Object> content = (ArrayList<Object>) inMessage.getContent();
		args = content;
		
		localPort = this.getRuntimeInfo().getEnv().getCommunicationManager().send((byte[]) args.get(0),
				(String) args.get(1), (int) args.get(2));

		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void terR(Queue local, Queue remote) {
		byte[] msg;
		ArrayList<Object> rcvInformation = new ArrayList<Object>();

		// receive through execution environment
		rcvInformation = this.runtimeInfo.getEnv().getCommunicationManager().receive(this.localPort);
		msg = (byte[]) rcvInformation.get(2);
		try {
			local.getQueue().put(new SAMessage(msg));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR(Queue local){
		try {
			terRToTerPQueue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());		
	}
	
	public void i_PreTerP(Queue local){
		try {
			local.getQueue().put(terRToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
}
