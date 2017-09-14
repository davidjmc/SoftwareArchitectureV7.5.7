package middleware.basic;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	private String op;
	private ArrayList<Object> args;
	
	public Request() {
	}

	public Request(String op, ArrayList<Object> list) {
		this.op = op;
		this.args = list;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public ArrayList<Object> getArgs() {
		return args;
	}

	public void setArgs(ArrayList<Object> args) {
		this.args = args;
	}
}
