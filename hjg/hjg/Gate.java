package hjg;

import java.util.ArrayList;

public class Gate {
	/**
	 * this gate's number
	 */
	protected static int gateNo;
	protected static int totalNum=0;
	protected static int entryNum=0;
	protected static int exitNum=0;
	protected static Integer unoccupiedNum=0;
	protected static boolean gateEntry=false;
	protected static Communicate comm = new Communicate();
	public static ArrayList<String> gateAddrList;
	protected static String gateAddr;
	protected boolean busy;
	
	protected static int localTime = 0;
	static boolean hasRequest = false;	//此入口是否存在请求
	static int requesttime;				//请求时间
	
	static ArrayList<Request> requestList;
	
	public Gate() {
		gateNo = 0;
		Gate.gateAddr = comm.getAddr();
	}
	
	public Gate(int gateNo, int totalNum) {
		Gate.totalNum = totalNum;
		Gate.unoccupiedNum = totalNum;
		Gate.gateAddr = comm.getAddr();
		Gate.gateNo = gateNo;
		Gate.gateAddrList = new ArrayList<String>();
		Gate.requestList = new ArrayList<Request>();
		System.out.println("There are totally "+totalNum+" carports");
	}
	
	public Gate(int gateNo, ArrayList<String> gateList) {
		Gate.gateAddrList = gateList;
		Gate.gateAddr = comm.getAddr();
		Gate.gateNo = gateNo;
		Gate.requestList = new ArrayList<Request>();
	}

	public boolean isGateEntry() {
		return gateEntry;
	}

	public void setGateEntry(boolean gateEntry) {
		Gate.gateEntry = gateEntry;
	}

	/**
	 * 向其他节点询问各个数目
	 * @return 成功与否
	 */
	public boolean SolveNum(ArrayList<String> gateList) {
		int[] num = comm.SolveNum(gateList.get(0)) ;
		Gate.totalNum = num[0];
		Gate.localTime = num[1]+1;
		Gate.entryNum = num[2];
		Gate.exitNum = num[3];
		Gate.unoccupiedNum = num[4];
		if (gateEntry) Gate.entryNum ++;
		else Gate.exitNum ++;
		System.out.println("Now time:"+localTime+", total:"+totalNum+",entry:"+entryNum+",exit:"+exitNum+", unoccupied:"+unoccupiedNum);
		return true;
	}
	
	public void Run() {
		String gate = gateEntry ? "Entry" : "Exit";
		System.out.println("This is an " + gate + " at " + gateAddr + ". Gate number is " + gateNo);
		Message mes = new Message(4, 0, gateNo, gateEntry?1:0, gateAddr, Gate.unoccupiedNum);		//messagetype : add
		comm.SendMessage(mes, gateAddrList);
	}
	
}
