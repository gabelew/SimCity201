package CMRestaurant.roles;

import java.util.TimerTask;

import restaurant.Restaurant;
import restaurant.RoleOrder;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

public class CMSharedWaiterRole extends CMWaiterRole{

	static final int CHECK_STAND_TIME = 10000;
	
	public CMSharedWaiterRole(PersonAgent p, Restaurant r) {
		super(p, r);
		haveNotRecentlyCheckedStand = true;
		revolvingStand = ((CMCookRole) this.restaurant.cook).getRevolvingStand();
	}
	
	@Override
	protected void putInOrder(MyCustomer c) {
		if(waiterGui!=null){
			doGoToKitchen(c);
		}
		if(!revolvingStand.isFull()) {
			revolvingStand.insert(new RoleOrder(this, c.choice, c.table));
			c.s = CustomerState.orderPlaced;
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
			if(waiterGui!=null){
				waiterGui.placedOrder();
			}
		} else {
			haveNotRecentlyCheckedStand = false;
			if(waiterGui!=null){
				waiterGui.placeOrderInPocket();
			}
			timer.schedule(new TimerTask() {
				public void run() {
					haveNotRecentlyCheckedStand = true;
					stateChanged();
				}
			}, CHECK_STAND_TIME);
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "The revolving stand is full. I'll try again later.");
		}
	}

}
