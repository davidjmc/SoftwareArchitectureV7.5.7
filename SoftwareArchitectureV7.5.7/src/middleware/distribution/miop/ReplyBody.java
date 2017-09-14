package middleware.distribution.miop;

import java.io.Serializable;

public class ReplyBody implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8600466569258190248L;
	private Object operationResult;

	public ReplyBody(){}

	public ReplyBody(Object opResult){
		this.operationResult = opResult;
		
	}
	public Object getOperationResult() {
		return operationResult;
	}

	public void setOperationResult(Object operationResult) {
		this.operationResult = operationResult;
	}
}
