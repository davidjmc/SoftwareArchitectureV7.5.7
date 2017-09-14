package adaptation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import utils.Utils;

public class MonitorLog {
	private FileWriter xesFileWriter;
	private FileWriter dataFileWriter;
	private ConnectionFactory factory = new ConnectionFactory();
	private Connection connection;
	private Channel channel;
	private int currentTrace = 0;
	private AtomicBoolean dataFileClosed = new AtomicBoolean(false);
	private String dataFileName = "";

	public MonitorLog() {
		configureQueue();
		this.dataFileName = configureDataFile();
	}

	public String monitor() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-priority", 10); // max priority
		
		Consumer consumer = new DefaultConsumer(channel) {
			long t1 = System.currentTimeMillis(), t2;

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				t2 = System.currentTimeMillis();
				if ((t2 - t1) < Utils.DELAY_BETWEEN_CHECKING * 0.3) {
					writeDataLog(new String(body, "UTF-8"));
				} else {
					closeDataLog();
					return;
				}
			}
		};
		try {
			channel.basicConsume(Utils.LOG_QUEUE, true, args, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!dataFileClosed.get()) {
		}

		return this.dataFileName;
	}

	public void closeXesLog() {
		StringBuilder xesXML = new StringBuilder(1000);

		try {
			xesXML.setLength(0);
			xesXML.append("</trace>");
			xesXML.append("</log>");
			xesFileWriter.write(xesXML.toString());
			xesFileWriter.flush();
			xesFileWriter.close();
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e1) {
			e1.printStackTrace();
		}
	}

	public void closeDataLog() {

		if (dataFileClosed.get())
			return;
		try {
			dataFileClosed.set(true);
			dataFileWriter.flush();
			dataFileWriter.close();
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeXesLog(String message) {
		StringBuilder xesXML = new StringBuilder(1000);
		String[] splitLine = null;

		splitLine = message.split(";");
		if (splitLine[0].contains("sender") && splitLine[1].contains("i_PreInvR")) {
			currentTrace++;
			xesXML.setLength(0);
			xesXML.append("<trace>" + "<string key=\"concept:name\" value=\"" + currentTrace + "\"/>");
		}

		xesXML.append("<event>" + "<string key=\"concept:name\" value=\"" + splitLine[0] + "_" + splitLine[1] + "\"/>"
				+ "<date key=\"time:timestamp\" value=\"" + splitLine[2] + "\"/>" + "</event>");

		if (splitLine[0].contains("receiver") && splitLine[1].contains("i_PosInvP"))
			xesXML.append("</trace>");

		try {
			xesFileWriter.write(xesXML.toString());
			xesFileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeDataLog(String message) {
		String[] splitLine = null;

		if (dataFileClosed.get())
			return;
		
		splitLine = message.split(";");
		if (splitLine[0].contains("sender") && splitLine[1].contains("i_PreInvR"))
			currentTrace++;
		try {
			dataFileWriter.write(message + "\n");
			dataFileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void configureXesFile() {
		try {
			xesFileWriter = new FileWriter(Utils.PROM_XES_DIR + "/" + Utils.PROM_XES_FILE);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		StringBuilder xesXML = new StringBuilder(1000);

		xesXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\">"
				+ "<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>"
				+ "<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>"
				+ "<classifier name=\"Event Name\" keys=\"concept:name\"/>"
				+ "<string key=\"concept:name\" value=\"events.csv\"/>");
		try {
			xesFileWriter.write(xesXML.toString());
			xesFileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String configureDataFile() {
		String dataFileName = new String(Utils.PROM_DATA_DIR + "/" + System.currentTimeMillis() + Utils.PROM_DATA_FILE);
		try {
			dataFileWriter = new FileWriter(dataFileName);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		return dataFileName;
	}

	public void configureQueue() {
		factory.setHost("localhost");

		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			this.channel.queueDeclare(Utils.LOG_QUEUE, false, false, false, null);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
		return;
	}
}
