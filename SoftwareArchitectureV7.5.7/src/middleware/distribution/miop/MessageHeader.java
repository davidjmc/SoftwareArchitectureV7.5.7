package middleware.distribution.miop;

import java.io.Serializable;

public class MessageHeader implements Serializable {

	private static final long serialVersionUID = 2188202457163724602L;

	private String magic;
	private int version;
	private boolean byteOrder;
	private int messageType;
	private long messageSize;

	public MessageHeader(){}
	
	public MessageHeader(final String m, final int pVersion,
			final boolean bOrder, final int mType, final long mSize) {
		this.setMagic(m);
		this.setVersion(pVersion);
		this.setByteOrder(bOrder);
		this.setMessageType(mType);
		this.setMessageSize(mSize);
	}

	public String getMagic() {
		return magic;
	}

	public void setMagic(String magic) {
		this.magic = magic;
	}

	public boolean isByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(boolean byteOrder) {
		this.byteOrder = byteOrder;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public long getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(long messageSize) {
		this.messageSize = messageSize;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
};
