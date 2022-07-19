import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Scheduling
{
	static int timeSlice;
    static Process p1 = new Process(0);
    static Process p2 = new Process(1);
    static Process p3 = new Process(4);
    static Queue < Integer > Rqueue = new LinkedList < Integer > ();
    static Queue < Integer > Bqueue = new LinkedList < Integer > ();
    static Queue < Integer > complete = new LinkedList < Integer > ();
	static int clock=0;
	static MemoryBlock Memory[] = new MemoryBlock[40];
	static int lastprocess = 0;
	static int memleft = Memory.length;

	
	public static void readDisk(String path, Process p) throws IOException {
		Path fn = Path.of(path);
		 boolean flag = false;
        String content = Files.readString(fn);
        String[] mem = content.split(","); 
        for(int i=0; i<Scheduling.Memory.length; i++) {
			if(Scheduling.Memory[i].name.equals("empty")) {
				if(Scheduling.Memory[i + 3].name.equals("empty")) {
					int pc = p.pcb.PC - p.pcb.start;
					p.pcb.start = i;
					p.pcb.end = i + mem.length - 1;
					p.pcb.PC = p.pcb.start + pc;
					p.state = "Memory";
					flag = true;
					break;
				}
			}
        }
      if(flag == true) {
        	for(int j = 0; j<mem.length; j++) {
        		String[] memfinal = mem[j].split("\\s");
        			if(memfinal[0].equals("inst")) {
        				if(memfinal[1].equals("input")) {
        					Memory[p.pcb.start + j] = new MemoryBlock(memfinal[0],memfinal[1]);
        				}
        				else if(memfinal.length == 4) {
        					Memory[p.pcb.start + j] = new MemoryBlock(memfinal[0],(memfinal[1] + " " + memfinal[2] + " " + memfinal[3]));
        				}
        				else {
        					Memory[p.pcb.start + j] = new MemoryBlock(memfinal[0],(memfinal[1] + " " + memfinal[2]));
        				}
        			}
        			else {
        				Memory[p.pcb.start + j] = new MemoryBlock(memfinal[0],memfinal[1]);
        			}
            }
        	
        	for(int y = (p.pcb.end-4); y<=p.pcb.end; y++) {
    			if(y==p.pcb.end-4) {
    				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.pid);
    				}
    			else if(y==p.pcb.end-3) {
    				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.pstate);
    				}
    			else if(y==p.pcb.end-2) {
    				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.PC);
    				}
    			else if(y==p.pcb.end-1) {
    				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.start);
    				}
    			else
    				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.end);
    			}
    }
        PrintWriter writer = new PrintWriter(path);
        writer.print("");
        writer.close();
        lastprocess = p.pcb.pid;
        System.out.println("Process " + p.pcb.pid + " has been swapped out from disk into memory.");
	}
	
	public static void writeDisk(Process p) throws IOException {
		File r = new File("Process_" + p.pcb.pid + ".txt");
    	r.createNewFile();
    	Path fn = Path.of("Process_" + p.pcb.pid + ".txt");
    	String y = "";
		for(int b=p.pcb.start; b<=p.pcb.end; b++) {
			y += Memory[b].name + " " + Memory[b].data + ",";
			Memory[b].name = "empty";
			Memory[b].data = "empty";
		}
		Files.writeString(fn, y, StandardCharsets.UTF_8);
		
      
		p.state = "Disk";
		System.out.println("Process " + p.pcb.pid + " has been swapped out from memory onto disk.");
	}
	
	public static void MemAssign(Process p) throws IOException {
		boolean space = false;
		int memind = 0;
		String k = "";
		for(int i=0; i<Scheduling.Memory.length; i++) {
				if(Scheduling.Memory[i].name.equals("empty")) {
					if(memleft >= (8 + p.inst.size()) && Scheduling.Memory[i + 3].name.equals("empty")) { //(i + 8 + p.inst.size()) < Memory.length
						if(Scheduling.Memory[i + 8 + p.inst.size()].name.equals("empty")) {
							space = true;
							memind = i;
							break;
						}
					}
				}	
		}
		if(space == true) {
			p.pcb.start = memind;
			p.pcb.end =p.pcb.start + p.inst.size() + 7;
			p.pcb.PC = p.pcb.start;
			p.state = "Memory";
			memleft -= (p.pcb.end + 1);
			System.out.println("Process " + p.pcb.pid + " is stored in the memory.");
		}
		else {
			p.state = "Disk";
			File r = new File("Process_" + p.pcb.pid + ".txt");
	    	r.createNewFile();
	    	Path fn = Path.of("Process_" + p.pcb.pid + ".txt");
	    	while(!p.inst.isEmpty()) {
	    		k += "inst " + p.inst.remove(0) + ",";
	    	}
	    	k += "empty empty,";
	    	k += "empty empty,";
	    	k += "empty empty,";
	    	k += "pcb " + Integer.toString(p.pcb.pid) +",";
	    	k += "pcb " + p.pcb.pstate +",";
	    	k += "pcb " + Integer.toString(p.pcb.PC) +",";
	    	k += "pcb " + Integer.toString(p.pcb.start) +",";
	    	k += "pcb " + Integer.toString(p.pcb.end)+",";
	    	Files.writeString(fn, k, StandardCharsets.UTF_8);
	    	System.out.println("Process " + p.pcb.pid + " is stored on the disk.");
	    	System.out.println();
	    	System.out.println("Disk Content: " + k);
	    	System.out.println("Disk Format: <memoryblock name> <memoryblock data>,<memoryblock name> <memoryblock data>,...");
	    	System.out.println();
			return;
		}
		
		for(int z = p.pcb.start; z<(p.pcb.end - 7); z++) {
			Scheduling.Memory[z] = new MemoryBlock("inst",p.inst.remove(0));
		}
		for(int y = (p.pcb.end-4); y<=p.pcb.end; y++) {
			if(y==p.pcb.end-4) {
				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.pid);
				}
			else if(y==p.pcb.end-3) {
				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.pstate);
				}
			else if(y==p.pcb.end-2) {
				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.PC);
				}
			else if(y==p.pcb.end-1) {
				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.start);
				}
			else
				Scheduling.Memory[y] = new MemoryBlock("pcb",p.pcb.end);
			}
	}
   
  public static void main (String args[]) throws IOException
  {
    int n=3, p = 0, j;

    Scanner s = new Scanner (System.in);

     

      Queue < Integer > inst = new LinkedList < Integer > ();
      j = 0;
      int d = 0;
      int r = 0;
      String space; 
      int remInst1 = 0;
      int remInst2 = 0;
      int remInst3 = 0;
      int myRemInst;
      
      for(int i=0; i<Memory.length; i++) { //initialising the whole memory so that it can be printed
    	  Memory[i] = new MemoryBlock("empty", "empty");
      }

      PriorityQueue < Integer > arrival = new PriorityQueue < Integer > (10);
      Queue < Integer > Rqueue2 = new LinkedList < Integer > ();
      boolean done = false;

      
    System.out.print ("Enter number of instructions per time slice= ");
    timeSlice = s.nextInt ();
    arrival.add(p1.arrival);
  	arrival.add(p2.arrival);
  	arrival.add(p3.arrival); //adding processes to queues by arrival times
  	
  	p1.inst = OS.getInst("Program_1.txt");
	p2.inst = OS.getInst("Program_2.txt");
	p3.inst = OS.getInst("Program_3.txt");
  	
  		if(clock == p1.arrival) {
  			p1.pcb.pid = (int) (Math.random()*(15));
  			remInst1 = p1.pcb.end - 7 - p1.pcb.PC;
  			Rqueue.add(p1.pcb.pid);
  			System.out.println ("Process " + p1.pcb.pid +
  				  " enters ready state. " + "Ready Queue: " + Rqueue 
  				  + ". Blocked Queue: " + Bqueue);
  			p1.pcb.pstate = "Ready";
  			MemAssign(p1);
  			if(p1.state == "Memory") {
  				lastprocess = p1.pcb.pid;
  			}
  			Rqueue2.add(p1.pcb.pid);
  			arrival.remove(p1.arrival);
  			inst.add(OS.getInst("Program_1.txt").size());
  			space=s.nextLine();
  			

  		}
  		else if(clock == p2.arrival) {
  			p2.pcb.pid = (int)(15 + Math.random()*(30-15));
  			remInst2 = p2.pcb.end - 7 - p2.pcb.PC;
  			Rqueue.add(p2.pcb.pid);
  			System.out.println ("Process " + p2.pcb.pid +
    				  " enters ready state. " + "Ready Queue: " + Rqueue 
    				  + ". Blocked Queue: " + Bqueue);
  			p2.pcb.pstate = "Ready";
  			MemAssign(p2);
  			if(p2.state == "Memory") {
  				lastprocess = p2.pcb.pid;
  			}
  			Rqueue2.add(p2.pcb.pid);
  			arrival.remove(p2.arrival);
  			inst.add(OS.getInst("Program_2.txt").size());
  			space=s.nextLine();
  		}
  		else if(clock==p3.arrival) {
  			p3.pcb.pid = (int)(30 + Math.random()*(45-30));
  			remInst3 = p3.pcb.end - 7 - p3.pcb.PC;
  			Rqueue.add(p3.pcb.pid);
  			System.out.println ("Process " + p3.pcb.pid +
  				  " enters ready state. " + "Ready Queue: " + Rqueue 
  				  + ". Blocked Queue: " + Bqueue);
  			p3.pcb.pstate = "Ready";
  			MemAssign(p3);
  			if(p3.state == "Memory") {
  				lastprocess = p3.pcb.pid;
  			}
  			Rqueue2.add(p3.pcb.pid);
  			arrival.remove(p3.arrival);
  			inst.add(OS.getInst("Program_3.txt").size());
  			space=s.nextLine();
  		}
	

    while (!Rqueue.isEmpty ())
      {
      for (j = 0; j < n; j++)
	  {   
    	p = Rqueue.remove();
	    System.out.println ("\nProcess " + p +
			      " is chosen to execute. Ready Queue: " + Rqueue
			      + ". Blocked Queue: " + Bqueue);
	    
	    
	    if(p == p1.pcb.pid) {
	    	if(p1.state.equals("Memory")) {
	    		p1.pcb.pstate = "Running";
	    		Scheduling.Memory[p1.pcb.end-3] = new MemoryBlock("pcb",p1.pcb.pstate);
	    		OS.codeParser(p1);
	    	}
	    	else {
	    		if(lastprocess == p2.pcb.pid) {
	    			writeDisk(p2);
	    		}
	    		else {
	    			writeDisk(p3);
	    		}
	    		readDisk("Process_" + p1.pcb.pid + ".txt", p1);
    			p1.state = "Memory";
    			p1.pcb.pstate = "Running";
	    		Scheduling.Memory[p1.pcb.end-3] = new MemoryBlock("pcb",p1.pcb.pstate);
    			OS.codeParser(p1);
	    	}
	    	
	    	remInst1 = p1.pcb.end - 7 - p1.pcb.PC; //calculating remaining instructions after leaving code parser
	    	myRemInst=remInst1;
	
	    }
	    else if(p == p2.pcb.pid) {
	    	if(p2.state.equals("Memory")) {
	    		p2.pcb.pstate = "Running";
	    		Scheduling.Memory[p2.pcb.end-3] = new MemoryBlock("pcb",p2.pcb.pstate);
	    		OS.codeParser(p2);
	    	}
	    	else {
	    		if(lastprocess == p1.pcb.pid) {
	    			writeDisk(p1);
	    		}
	    		else {
	    			writeDisk(p3);
	    		}
	    		readDisk("Process_" + p2.pcb.pid + ".txt", p2);
    			p2.state = "Memory";
	    		p2.pcb.pstate = "Running";
    			Scheduling.Memory[p2.pcb.end-3] = new MemoryBlock("pcb",p2.pcb.pstate);
    			OS.codeParser(p2);
	    	}
	    	remInst2 = p2.pcb.end - 7 - p2.pcb.PC;
	    	myRemInst=remInst2;
	    	
	    }
	    else {
	    	if(p3.state.equals("Memory")) {
	    		p3.pcb.pstate = "Running";
	    		Scheduling.Memory[p3.pcb.end-3] = new MemoryBlock("pcb",p3.pcb.pstate);
	    		OS.codeParser(p3);
	    	}
	    	else {
	    		if(lastprocess == p2.pcb.pid) {
	    			writeDisk(p2);
	    		}
	    		else {
	    			writeDisk(p1);
	    		}
	    		readDisk("Process_" + p3.pcb.pid + ".txt", p3);
	    		p3.pcb.pstate = "Running";
	    		Scheduling.Memory[p3.pcb.end-3] = new MemoryBlock("pcb",p3.pcb.pstate);
    			p3.state = "Memory";
    			OS.codeParser(p3);
	    	}
	    	remInst3 = p3.pcb.end - 7 - p3.pcb.PC;
	    	myRemInst=remInst3;

	    }
	    
	    if(clock == p1.arrival && (!Rqueue.contains(p1.pcb.pid))) {
			if(!((OS.userInput.blockedqueue.contains(p1.pcb.pid))
					|| (OS.userOutput.blockedqueue.contains(p1.pcb.pid)) || (OS.file.blockedqueue.contains(p1.pcb.pid)))) {
				p1.pcb.pid = (int) (Math.random()*(15));
	  			remInst1 = p1.pcb.end - 7 - p1.pcb.PC;
				Rqueue.add(p1.pcb.pid);
				System.out.println ("Process " + p1.pcb.pid +
	    				  " enters ready state. " + "Ready Queue: " + Rqueue 
	    				  + ". Blocked Queue: " + Bqueue);
				p1.pcb.pstate = "Ready";
				MemAssign(p1);
				if(p1.state == "Memory") {
	  				lastprocess = p1.pcb.pid;
	  			}
	  			Rqueue2.add(p1.pcb.pid);
	  			arrival.remove(p1.arrival);
	  			inst.add(OS.getInst("Program_1.txt").size());
	  			space=s.nextLine();
			}

  		}
  		else if(clock == p2.arrival && (!Rqueue.contains(p2.pcb.pid))) {
  			if(!((OS.userInput.blockedqueue.contains(p2.pcb.pid))
					|| (OS.userOutput.blockedqueue.contains(p2.pcb.pid)) || (OS.file.blockedqueue.contains(p2.pcb.pid)))) {
  				p2.pcb.pid = (int)(15 + Math.random()*(30-15));
  				remInst2 = p2.pcb.end - 7 - p2.pcb.PC;
				Rqueue.add(p2.pcb.pid);
				System.out.println ("Process " + p2.pcb.pid +
	    				  " enters ready state. " + "Ready Queue: " + Rqueue 
	    				  + ". Blocked Queue: " + Bqueue);
				p2.pcb.pstate = "Ready";
				MemAssign(p2);
				if(p2.state == "Memory") {
	  				lastprocess = p2.pcb.pid;
	  			}
	  			Rqueue2.add(p2.pcb.pid);
	  			arrival.remove(p2.arrival);
	  			inst.add(OS.getInst("Program_2.txt").size());
	  			space=s.nextLine();
			}
  		}
  		else if(clock==p3.arrival && (!Rqueue.contains(p3.pcb.pid))) {
  			if(!((OS.userInput.blockedqueue.contains(p3.pcb.pid))
					|| (OS.userOutput.blockedqueue.contains(p3.pcb.pid)) || (OS.file.blockedqueue.contains(p3.pcb.pid)))) {
  				p3.pcb.pid = (int)(30 + Math.random()*(45-30));
  				remInst3 = p3.pcb.end - 7 - p3.pcb.PC;
				Rqueue.add(p3.pcb.pid);
				System.out.println ("Process " + p3.pcb.pid +
	    				  " enters ready state. " + "Ready Queue: " + Rqueue 
	    				  + ". Blocked Queue: " + Bqueue);
				p3.pcb.pstate = "Ready";
				MemAssign(p3);
				if(p3.state == "Memory") {
	  				lastprocess = p3.pcb.pid;
	  			}
	  			Rqueue2.add(p3.pcb.pid);
	  			arrival.remove(p3.arrival);
	  			inst.add(OS.getInst("Program_3.txt").size());
	  			space=s.nextLine();
			}
  		}
	    if (myRemInst > 0)
	      {
		
		if(!((OS.userInput.blockedqueue.contains(p))
					|| (OS.userOutput.blockedqueue.contains(p)) || (OS.file.blockedqueue.contains(p)))){
			Rqueue.add (p);
			if(p == p1.pcb.pid) {
	    		p1.pcb.pstate = "Ready";
	    		if(p1.state.equals("Memory")) {
	    			Scheduling.Memory[p1.pcb.end-3] = new MemoryBlock("pcb",p1.pcb.pstate);
	    		}
	    	}
	    	else if(p == p2.pcb.pid) {
	    		
	    		p2.pcb.pstate = "Ready";
	    		if(p2.state.equals("Memory")) {
	    		
	    			Scheduling.Memory[p2.pcb.end-3] = new MemoryBlock("pcb",p2.pcb.pstate);
	    		}
	    	}
	    	else {
	    		p3.pcb.pstate = "Ready";
	    		if(p3.state.equals("Memory")) {
	    			Scheduling.Memory[p3.pcb.end-3] = new MemoryBlock("pcb",p3.pcb.pstate);
	    		}
	    	}
			System.out.println ("\nProcess " + (p) +
					  " is preempted and back to ready state. Ready Queue: "
					+
					  Rqueue + ". Blocked Queue: " + Bqueue);
		}
			
	      }
	    else 
	      {  
		complete.add ((p));
		if(p == p1.pcb.pid) {
    		p1.pcb.pstate = "Finished";
    		if(p1.state.equals("Memory")) {
    			Scheduling.Memory[p1.pcb.end-3] = new MemoryBlock("pcb",p1.pcb.pstate);
    		}
    	}
    	else if(p == p2.pcb.pid) {
    		p2.pcb.pstate = "Finished";
    		if(p2.state.equals("Memory")) {
	    		
    			Scheduling.Memory[p2.pcb.end-3] = new MemoryBlock("pcb",p2.pcb.pstate);
    		}
    	}
    	else {
    		p3.pcb.pstate = "Finished";
    		if(p3.state.equals("Memory")) {
    			Scheduling.Memory[p3.pcb.end-3] = new MemoryBlock("pcb",p3.pcb.pstate);
    		}
    	}
		n--;
		System.out.print ("\nProcess " + (p) + " is finished. ");
		System.out.print("Blocked Queue: " + Bqueue + ".");
		System.out.print (" Ready Queue: " + Rqueue + ".");
		System.out.println (" Finished Queue: " + complete + ".");
	      }
	  }
      
		

		
      }

  }
  }

