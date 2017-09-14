package framework.basic;

import container.ExecutionEnvironment;

public class RuntimeInfo {
	private ExecutionEnvironment env;
	
	public RuntimeInfo(){
		this.env = new ExecutionEnvironment();
	}

	public RuntimeInfo(ExecutionEnvironment e){
		this.env = e;
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public void setEnv(ExecutionEnvironment env) {
		this.env = env;
	}

}
