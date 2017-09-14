package tests;

public class TestRegExp {

	public static void main(String[] args) {
		String exp = new String("[aa1a<-bb]]");
		// "^(\\d{3}-?\\d{2}-?\\d{4})$" \p{Punct}
		if (exp.matches("^\\[\\[\\w*<-\\w*\\]\\]$"))
			System.out.println("match");
		else
			System.out.println("no match");
	}

}
