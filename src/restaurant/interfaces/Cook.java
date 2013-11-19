package restaurant.interfaces;

import java.util.List;

import city.roles.CookRole.RoleOrder;
import restaurant.gui.CookGui;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Cook {

	public CookGui cookGui = null;
	
	public abstract void msgHereIsOrder(Waiter w, String choice, int table);
	
	public abstract void msgFoodDone(RoleOrder o);
	
	//public abstract void msgDelivering(Market m, List<MarketAgent.MyFood>orderlist);
	
	//public abstract void msgOutOfOrder(Market m, List<MarketAgent.MyFood> orderList);
	
	public abstract void msgAnimationFinishedAtFidge();
	
	public abstract void msgAnimationFinishedPutFoodOnGrill();
	
	public abstract void msgAnimationFinishedWaiterPickedUpFood();
	
	public abstract void msgAnimationFinishedPutFoodOnPickUpTable(RoleOrder o);
	
	public abstract void badSteaks();

	public abstract void cookieMonster();

	public abstract void setSteaksAmount(int i);
	

	
}
