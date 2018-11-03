// Triple serves as a way to store a peer'name IP Address, port number,
// and user name for the P2P Transfer Program

public class Triple {
	private String IPAddress;
	private int portNum;
	private String name;

	public Triple(String ip, int portNum, String name){
		this.setIP(ip);
		this.setPortNum(portNum);
		this.setName(name);
	}

	public String getIPAddress() {
		return IPAddress;
	}
	public void setIP(String IPAddress) {
		this.IPAddress = IPAddress;
	}

	public int getPortNum() {
		return portNum;
	}
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
