package middleware.distribution.miop;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5728361228836267306L;
	private MessageHeader header;
	private MessageBody body;

	public Message(){}
		
	public Message(final MessageHeader h, final MessageBody b) {
		this.header = h;
		this.body = b;
	}

	public MessageHeader getHeader() {
		return header;
	}

	public void setHeader(MessageHeader header) {
		this.header = header;
	}

	public MessageBody getBody() {
		return body;
	}

	public void setBody(MessageBody body) {
		this.body = body;
	}
}
