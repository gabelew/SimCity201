package CMRestaurant.roles;

import restaurant.Restaurant;
import city.PersonAgent;
import city.gui.Gui;

public class CMNormalWaiterRole extends CMWaiterRole {

	public CMNormalWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
	}
	
	protected void putInOrder(MyCustomer c) {
		doGoToKitchen(c);
		c.s = CustomerState.orderPlaced;
		((CMCookRole) restaurant.cook).msgHereIsOrder(this, c.choice, c.table);
		waiterGui.placedOrder();
	}

	
}
