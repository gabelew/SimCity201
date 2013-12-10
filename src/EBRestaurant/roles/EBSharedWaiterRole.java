package EBRestaurant.roles;

import java.util.TimerTask;

import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCookRole.state;
import EBRestaurant.roles.EBWaiterRole.customerState;
import restaurant.Restaurant;
import restaurant.test.mock.LoggedEvent;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class EBSharedWaiterRole extends EBWaiterRole{

	static final int CHECK_STAND_TIME = 7000;
	
	public EBSharedWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
		checked = true;
		revolvingStand = ((EBCookRole) this.restaurant.cook).getRevolvingStand();
	}
	
	@Override
	protected void giveOrderToCook(MyCustomer c) {
		//c.S=customerState.asked;
		if(!revolvingStand.isFull()) {
			c.S=customerState.waitForFood;
			log.add(new LoggedEvent("Inserting order"));
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
			revolvingStand.insert(new Order(this, c.choice, c.tableNumber,state.pending));
		}
		 else {
			 log.add(new LoggedEvent("Full, will check again later"));
				checked= false;
				if(waiterGui!=null){
				}
				timer.schedule(new TimerTask() {
					public void run() {
						checked = true;
						stateChanged();
					}
				}, CHECK_STAND_TIME);
				AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "The revolving stand is full. I'll try again later.");
			}
	}

}
