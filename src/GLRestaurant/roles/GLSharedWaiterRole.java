package GLRestaurant.roles;

import java.util.TimerTask;

import GLRestaurant.roles.GLCookRole.orderState;
import restaurant.Restaurant;
import restaurant.test.mock.LoggedEvent;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class GLSharedWaiterRole extends GLWaiterRole{

	//static final int CHECK_STAND_TIME = 7000;
	
	public GLSharedWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
		revolvingStand = ((GLCookRole) this.restaurant.cook).getRevolvingStand();
	}
	
	protected void sendOrderToCook(MyCustomer mc) {
		if(waiterGui!=null){
			waiterGui.DoGoToRevolvingStand();
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(!revolvingStand.isFull()) {
			revolvingStand.insert(new WaiterOrder(this, mc.choice, mc.c));
			mc.cs = customerState.waitingForFood;
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
		
		} else {
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "The revolving stand is full. I'll try again later.");
		}
		waiterGui.DoLeaveCustomer();
	}

}
