package hjg;

public class Request {
	int gateNo;
	String gateAddr;
	int requestTime;
	
	public Request(int gateNo, String gateAddr, int requestTime) {
		this.gateNo = gateNo;
		this.gateAddr = gateAddr;
		this.requestTime = requestTime;
	}
	
	@Override
	public boolean equals(Object re) {
		Request r=(Request)re;
		if(this.gateNo == r.gateNo && this.requestTime == r.requestTime && this.gateAddr.equals(r.gateAddr))
			return true;
		return false;
	}

}
