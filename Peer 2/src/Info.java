/**
 *  Info serves as the object containing information about files that is passed between
 *  the P2P Transfer Program's broker thread and peers
 */

import java.io.Serializable;

public class Info implements Serializable{
	private static final long serialVersionUID = 1L;
	private String action;
	private String name;
	private String peerRes;
	private String servRes;
	private String peer2IP;
	private int peer1Port;
	private int peer2Port;
	private int peer2Rating;
	private int numOfOptions;
	private int selection;
	private boolean fileExists = false;

	public Info(String name){
		this.setName(name);
	}
	public Info(){}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPeerRes() {
		return peerRes;
	}
	public void setPeerRes(String peerRes) {
		this.peerRes = peerRes;
	}

	public String getServRes() {
		return servRes;
	}
	public void setServRes(String servRes) {
		this.servRes = servRes;
	}

	public String getPeer2IP() {
		return peer2IP;
	}
	public void setPeer2IP(String peer2ip) {
		peer2IP = peer2ip;
	}

	public int getPeer1Port() {
		return peer1Port;
	}
	public void setPeer1Port(int peer1Port) {
		this.peer1Port = peer1Port;
	}

	public int getPeer2Port() {
		return peer2Port;
	}
	public void setPeer2Port(int peer2Port) {
		this.peer2Port = peer2Port;
	}

	public int getPeer2Rating() {
		return peer2Rating;
	}
	public void setPeer2Rating(int peer2Rating) {
		this.peer2Rating = peer2Rating;
	}

	public int getNumOfOptions() {
		return numOfOptions;
	}
	public void setNumOfOptions(int numOfOptions) {
		this.numOfOptions = numOfOptions;
	}

	public int getSelection() {
		return selection;
	}
	public void setSelection(int selection) {
		this.selection = selection;
	}

	public boolean isFileExists() {
		return fileExists;
	}
	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}
}
