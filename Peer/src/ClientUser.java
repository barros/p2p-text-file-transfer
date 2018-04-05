/**
 * 	This is a helping class that uses the string returned by the peer.
 *	It stores the contents of the string in a file whose name is defined as the 
 *	argument to the client program.   
 */

import java.util.*;
import java.io.*;

public class ClientUser {
	public void store (String fileName, String response){	
		Scanner scan = new Scanner(response);
		try{
			BufferedWriter bw = new BufferedWriter (new FileWriter (fileName));
			while (scan.hasNext()){
				bw.write (scan.nextLine());
				bw.newLine();
			}
			bw.flush();
			bw.close();
			scan.close();
		} catch (Exception e) {}
	}
}
