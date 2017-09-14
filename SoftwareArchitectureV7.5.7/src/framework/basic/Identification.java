package framework.basic;

public class Identification {
	private int id;
	private String name;
	
	public Identification(){
		this.name = new String();
	}
	
	public Identification(int i, String n){
		this.id = i;
		this.name = n;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
