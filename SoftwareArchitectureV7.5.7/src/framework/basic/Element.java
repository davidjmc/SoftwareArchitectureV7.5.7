package framework.basic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import container.ExecutionEnvironment;
import container.ExecutionUnit;
import container.Queue;
import utils.Utils;

public abstract class Element implements IElement {
	protected Identification id;
	protected List<Object> interfaces;
	protected Type type;
	protected Semantics semantics;
	protected Constraints constraints;
	protected NonFunctionalProperties nfr;
	protected Evolution evolution;
	protected RuntimeInfo runtimeInfo;

	public Element() {
		this.id = new Identification();
		this.interfaces = new ArrayList<Object>();
		this.type = new Type(this);
		this.semantics = new Semantics();
		this.constraints = new Constraints();
		this.nfr = new NonFunctionalProperties();
		this.evolution = new Evolution();
		this.runtimeInfo = new RuntimeInfo();
	}

	public void configure(int inDegree, int outDegree) {

	}

	public void addProvidedInterface(int type) {
		this.interfaces.add(new ProvidedInterface(type));
	}

	public void addRequiredInterface(int type) {
		this.interfaces.add(new ProvidedInterface(type));
	}

	public Object findAvailabeInterfaceByType(Class<?> interfaceClass) {
		Interface tempInterface = null;
		Object returnObj = null;

		for (Object obj : this.getInterfaces()) {
			tempInterface = (Interface) obj;
			if (obj.getClass() == interfaceClass && tempInterface.isAvailable) {
				tempInterface.setAvailable(false);
				returnObj = obj;
				break;
			}
		}
		if (returnObj == null) {
			System.out.println(this.getClass() + " ERROR: " + interfaceClass.getName() + " Interface NOT AVAILABLE!!");
			System.exit(0);
		}
		return returnObj;
	}

	public Identification getIdentification() {
		return id;
	}

	public void setIdentification(Identification id) {
		this.id = id;
	}

	public List<Object> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Object> interfaces) {
		this.interfaces = interfaces;
	}

	public synchronized Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Semantics getSemantics() {
		return semantics;
	}

	public void setSemantics(Semantics semantics) {
		this.semantics = semantics;
	}

	public Constraints getConstraints() {
		return constraints;
	}

	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}

	public NonFunctionalProperties getNfr() {
		return nfr;
	}

	public void setNfr(NonFunctionalProperties nfr) {
		this.nfr = nfr;
	}

	public Evolution getEvolution() {
		return evolution;
	}

	public void setEvolution(Evolution evolution) {
		this.evolution = evolution;
	}

	public RuntimeInfo getRuntimeInfo() {
		return runtimeInfo;
	}

	public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}

	public synchronized void setRuntimeInfo(ExecutionEnvironment e) {
		this.runtimeInfo.setEnv(e);
	}

	public void here(String info) {
		String filter = new String(Utils.STRING_FILTER);
		String line;

		line = this.getIdentification().getName() + "." + info;
		if (line.contains(filter))
			System.out.println(line);
	}

	public void logRemote(String info) {
		String loggedInfo = new String();
		String[] className = this.getClass().getName().split("\\.");
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date());

		// inject an artificial delay
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// format of *.data log
		loggedInfo = className[className.length - 1] + ";" + info.trim() + ";" + timeStamp;

		// create connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(Utils.LOG_QUEUE, false, false, false, null);
			channel.basicPublish("", Utils.LOG_QUEUE, null, loggedInfo.getBytes("UTF-8"));
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object take(Queue local, ExecutionUnit unit) {
		Object inObject = null;
		boolean tryPoll = true;
		int numberOfAttempts = 0;

		try {
			while (tryPoll) {
				if (unit.getState().compareAndSet(3, 3))
					tryPoll = false;
				else if (unit.getState().compareAndSet(2, 2)) {
					inObject = local.getQueue().poll(Utils.QUEUE_WAITING_TIME, TimeUnit.MILLISECONDS);
					if (inObject != null) {
						tryPoll = false;
					}
					else
						numberOfAttempts++;
					
					//if (numberOfAttempts > Utils.ATTEMPTS) // TODO
					//	tryPoll = false;
				} else
					tryPoll = false;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return inObject;
	}

	public void takePut(Queue local, Queue remote, ExecutionUnit unit) {
		Object obj;
		boolean tryPoll = true;
		int numberOfAttempts = 0;

		try {
			while (tryPoll) {
				if (unit.getState().compareAndSet(3, 3))
					tryPoll = false;
				else if (unit.getState().compareAndSet(2, 2)) {
					obj = local.getQueue().poll(Utils.QUEUE_WAITING_TIME, TimeUnit.MILLISECONDS);
					if (obj != null) {
						remote.getQueue().offer(obj);
						tryPoll = false;
					}
					else
						numberOfAttempts++;
					
					//if(numberOfAttempts > Utils.ATTEMPTS) // TODO
					//	tryPoll = false;
				} else
					tryPoll = false;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void invR(Queue local, Queue remote, ExecutionUnit unit) {
		takePut(local, remote, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void terR(Queue local, Queue remote, ExecutionUnit unit) {

		// local.getQueue().put(remote.getQueue().take());
		takePut(remote, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void invP(Queue local, Queue remote, ExecutionUnit unit) {

		// local.getQueue().put(remote.getQueue().take());
		takePut(remote, local, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void terP(Queue local, Queue remote, ExecutionUnit unit) {

		// remote.getQueue().put(local.getQueue().take());
		takePut(local, remote, unit);
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PosInvP(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PreInvR(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PosTerR(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PreTerP(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PosTerP(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PosInvR(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PreTerR(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PreInvP(Queue local, ExecutionUnit unit) {
		// TODO Auto-generated method stub
	}
}