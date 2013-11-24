package market.test.mock;


import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;
/**
 * A sample MockCustomer built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public class MockMarket extends Mock implements Market {


	public Map<String, Integer> Inventory = new HashMap<String, Integer>();
	public EventLog log = new EventLog();
	public Point location = null;
	
	public MockMarket(String name) {
		super(name);

	}

	
	public void msgPlaceOrder(MarketCustomer CR){
		log.add(new LoggedEvent("Received msgPlaceOrder from MarketCustomer."));
	}

	public void msgPlaceDeliveryOrder(Cook cook){
		log.add(new LoggedEvent("Received msgPlaceDeliveryOrder from CookCustomer."));
	}
	
	public void msgClerkDone(Clerk c){
		log.add(new LoggedEvent("Received msgClerkDone from clerk."));
	}
	
	public void msgDeliveryDone(DeliveryMan DM){
		log.add(new LoggedEvent("Received msgDeliveryDone from deliveryMan."));
	}
	
	public void setInventory(int steak, int chicken, int cars, int salad, int cookie){
		Inventory.put("steak", steak);
		Inventory.put("car", cars);
		Inventory.put("chicken",chicken);
		Inventory.put("salad", salad);
		Inventory.put("cookie", cookie);
		log.add(new LoggedEvent("Received setInventory."));
	}
	
}
