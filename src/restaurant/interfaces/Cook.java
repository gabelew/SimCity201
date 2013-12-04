package restaurant.interfaces;

import java.util.List;
import java.util.Map;

import CMRestaurant.gui.CMCookGui;
import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import restaurant.RoleOrder;
import city.roles.DeliveryManRole;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Cook {

	public CMCookGui cookGui = null;
	
	public abstract void msgHereIsOrder(Waiter w, String choice, int table);
	
	public abstract void msgFoodDone(RoleOrder o);
	
	//public abstract void msgDelivering(Market m, List<MarketAgent.MyFood>orderlist);
	
	//public abstract void msgOutOfOrder(Market m, List<MarketAgent.MyFood> orderList);
	
	public abstract void msgCanIHelpYou(DeliveryMan DM, MarketAgent  M);

	public abstract void msgNeverOrderFromMarketAgain(MarketAgent market);
	
	public abstract void msgHereIsOrderFromMarket(DeliveryMan Dm,Map<String,Integer>choices,List<String> outOf,double amount);

	public abstract void msgIncompleteOrder(DeliveryMan deliveryMan,List<String> outOf);
	public abstract void msgRelieveFromDuty(PersonAgent p);

	public abstract void addMarket(MarketAgent m);
}
