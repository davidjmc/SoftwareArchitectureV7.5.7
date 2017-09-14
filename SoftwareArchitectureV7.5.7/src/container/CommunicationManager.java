package container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import framework.configuration.Configuration;
import middleware.basic.Request;
import utils.Utils;

public class CommunicationManager implements Runnable {
	private volatile Queue in = new Queue();
	private volatile Queue out = new Queue();
	private volatile ServerSocket welcomeSocket = null;
	private volatile Socket clientSocket = null;
	private volatile Socket connectionSocket = null;
	private ExecutionEnvironment env;

	public CommunicationManager(ExecutionEnvironment env) {
		this.env = env;
	}

	public void run() {
		Thread client = new Thread(new ClientThread());
		Thread server = new Thread(new ServerThread());

		server.start();
		try {
			Thread.currentThread().sleep(100); // TODO
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.start();
	}

	private class ClientThread implements Runnable {
		public void run() {
			DataOutputStream out = null;
			int msgSize = 0;

			try {
				if (welcomeSocket.getLocalPort() == 5000)
					clientSocket = new Socket("localhost", 5001);
				else if (welcomeSocket.getLocalPort() == 5001)
					clientSocket = new Socket("localhost", 5000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// send data
			try {
				out = new DataOutputStream(clientSocket.getOutputStream());
				while (true) {
					byte[] outMessage = null;
					outMessage = (byte[]) env.getCommunicationManager().getOut().getQueue().poll();
					if (outMessage != null) {
						msgSize = outMessage.length;
						out.writeInt(msgSize);
						out.write(outMessage, 0, msgSize);
						out.flush();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ServerThread implements Runnable {
		public void run() {
			DataInputStream inFromClient = null;
			int rcvMsgSize;
			byte[] rcvMsg = null;

			// start server
			if (welcomeSocket == null) {
				try {
					welcomeSocket = new ServerSocket(5000); // TODO
					connectionSocket = welcomeSocket.accept();
				} catch (IOException e) {
					try {
						welcomeSocket = new ServerSocket(5001); // TODO
						connectionSocket = welcomeSocket.accept();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			// receive data
			try {
				inFromClient = new DataInputStream(connectionSocket.getInputStream());
				while (true) {
					// System.out.println(this.getClass()+" RECEIVING ");
					rcvMsgSize = inFromClient.readInt();
					rcvMsg = new byte[rcvMsgSize];
					inFromClient.read(rcvMsg, 0, rcvMsgSize);
					 env.getCommunicationManager().getIn().getQueue().offer(rcvMsg);
				}
			} catch (IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int executionEnvironmentPort(Configuration conf) {
		int port = 0;

		if (conf.hasNamingService())
			port = Utils.NAMING_SERVICE_PORT;
		// else if (conf.hasMessagingService())
		// port = Utils.MESSAGING_SERVICE_PORT;
		else
			port = Utils.nextPortAvailable();

		return port;
	}

	public void send(Request r) {

	}

	public void createConnection(String host, int port) {

	}

	public boolean connectionExist(String host, int port) {
		boolean result = false;
		try {
			(new Socket(host, port)).close();
		} catch (IOException e) {
		}
		result = true;

		return result;
	}

	public synchronized Queue getIn() {
		return in;
	}

	public void setIn(Queue in) {
		this.in = in;
	}

	public synchronized Queue getOut() {
		return out;
	}

	public void setOut(Queue out) {
		this.out = out;
	}
}
