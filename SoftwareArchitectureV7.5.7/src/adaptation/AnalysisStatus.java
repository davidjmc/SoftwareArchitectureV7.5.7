package adaptation;

public class AnalysisStatus {
	private boolean isAdaptationNecessary;
	private int code;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean isAdaptationNecessary() {
		return isAdaptationNecessary;
	}
	public void setAdaptationNecessary(boolean isAdaptationNecessary) {
		this.isAdaptationNecessary = isAdaptationNecessary;
	} 
}
