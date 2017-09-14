package framework.basic;

import container.ExecutionUnit;
import container.Queue;

public interface IElement {

	public void i_PreInvP(Queue local, ExecutionUnit unit);

	public void invP(Queue local, Queue remote, ExecutionUnit unit);

	public void i_PosInvP(Queue local, ExecutionUnit unit);

	public void i_PreTerP(Queue local, ExecutionUnit unit);

	public void terP(Queue local, Queue remote, ExecutionUnit unit);

	public void i_PosTerP(Queue local, ExecutionUnit unit);

	public void i_PreInvR(Queue local, ExecutionUnit unit);

	public void invR(Queue local, Queue remote, ExecutionUnit unit);

	public void i_PosInvR(Queue local,ExecutionUnit unit);

	public void i_PreTerR(Queue local,ExecutionUnit unit);

	public void terR(Queue local, Queue remote, ExecutionUnit unit);

	public void i_PosTerR(Queue local, ExecutionUnit unit);
}