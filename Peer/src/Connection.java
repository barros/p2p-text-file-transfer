/**
 * 	This simulates a very simple version of FTP protocol. A peer requests the name
 *	of the file and the contributing peer sends the contents. The name of the file is
 *	passed to the program as the command argument.
 */

import java.net.*;
import java.io.*;

public class Connection {
	Socket sock;
	OutputStream sendStream;
	InputStream recvStream;
	String request;
	String response;
	String fileName;
	Boolean noFile=false;

	Connection(String server, int port, String fileName) throws IOException, UnknownHostException {
		this.fileName = fileName;
		this.sock = new Socket(server, port);
		this.sendStream = sock.getOutputStream();
		this.recvStream = sock.getInputStream();
	}

	public void makeRequest(){
		request = fileName;
	}

	public void sendRequest(){
		try {
			byte[] sendBuff = new byte[request.length()];
			sendBuff = request.getBytes();
			sendStream.write(sendBuff, 0, sendBuff.length);
		} catch (IOException ex) {
			System.err.println("IOException in sendRequest (Connection.java)");
		}
	}

	public void getResponse() throws IOException {
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
			System.err.println("IOException in getResponse (Connection.java)");
		}
	}

	public void useResponse(){
		if (!noFile){
			ClientUser user = new ClientUser();
			user.store(fileName, response);
		}
	}

	public void close(){
		try {
			sendStream.close();
			recvStream.close();
			sock.close();
		} catch (IOException ex) {
			System.err.println("IOException in close (Connection.java)");
		}
	}
}
