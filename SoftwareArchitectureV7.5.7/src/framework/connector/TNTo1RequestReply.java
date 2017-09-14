package framework.connector;

import container.Queue;
import framework.basic.Behaviour;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public class TNTo1RequestReply extends Connector {
	private Queue invPToInvRQueue = new Queue();
	private Queue terRToTerPQueue = new Queue();

	public TNTo1RequestReply(String name, int NProvidedInterfaces) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.semantics = new Semantics(new Behaviour(new String("")));

		for (int idx = 0; idx < NProvidedInterfaces; idx++) {
			this.interfaces.add(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
			this.semantics
					.setStandardBehaviour(this.getSemantics().getStandardBehaviour().getActions() + "i_PreInvP"
							+ Utils.PREFIX_ACTION + "invP.e" + (idx + 1) + Utils.PREFIX_ACTION + "i_PosInvP"
							+ Utils.PREFIX_ACTION + "i_PreInvR" + Utils.PREFIX_ACTION + "invR.e0" + Utils.PREFIX_ACTION
							+ "i_PosInvR" + Utils.PREFIX_ACTION + "i_PreTerR" + Utils.PREFIX_ACTION + "terR.e0"
							+ Utils.PREFIX_ACTION + "i_PosTerR" + Utils.PREFIX_ACTION + "i_PreTerP"
							+ Utils.PREFIX_ACTION + "terP.e" + (idx + 1) + Utils.PREFIX_ACTION + "i_PosTerP");
			if (idx < NProvidedInterfaces - 1)
				this.semantics.setStandardBehaviour(
						this.getSemantics().getStandardBehaviour().getActions() + Utils.PREFIX_ACTION);
		}
		this.semantics.setRuntimeBehaviour(this.semantics.getStandardBehaviour());
		this.semantics.setGraph(this.semantics.createGraph());		
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