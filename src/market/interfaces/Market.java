package market.interfaces;

import java.util.HashMap;
import java.util.Map;


/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface Market {
	
	public Map<String, Integer> Inventory = new HashMap<String, Integer>();

	public abstract void msgPlaceOrder(MarketCustomer CR);
	
	public abstract void msgPlaceDeliveryOrder(Cook cook);
	
	public abstract void msgClerkDone();
	
	public abstract void msgDeliveryDone();
	
	public abstract void setInventory(int steak, int chicken, int cars, int salad, int cookie);
	
	

}