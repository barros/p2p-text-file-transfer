import java.net.*;
import java.io.*;

public class Peer {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, Exception{

		if ((args.length != 2)) {
			throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
		}
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in)); //from keyboard
		
		InetAddress serverAddress = InetAddress.getByName(args[0]); 
		int servPort = Integer.parseInt(args[1]);  
		Socket socket = new Socket(serverAddress, servPort);
		
		ServerSocket peerSocket = new ServerSocket(0);
		int peerPort = peerSocket.getLocalPort();
		PeerTransfer PT = new PeerTransfer(peerSocket);
		Thread T = new Thread(PT);
		T.start();
		
		System.out.println("\nWelcome to the Peer2Peer Transfer Program!\n\nEnter your name or a username "
				+ "(if you've used this service before, please use the same username):\n");
		String name = fromKeyboard.readLine();
		Info info = new Info(name);
		String input;
		String action;
		
		OutputStream os = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.flush();
		oos.writeObject(info);
		oos.flush();
		
		while (true){
			System.out.println("\nWhat would you like to do? Type the following for each action:\n"
				+ "To receive a file: 'receive' \nTo register a file: 'register' \nTo unregister a file: 'unregister' \nQuit: 'exit'\n");
			action = fromKeyboard.readLine();
			info.setAction(action);
			if (action.equalsIgnoreCase("exit")){
				System.out.println("\nThank you for using the Peer2Peer Transfer Program!");
				System.exit(-1);
				
			} else if (action.equalsIgnoreCase("receive")){
				System.out.println("\nWhat file would you like to receive? Enter the full file name"
						+ "\nincluding the '.txt' extension.\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);
				
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();
				
				InputStream is = socket.getInputStream();
	    		ObjectInputStream ois = new ObjectInputStream(is);
	    		info = (Info)ois.readObject();
	    		System.out.println(info.getServRes());
	    		
	    		if (info.getServRes().contains("Please try again at a later time or request a different file")){
	    			Thread.sleep(3000);
	    			continue;
	    		}
	    		
	    		int range = info.getNumOfOptions();
	    		int select = Integer.parseInt(fromKeyboard.readLine());
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
				
				is = socket.getInputStream();
        		ois = new ObjectInputStream(is);
        		info = (Info)ois.readObject();
	    		
        		String peer2IP = (info.getPeer2IP()).substring(1);
	    		int peer2Port = info.getPeer2Port();
	    		
	    		try {
					Client client = new Client(peer2IP, peer2Port, input);
					client.makeRequest();
					client.sendRequest();
					System.out.println("\nRequest sent for file!");
					client.getResponse();
					client.useResponse();
					client.close();
					
				} catch (UnknownHostException e) {
					e.printStackTrace();

				} catch (ConnectException e) {
					System.out.println("Connection refused, contributor's system must be offline.");
				}
	    		
	    		System.out.println(info.getServRes());
	    		
	    		input = fromKeyboard.readLine();
	    		while(!(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no"))){
	    			System.out.println("\nPlease enter (yes) or (no).\n"); 
	    			input = fromKeyboard.readLine();
	    		}
	    		info.setPeerRes(input);
	    		os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.flush();
				oos.writeObject(info);
				oos.flush();
				
			} else if (action.equalsIgnoreCase("register")){
				System.out.println("\nWhat file would you like to register? Please register files that you have available and "
						+ "don't register duplicate files.\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);
				info.setPeer1Port(peerPort);
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
			} else if (action.equalsIgnoreCase("unregister")){
				System.out.println("\nWhat file would you like to unregister? Include the '.txt' extension.\n");
				input = fromKeyboard.readLine();
				info.setPeerRes(input);
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
			} else {
				System.out.println("\nOops... Seems like you've entered an invalid response! Please try again.\n");
			}
		}
	}
}