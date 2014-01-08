package hjg;

import java.util.ArrayList;


public class DisPark {
	private static Gate gate;
	private static int gateNo;
	private static ArrayList<String> gateAddrList;

	public static void main(String args[]) throws Exception {
		int argnum =args.length;
		if (argnum < 3) {
			throw new Exception("错误：参数错误！");
		}
		gateNo = Integer.parseInt(args[0]);
		if (gateNo == 1) {
			int totalnum = Integer.parseInt(args[1]);
			if (totalnum<=0) {
				throw new Exception("错误：车位数不能为负数！");
			}
			if (args[2].equalsIgnoreCase(new String("entry"))) {
				gate = new Entry(gateNo,totalnum);
				gate.setGateEntry(true);
			}
			else {
				gate = new Exit(gateNo, totalnum);
			}
			
		}
		else {
			gateAddrList = new ArrayList<String>(argnum-2);
			for (int i=2; i<argnum; i++) {
				gateAddrList.add(args[i]);
			}
			
			if (args[1].equalsIgnoreCase(new String("entry"))) {
				gate = new Entry(gateNo, gateAddrList);
				gate.setGateEntry(true);
			}
			else {
				gate = new Exit(gateNo, gateAddrList);
			}
			
			
			gate.SolveNum(gateAddrList);
		}
		
		gate.Run();		
	}
}
