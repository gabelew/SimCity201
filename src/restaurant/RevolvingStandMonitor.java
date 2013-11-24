package restaurant;

import java.util.Vector;

public class RevolvingStandMonitor extends Object{
	private final int N = 5;
	private int count = 0;
	private Vector<RoleOrder> orders;
	
	public RevolvingStandMonitor() {
		orders = new Vector<RoleOrder>();
	}
	
	synchronized public void insert(RoleOrder order) {
		while (count == N) {
			try {
				System.out.println("Full, waiting");
				wait(5000); // Full, wait to add
			} catch(InterruptedException ex) {}
		}
		
		insertOrder(order);
		count++;
		if(1 == count) {
			System.out.println("Not Empty, notify");
			notify(); // Not empty, notify waiting consumer
		}
	}
	
	synchronized public RoleOrder remove() {
		RoleOrder order;
		while(0 == count) {
			try {
				System.out.println("Empty, waiting");
				wait(5000);
			} catch (InterruptedException ex) {}
		}
		
		order = removeOrder();
		count--;
		if(N-1 == count) {
			System.out.println("Not full, notify");
			notify();
		}
		return order;
	}
	
	private void insertOrder(RoleOrder order) {
		orders.addElement(order);
	}
	
	private RoleOrder removeOrder() {
		RoleOrder order = (RoleOrder) orders.firstElement();
		orders.removeElementAt(0);
		return order;
	}
	
	public int getCount() {
		return count;
	}
}
