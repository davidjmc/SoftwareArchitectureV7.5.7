package container.csp;

public class Channel {
	private String channel;
	
	public Channel(String c){
		this.channel = new String(c);
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
