package container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.server.RemoteObject;
import java.util.HashMap;
import java.util.Random;

import framework.basic.SAMessage;
import framework.configuration.Configuration;
import framework.connector.TRequestReply;
import middleware.basic.Reply;
import middleware.basic.Request;
import middleware.distribution.clientproxy.ClientProxy;
import middleware.distribution.invoker.CInvoker;
import middleware.services.naming.AbsoluteObjectReference;

public class Broker {
	private Configuration conf = null;
	private Container env = null;
	private HashMap<Integer, Object> remoteObjects = new HashMap<Integer, Object>();

	public Broker(Object conf) {
		this.conf = (Configuration) conf;
		this.env = new Container(conf);
	}

	public void initialize() {
		this.env.start();
	}

	private class ThreadProcessRequest implements Runnable {
		private Object remoteObject;

		public ThreadProcessRequest(Object remoteObject) {
			this.remoteObject = remoteObject;
		}

		public void run() {
			SAMessage inMessage = new SAMessage();
			SAMessage outMessage = null;
			Reply reply = new Reply();
			Method method = null;
			String methodName = null;
			Object[] args = null;
			Class<?>[] argsType = null;
			CWrapperObjectRT wrapperObject = null;
			Method[] methods = null;

			// prepare names for class instantiation
			String interfaceFullName = remoteObject.getClass().getInterfaces()[0].getName();
			String interfaceName = interfaceFullName.substring(interfaceFullName.lastIndexOf(".") + 1,
					interfaceFullName.length());

			wrapperObject = (CWrapperObjectRT) env.getGraphRT().getComponentByName("CWrapperRT" + interfaceName + "Object");

			while (!Thread.currentThread().isInterrupted()) {
				try {

					// obtain invoked method/args from wrapper
					inMessage = wrapperObject.invRExtra();
					methodName = ((Request) inMessage.getContent()).getOp();
					args = (((Request) inMessage.getContent()).getArgs()).toArray();
					argsType = new Class[args.length];

					methods = remoteObject.getClass().getDeclaredMethods();
					for (Method m : methods) {
						if (m.getName().contains(methodName)) {
							argsType = m.getParameterTypes();
							method = remoteObject.getClass().getMethod(methodName, argsType);
							break;
						}
					}

					// invoke method to remote object & sent back to wrapper
					reply.setR(method.invoke(remoteObject, args));
					outMessage = new SAMessage(reply);
					wrapperObject.terR(outMessage);

				} catch (InterruptedException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	public void start() throws InterruptedException {

		this.remoteObjects.forEach((key, remoteObject) -> (new Thread(new ThreadProcessRequest(remoteObject))).run());
	}

	public Container getEnv() {
		return this.env;
	}

	public ClientProxy register(RemoteObject remoteObject)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AbsoluteObjectReference aorRemoteObject = new AbsoluteObjectReference();
		int objectKey, invokerKey;
		Random randomGenerator = new Random();

		// prepare names for class instantiation
		String interfaceFullName = remoteObject.getClass().getInterfaces()[0].getName();
		String interfaceName = interfaceFullName.substring(interfaceFullName.lastIndexOf(".") + 1,
				interfaceFullName.length());
		String packageName = interfaceFullName.substring(0, interfaceFullName.lastIndexOf("."));
		String clientProxyClassName = packageName + "." + interfaceName + "Proxy";
		String clientObjectWrapperName = packageName + "." + "CWrapper" + interfaceName + "Object";

		// create client proxy and invoker
		Class<?> clientProxyClass = Class.forName(clientProxyClassName);
		ClientProxy clientProxy = (ClientProxy) clientProxyClass.newInstance();

		// create aor and configure clientProxy
		invokerKey = randomGenerator.nextInt(10000);
		objectKey = randomGenerator.nextInt(10000);
		aorRemoteObject.setHost("localhost");
		aorRemoteObject.setInvokerKey(invokerKey);
		aorRemoteObject.setObjectKey(objectKey);
		aorRemoteObject.setPort(env.getPortExecutionEnvironment());
		clientProxy.setAor(aorRemoteObject);

		// register a remote object in broker
		remoteObjects.put(aorRemoteObject.getObjectKey(), remoteObject);

		// configure wrapper object
		Class<?> clientObjectWrapperClass = Class.forName(clientObjectWrapperName);
		CWrapperObject wrapperObject = (CWrapperObject) clientObjectWrapperClass.newInstance();
		wrapperObject.setName("CWrapper" + interfaceName + "Object");		
		
		// configure connector 
		TRequestReply t = new TRequestReply("t" + randomGenerator.nextFloat());
		
		// configure invoker
		CInvoker invoker = (CInvoker) env.getConf().getGraph().getComponentByName("invoker"); // TODO
		invoker.addCaller();

		// update development graph
		this.conf.connect(wrapperObject, t, invoker);
		
		// update runtime graph
		this.getEnv().getGraphRT().generateRuntimeGraph(this.conf.getGraph());

		// update runtime invoker maps
		((CInvokerRT) this.getEnv().getGraphRT().findVertexByName("invoker").getData()).updateMaps(invokerKey); // TODO

		return clientProxy;
	}
}
