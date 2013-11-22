package market.interfaces;


import java.util.List;
import java.util.Map;

import city.roles.ClerkRole;
import restaurant.gui.CustomerGui;

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

}