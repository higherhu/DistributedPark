package hjg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Entry extends Gate {

	public Entry() {}
	
	public Entry(int gateNo, int totalNum) {
		super(gateNo, totalNum);
		Gate.entryNum = 1;
	}
	
	public Entry(int gateNo, ArrayList<String> gateList) {
		super(gateNo, gateList);
	}
	
	public void Run() {
		super.Run();
		System.out.println("In the Entry");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		while (true) {
			try {
				//System.out.print(">>");
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
		case "enter":
			if (Gate.hasRequest) { // 一次只能接受一个进入请求
				System.out.println("There has already exist a request in this gate, please try later");
				return;
			}
			Gate.hasRequest = true;
			EntryACar();
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
			System.out.println("Error: No meaning command! Type \"h\" for help");	
		}
	}
	
	public void EntryACar() {
		System.out.println("There is a car request to enter ... ");		
		Gate.requesttime = Gate.localTime;	//记录请求时间
		Gate.requestList.add(new Request(Gate.gateNo, Gate.gateAddr, requesttime));	// add to requestList;
		Gate.localTime ++;		//time ++
		Message mess = new Message(1, requesttime, Gate.gateNo, Gate.gateEntry?1:0, Gate.gateAddr, Gate.unoccupiedNum); // request to enter
		ExecutorService pool = Executors.newFixedThreadPool(Gate.gateAddrList.size());
		ArrayList<Future<Object>> requestFuture = new ArrayList<Future<Object>>();
		for (String address : Gate.gateAddrList) {
			SendRequestCallable c = new SendRequestCallable(mess, address);
			Future<Object> f = pool.submit(c);
			requestFuture.add(f);
		}
		pool.shutdown();
		for (Future<Object> f : requestFuture) {
			try {
				Message me = (Message) f.get();
				//me.printmsg();
				if (me.getSourceType() == 1) {
					System.out.println("Receive Reply from Entry "+me.getSource());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		doEnter();
	}

	public class SendRequestCallable implements Callable<Object> {
		Message mess;
		String addr;
		public SendRequestCallable(Message mess, String addr) {
			this.mess = mess;
			this.addr = addr;
		}
		
		public Message call() throws Exception {
			Message re = Gate.comm.SendRequest(mess, addr);
			return re;
		}
	}
	
	public static void doEnter() {
		boolean canEnter = true;
		if (Gate.unoccupiedNum < 1) {
			canEnter = false;
			System.out.println("Error! There is not enough room for a new car!");
			return;
		}
		for (Request re : Gate.requestList) {
			if (re.requestTime < requesttime ) {
				//System.out.println("remotetime:"+re.requestTime+", localtime:"+requesttime);
				canEnter = false;
				break;
			} else if (re.requestTime == requesttime && re.gateNo < Gate.gateNo) {
				canEnter = false;
				break;
			}
		}
		if (canEnter) {
			System.out.println("Now the car can Enter!");
			System.out.println("The car is Entering. This process will last for 5 seconds ...");
			Gate.unoccupiedNum --;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Gate.hasRequest = false;
			System.out.println("The car has entered sucessful!");
			/*
			 * 从本节点的request列表中移除本次请求
			 */
			Gate.requestList.remove(new Request(Gate.gateNo, Gate.gateAddr, requesttime)); 
			Message mess = new Message(2, requesttime, Gate.gateNo, Gate.gateEntry?1:0, Gate.gateAddr, Gate.unoccupiedNum); //release message
			comm.SendMessage(mess, Gate.gateAddrList);
			return ;
		} else
			System.out.println("Waiting for anther Entry ...");
	}

}
