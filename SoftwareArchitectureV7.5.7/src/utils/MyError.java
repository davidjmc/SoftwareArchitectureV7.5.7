package utils;

public class MyError {
	String message;
	int typeOfError;

	public MyError(String m, int t) {
		this.message = m;
		this.typeOfError = t;
	}

	public void print() {
		System.out.println("ERROR: " + message);
		if (typeOfError == Utils.FATAL_ERROR)
			System.exit(0);
	}
}
