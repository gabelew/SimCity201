package GCRestaurant.roles;

import java.util.TimerTask;

import GCRestaurant.roles.GCWaiterRole.CustomerState;
import restaurant.Restaurant;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class GCSharedWaiterRole extends GCWaiterRole 
{
	private final int CHECK_STAND_TIME = 4000;
	public boolean unitTesting = false;
	public GCSharedWaiterRole(PersonAgent p, Restaurant r)
	{
		super(p, r);
	}
	
	public void HereIsOrderCookAction(MyCustomer c)
	{
		print("Entering order on computer");
		//animation details
		if(!unitTesting)
		{
			waiterGui.checkOrderStand();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
		}
		
		//sends msg to cook
		if(!orderStand.isFull())
		{
			GCOrder order = new GCOrder(this, c.c, c.table, c.choice);
			orderStand.insert(order);
			c.state = CustomerState.FoodCooking;
		}
		else
		{
			print("Order stand was full, check again later");
			checkStand = false;
			timer.schedule(new TimerTask() 
			{
				public void run() {
					checkStand = true;
					stateChanged();
				}
			}, CHECK_STAND_TIME);
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "The revolving stand is full. I'll try again later.");
		}
		stateChanged();
	}

	
}
