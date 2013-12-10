package EBRestaurant.roles;

import java.util.TimerTask;

import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCookRole.state;
import restaurant.Restaurant;
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
    	//AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), "Shared data waiter put order on revolving stand");
		c.S=customerState.waitForFood;
		if(!revolvingStand.isFull()) {
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
			revolvingStand.insert(new Order(this, c.choice, c.tableNumber,state.pending));
		}
		 else {
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
