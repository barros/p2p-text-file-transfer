import java.net.*;
import java.io.*;

public class Peer {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, Exception{

		if ((args.length != 2)) {
			throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
		}
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in)); // from keyboard

		InetAddress serverAddress = InetAddress.getByName(args[0]);
		int servPort = Integer.parseInt(args[1]);
		Socket socket = new Socket(serverAddress, servPort);

		ServerSocket peerSocket = new ServerSocket(0);
		int peerPort = peerSocket.getLocalPort();
		PeerTransfer PT = new PeerTransfer(peerSocket);
		Thread T = new Thread(PT);
		T.start();

		// ask peer for a name/username
		System.out.println("\nWelcome to the Peer2Peer Transfer Program!\n\nEnter your name or a username "
				+ "(if you've used this service before, please use the same username):\n");
		String name = fromKeyboard.readLine();
		Info info = new Info(name);
		String input;
		String action;

		// send name to broker
		OutputStream os = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.flush();
		oos.writeObject(info);
		oos.flush();

		while (true){
			// ask peer what they would like to do
			System.out.println("\nWhat would you like to do? Type the following for each action:\n"
				+ "To receive a file: 'receive' \nTo register a file: 'register' \nTo unregister a file: 'unregister' \nQuit: 'exit'\n");
			action = fromKeyboard.readLine();
			info.setAction(action);

			// if peer would like to exit, quit program
			if (action.equalsIgnoreCase("exit")){
				System.out.println("\nThank you for using the Peer2Peer Transfer Program!");
				System.exit(-1);
			}
			// if peer would like to "receive" a file...
			else if (action.equalsIgnoreCase("receive")){
				System.out.println("\nWhat file would you like to receive? Enter the full file name"
						+ "\nincluding the '.txt' extension.\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);

				// send name of requested file to broker
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();

				InputStream is = socket.getInputStream();
	    		ObjectInputStream ois = new ObjectInputStream(is);
	    		info = (Info)ois.readObject();
	    		System.out.println(info.getServRes());

	    		// if nobody is sharing the file, loop back and ask for new action
	    		if (info.getServRes().contains("Please try again at a later time or request a different file")){
	    			Thread.sleep(3000);
	    			continue;
	    		}

	    		int range = info.getNumOfOptions();
	    		int select = Integer.parseInt(fromKeyboard.readLine());

	    		// peer's selection of requested source must be within the range of the number of available sources
	    		while (!(select >= 1 && select <= range)){
	    			System.out.println("\nPlease select a source from the list above.\n");
	    			select = Integer.parseInt(fromKeyboard.readLine());
	    		}
	    		info.setSelection(select);
	    		os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();

				// receive info of the peer sharing the file
				is = socket.getInputStream();
        		ois = new ObjectInputStream(is);
        		info = (Info)ois.readObject();

        		String peer2IP = (info.getPeer2IP()).substring(1);
	    		int peer2Port = info.getPeer2Port();

	    		// request/receive file from contributor using given IP Address, port number, and file name
	    		try {
					Connection peer = new Connection(peer2IP, peer2Port, input);
					peer.makeRequest();
					peer.sendRequest();
					System.out.println("\nRequest sent for file!");
					peer.getResponse();
					peer.useResponse();
					peer.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (ConnectException e) {
					System.out.println("Connection refused, contributor's system must be offline.");
				}

	    		// print request for evaluation from broker
	    		System.out.println(info.getServRes());

	    		input = fromKeyboard.readLine();
	    		while(!(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no"))){
	    			System.out.println("\nPlease enter (yes) or (no).\n");
	    			input = fromKeyboard.readLine();
	    		}
	    		// send evaluation of contributor to broker
	    		info.setPeerRes(input);
	    		os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();

			}
			// if peer would like to "register" a file...
			else if (action.equalsIgnoreCase("register")){
				System.out.println("\nWhat file would you like to register? Please register files that you have available and "
						+ "don't register duplicate files.\n(Make sure to include the .txt extension in the file name)\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);
				info.setPeer1Port(peerPort);
				// send to broker the name of file peer wants to register along with peer's information
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();

				InputStream is = socket.getInputStream();
	    		ObjectInputStream ois = new ObjectInputStream(is);
	    		info = (Info)ois.readObject();
	    		System.out.println(info.getServRes());

	    		Thread.sleep(3000);
			}
			// if peer would like to "unregister" a file...
			else if (action.equalsIgnoreCase("unregister")){
				System.out.println("\nWhat file would you like to unregister? Include the '.txt' extension.\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);
				// send to broker the name of the file peer wants to unregister
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();

				// receive response from broker after trying to unregister a file
				InputStream is = socket.getInputStream();
	    		ObjectInputStream ois = new ObjectInputStream(is);
	    		info = (Info)ois.readObject();
	    		System.out.println(info.getServRes());

	    		Thread.sleep(3000);
			}
			// if peer did not enter a valid action, let them know and loop back to receive new action
			else {
				System.out.println("\nOops... Seems like you've entered an invalid response! Please try again.\n");
			}
		}
	}
}
