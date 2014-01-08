package hjg;

import java.io.Serializable;

public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6869454523715050428L;
	/**
	 * 1:request
	 * 2:release
	 * 3:exit a car, add a unoccupied
	 * 4:add a new gate
	 */
	private int messageType;	
	private int time;			
	private int source;		
	private int sourceType;	
	private String addr;
	private int unoccupied;	
	
	public Message() { }
	
//	public Message(int msgType, int timeStamp, int source, int portType)
//	{
//		this.setMessageType(msgType);
//		this.setTime(timeStamp);
//		this.setSource(source);
//		this.setSourceType(portType);
//	}
//	
//	public Message(int msgType, int timeStamp, int source, int portType, String addr)
//	{
//		this.setMessageType(msgType);
//		this.setTime(timeStamp);
//		this.setSource(source);
//		this.setSourceType(portType);
//		this.setAddr(addr);
//	}
	
	public Message(int msgType, int timeStamp, int source, int sourceType, String addr, int unoccupied)
	{
		this.setMessageType(msgType);
		this.setTime(timeStamp);
		this.setSource(source);
		this.setSourceType(sourceType);
		this.setAddr(addr);
		this.setUnoccupied(unoccupied);
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getUnoccupied() {
		return unoccupied;
	}

	public void setUnoccupied(int unoccupied) {
		this.unoccupied = unoccupied;
	}
	
	public void printmsg() {
		System.out.println("messageType:"+messageType+", time:"+time+", source:"+source+
				", sourceType:"+sourceType+", addr:"+addr+", unoccupied:"+unoccupied);
	}

}
