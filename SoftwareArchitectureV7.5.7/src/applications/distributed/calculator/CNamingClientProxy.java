package applications.distributed.calculator;

import java.util.ArrayList;

import container.Queue;
import framework.basic.SAMessage;
import middleware.basic.Request;
import middleware.distribution.clientproxy.CClientProxy;
import utils.MyError;
import utils.Utils;

public class CNamingClientProxy extends CClientProxy {
	private Queue invPToInvRQueue = new Queue();
	private Queue terRToTerPQueue = new Queue();
	private String host = "localhost"; 
	private int port = Utils.NAMING_SERVICE_PORT; // TODO 

	public CNamingClientProxy(String name) {
		super(name);
	}

	@Override
	public void i_PosInvP(Queue local) {
		Queue tempQueue = new Queue();
		Request inRequest = new Request();
		Request outRequest = null;
		ArrayList<Object> args = new ArrayList<Object>();
		
		try {
			tempQueue.getQueue().put(local.getQueue().take());
			inRequest = (Request) ((SAMessage) tempQueue.getQueue().take()).getContent();
			switch(inRequest.getOp()){
			case "bind":
				args.add(inRequest);
				args.add(host);
				args.add(port);
				outRequest = new Request("invoke",args);
				break;
				default:
					new MyError("Operation '" + inRequest.getOp() + "' is not implemented by "+this.getClass(), Utils.FATAL_ERROR).print();					break;
			}
			invPToInvRQueue.getQueue().put(new SAMessage(outRequest));
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
