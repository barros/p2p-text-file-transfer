/**
 * This is a thread that provides methods to process peer-to-peer transfers.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerTransfer implements Runnable{
	private ServerSocket peerSocket = null;

	InputStream recvStream;
	OutputStream sendStream;
	String request;
	String response;

	public PeerTransfer(ServerSocket peerSocket){
		this.peerSocket = peerSocket;
	}

	public void run(){
		try{
			while (true){
				Socket s = peerSocket.accept();
				recvStream = s.getInputStream();
				sendStream = s.getOutputStream();
				getRequest();
				process();
				sendResponse();
				close(s);
				System.out.println("(a peer has disconnected)");
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private void getRequest(){
		try{
			int dataSize;
			while ((dataSize = recvStream.available()) == 0);
			byte[] recvBuff = new byte[dataSize];
			recvStream.read (recvBuff, 0, dataSize);
			request = new String (recvBuff, 0, dataSize);
		} catch (IOException ex){
			System.err.println ("IOException in getRequest (PeerTransfer.java)");
		}
	}

	private void process(){
		ServerProcessor processor = new ServerProcessor();
		response = processor.getContents(request);
	}

	private void sendResponse(){
		try{
			if (response == null){
				byte[] sendBuff = new byte[100];
				sendBuff = "\nUnfortunately, the file could not be found in the contributor's system.".getBytes();
				sendStream.write (sendBuff, 0, sendBuff.length);
				return;
			}
			byte[] sendBuff = new byte [response.length()];
			sendBuff = response.getBytes();
			sendStream.write (sendBuff, 0, sendBuff.length);
			sendStream.flush();
		} catch (IOException ex){
			System.err.println ("IOException in sendResponse (PeerTransfer.java)");
		}
	}

	private void close (Socket s){
		try{
			recvStream.close();
			sendStream.close();
			s.close();
		} catch (IOException ex){
			System.err.println ("IOException in close (PeerTransfer.java)");
		}
	}
}
