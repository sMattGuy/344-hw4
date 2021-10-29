import java.io.*;

class Flammia_TA extends Thread{
	//prim
	private InputStream PrimIS;
	private OutputStream PrimOS;
	//obj
	private ObjectInputStream ObjIS;
	//constructor
	public Flammia_TA(InputStream PrimIS, OutputStream PrimOS, ObjectInputStream ObjIS){
		this.PrimIS = PrimIS;
		this.PrimOS = PrimOS;
		this.ObjIS = ObjIS;
	}
	private void send() throws Exception{
		System.out.println("TA is Sending");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("TA.txt")));
		int next = 0;
		int read = 0;
		do{
			read = this.PrimIS.read();
			String msg = "";
			for(int i=0;i<read;i++){
				next = br.read();
				if(next != 13 && next != 10){
					msg += (char)next;
				}
				this.PrimOS.write(next);
				if(next == -1){
					break;
				}
			}
			System.out.println("TA Sent: " + msg);
		}while(read != 0 && next != -1);
		System.out.println("TA Done Sending");
	}
	private void receive() throws Exception{
		System.out.println("TA is Receiving");
		int curr = 0;
		while(curr != 255){
			this.PrimOS.write(10);
			String msg = "";
			do{
				curr = this.PrimIS.read();
				if(curr == 255){
					break;
				}
				if(curr != 13 && curr != 10){
					msg += (char)curr;
				}
			}while(this.PrimIS.available() != 0);
			System.out.println("TA Reads: " + msg);
		}
		System.out.println("TA Done Reading");
	}
	
	private void receiveObject(){
		System.out.println("TA is Receiving an Object");
		try{
			this.ObjIS = new ObjectInputStream(this.PrimIS);
			Flammia_Message m = (Flammia_Message)this.ObjIS.readObject();
			System.out.println("TA Received Object: " + m);
		}
		catch(Exception e){
			System.out.println("Error in TA: " + e);
		}
	}
	
	public void run(){
		System.out.println("TA has started execution");
		
		try{
			this.receiveObject();
			this.send();
			this.receive();
		}
		catch(Exception e){
			System.out.println("Error in TA: " + e);
		}
	}
}

class Flammia_TB extends Thread{
	//prim
	private InputStream PrimIS;
	private OutputStream PrimOS;
	//obj
	private ObjectInputStream ObjIS;
	//constructor
	public Flammia_TB(InputStream PrimIS, OutputStream PrimOS, ObjectInputStream ObjIS){
		this.PrimIS = PrimIS;
		this.PrimOS = PrimOS;
		this.ObjIS = ObjIS;
	}
	private void send() throws Exception{
		System.out.println("TB is Sending");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("TB.txt")));
		int next = 0;
		int read = 0;
		do{
			read = this.PrimIS.read();
			String msg = "";
			for(int i=0;i<read;i++){
				next = br.read();
				if(next != 13 && next != 10){
					msg += (char)next;
				}
				this.PrimOS.write(next);
				if(next == -1){
					break;
				}
			}
			System.out.println("TB Sent: " + msg);
		}while(read != 0 && next != -1);
		System.out.println("TB Done Sending");
	}
	private void receive() throws Exception{
		System.out.println("TB is Receiving");
		int curr = 0;
		while(curr != 255){
			this.PrimOS.write(10);
			String msg = "";
			do{
				curr = this.PrimIS.read();
				if(curr == 255){
					break;
				}
				if(curr != 13 && curr != 10){
					msg += (char)curr;
				}
			}while(this.PrimIS.available() != 0);
			System.out.println("TB Reads: " + msg);
		}
		System.out.println("TB Done Reading");
	}
	
	private void receiveObject(){
		System.out.println("TB is Receiving an Object");
		try{
			this.ObjIS = new ObjectInputStream(this.PrimIS);
			Flammia_Message m = (Flammia_Message)this.ObjIS.readObject();
			System.out.println("TB Received Object: " + m);
		}
		catch(Exception e){
			System.out.println("Error in TB: " + e);
		}
	}
	
	public void run(){
		System.out.println("TB has started execution");
		try{
			this.receiveObject();
			this.receive();
			this.send();
		}
		catch(Exception e){
			System.out.println("Error in TB: " + e);
		}
	}
}

class Flammia_TC extends Thread{
	//prim
	private OutputStream TAPrimOS;
	private OutputStream TBPrimOS;
	//obj
	private ObjectOutputStream ObjOS;
	//constructor
	public Flammia_TC(OutputStream TAPrimOS, OutputStream TBPrimOS, ObjectOutputStream ObjOS){
		this.TAPrimOS = TAPrimOS;
		this.TBPrimOS = TBPrimOS;
		this.ObjOS = ObjOS;
	}
	private void send() throws Exception{
		System.out.println("TC is Sending");
		Flammia_Message message = new Flammia_Message(14,1);
		System.out.println("TC Sends: " + message);
		this.ObjOS = new ObjectOutputStream(this.TAPrimOS);
		this.ObjOS.writeObject(message);
		
		message = new Flammia_Message(41,2);
		System.out.println("TC Sends: " + message);
		this.ObjOS = new ObjectOutputStream(this.TBPrimOS);
		this.ObjOS.writeObject(message);
		System.out.println("TC is Closing");
	}
	public void run(){
		try{
			System.out.println("TB has started execution");
			this.send();
		}
		catch(Exception e){
			System.out.println("Error in TC: " + e);
		}
	}
}

class Flammia_Message implements Serializable{
	public int number, id;
	public Flammia_Message(int number, int id){
		this.number = number; 
		this.id = id;
	}
	public String toString(){
		return "ID: " + this.id + ", NUM: " + this.number;
	}
}

public class Flammia_Manager{
	//TA input
	static private PipedInputStream pis1;
	//TA output
	static private PipedOutputStream pos1;
	//TB input
	static private PipedInputStream pis2;
	//TB output
	static private PipedOutputStream pos2;
	
	static private ObjectInputStream ois;
	static private ObjectOutputStream oos;
	
	public static void main (String args[]) {
		try {
			// set up a pipe
			System.out.println("Pipe setup");
			//TA pipes
			pos1 = new PipedOutputStream();
			pis1 = new PipedInputStream(pos1);
			
			//TB pipes
			pos2 = new PipedOutputStream();
			pis2 = new PipedInputStream(pos2);
			
			System.out.println("Thread creation");
			Flammia_TA TA = new Flammia_TA(pis2, pos1, ois);
			Flammia_TB TB = new Flammia_TB(pis1, pos2, ois);
			Flammia_TC TC = new Flammia_TC(pos2, pos1, oos);
			
			System.out.println("Thread execution");
			
			TA.start(); 
			TB.start();
			TC.start();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}