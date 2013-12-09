package GLRestaurant.roles;

import java.util.TimerTask;

import GLRestaurant.roles.GLCookRole.orderState;
import GLRestaurant.roles.GLWaiterRole.customerState;
import restaurant.Restaurant;
import restaurant.test.mock.LoggedEvent;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class GLNormalWaiterRole extends GLWaiterRole{

	
	public GLNormalWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
	}
	
	protected void sendOrderToCook(MyCustomer mc) {
		Do("Giving order to " + ((GLCookRole)restaurant.cook).getName());	
		mc.cs = customerState.waitingForFood;
		((GLCookRole)restaurant.cook).msgHereIsOrder(this, mc.choice, mc.c);
		waiterGui.DoLeaveCustomer();
	}
}
