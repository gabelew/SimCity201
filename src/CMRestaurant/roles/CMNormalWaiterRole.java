package CMRestaurant.roles;

import restaurant.Restaurant;
import restaurant.test.mock.LoggedEvent;
import city.PersonAgent;
import city.gui.Gui;

public class CMNormalWaiterRole extends CMWaiterRole {

	public CMNormalWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
	}
	
	protected void putInOrder(MyCustomer c) {
		log.add(new LoggedEvent("Gave cook order directly"));
		if(waiterGui != null){
			doGoToKitchen(c);
		}
		c.s = CustomerState.orderPlaced;
		((CMCookRole) restaurant.cook).msgHereIsOrder(this, c.choice, c.table);
		if(waiterGui != null){
			waiterGui.placedOrder();
		}
	}

	
}
