import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class BrokerThread implements Runnable{
	private Socket socket = null;
	private HashMap<String, ArrayList<Triple>> library;
	private HashMap<String, Integer> ratings;
	private int threadNum;

	public BrokerThread(Socket socket, HashMap<String, ArrayList<Triple>> library, HashMap<String, Integer> ratings, int threadNum) {
		this.socket = socket;
		this.library = library;
		this.ratings = ratings;
		this.threadNum = threadNum;
	}

	public void run(){
		InetAddress clientAddress = socket.getInetAddress();
		int clientPort = socket.getPort();
		String name = null, peerRes, action;
		int rating;
		try{
    		InputStream is = socket.getInputStream();
    		ObjectInputStream ois = new ObjectInputStream(is);
    		Info info = (Info)ois.readObject();
    		name = info.getName();

			// display thread number handling certain peer
    		System.out.println("\nThread " + threadNum + " is handing " + name + " at " + clientAddress + " on port " + clientPort + ".");

    		while (true){
    			is = socket.getInputStream();
        		ois = new ObjectInputStream(is);
        		info = (Info)ois.readObject();
        		action = info.getAction();
        		peerRes = info.getPeerRes();

				// if peer would like to receive a file...
        		if (action.equalsIgnoreCase("receive")){
        			System.out.println("\n" + name + " at " + clientAddress + " on port " + clientPort + " has requested the file: " + peerRes);
        			boolean fileExists = library.containsKey(peerRes.toLowerCase());
        			String servRes = "";

					// if file exists in the look-up database...
        			if (fileExists){
        				servRes += "\nThe file you've requested is available from the following sources.\n"
        						+ "Please choose the number of the source you'd like to receive the file from:\n\n";
        				int count = 1;
        				ArrayList<Triple> temp = library.get(peerRes.toLowerCase());
        				for (Triple triple : temp){
        					rating = ratings.get(triple.getName());
							if (rating == -1){
								servRes += ("("+count+") User: " + triple.getName() + ", Rating: \033[3mnew contributor\033[0m\n");
							} else {
        						servRes += ("("+count+") User: " + triple.getName() + ", Rating: " + rating + "\n");
							}
							count++;
        				}
        				info.setNumOfOptions(temp.size());
        				info.setServRes(servRes);
						// send peer the name and rating of a peer that is willing to share file
        				OutputStream os = socket.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(os);
        				oos.flush();
        				oos.writeObject(info);
        				oos.flush();

						// receive selection from peer of where they want to receive file from
        				is = socket.getInputStream();
                		ois = new ObjectInputStream(is);
                		info = (Info)ois.readObject();

                		Triple toSend = temp.get(info.getSelection()-1);
						String contributor = toSend.getName();
                		info.setPeer2IP(toSend.getIPAddress());
                		info.setPeer2Port(toSend.getPortNum());
        				servRes = "\nIf you received a file, please evaluate it...\nWould you recommend this contributor?"
        						+ "\n\nPlease respond with either (yes) or (no).\n";
        				info.setServRes(servRes);
						// ask peer to evaluate the file (if one has been received)
        				os = socket.getOutputStream();
        				oos = new ObjectOutputStream(os);
        				oos.flush();
        				oos.writeObject(info);
        				oos.flush();
        				System.out.println("\nSent " + name + " the information of '" + contributor + "'.");

						// receive peer's evaluation
        				is = socket.getInputStream();
                		ois = new ObjectInputStream(is);
                		info = (Info)ois.readObject();

						// update ratings
                		if (info.getPeerRes().equalsIgnoreCase("yes")){
                			if (((ratings.get(contributor)+10) > 100) || ((ratings.get(contributor)) == -1)){
                				ratings.put(contributor, 100);
                			} else {
                				int rate = ratings.get(contributor);
                				ratings.put(contributor, rate+10);
                			}
                		} else {
							if (ratings.get(contributor) == -1){
								ratings.put(contributor, 70);
							} else if ((ratings.get(contributor)-10) < 0){
                				ratings.put(contributor, 0);
                			} else {
                				int rate = ratings.get(contributor);
                				ratings.put(contributor, rate-10);
                			}
                		}
        			}
					// if the file is not currently being shared...
					else {
        				servRes += "\nNobody is currently sharing the file you have requested.\n"
        						+ "Please try again at a later time or request a different file.\n";
        				info.setServRes(servRes);
        				OutputStream os = socket.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(os);
        				oos.flush();
        				oos.writeObject(info);
        				oos.flush();
        				continue;
        			}
        		}
				// if the peer would like to register their version of a file...
				else if (action.equalsIgnoreCase("register")){
					// if this is the peer's first registration, set rating to -1
        			if (!(ratings.containsKey(name))){
        				ratings.put(name, (-1));
        			}
        			Triple temp = new Triple(clientAddress.toString(), info.getPeer1Port(), name);
					// if the file is not already a part of the look-up database, add it
        			if (!(library.containsKey(peerRes))){
        				ArrayList<Triple> newAL = new ArrayList<>();
        				newAL.add(temp);
        				library.put(peerRes, newAL);
        			}
					// if the file exists in the look-up database, check for duplicates from same peer
					else {
        				ArrayList<Triple> update = library.get(peerRes);
        				boolean isDuplicate = false;
        				for (int i=0; i<update.size(); i++){
        					Triple trip = update.get(i);
							// if duplicate exists, inform peer they cannot register the same file twice
        					if (trip.getName().equals(name)){
        						info.setServRes("\nYou cannot register two of the same files. "
        								+ "\nIn order to update the file you have registered, please unregister it first.");
        						OutputStream os = socket.getOutputStream();
                				ObjectOutputStream oos = new ObjectOutputStream(os);
                				oos.flush();
                				oos.writeObject(info);
                				oos.flush();
                				isDuplicate = true;
                				break;
        					}
        				}
						// if duplicate was found, continue to receive next action from peer
        				if (isDuplicate){
        					continue;
        				}
        				update.add(temp);
        				library.put(peerRes, update);
        			}
					// thank peer for registering a file
        			info.setServRes("\nThank you for registering '" + peerRes + "'.");
    				OutputStream os = socket.getOutputStream();
    				ObjectOutputStream oos = new ObjectOutputStream(os);
    				oos.flush();
    				oos.writeObject(info);
    				oos.flush();
        			System.out.println("\n" + name + " just registered the file: " + peerRes);
        		}
				// if the peer would like to unregister their version of a file...
        		else if (action.equalsIgnoreCase("unregister")){
        			// if the file does not exist in the look-up database, let peer know
        			if (!(library.containsKey(peerRes))){
        				info.setServRes("\nThat file is currently not being shared.");
        				OutputStream os = socket.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(os);
        				oos.flush();
        				oos.writeObject(info);
        				oos.flush();
        				continue;
        			}
					// if the file does exist in the look-up database...
					else {
        				ArrayList<Triple> update = library.get(peerRes);

						// check if the peer is among list of sharing peers. if so, remove
        				for (int i=0; i<update.size(); i++){
        					Triple temp = update.get(i);
        					if (temp.getName().equals(name)){
        						update.remove(i);
        						System.out.println("\n" + name + "'s copy of the file '" + peerRes + "' has been removed from the look-up database.");
        					}
        				}
						// if no more peers are sharing a certain file, delete file from look-up database
        				if (update.size() == 0){
        					library.remove(peerRes);
        					System.out.println("\n'" + peerRes + "' has been removed from the look-up database.");
        				} else {
        					library.put(peerRes, update);
        				}
						// let peer know their file has been removed
        				info.setServRes("\nYour file has been removed from the look-up database. Thank you for sharing it.");
        				OutputStream os = socket.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(os);
        				oos.flush();
        				oos.writeObject(info);
        				oos.flush();
        			}
        		}
    		}
		} catch (EOFException eof){
			// display the name of the user that has disconnected
			System.out.println("\n" + name + " at " + clientAddress + " on port " + clientPort + " has disconnected.\n");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
