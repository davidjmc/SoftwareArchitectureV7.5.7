package framework.basic;

public class Error {
	private int code;
	private String description;

	public Error() {
		this.description = new String("");
	}

	public Error(int c, String d) {
		this.code = c;
		this.description = d;
	}

	public void printError() {
		System.out.println("ERROR " + this.code + ": " + this.description);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
