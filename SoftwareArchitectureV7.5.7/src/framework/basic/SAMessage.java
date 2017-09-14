package framework.basic;

import java.io.Serializable;

public class SAMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int senderID;
	private Object content;

	public SAMessage() {
	}

	public SAMessage(int senderID) {
		this.senderID = senderID;
	}

	public SAMessage(int senderID, SAMessage m) {
		this.senderID = senderID;
		this.content = m.content;
	}

	public SAMessage(int senderID, Object m) {
		this.senderID = senderID;
		this.content = m;
	}

	public SAMessage(SAMessage m) {
		this.content = m.content;
	}

	public SAMessage(Object m) {
		this.content = m;
	}

	public int getSenderID() {
		return this.senderID;
	}

	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}

	public Object getContent() {
		return this.content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
}
