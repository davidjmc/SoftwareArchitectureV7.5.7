package framework.connector;

import java.util.ArrayList;

import container.Queue;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Type;
import utils.Utils;

public class TNTo1OneWay extends Connector {
	private Queue invPToInvRQueue = new Queue();

	public TNTo1OneWay(String name,int NProvidedInterfaces) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.interfaces = new ArrayList<Object>();
		this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY));
		for (int idx = 1; idx <= NProvidedInterfaces; idx++) {
			this.interfaces.add(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
			this.semantics.setStandardBehaviour(this.getSemantics().getStandardBehaviour().getActions() + 
					"i_PreInvP" + Utils.PREFIX_ACTION+
					"invP.e" + idx + Utils.PREFIX_ACTION+
					"i_PosInvP" + Utils.PREFIX_ACTION+
					"i_PreInvR" + Utils.PREFIX_ACTION+
					"invR.e0"   + Utils.PREFIX_ACTION+
					"i_PosInvR "+ Utils.PREFIX_ACTION);
		}
		// adjust last operator
		String tempSemantics = this.getSemantics().getStandardBehaviour().getActions();
		this.semantics.setStandardBehaviour(tempSemantics.substring(0, tempSemantics.lastIndexOf(Utils.PREFIX_ACTION)));
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
}