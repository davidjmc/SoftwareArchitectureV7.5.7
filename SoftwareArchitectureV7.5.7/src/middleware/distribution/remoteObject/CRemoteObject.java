package middleware.distribution.remoteObject;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import container.Queue;
import framework.basic.Behaviour;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RuntimeInfo;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.basic.Type;
import framework.component.Component;
import utils.Utils;

public class CRemoteObject extends Component {

	public CRemoteObject(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.semantics = new Semantics(
				new Behaviour(new String("iPreInvP -> invP[1] -> iPosInvP"+
		                                 "iPreTerP -> terP[1] -> iPosTerP" )));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
	}

	public void invP(String me) {
		String partner = this.getConstraints().getInvPPartner().get(me);
		Queue in = new Queue();
		BlockingQueue<Object> invP = this.runtimeInfo.getInvP().get(me).getQueue();

		in = matchQueue(me, "invP", partner); 

		try {
			invP.put((SAMessage) in.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " "+partner);
	}

	public void terP(String me) {
		String partner = this.getConstraints().getTerPPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> terP = this.runtimeInfo.getTerP().get(me).getQueue();

		out = matchQueue(me, "terP", partner); 
		
		try {
			out.getQueue().put(terP.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " "+partner);
	}

	@Override
	public void i_PreInvP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreInvR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerR(String partner) {
		// TODO Auto-generated method stub
		
	}
}