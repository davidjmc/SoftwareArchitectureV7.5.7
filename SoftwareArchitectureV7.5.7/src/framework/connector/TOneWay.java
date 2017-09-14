package framework.connector;

import java.util.Arrays;

import container.ExecutionUnit;
import container.Queue;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public class TOneWay extends Connector {
	private Queue invPToInvRQueue = new Queue();

	public TOneWay(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new ProvidedInterface(Utils.INTERFACE_ONE_WAY),
				new RequiredInterface(Utils.INTERFACE_ONE_WAY));
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.semantics = new Semantics("invP.e1 -> i_PosInvP -> i_PreInvR -> invR.e2");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		takePut(local, invPToInvRQueue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
	
	@Override
	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		takePut(invPToInvRQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}