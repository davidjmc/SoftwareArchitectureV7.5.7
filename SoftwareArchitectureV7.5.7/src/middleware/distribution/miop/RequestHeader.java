package middleware.distribution.miop;

import java.io.Serializable;

public class RequestHeader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2086621012352355762L;
	private String context;
	private int requestId;
	private boolean responseExpected;
	private int objectKey;
	private int invokerKey;
	private String operation;

	public RequestHeader(){}
	
	public RequestHeader(final String context, final int requestId,
			final boolean responseExpected, final int objectKey,
			final int invokerKey, final String operationName) {
		this.setContext(context);
		this.setRequestId(requestId);
		this.setResponseExpected(responseExpected);
		this.setObjectKey(objectKey);
		this.setInvokerKey(invokerKey);
		this.setOperation(operationName);
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public boolean isResponseExpected() {
		return responseExpected;
	}

	public void setResponseExpected(boolean responseExpected) {
		this.responseExpected = responseExpected;
	}

	public int getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(int objectKey) {
		this.objectKey = objectKey;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public int getInvokerKey() {
		return invokerKey;
	}

	public void setInvokerKey(int invokerKey) {
		this.invokerKey = invokerKey;
	}
}
