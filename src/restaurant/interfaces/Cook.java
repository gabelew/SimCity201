package restaurant.interfaces;

import java.util.List;

import restaurant.CookAgent.Order;
import restaurant.gui.CookGui;
import restaurant.MarketAgent;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Cook {

	public CookGui cookGui = null;
	
	public abstract void msgHereIsOrder(Waiter w, String choice, int table);
	
	public abstract void msgFoodDone(Order o);
	
	public abstract void msgDelivering(Market m, List<MarketAgent.MyFood>orderlist);
	
	public abstract void msgOutOfOrder(Market m, List<MarketAgent.MyFood> orderList);
	
	public abstract void msgAnimationFinishedAtFidge();
	
	public abstract void msgAnimationFinishedPutFoodOnGrill();
	
	public abstract void msgAnimationFinishedWaiterPickedUpFood();
	
	public abstract void msgAnimationFinishedPutFoodOnPickUpTable(Order o);
	
	

	
}
