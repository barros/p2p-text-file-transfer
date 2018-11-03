/**
 * 	This is  a helping class that uses the string given as the file name.
 *  It then reads the contents of the file as a string and passes the string to the PeerTransfer.java thread.
 */

import java.io.*;
import java.util.Scanner;

public class ServerProcessor {
	StringBuffer buff;
	BufferedReader br;
	String line;

	public String getContents(String fileName) {
		buff = new StringBuffer();
		try {
			File openFile = new File(fileName);
			File f = new File(fileName);

			if (f.isFile()) {
				System.out.println("\n(a file that a currently connected peer is looking for ('" + fileName + "') is being sent)");
				Scanner inFile = new Scanner(openFile);
				while (inFile.hasNext()) {
					line = inFile.nextLine();
					buff.append(line);
					buff.append("\n");
				}
				inFile.close();
				return buff.toString();
			} else {
				System.out.println("\n(a file that a currently connected peer is looking for ('" + fileName + "') does not exist on your system)");
			}
		} catch (Exception e) {
		}
		return null;
	}
}
