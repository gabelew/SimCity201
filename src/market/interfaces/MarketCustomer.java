package market.interfaces;


import java.util.List;
import java.util.Map;

import CMRestaurant.gui.CMCustomerGui;
import city.MarketAgent;
import city.roles.ClerkRole;

/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface MarketCustomer {

	public abstract void msgCanIHelpYou(ClerkRole clerk);
	
	public abstract void msgHereIsPrice(double amount);
	
	public abstract void msgHereIsOrder(Map<String,Integer>choice,List<String>outOf);
	
	public abstract void startShopping(MarketAgent m, Map<String, Integer> toOrderFromMarket);
}