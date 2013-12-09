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
		if(waiterGui != null){
		c.S=customerState.waitForFood;
		}
		//((EBCookRole) restaurant.cook).msgHereIsOrder(c.choice, c.tableNumber,this);
		if(!revolvingStand.isFull()) {
			revolvingStand.insert(new Order(this, c.choice, c.tableNumber,state.pending));
			c.S = customerState.ordered;
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
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
	/*protected void putInOrder(MyCustomer c) {
		//if(waiterGui!=null){
			//doGoToKitchen(c);
		//}
		if(!revolvingStand.isFull()) {
			log.add(new LoggedEvent("Check revoliving stand and put in order"));
			revolvingStand.insert(new RoleOrder(this, c.choice, c.table));
			c.s = CustomerState.orderPlaced;
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Inserting order into revolving stand");
			if(waiterGui!=null){
				waiterGui.placedOrder();
			}
		} else {
			log.add(new LoggedEvent("Check revoliving stand and satand was full"));
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
	}*/

}
