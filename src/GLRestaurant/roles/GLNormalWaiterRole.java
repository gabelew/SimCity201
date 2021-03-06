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
		log.add(new LoggedEvent("Electronically sent order to cook."));
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Electronically sent " + mc.c.getName() + "'s order to cook.");	
		mc.cs = customerState.waitingForFood;
		((GLCookRole)restaurant.cook).msgHereIsOrder(this, mc.choice, mc.c);
		if(waiterGui != null)
			waiterGui.DoLeaveCustomer();
	}
}
