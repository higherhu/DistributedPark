package hjg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Exit extends Gate {
	
	public Exit() {}
	
	public Exit(int gateNo, int totalNum) {
		super(gateNo, totalNum);
		// TODO Auto-generated constructor stub
		Gate.exitNum=1;
	}
	
	public Exit(int gateNo, ArrayList<String> gateList) {
		super(gateNo, gateList);
		// TODO Auto-generated constructor stub
	}
	
	public void Run() {
		super.Run();
		System.out.println("In the Exit");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		while (true) {
			try {
				input = br.readLine();
				ExecCommand(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void ExecCommand(String command) {
		String[] com = command.split(" ");
		switch (com[0]) {
		case "exit":
			if (Gate.totalNum - Gate.unoccupiedNum > 0) {
				System.out.println("There is a car exiting. This process will last for 2 seconds ... ");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message mess = new Message(3, ++localTime, gateNo, 0, Gate.gateAddr, Gate.unoccupiedNum);	//message type=3, exit a car
				comm.SendMessage(mess, Gate.gateAddrList);
				Gate.unoccupiedNum ++;
				System.out.println("Exit a car in this gate. Now, there are "+(Gate.totalNum-Gate.unoccupiedNum)+" cars in the park");
			} else {
				System.out.println("There is No car in the park");
			}
			break;
		case "show":
			String output = "\n This is the "+Gate.gateNo+" gate. This is an ";
			output += this.isGateEntry()?"Entry":"Exit";
			output += " gate.\n The local time is "+Gate.localTime;
			output += "\n There are "+Gate.totalNum+" parking lots in total, and left "+Gate.unoccupiedNum+".";
			output += "\n There are "+Gate.entryNum+" entry, and "+Gate.exitNum+" exit. They are:\n";
			for (String add : Gate.gateAddrList) {
				output += "\t"+add+"\n";
			}
			output += "\t"+Gate.gateAddr;
			output += "\n There are "+Gate.requestList.size()+" requests in the list. They are:\n";
			for (Request re : Gate.requestList) {
				output += "\tGate "+re.gateNo+" at time "+re.requestTime+"\n";
			}
			System.out.println(output);
			break;
		case "help":
		case "h":
			String help = "  Help:\n  The commands and meanings are as follows:\n";
			help += "\tenter:\tEnter a car at this entry.\n";
			help += "\tshow:\tShow the status of the parking lot.\n";
			help += "\thelp:\tShow this help information\n";
			System.out.println(help);
			break;
		default:
			System.out.println("Error:No meaning command! Type \"h\" for help");	
		}
//		if (com[0].equals("exit")) {
//			if (Gate.totalNum - Gate.unoccupiedNum > 0) {
//				System.out.println("There is a car exiting. This process will last for 5 seconds ... ");
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Message mess = new Message(3, ++localTime, gateNo, 0, Gate.gateAddr, Gate.unoccupiedNum);	//message type=3, exit a car
//				comm.SendMessage(mess, Gate.gateAddrList);
//				Gate.unoccupiedNum ++;
//				System.out.println("Exit a car in this gate. Now, there are "+(Gate.totalNum-Gate.unoccupiedNum)+" cars in the park");
//			} else {
//				System.out.println("There is No car in the park");
//			}
//		} else if (com[0].equals("show")){
//			String output = "\n This is the "+Gate.gateNo+" gate. This is an ";
//			output += this.isGateEntry()?"Entry":"Exit";
//			output += " gate.\n The local time is "+Gate.localTime;
//			output += "\n There are "+Gate.totalNum+" parking lots in total, and left "+Gate.unoccupiedNum+".";
//			output += "\n There are "+Gate.entryNum+" entry, and "+Gate.exitNum+" exit. They are:\n";
//			for (String add : Gate.gateAddrList) {
//				output += "\t"+add+"\n";
//			}
//			output += "\t"+Gate.gateAddr;
//			output += "\n There are "+Gate.requestList.size()+" requests in the list. They are:\n";
//			for (Request re : Gate.requestList) {
//				output += "\tGate "+re.gateNo+" at time "+re.requestTime+"\n";
//			}
//			System.out.println(output);
//		}
//		else {
//			System.out.println("No meaning command");
//		}
	}

}
