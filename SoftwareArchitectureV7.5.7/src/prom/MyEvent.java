package prom;


import java.text.SimpleDateFormat;
import java.util.Date;

public class MyEvent {
	private String eventSerialized = null;

	public MyEvent(String eventId) {
		SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");	
		this.setEventSerialized("<event>" + "\n"
				+ "<string key=\"concept:name\" value=\"" + eventId + "\"/>"
				+ "<string key=\"lifecycle:transition\" value=\"complete\"/>"
				+ "<date key=\"time:timestamp\" value=\"" + timeStamp.format(new Date())
				+ "\"/>" + "</event>");
	}

	public String getEventSerialized() {
		return eventSerialized;
	}

	public void setEventSerialized(String eventSerialized) {
		this.eventSerialized = eventSerialized;
	}
}