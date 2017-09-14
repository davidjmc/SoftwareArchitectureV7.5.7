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

public class CServerRemote extends Component {
	protected int localPort = 9090;
	protected int remotePort = 99999; 
	protected String remoteHost = "";
	protected static Queue invPToInvRQueue = new Queue();
	protected static Queue terRToTerPQueue = new Queue();

	public CServerRemote(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.semantics = new Semantics(
				"i_PreInvP -> invP.e1 -> i_PosInvP -> i_PreInvR -> invR.e2-> i_PosInvR -> i_PreTerR -> terR.e2 -> i_PosTerR -> i_PreTerP -> terP.e1 -> i_PosTerP");
	}

	public void invP(Queue local, Queue remote) {
			byte[] msg;
			ArrayList<Object> rcvInformation = new ArrayList<Object>();

			// receive through execution environment
			rcvInformation = this.runtimeInfo.getEnv().getCommunicationManager().receive(localPort);
			this.remoteHost = (String) rcvInformation.get(0);
			this.remotePort = (int) rcvInformation.get(1);
			msg = (byte[]) rcvInformation.get(2);
			try {
				local.getQueue().put(new SAMessage(msg));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	public void terP(Queue local, Queue remote) {
		byte [] outMessage = null;

		// send through execution environment
		try {
			outMessage = (byte[]) terRToTerPQueue.getQueue().take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.getRuntimeInfo().getEnv().getCommunicationManager().send(outMessage,
				this.remoteHost, this.remotePort);

		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}