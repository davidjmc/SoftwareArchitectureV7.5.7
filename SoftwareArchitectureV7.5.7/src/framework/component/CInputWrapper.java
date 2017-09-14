package framework.component;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import container.Queue;
import framework.basic.Behaviour;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.basic.Type;
import middleware.distribution.miop.Reply;
import utils.Utils;

public class CInputWrapper extends Component {
	private Queue fromInvPtoInvRQueue = new Queue();
	private Queue fromTerRtoTerPQueue = new Queue();
	
	public CInputWrapper(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.semantics = new Semantics(new Behaviour(new String(
				"iPreInvP -> invP[1] -> iPosInvP -> "+
				"iPreInvR -> invR[2]-> iPosInvR -> "+
				"iPreTerR -> terR[2] -> iPosTerR -> "+
				"iPreTerP -> terP[1] -> PosTerP")));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
	}


	// action
	public void invP(SAMessage m) throws InterruptedException { 
		BlockingQueue<Object> invP = this.runtimeInfo.getInvP().get(0).getQueue();

		invP.put(m);
		this.fromInvPtoInvRQueue.getQueue().put(invP.take());

		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void invR(String me) {
		String partner = this.getConstraints().getInvRPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> invR = this.runtimeInfo.getInvR().get(me).getQueue();

		try {
			invR.put(fromInvPtoInvRQueue.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		out = matchQueue(me, "invR", partner);

		try {
			out.getQueue().put(invR.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	// action
	public void terR(String me) {
		String partner = this.getConstraints().getTerRPartner().get(me);
		Queue in = new Queue();
		BlockingQueue<Object> terR = this.runtimeInfo.getTerR().get(me).getQueue();

		in = matchQueue(me, "terR", partner);

		try {
			terR.put((SAMessage) in.getQueue().take());
			fromTerRtoTerPQueue.getQueue().put(terR.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	// action
	public Object terP() throws InterruptedException { 
		SAMessage inMessage = new SAMessage();
		Reply inReply = new Reply();
		 
		inMessage = (SAMessage) fromTerRtoTerPQueue.getQueue().take();
		inReply = (Reply) inMessage.getContent();

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");

		return inReply.getRep(); 
	}


	@Override
	public void i_PreInvP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void invP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PosInvP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PreTerP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void terP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PosTerP(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PreInvR(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PosInvR(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PreTerR(String me) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void i_PosTerR(String me) {
		// TODO Auto-generated method stub
		
	}
}