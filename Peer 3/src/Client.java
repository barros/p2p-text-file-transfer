/**
 * 	This simulates a very simple version of FTP protocol. The client requests the name
 *	of the file; the server sends the contents of the file. The name of the file is
 *	passed to the program as the command argument. 
 */

import java.net.*;
import java.io.*;

public class Client {
	Socket sock;
	OutputStream sendStream;
	InputStream recvStream;
	String request;
	String response;
	String fileName;
	Boolean noFile=false;

	Client(String server, int port, String fileName) throws IOException, UnknownHostException {
		this.fileName = fileName;
		sock = new Socket(server, port);
		sendStream = sock.getOutputStream();
		recvStream = sock.getInputStream();
	}

	void makeRequest(){
		request = fileName;
	}

	void sendRequest(){
		try {
			byte[] sendBuff = new byte[request.length()];
			sendBuff = request.getBytes();
			sendStream.write(sendBuff, 0, sendBuff.length);
		} catch (IOException ex) {
			//System.err.println("IOException in sendRequest");
		}
	} 

	void getResponse() throws IOException {
		try {
			int dataSize = 9999999; 
			byte[] recvBuff = new byte[dataSize];
			recvStream.read(recvBuff, 0, dataSize);
			response = new String(recvBuff, 0, dataSize);
			if (response.contains("Unfortunately, the file could not be found in the contributor's system")){
				noFile = true;
			}
		} catch (SocketException e) {
			System.out.println("\nConnection closed prematurely");
			sock.close();

		} catch (IOException ex) {
			//System.err.println("IOException in getResponse");
		}
	}

	void useResponse(){
		if (!noFile){
			ClientUser user = new ClientUser();
			user.store(fileName, response);
		}
	}

	void close(){
		try {
			sendStream.close();
			recvStream.close();
			sock.close();
		} catch (IOException ex) {
			//System.err.println("IOException in close");
		}
	}
}