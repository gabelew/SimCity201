package GCRestaurant.roles;

import restaurant.Restaurant;
import city.PersonAgent;

public class GCNormalWaiterRole extends GCWaiterRole {

	public GCNormalWaiterRole(PersonAgent p, Restaurant r)
	{
		super(p, r);
	}
	
	protected void HereIsOrderCookAction(MyCustomer c)
	{
		print("giving order to cook");
		
		//animation details
		waiterGui.goToCook();
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		
		//sends msg to cook
		((GCCookRole)cook).HereIsOrderMsg(this, c.c, c.table, c.choice);
		stateChanged();
	}
	
}
