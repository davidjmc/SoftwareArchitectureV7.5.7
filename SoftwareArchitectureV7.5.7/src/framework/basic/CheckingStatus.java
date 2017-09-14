package framework.basic;

public class CheckingStatus {
	private boolean structureStatus;
	private boolean behaviourStatus;
	private Error structureError;
	private Error behaviourError;
	
	public CheckingStatus(){
		this.structureStatus = true;
		this.behaviourStatus = true;
		this.structureError = new Error();
		this.behaviourError = new Error();
	}
	public boolean getBehaviourStatus() {
		return behaviourStatus;
	}
	public void setBehaviourStatus(boolean behaviourStatus) {
		this.behaviourStatus = behaviourStatus;
	}
	public Error getStructureError() {
		return structureError;
	}
	public void setStructureError(Error structureError) {
		this.structureError = structureError;
	}
	public Error getBehaviourError() {
		return behaviourError;
	}
	public void setBehaviourError(Error behaviourError) {
		this.behaviourError = behaviourError;
	}
	public boolean getStructureStatus() {
		return structureStatus;
	}
	public void setStructureStatus(boolean structureStatus) {
		this.structureStatus = structureStatus;
	}

}
