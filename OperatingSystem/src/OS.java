import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class OS{
	
	static int counter = 1;
	static Mutex file = new Mutex();
	static Mutex userInput = new Mutex();
	static Mutex userOutput = new Mutex();
	static String space2;
	static Scanner sc = new Scanner (System.in);
	static String data;
	
	public static ArrayList<String> getInst(String path) throws IOException {
		ArrayList<String> finalinst = new ArrayList<String>();
		List<String> file = Files.readAllLines(Paths.get(path));
		Queue<String> inst = new LinkedList<String>();
		
		for(int i = 0; i<file.size(); i++) {
			String str = file.get(i);
			String[] array = str.split("\\s");
			for(int j=0; j<array.length; j++) {
				inst.add(array[j]);
			}
		}
		while(!inst.isEmpty()) {
			String x="";
			if(inst.peek().equals("assign")) {
				x += inst.remove();
				x += " ";
				x+=inst.remove();
				x += " ";
				if(inst.peek().equals("readFile")) {
					x+="rf";
				}
				else if(inst.peek().equals("input")) {
					x+="in";
				}
				else {
					x+=inst.remove();
				}
			}
			else if(inst.peek().equals("readFile")) {
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			else if(inst.peek().equals("print")) {
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			else if(inst.peek().equals("writeFile")) {
				x += inst.remove();
				x += " ";
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			else if(inst.peek().equals("printFromTo")) {
				x += inst.remove();
				x += " ";
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			else if(inst.peek().equals("semWait")) {
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			else if(inst.peek().equals("input")) {
				x += inst.remove();
			}
			else {
				x += inst.remove();
				x += " ";
				x += inst.remove();
			}
			finalinst.add(x);
		}
		return finalinst;
	}
	
    public static void codeParser(Process p) throws IOException{
    	counter = 1;
        while(p.pcb.PC < (p.pcb.end - 7) && counter <= Scheduling.timeSlice){
        	if(Scheduling.clock == Scheduling.p1.arrival && 
        			(!Scheduling.Rqueue.contains(Scheduling.p1.pcb.pid)) && p!=Scheduling.p1) {
        		Scheduling.p1.pcb.pid = (int) (Math.random()*(15));
        		Scheduling.p1.pcb.pstate = "Ready";
        		Scheduling.Rqueue.add(Scheduling.p1.pcb.pid);
        		System.out.println("Process " + Scheduling.p1.pcb.pid + " enters the ready state"
        				+ ". Ready Queue: " + Scheduling.Rqueue 
        				+ ". Blocked Queue: " + Scheduling.Bqueue);
        		Scheduling.MemAssign(Scheduling.p1);
        		if(Scheduling.p1.state == "Memory") {
      				Scheduling.lastprocess = Scheduling.p1.pcb.pid;
      			}
        	}
        	if(Scheduling.clock == Scheduling.p2.arrival && 
        			(!Scheduling.Rqueue.contains(Scheduling.p2.pcb.pid)) && p!=Scheduling.p2) {
        		Scheduling.p2.pcb.pid = (int)(15 + Math.random()*(30-15));
        		Scheduling.p2.pcb.pstate = "Ready";
        		Scheduling.Rqueue.add(Scheduling.p2.pcb.pid);
        		System.out.println("Process " + Scheduling.p2.pcb.pid + " enters the ready state"
        				+ ". Ready Queue: " + Scheduling.Rqueue 
        				+ ". Blocked Queue: " + Scheduling.Bqueue);
  	  			Scheduling.MemAssign(Scheduling.p2);
  	  		if(Scheduling.p2.state == "Memory") {
  				Scheduling.lastprocess = Scheduling.p2.pcb.pid;
  			}
        	}
        	if(Scheduling.clock == Scheduling.p3.arrival && 
        			(!Scheduling.Rqueue.contains(Scheduling.p3.pcb.pid)) && p!=Scheduling.p3) {
        		Scheduling.p3.pcb.pid = (int)(30 + Math.random()*(45-30));
        		Scheduling.p3.pcb.pstate = "Ready";
        		Scheduling.Rqueue.add(Scheduling.p3.pcb.pid);
        		System.out.println("Process " + Scheduling.p3.pcb.pid + " enters the ready state"
        				+ ". Ready Queue: " + Scheduling.Rqueue 
        				+ ". Blocked Queue: " + Scheduling.Bqueue);
  	  			Scheduling.MemAssign(Scheduling.p3);
  	  		if(Scheduling.p3.state == "Memory") {
  				Scheduling.lastprocess = Scheduling.p3.pcb.pid;
  			}
        	}
        	System.out.print("Memory: [");
			for(int i = 0; i<Scheduling.Memory.length; i++) {
				if(i != (Scheduling.Memory.length-1)){
					System.out.print(Scheduling.Memory[i].data + ", ");
				}
				else {
					System.out.print(Scheduling.Memory[i].data);
				}
			}
			System.out.println("]");
        	String currentLine = (String) Scheduling.Memory[p.pcb.PC].data;
            String[] x = currentLine.split("\\s");
            if(x[0].equals("print")){
            	print(x[1],p);
            	p.pcb.PC++;
            	counter++;
            	Scheduling.clock++;
            }
            else if(x[0].equals("assign")){
                if(x[2].equals("in")){
                	p.pcb.PC++;
            		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);
                	Object data = userInput();
                	counter++;
                	Scheduling.clock++;
                	if(Scheduling.clock == Scheduling.p1.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p1.pcb.pid)) && p!=Scheduling.p1) {
                		Scheduling.p1.pcb.pid = (int) (Math.random()*(15));
                		Scheduling.p1.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p1.pcb.pid);
                		System.out.println("Process " + Scheduling.p1.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
        	  			Scheduling.MemAssign(Scheduling.p1);
        	  			if(Scheduling.p1.state == "Memory") {
              				Scheduling.lastprocess = Scheduling.p1.pcb.pid;
              			}
                	}
                	if(Scheduling.clock == Scheduling.p2.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p2.pcb.pid)) && p!=Scheduling.p2) {
                		Scheduling.p2.pcb.pid = (int)(15 + Math.random()*(30-15));
                		Scheduling.p2.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p2.pcb.pid);
                		System.out.println("Process " + Scheduling.p2.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
                		Scheduling.MemAssign(Scheduling.p2);
                		if(Scheduling.p2.state == "Memory") {
              				Scheduling.lastprocess = Scheduling.p2.pcb.pid;
              			}
                	}
                	if(Scheduling.clock == Scheduling.p3.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p3.pcb.pid)) && p!=Scheduling.p3) {
                		Scheduling.p3.pcb.pid = (int)(30 + Math.random()*(45-30));
                		Scheduling.p3.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p3.pcb.pid);
                		System.out.println("Process " + Scheduling.p3.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
                		Scheduling.MemAssign(Scheduling.p3);
                		if(Scheduling.p3.state == "Memory") {
              				Scheduling.lastprocess = Scheduling.p3.pcb.pid;
              			}
                	}
                	if(counter <= Scheduling.timeSlice) {
                		writeMem(x[1],data,p);
                		p.pcb.PC++;
                		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);
                		counter++;
                		Scheduling.clock++;
                	}
                	else {
                		String y = "";
                		for(int i=0; i<(x.length-1); i++) {
                			y+=x[i];
                			y+= " ";
                		}
                		y+=data;
                		Scheduling.Memory[p.pcb.PC].data = y;
                		break;
                	}	
                }
                else if(x[2].equals("rf")) {
                	String z = (String)Scheduling.Memory[p.pcb.PC + 1].data;
                	String[] y = z.split("\\s");
                	Object data = readFile(y[1]);
                	p.pcb.PC++;
            		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);
                	counter++;
                	Scheduling.clock++;
                	if(Scheduling.clock == Scheduling.p1.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p1.pcb.pid)) && p!=Scheduling.p1) {
                		Scheduling.p1.pcb.pid = (int) (Math.random()*(15));
                		Scheduling.p1.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p1.pcb.pid);
                		System.out.println("Process " + Scheduling.p1.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
        	  			Scheduling.MemAssign(Scheduling.p1);
        	  			if(Scheduling.p1.state == "Memory") {
              				Scheduling.lastprocess = Scheduling.p1.pcb.pid;
              			}
                	}
                	if(Scheduling.clock == Scheduling.p2.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p2.pcb.pid)) && p!=Scheduling.p2) {
                		Scheduling.p2.pcb.pid = (int)(15 + Math.random()*(30-15));
                		Scheduling.p2.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p2.pcb.pid);
                		System.out.println("Process " + Scheduling.p2.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
          	  			Scheduling.MemAssign(Scheduling.p2);
          	  		if(Scheduling.p2.state == "Memory") {
          				Scheduling.lastprocess = Scheduling.p2.pcb.pid;
          			}
                	}
                	if(Scheduling.clock == Scheduling.p3.arrival && 
                			(!Scheduling.Rqueue.contains(Scheduling.p3.pcb.pid)) && p!=Scheduling.p3) {
                		Scheduling.p3.pcb.pid = (int)(30 + Math.random()*(45-30));
                		Scheduling.p3.pcb.pstate = "Ready";
                		Scheduling.Rqueue.add(Scheduling.p3.pcb.pid);
                		System.out.println("Process " + Scheduling.p3.pcb.pid + " enters the ready state"
                				+ ". Ready Queue: " + Scheduling.Rqueue 
                				+ ". Blocked Queue: " + Scheduling.Bqueue);
                		Scheduling.MemAssign(Scheduling.p3);
                		if(Scheduling.p3.state == "Memory") {
              				Scheduling.lastprocess = Scheduling.p3.pcb.pid;
              			}
                	}
                	if(counter <= Scheduling.timeSlice) {
                		writeMem(x[1],data,p);
                		p.pcb.PC++;
                		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);
                		counter++;
                		Scheduling.clock++;
                	}
                	else {
                		String r = "";
                		for(int i=0; i<(x.length-1); i++) {
                			r+=x[i];
                			r+= " ";
                		}
                		r+=data;
                		Scheduling.Memory[p.pcb.PC].data = r;
                		break;
                	}
                }
                else {
                	Object data = readMem(p,x[2]);
                	writeMem(x[1],data,p);
                	p.pcb.PC++;
            		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);

                	counter++;
                	Scheduling.clock++;
                }
            }
            else if(x[0].equals("writeFile")){
                writeFile(x[1],x[2]);
                p.pcb.PC++;
        		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);

                counter++;
            	Scheduling.clock++;
            }
            else if(x[0].equals("readFile")){
                readFile(x[1]);
                p.pcb.PC++;
        		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);

                counter++;
                Scheduling.clock++;

            }
            else if(x[0].equals("printFromTo")){
            	System.out.print("Currently executing: printFromTo " + x[1] + " " + x[2]);
            	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
            			+ ". Blocked Queue: " + Scheduling.Bqueue);
                int a = Integer.parseInt((String) readMem(p,x[1]));
                int b = Integer.parseInt((String) readMem(p,x[2]));
                boolean bool = false;
                if(a>b) {
                	bool = true;
                }
                if(bool == true) {
                	for(int i=a; i>=b; i--) {
                		System.out.println(i);
                	}
                }
                else {
                	for(int i=a; i<=b; i++) {
                		System.out.println(i);
                	}
                }
                p.pcb.PC++;
            	Scheduling.clock++;
                counter++;
            }
            else if(x[0].equals("semWait")){
            	p.pcb.PC++;
        		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);

            	counter++;
            	Scheduling.clock++;
            	if(x[1].equals("userInput")) {
            		System.out.print("Currently executing: semWait userInput");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
            		Mutex.semWait(userInput, p);
            		if(userInput.blockedqueue.contains(p.pcb.pid)) {
            			p.pcb.pstate = "Blocked";
            			Scheduling.Memory[p.pcb.end-3] = new MemoryBlock("pcb",p.pcb.pstate);

            			System.out.println("Mutex is not available. Process has been added to blocked queue.");
            			break;
            		}
            		else {
            			System.out.println("Process " + p.pcb.pid + " has acquired Mutex.");
            		}
            		
            	}
            	else if(x[1].equals("userOutput")) {
            		System.out.print("Currently executing: semWait userOutput");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
            		Mutex.semWait(userOutput, p);
            		if(userOutput.blockedqueue.contains(p.pcb.pid)) {
            			p.pcb.pstate = "Blocked";
            			Scheduling.Memory[p.pcb.end-3] = new MemoryBlock("pcb",p.pcb.pstate);

            			System.out.println("Mutex is not available. Process has been added to blocked queue.");
            			break;
            			
            		}
            		else {
            			System.out.println("Process " + p.pcb.pid + " has acquired Mutex.");
            		}
            	}
            	else {
            		System.out.print("Currently executing: semWait file");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
        			Mutex.semWait(file, p);
        			if(file.blockedqueue.contains(p.pcb.pid)) {
        				p.pcb.pstate = "Blocked";
            			Scheduling.Memory[p.pcb.end-3] = new MemoryBlock("pcb",p.pcb.pstate);

        				System.out.println("Mutex is not available. Process has been added to blocked queue.");
            			break;
            		}
        			else {
        				System.out.println("Process " + p.pcb.pid + " has acquired Mutex.");
        			}
            	}

            }
            else if(x[0].equals("semSignal")){
            	if(x[1].equals("userInput")) {
            		System.out.print("Currently executing: semSignal userInput");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
            		Mutex.semSignal(userInput, p);
            	}
            	else if(x[1].equals("userOutput")) {
            		System.out.print("Currently executing: semSignal userOutput");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
            		Mutex.semSignal(userOutput, p);
            	}
            	else {
            		System.out.print("Currently executing: semSignal file");
            		System.out.println(". Ready Queue: " + Scheduling.Rqueue 
                			+ ". Blocked Queue: " + Scheduling.Bqueue);
            		Mutex.semSignal(file, p);
            	}
            	p.pcb.PC++;
        		Scheduling.Memory[p.pcb.end-2] = new MemoryBlock("pcb",p.pcb.PC);
            	counter++;
            	Scheduling.clock++;
            }
        }
	  
	}	
    
    public static void print(String x, Process p){
    	System.out.print("Currently executing: print " + x);
    	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
    			+ ". Blocked Queue: " + Scheduling.Bqueue);
    	Object data = null;
        for(int i=(p.pcb.end - 7); i<(p.pcb.end-4); i++) {
        	if(Scheduling.Memory[i].name.equals(x)) {
        		data = Scheduling.Memory[i].data;
        		break;
        	}
        }
        if(data == null) {
        	System.out.println(x);
        }
        else {
        	System.out.println(data);
        }
    }

    public static void writeFile(String x, String y) throws IOException{
    	System.out.print("Current executing: writeFile " + x + " " + y);
    	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
    			+ ". Blocked Queue: " + Scheduling.Bqueue);
    	File r = new File(x + ".txt");
    	r.createNewFile();
        Path fn = Path.of(x + ".txt");
        Files.writeString(fn, y, StandardOpenOption.APPEND);
    }
    
    public static Object readFile(String x) throws IOException{
    	System.out.print("Currently executing: readFile " + x);
    	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
    			+ ". Blocked Queue: " + Scheduling.Bqueue);
        Path fn = Path.of(x + ".txt");
        Object content = Files.readString(fn);
        return content;
    }
    
    public static Object readMem(Process p, String n) throws IOException{
    	 data = null;
        for(int i=(p.pcb.end - 7); i<(p.pcb.end-4); i++){
            if (!Scheduling.Memory[i].name.equals("empty")){
            	if(Scheduling.Memory[i].name.equals(n)) {
            		data = (String) Scheduling.Memory[i].data;
                	break;
            	}
            }
        }
       if(data==null) {
    	   return n;
       }
        
       else {
    	   System.out.println("\nReading from memory location " + n);
            return data;
       }
    }
    
    public static void writeMem(String n, Object d, Process p) throws IOException{
    	System.out.print("Currently executing: assign " + n + " " + d);
    	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
    			+ ". Blocked Queue: " + Scheduling.Bqueue);
    	boolean bool = false;
    	for(int i=(p.pcb.end - 7); i<(p.pcb.end - 4); i++) {
    		if(!Scheduling.Memory[i].name.equals("empty")) {
    			if(Scheduling.Memory[i].name.equals(n)) {
    				bool = true;
        			System.out.println("\nWriting data " + d + " in memory location " + n);
        			Scheduling.Memory[i].data = d;
        			break;
    			}
    		}
    	}
        if(bool == false) {
        	System.out.println("\nWriting data " + d + " in memory location " + n);
        	MemoryBlock mb = new MemoryBlock(n,d);
        	boolean empty = false;
        	int index = 0;
            for(int i=(p.pcb.end - 7); i<(p.pcb.end-4); i++) {
            	if(Scheduling.Memory[i].name.equals("empty")) {
            		empty = true;
            		index = i;
            		break;
            	}
            }
            if(empty == true) {
            	Scheduling.Memory[index] = mb;
            }
            else {
            	Scheduling.Memory[p.pcb.end - 7] = mb;
            }
        }
    }
    
    public static Object userInput() throws IOException{
    	System.out.print("Currently executing: input");
    	System.out.println(". Ready Queue: " + Scheduling.Rqueue 
    			+ ". Blocked Queue: " + Scheduling.Bqueue);
        System.out.println("Receiving input from the user.");  
        System.out.print("Please enter a value: ");  
        Object str= sc.nextLine();
        return str;
    }

    public static void main(String[] args) throws IOException{
    	Scheduling.main(args);
    }
}
