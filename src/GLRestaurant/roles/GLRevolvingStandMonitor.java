package GLRestaurant.roles;

import java.util.Vector;

import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class GLRevolvingStandMonitor extends Object{
	private final int N_MAX_COUNT = 5;
	private final int WAIT_TIME = 5000;
	private int count = 0;
	private Vector<WaiterOrder> orders;
	
	
	public GLRevolvingStandMonitor() {
		orders = new Vector<WaiterOrder>();
	}
	
	synchronized public void insert(WaiterOrder order) {
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
	
	synchronized public WaiterOrder remove() {
		WaiterOrder order;
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
	
	private void insertOrder(WaiterOrder order) {
		orders.addElement(order);
	}
	
	private WaiterOrder removeOrder() {
		WaiterOrder order = (WaiterOrder) orders.firstElement();
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
