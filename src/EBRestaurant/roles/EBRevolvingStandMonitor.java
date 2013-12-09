package EBRestaurant.roles;

import java.util.Vector;

import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class EBRevolvingStandMonitor extends Object{
	private final int N_MAX_COUNT = 5;
	private final int WAIT_TIME = 5000;
	private int count = 0;
	public Vector<Order> orders;
	
	
	public EBRevolvingStandMonitor() {
		orders = new Vector<Order>();
	}
	
	synchronized public void insert(Order order) {
		while (isFull()) {
			try {
				AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, "Revolving Stand", "Full, waiting");
				wait(WAIT_TIME); // Full, wait to add
			} catch(InterruptedException ex) {}
		}
		
		insertOrder(order);
		count++;
		if(count == 1) {
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, "Revolving Stand", "Not Empty, notify");
			notify(); // Not empty, notify waiting consumer
		}
	}
	
	synchronized public Order remove() {
		Order order;
		while(isEmpty()) {
			try {
				System.out.println("Empty, waiting");
				wait(WAIT_TIME);
			} catch (InterruptedException ex) {}
		}
		
		order = removeOrder();
		count--;
		if(count == N_MAX_COUNT-1) {
			System.out.println("Not full, notify");
			notify();
		}
		return order;
	}
	
	private void insertOrder(Order order) {
		orders.addElement(order);
	}
	
	private Order removeOrder() {
		Order order = (Order) orders.firstElement();
		orders.removeElementAt(0);
		return order;
	}
	
	public int getCount() {
		return count;
	}
	
	public boolean isFull(){
		return count == N_MAX_COUNT;
	}
	
	public boolean isEmpty(){
		return count == 0;
	}
}
