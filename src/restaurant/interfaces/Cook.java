package restaurant.interfaces;

import java.util.List;
import java.util.Map;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import restaurant.RoleOrder;
import city.roles.DeliveryManRole;
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
	
	public abstract void msgCanIHelpYou(DeliveryMan DM, MarketAgent  M);

	public abstract void msgNeverOrderFromMarketAgain(MarketAgent market);
	
	public abstract void msgHereIsOrderFromMarket(DeliveryMan Dm,Map<String,Integer>choices,List<String> outOf,double amount);

	public abstract void msgIncompleteOrder(DeliveryMan deliveryMan,List<String> outOf);
}
