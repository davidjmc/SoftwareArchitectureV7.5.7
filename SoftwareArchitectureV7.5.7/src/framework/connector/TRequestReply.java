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

public class TRequestReply extends Connector {
	private Queue invPToInvRQueue = new Queue();
	private Queue terRToTerPQueue = new Queue();

	public TRequestReply(String name) {
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
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		takePut(local, invPToInvRQueue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		takePut(invPToInvRQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {
		takePut(terRToTerPQueue, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		takePut(local, terRToTerPQueue, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}