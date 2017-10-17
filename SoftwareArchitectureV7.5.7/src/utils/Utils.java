package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {

	// Execution unit states
	//public static final int EU_PROCESSING = 1;
	//public static final int EU_PROCESSING = 1;
	
	// AUXdea
	public static final String STRING_FILTER = "xx";
	public static final int FATAL_ERROR = 0;
	
	public static final String DEADLOCK_ASSERTION = "assert P1 :[deadlock free]";
	public static final String LOG_QUEUE = "logQueue";

	public static final int QUEUE_WAITING_TIME = 10;
	public static final int ATTEMPTS = 3;
	
	// SOFTWARE ARCHITECTURE
	public static final int ROLE_CALLER = 0;
	public static final int ROLE_RESPONDER = 1;
	public static final int BLOCKING_QUEUE_MAX_SIZE = 1;
	public static final int MAX_POOL_THREADS = 1;
	public static final int MOO = 1;
	public static final int MOM = 2;
	public static final int NUMBER_OF_INVOCATIONS = 1000;
	public static final int NAMING_INVOKER = 1315;
	public static final int NAMING_REMOTE_KEY = 1111;
	public static final int ADD_COMPONENT = 1;
	public static final int REMOVE_COMPONENT = 2;
	public static final int REPLACE_COMPONENT = 3;
	public static final int REPLACE_BEHAVIOUR = 4;
	
	public static final String PREFIX_ACTION = "->";
	public static final String EXTERNAL_CHOICE_ACTION = "\\->|\\[]";
	public static final String REGEX_ACTION = "\\->|\\[]";
	public static final String CHOICE_ACTION = "[]";
	
	public static final String [] SET_OF_ACTIONS = {"i_PreInvP","invP","i_PosInvP","i_PreInvR","invR","i_PosInvR","i_PreTerP","terP",
			"i_PosTerP","i_PreTerR","terR","i_PosTerR","i_PreInvR1","i_PosTerR1","i_PreInvR2","i_PosTerR2","i_PreInvR3","i_PosTerR3",
			"i_PosInvR1","i_PosInvR2","i_PosInvR3","i_PreTerR1","i_PreTerR2","i_PreTerR3"};
	
	public static final int PROVIDER_INVOKER = 1316;
	public static final int PROVIDER_REMOTE_KEY = 1112;

	public static final int MAX_NUMBER_OF_PROXIES = 1;
	public static final int MAX_NUMBER_OF_REMOTE_OBJECTS = 1;
	public static final int MAX_NUMBER_OF_COMPONENTS = 10;
	public static final int MAX_NUMBER_OF_CONNECTORS = 10;
	public static final int MAX_NUMBER_OF_ATTACHMENTS = 100;
	public static final int MAX_NUMBER_OF_PORTS = 10;
	public static final int MAX_NUMBER_OF_INTERFACES = 10;

	public static final int INTERFACE_ONE_WAY = 1;
	public static final int INTERFACE_TWO_WAY = 2;
	public static final boolean INTERFACE_AVAILABLE = true;
	public static final boolean INTERFACE_NOT_AVAILABLE = false;

	public static final int PORT_IN = 0;
	public static final int PORT_OUT = 1;
	public static final boolean PORT_AVAILABLE = true;
	public static final boolean PORT_NOT_AVAILABLE = false;

	public static final int ENDPOINT_OUT = 0;
	public static final int ENDPOINT_IN = 1;

	// MIDDLEWARE
	public static final int DELAY = 0;
	public static final int DELAY_INTERCEPTOR_1 = 0;
	public static final int DELAY_INTERCEPTOR_2 = 1000;
	public static final int DELAY_INTERCEPTOR_3 = 500;
	public static final int DELAY_BETWEEN_CHECKING = 1000; 
	public static final int DELAY_FIRST_CHECKING = 1000;
	public static final int DELAY_SERVICE = 5; // used in marshaller 2
	public static final int PROM_TRACE_SIZE = 100; 
	
	public static final String CLASS_PACKAGE = "middleware.distribution.marshaller";
	//public static final String CLASS_PACKAGE = "applications.distributed.generic";
	//public static final String CLASS_PACKAGE = "applications.simple";
	//public static final String CLASS_PACKAGE = "applications.distributed.generic";
	public static final String PROM_XES_DIR = "/Users/nsr/Dropbox/java/SoftwareArchitectureV7.5.3/src/utils";
	public static final String PROM_DATA_DIR = "/Users/nsr/Dropbox/java/SoftwareArchitectureV7.5.3/src/utils";
	public static final String PROM_XES_FILE = "log-middleware.xes";
	public static final String PROM_DATA_FILE = "log-middleware.data";
	public static final String LOG_SERVER_HOST = "localhost";
	//public static final String CSP_DIR = "/home/david/Doutorado/project";
	public static final String CSP_DIR = "/home/david/Doutorado/repositories/SoftwareArchitectureV7.5.7/SoftwareArchitectureV7.5.7/resources";

	public static final String SCRIPTS_DIR = "/Users/nsr/Dropbox/bam/scripts";
	public static final String PROPERTY_DIR = "/Users/nsr/Dropbox/bam/properties";
	public static final String LOG_DIR = "/Users/nsr/Dropbox/java/BamV2.0/src1/bam";

	public static final String MIDDLEWARE_EXECUTION_TRACE = "middlewareexecutiontrace.log";
	public static final String SEQ_FILE_NAME = "seqfile.seq";
	public static final String CHAIN_OF_INTERCEPTOR_FILE = "chainofinterceptors.conf";
	public static final String CADP_OUTPUT_FILE = "cadp.txt";
	public static final String LOTOS_SPEC = "test.lot";

	public static final String PROM_MIDDLEWARE_XES = "log-middleware.xes";
	public static final String PROM_MIDDLEWARE_DATA = "log-middleware.data";

	// public static final int BAM_PORT = 5000;
	public static final int MESSAGING_SERVICE_PORT = 2000;
	public static final int NAMING_SERVICE_PORT = 3000;
	public static final int LOG_SERVER_PORT = 4000;
	public static final int MIN_PORT_NUMBER = 1500;
	public static final int MAX_PORT_NUMBER = 50000;
	public static final String NAMING_SERVICE_HOST = "localhost";
	public static final String MESSAGING_SERVICE_HOST = "localhost";

	public static final int CALCULATOR_SERVICE_PORT = 4000;
	public static final int GENERIC_SERVICE_PORT = 4000;
	public static final int ECHO_SERVICE_PORT = 4000;
	
	public static final boolean LOGGER_ON = true;
	public static final int NUMBER_OF_REPETITIONS = 10;
	public static final int MAX_LENGHT_OF_INTERCEPTOR_CHAIN = 10;
	public static final String OPERATING_SYSTEM = "macos";
	public static final String EVALUATION_TOOL = "prom";

	public static final String CADPCommand_VerifyTemporalFormula = "VerifyTemporalFormula";

	/*
	 * public void init() throws IOException {
	 * 
	 * confLogs(); confInterceptors(); listConfiguration(); }
	 */

	public void listConfiguration() {
		System.out.println("-------- Work Directories -----");
		System.out.println("Directory of Logs      : " + LOG_DIR);
		System.out.println("Directory of Scripts   : " + SCRIPTS_DIR);
		System.out.println("Directory of Properties: " + PROPERTY_DIR + "\n");
		System.out.println("-------- Delays -----");
		System.out.println("Delay for Logging (ms)    : " + DELAY);
		System.out.println("Delay Interceptor 1 (ms)  : " + DELAY_INTERCEPTOR_1);
		System.out.println("Delay Interceptor 2 (ms)  : " + DELAY_INTERCEPTOR_2);
		System.out.println("Delay Interceptor 3 (ms)  : " + DELAY_INTERCEPTOR_3);
		System.out.println("Delay between Checks (ms) : " + DELAY_BETWEEN_CHECKING);
		System.out.println("Delay of Echo Service (ms): " + DELAY_SERVICE);
		System.out.println("-------- Files -----");
		System.out.println("Execution Trace       : " + MIDDLEWARE_EXECUTION_TRACE);
		System.out.println("CADP Input            : " + SEQ_FILE_NAME);
		System.out.println("CADP Output           : " + MIDDLEWARE_EXECUTION_TRACE);
		System.out.println("Chain of Interceptors : " + MIDDLEWARE_EXECUTION_TRACE);
		System.out.println("-------- General Configurations -----");
		System.out.println("Naming Service Port (TCP): " + NAMING_SERVICE_PORT);
		System.out.println("Logging active           : " + LOGGER_ON);
		System.out.println("Operating System         : " + OPERATING_SYSTEM);
		System.out.println("-------- Performance Evaluation -----");
		System.out.println("Number of Repetitions: " + NUMBER_OF_REPETITIONS);
		System.out.println("Number of Invocation of Client: " + NUMBER_OF_INVOCATIONS);
		System.out.println("Max lenght of Chain of Interceptors: " + MAX_LENGHT_OF_INTERCEPTOR_CHAIN);
	}

	public void confLogs() throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		Path logFile = FileSystems.getDefault().getPath(Utils.LOG_DIR, MIDDLEWARE_EXECUTION_TRACE);
		Path seqFile = FileSystems.getDefault().getPath(Utils.LOG_DIR, SEQ_FILE_NAME);
		Path cadpFile = FileSystems.getDefault().getPath(Utils.LOG_DIR, CADP_OUTPUT_FILE);

		// Empty the execution trace
		BufferedWriter writerLog = Files.newBufferedWriter(logFile, charset);
		writerLog.close();

		// empty seq file
		BufferedWriter writerSeq = Files.newBufferedWriter(seqFile, charset);
		writerSeq.close();

		// empty cadp file
		BufferedWriter writerCADP = Files.newBufferedWriter(cadpFile, charset);
		writerCADP.write("FALSE");
		writerCADP.close();
	}

	/*
	 * public void confInterceptors() throws IOException { InterceptorRegistry
	 * intRegistry = new InterceptorRegistry();
	 * 
	 * intRegistry.addInterceptor(0, new
	 * MyInterceptor("InvocationInterceptor1")); intRegistry.addInterceptor(1,
	 * new MyInterceptor("InvocationInterceptor2"));
	 * 
	 * Charset charset = Charset.forName("US-ASCII"); Path chainFile =
	 * FileSystems.getDefault().getPath(Utils.LOG_DIR,
	 * CHAIN_OF_INTERCEPTOR_FILE);
	 * 
	 * BufferedWriter writer = Files.newBufferedWriter(chainFile, charset);
	 * 
	 * for (MyInterceptor m : intRegistry.getListOfInterceptors()) {
	 * writer.write(m.getInterceptorName()); writer.newLine(); } writer.close();
	 * }
	 */
	public static int nextPortAvailable() {
		int p = MIN_PORT_NUMBER;

		while (!portAvailable(p))
			p++;
		return p;

	}

	private boolean isPortInUse(String host, int port) {
		  // Assume no connection is possible.
		  boolean result = false;

		    try {
				(new Socket(host, port)).close();
			} catch (IOException e) {
			}
		    result = true;
		    
		  return result;
		}
	
	public static boolean portAvailable(int port) {
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}
}
