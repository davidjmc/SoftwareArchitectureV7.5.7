package middleware.services.naming;

import java.io.Serializable;

public class AbsoluteObjectReference implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;
	private int objectKey;
	private int invokerKey;
	
	public AbsoluteObjectReference(){
	}

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public int getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(int objectKey) {
		this.objectKey = objectKey;
	}

	public int getInvokerKey() {
		return invokerKey;
	}

	public void setInvokerKey(int invokerKey) {
		this.invokerKey = invokerKey;
	}
}
