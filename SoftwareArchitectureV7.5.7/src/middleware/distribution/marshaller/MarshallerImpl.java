package middleware.distribution.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.util.logging.Logger;

public class MarshallerImpl implements Marshaller {
	// private static final Logger logger = Logger.getLogger(Thread
	// .currentThread().getStackTrace()[0].getClassName());

	public byte[] marshall(Object msg) throws IOException, InterruptedException {

		// log
		// logger.info("start");

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(msg);

		// log
		// logger.info("complete");

		return byteStream.toByteArray();
	}

	public Object unmarshall(byte[] msgToBeUnmarshalled) {

		Object msg = null;

		ByteArrayInputStream byteStream = new ByteArrayInputStream(msgToBeUnmarshalled);
		ObjectInputStream objectStream = null;
		try {
			objectStream = new ObjectInputStream(byteStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			msg = (Object) objectStream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// log
		// logger.info("complete");

		return msg;
	}
}