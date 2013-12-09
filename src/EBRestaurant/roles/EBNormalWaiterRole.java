package EBRestaurant.roles;

import restaurant.Restaurant;
import city.PersonAgent;

public class EBNormalWaiterRole extends EBWaiterRole {

	public EBNormalWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
	}
	@Override
	protected void giveOrderToCook(MyCustomer c) {
		c.S=customerState.asked;
		if(waiterGui != null){
			waiterGui.DoGoToCook();
		}
		((EBCookRole) restaurant.cook).msgHereIsOrder(c.choice, c.tableNumber,this);
		if(waiterGui != null){
		waiterGui.DoLeaveCustomer();
		}
	}

	
}
