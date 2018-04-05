import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Broker {
	public static void main(String[] args) throws IOException, ClassNotFoundException, EOFException {
		if (args.length!=1){
            throw new IllegalArgumentException("Parameter(s): <Port>");
        }
		// port number of broker
		int portNum = Integer.parseInt(args[0]);
		// server socket to accept connections from peers
		ServerSocket serverSocket = null;
		BrokerThread BT;
		// keep track of the number of threads initialized
		int threadNum = 1;
		boolean listening = true;

		// 'library' to map file names to info of possible sources, serve as a look-up database
		HashMap<String, ArrayList<Triple>> library = new HashMap<>();
		// 'ratings' to map a number rating to a user
		HashMap<String, Integer> ratings = new HashMap<>();

		try {
			serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			System.err.println("Port " + args[0] + " is unavailable.");
			System.exit(-1);
		}
		System.out.println("Peer2Peer Server is up and running.....");


		while (listening){
			Socket clientSocket = serverSocket.accept();
			// initialize thread to handle connecting peer
			BT = new BrokerThread(clientSocket, library, ratings, threadNum);
			threadNum++;
			Thread T = new Thread(BT);
			T.start();
		}
		serverSocket.close();
	}
}
