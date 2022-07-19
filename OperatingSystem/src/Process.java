import java.util.ArrayList;

public class Process {

	PCB pcb = new PCB();
	int arrival;
	String state = "";
	ArrayList<String> inst = new ArrayList<String>();
	
	public Process(int arrival) {
		this.arrival = arrival;
	}
}
