package middleware.services.naming;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import container.Broker;
import framework.basic.SAMessage;
import framework.component.CWrapperProxy;
import framework.connector.TRequestReply;
import middleware.basic.Invocation;
import middleware.basic.Request;
import middleware.basic.Termination;
import middleware.distribution.clientproxy.ClientProxy;
import middleware.distribution.requestor.CRequestor;
import utils.Utils;

public class NamingProxy implements Naming {
	private ClientProxy namingProxy;
	private Broker broker;

	public NamingProxy(String host, int port, Broker broker) {
		ClientProxy clientProxy = new ClientProxy();
		AbsoluteObjectReference aorNaming = new AbsoluteObjectReference();
		Random randomGenerator = new Random();

		aorNaming.setHost(host);
		aorNaming.setPort(port);
		aorNaming.setInvokerKey(Utils.NAMING_INVOKER);
		aorNaming.setObjectKey(Utils.NAMING_REMOTE_KEY);
		clientProxy.setAor(aorNaming);

		this.broker = broker;
		this.namingProxy = clientProxy;
		
		// add a new component
		CWrapperNamingProxy namingProxy = new CWrapperNamingProxy("CWrapperNamingProxy"); // TODO
		this.broker.getEnv().getConf().addElement(namingProxy); 
		
		//add a new connector  
		TRequestReply t = new TRequestReply("t"+randomGenerator.nextFloat()); // TODO
		this.broker.getEnv().getConf().addElement(t); 
		
		// update requestor's proxy ports
		CRequestor requestor = (CRequestor) this.broker.getEnv().getConf().getGraph().getComponentByName("requestor"); // TODO		
		requestor.addProvidedInterface();
		
		// add connection
		this.broker.getEnv().getConf().connect(namingProxy, t, requestor);
		
		this.broker.getEnv().getConf().getGraph().printVertices();
		
		System.exit(0);
		//this.broker.getEnv().getGraphRT().generateRuntimeGraph(this.broker.getEnv().getConf().getGraph());
	}

	@Override
	public void bind(String serviceName, ClientProxy clientProxy) throws UnknownHostException, IOException, Throwable {

		Invocation inv = new Invocation();
		new Termination();
		ArrayList<Object> parameters = new ArrayList<Object>();
		class Local {
		}
		;
		String methodName;

		// information received from Client
		methodName = Local.class.getEnclosingMethod().getName();
		parameters.add(serviceName);
		parameters.add(clientProxy);

		// information sent to Requestor
		inv.setClientProxy(this.namingProxy);
		inv.setOperationName(methodName);
		inv.setParameters(parameters);

		ArrayList<Object> args = new ArrayList<Object>();
		args.add(inv);
		SAMessage outMessage = new SAMessage(new Request("invoke", args));

		CWrapperNamingProxy requestor = new CWrapperNamingProxy(""); // TODO
		requestor = (CWrapperNamingProxy) this.broker.getEnv().getConf().getGraph().getComponentByName("CWrapperNamingProxy"); // TODO
		requestor.invP(outMessage);
		requestor.terP();

		// Result sent back to Client TODO

		return;
	}

	@Override
	public ClientProxy lookup(String serviceName) throws UnknownHostException, IOException, Throwable {
		Invocation inv = new Invocation();
		new Termination();
		ArrayList<Object> parameters = new ArrayList<Object>();
		class Local {
		}
		;
		String methodName;
		ClientProxy clientProxy = new ClientProxy();
		Random randomGenerator = new Random();

		// information received from Client
		methodName = Local.class.getEnclosingMethod().getName();

		// information sent to Requestor
		parameters.add(serviceName);
		inv.setClientProxy(this.namingProxy);
		inv.setOperationName(methodName);
		inv.setParameters(parameters);

		ArrayList<Object> args = new ArrayList<Object>();
		args.add(inv);
		SAMessage outMessage = new SAMessage(new Request("invoke", args));

		// wrapper invocation
		CWrapperNamingProxy requestor = new CWrapperNamingProxy(""); // TODO
		requestor = (CWrapperNamingProxy) this.broker.getEnv().getConf().getGraph().getComponentByName("CWrapperNamingProxy"); // TODO
		requestor.invP(outMessage);

		// Result sent back to Client
		clientProxy = (ClientProxy) requestor.terPExtra();
		clientProxy.setBroker(this.broker);

		// add a new component to configuration		
		String proxyFullName = clientProxy.getClass().getName();
		String packageName = proxyFullName.substring(0, proxyFullName.lastIndexOf("."));
		String interfaceName = (proxyFullName.substring(proxyFullName.lastIndexOf(".") + 1,
				proxyFullName.length())).replace("Proxy","");
		String wrapperProxyClassName = packageName + "." + "CWrapper"+interfaceName + "Proxy";

		Class<?> wrapperProxyClass = Class.forName(wrapperProxyClassName);
		CWrapperProxy wrapperProxy = (CWrapperProxy) wrapperProxyClass.newInstance();
		wrapperProxy.setName("CWrapper"+interfaceName + "Proxy");
		//this.broker.getEnv().getConf().addComponent(wrapperProxy); 
		
		//add a new connector to configuration 
		//TRequestReply t = new TRequestReply("t"+randomGenerator.nextFloat(),this.broker.getEnv());
		TRequestReply t = new TRequestReply("t"+randomGenerator.nextFloat());
		//this.broker.getEnv().getConf().addConnector(t); 
		
		// update requestor's proxy ports
		CRequestor cRequestor = (CRequestor) this.broker.getEnv().getConf().getGraph().getComponentByName("requestor"); // TODO		
		cRequestor.addProvidedInterface();
		
		// update development graph
		this.broker.getEnv().getConf().connect(wrapperProxy, t, cRequestor);
		//Attachment att = new Attachment(wrapperProxy,t,cRequestor);
		//this.broker.getEnv().getConf().addAttachment(att);
		//this.broker.getEnv().getConf().updateEnvInfo(this.broker.getEnv());
		
		// update runtime graph
		this.broker.getEnv().getGraph().generateRuntimeGraph(this.broker.getEnv().getConf().getGraph());
		// update links
		//this.broker.getEnv().getLinkManager().createLinks(att);		
		//wrapperProxy.setLinks(this.broker.getEnv().getLinkManager().getHardLinks());
		//t.setLinks(this.broker.getEnv().getLinkManager().getHardLinks());  
		//cRequestor.setLinks(this.broker.getEnv().getLinkManager().getHardLinks());

		// create/start new Threads
		
		this.broker.getEnv().getThreadManager().createAndStartThreadElement(t);
		this.broker.getEnv().getThreadManager().createAndStartThreadElement(wrapperProxy);
		CRequestor cRequestor = new CRequestor();
		cRequestor.setElement(cRequestor);
		cRequestor.addNewThreadAndExecute(); // new responder thread 
		
		return clientProxy; 
	}

	@Override
	public ArrayList<String> list() throws InterruptedException {
		Invocation inv = new Invocation();
		new Termination();
		ArrayList<Object> parameters = new ArrayList<Object>();
		class Local {
		}
		;
		String methodName;
		ArrayList<String> result = new ArrayList<String>();

		// information received from Client
		methodName = Local.class.getEnclosingMethod().getName();

		// information sent to Requestor
		inv.setClientProxy(this.namingProxy);
		inv.setOperationName(methodName);
		inv.setParameters(parameters);

		ArrayList<Object> args = new ArrayList<Object>();
		args.add(inv);
		SAMessage outMessage = new SAMessage(new Request("invoke", args));

		CWrapperNamingProxy requestor = new CWrapperNamingProxy(""); // TODO
		requestor = (CWrapperNamingProxy) this.broker.getEnv().getConf().getGraph().getComponentByName("CWrapperNamingProxy"); // TODO
		requestor.invP(outMessage);

		// Result sent back to Client
		@SuppressWarnings("unchecked")
		ArrayList<String> p = (ArrayList<String>) requestor.terPExtra();
		result = p;

		return result;
	}
}
