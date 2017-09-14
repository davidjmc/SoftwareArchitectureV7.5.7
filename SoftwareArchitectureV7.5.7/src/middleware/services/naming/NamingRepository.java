package middleware.services.naming;

import java.util.ArrayList;

public class NamingRepository {
	private ArrayList<NamingRecord> namingRecord = new ArrayList<NamingRecord>();
	
	public NamingRepository(){
		
	}

	public ArrayList<NamingRecord> getNamingRecord() {
		return namingRecord;
	}

	public void setNamingRecord(ArrayList<NamingRecord> namingRecord) {
		this.namingRecord = namingRecord;
	}
}
