package hjg;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Communicate {
	private String addr;
	private int port;
	private ServerSocket server;
	//private ArrayList<Message> message = new ArrayList<Message>();
	//Message message;
	
	public Communicate() {
		addr = GetLocalAddr();
		try{
			server=new ServerSocket(port);
			Listen();
		} catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
	
	public String getAddr() {
		return addr;
	}

	public String GetLocalAddr() {
        String address;
        try {
            address= InetAddress.getLocalHost().getHostAddress().toString();
            port = GetPort();
            address += ":"+port;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return address;
    }
    
    public int GetPort() {
        int port=10000;
        while (!isPortAvail(port)) {
            port ++;
        }          
        return port;
    }
    
    public boolean isPortAvail(int port) {
         try {
             ServerSocket ss = new ServerSocket(port);
             ss.close();
         } catch (Exception e) {
             return false;
         }
         return true;
    }
    
    public int[] SolveNum(String address) {
    	int[] number = new int[5];
    	Socket sock = null;
    	Message mess = new Message();
    	ObjectOutputStream os = null;  
        ObjectInputStream is = null;  
    	String[] host = address.split(":");
    	try {
    		sock = new Socket(host[0],Integer.parseInt(host[1]));
    		mess.setMessageType(5);				//message type
    		os = new ObjectOutputStream(sock.getOutputStream());
    		os.writeObject(mess);
    		os.flush();
    		
    		is = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
    		Object obj = is.readObject();
    		if (obj != null) {
    			mess = (Message)obj;
    			number[0] = mess.getMessageType();
    			number[1] = mess.getTime();
    			number[2] = mess.getSource();
    			number[3] = mess.getSourceType();
    			number[4] = mess.getUnoccupied();
    		}
    	} catch (Exception e) {
    		System.out.println("Error"+e);		
    	}
    	finally {
    		try {
    			sock.close();
    			is.close();
    			os.close();
    		} catch (Exception e) {
    			System.out.println("Error."+e);
    		}
    	}
    	return number;
    }
    
    public void Listen() {
    	new Thread(new Runnable() {
    		public void run() {
    			while (true) {
    				try {
	    				Socket socket = server.accept();
	    				//System.out.println(socket.getRemoteSocketAddress());
	    				ListenThread lis = new ListenThread(socket);
	    				Thread lisThread = new Thread(lis);
	    				lisThread.start();
    				} catch (Exception e) {
    					System.out.println("Error."+e);
    				}
    			}
    		}
    	}).start();
    }
    
    public class ListenThread implements Runnable {
    	Socket socket;
    	public ListenThread(Socket socket) {
    		this.socket = socket;
    	}
    	public void run() {
			try{
				ObjectInputStream is = null;  
                ObjectOutputStream os = null;
                try {
	                is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
	                os = new ObjectOutputStream(socket.getOutputStream());  
	  
	                Object obj = is.readObject();  
	                Message message = (Message)obj;
	                
	                reply(is, os, message);
                } catch (Exception e) {
    				System.out.println("Error."+e);
    			}
                socket.close();
                is.close();
                os.close();
			} catch(Exception e) {
				System.out.println("Error."+e);
			}
    	}
    	
    	public void reply(ObjectInputStream is, ObjectOutputStream os, Message message) {
        	try {
    			int type = message.getMessageType();
    			int time = message.getTime(), source = message.getSource(), sourceType = message.getSourceType();
    			String addr = message.getAddr();
    			switch(type) {
    				case 1:		//request
    					
    					// ...
    					Gate.localTime = Math.max(time, Gate.localTime) + 1;
    					System.out.println("There is a car entering at gate "+source/*+" localtime:"+Gate.localTime+", remotetime:"+time*/);
    					Gate.requestList.add(new Request(source, addr, time));
    					int t = Math.abs((new Random(Gate.gateNo).nextInt())%5000);
    					System.out.println("wait for "+t/1000+" seconds to Reply");
    					Thread.sleep(t);
    					Message rep = new Message(6, Gate.localTime, Gate.gateNo, Gate.gateEntry?1:0, Gate.gateAddr, Gate.unoccupiedNum);
    					os.writeObject(rep);
    					os.flush();
//    					synchronized(Gate.unoccupiedNum) {
//    						Gate.unoccupiedNum --;	//收到一个请求，可用的车位减一。虽然请求并不代表车以进入，但由于对方请求在先，本入口可用的车位数需要减一.Has some problems
//    					}
    					break;
    				case 2:		//release
    					synchronized(Gate.unoccupiedNum) {
    						Gate.unoccupiedNum --;	
    					}
    					Gate.localTime = Math.max(time, Gate.localTime) + 1;
    					//System.out.println("Receive a release");
    					Gate.requestList.remove(new Request(source, addr, time));
    					System.out.println("Receive a release! Now, there are "+Gate.requestList.size()+" requests in the request list.");
    					if (Gate.hasRequest) {
    						Entry.doEnter();
    					}
    					break;
    				case 3:		//exit a car
    					Gate.localTime = Math.max(time, Gate.localTime) + 1;
    					//System.out.println("Receive a exit");
    					synchronized(Gate.unoccupiedNum) {
    						Gate.unoccupiedNum ++;
    					}
    					System.out.println("Exit a car from "+source+". Now ,there are "+(Gate.totalNum-Gate.unoccupiedNum)+" cars in the park");
    					if (Gate.hasRequest) {
    						Entry.doEnter();
    					}
    					break;
    				case 4:		//add a new gate
    					//System.out.println("Receive a add");
    					boolean isEntry = sourceType==1;
    					String gatename = isEntry?"Entry":"Exit";
    					Gate.gateAddrList.add(addr);
    					if (isEntry) Gate.entryNum ++;
    					else Gate.exitNum++;
    					System.out.println("Add an "+gatename+" at "+ addr+". Now, there are "+Gate.entryNum+" Entry, "+Gate.exitNum+" Exit");
    					//System.out.println("Now, there are "+Gate.entryNum+" Entry, "+Gate.exitNum+" Exit");
    					break;	
    				case 5:		//the new gate ask for information
    					Message mess = new Message(Gate.totalNum, Gate.localTime, Gate.entryNum, Gate.exitNum, Gate.gateAddr, Gate.unoccupiedNum);
                		os.writeObject(mess);
                		os.flush();
                		break;
    			}
        	}
    		catch (Exception e) {
    			System.out.println("Error in reply");
    		}
        }
    }
      
    public void SendMessage(Message mess, ArrayList<String> addressList) {
    	Socket sock = null;
    	ObjectOutputStream os = null;  
        for (String address : addressList) {
	    	String[] host = address.split(":");
	    	try {
	    		sock = new Socket(host[0],Integer.parseInt(host[1]));
	    		os = new ObjectOutputStream(sock.getOutputStream());
	    		os.writeObject(mess);
	    		os.flush();
	    	} catch (Exception e) {
	    		System.out.println("Error"+e);		
	    	}
	    	finally {
	    		try {
	    			sock.close();
	    			os.close();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
        }
    }
     
    public Message SendRequest(Message mess, String address) {
    	Socket sock = null;
    	ObjectOutputStream os = null;  
    	ObjectInputStream is = null; 
    	String[] host = address.split(":");
    	try {
    		sock = new Socket(host[0],Integer.parseInt(host[1]));
    		os = new ObjectOutputStream(sock.getOutputStream());
    		os.writeObject(mess);
    		os.flush();
    		
    		is = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
    		Object obj = is.readObject();	// the reply from other gates
    		if (obj != null) {
    			Message recvmess = (Message)obj;
    			//recvmess.printmsg();
    			return recvmess;
    		}
    	} catch (Exception e) {
    		System.out.println("Error:"+e);		
    	}
    	finally {
    		try {
    			sock.close();
    			os.close();
    			is.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return null;
    }
    
}
