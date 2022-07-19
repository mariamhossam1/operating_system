import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
	boolean bool = true;
	Queue<Integer> blockedqueue = new LinkedList<Integer>();
	int ownerID = 0;
	
public static void semWait(Mutex m, Process p) {
	//dont forget to block process if you enter else
	if (m.bool == true) {
		m.ownerID = p.pcb.pid;
		m.bool = false;
		} 
	
	else {
		m.blockedqueue.add(p.pcb.pid);
		Scheduling.Bqueue.add(p.pcb.pid);
		}
}
public static void semSignal(Mutex m, Process p) {

if(m.ownerID == p.pcb.pid) {
	if (m.blockedqueue.isEmpty())
		 m.bool = true;
	else {//dont forget to unblock process
		int pid1 = m.blockedqueue.remove();
		Queue<Integer> temp = new LinkedList<Integer>();
		while(!Scheduling.Bqueue.isEmpty()) {
			if(Scheduling.Bqueue.peek() != pid1) {
				int pid = Scheduling.Bqueue.remove();
				temp.add(pid);
			}
			else {
				Scheduling.Bqueue.remove();
			}
		}
		while(!temp.isEmpty()) {
			Scheduling.Bqueue.add(temp.remove());
		}
		Scheduling.Rqueue.add(pid1);
		m.ownerID = pid1;
		}
}
}

}
