package middleware.distribution.marshaller;

import java.io.IOException;

public interface Marshaller {
	public byte[] marshall(Object msg) throws IOException, InterruptedException;

	public Object unmarshall(byte[] msg);
}
