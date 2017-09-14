package container.csp;

public class Process {
	private String processBody;
	
	public Process(String p){
		this.processBody = new String(p);
	}

	public String getProcessBody() {
		return processBody;
	}

	public void setProcessBody(String processBody) {
		this.processBody = processBody;
	}

}
