package GCRestaurant.roles;

import java.util.ArrayList;
import java.util.List;

import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class GCRevolvingStandMonitor 
{
	private final int N_MAX_COUNT = 3;
	private final int WAIT_TIME = 5000;
	private int count = 0;
	private List<GCOrder> orders = new ArrayList<GCOrder>();
	
	
	//Default Constructor
	public GCRevolvingStandMonitor() 
	{
		
	}
	
	//attempts to add order onto stand
	synchronized public void insert(GCOrder order) {
		while (isFull()) 
		{
			try 
			{
				AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, "Revolving Stand", "Full, waiting");
				wait(WAIT_TIME); // Full, wait to add
			} 
			catch(InterruptedException ex) {}
		}
		
		insertOrder(order);
		count++;
		if(count == 1)
		{
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, "Revolving Stand", "Not Empty, notify");
			notify(); // Not empty, notify waiting consumer
		}
	}
	
	//removes order from stand
	synchronized public GCOrder remove() {
		GCOrder order;
		while(isEmpty()) 
		{
			try 
			{
				System.out.println("Empty, waiting");
				wait(WAIT_TIME);
			} 
			catch (InterruptedException ex) {}
		}
		
		order = removeOrder();
		count--;
		if(count == N_MAX_COUNT-1) 
		{
			System.out.println("Not full, notify");
			notify();
		}
		return order;
	}
	
	//queues order for cook
	private void insertOrder(GCOrder order) {
		orders.add(order);
	}
	
	//cook receives order, takes off list
	private GCOrder removeOrder() 
	{
		if(!isEmpty())
		{
			GCOrder order = orders.get(0);
			orders.remove(0);
			return order;
		}
		return null;
	}
	
	/**
	 * START
	 * getter functions
	 */
	public int getCount() 
	{
		return count;
	}
	
	public boolean isFull()
	{
		return count == N_MAX_COUNT;
	}
	
	public boolean isEmpty()
	{
		return count == 0;
	}
	
	/**
	 * END
	 * getter functions
	 */
}
