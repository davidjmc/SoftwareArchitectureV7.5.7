package framework.component;

import java.util.Arrays;

import container.Queue;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public class CClientServer extends Component {
	private Queue invPToInvRQueue = new Queue();
	private Queue terRToTerPQueue = new Queue();
	
	public CClientServer(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.semantics = new Semantics(
				"invP.e1 -> i_PosInvP -> i_PreInvR -> invR.e2  -> terR.e2 -> i_PosTerR -> i_PreTerP -> terP.e1");
	}

	@Override
	public void i_PosInvP(Queue local) {
		try {
			invPToInvRQueue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreInvR(Queue local) {
		try {
			local.getQueue().put(invPToInvRQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreTerP(Queue local) {
		try {
			local.getQueue().put(this.terRToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PosTerR(Queue local) {
		try {
			terRToTerPQueue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
