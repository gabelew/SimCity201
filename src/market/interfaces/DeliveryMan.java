package market.interfaces;

import java.util.Map;

import city.MarketAgent;
import city.roles.CashierRole;
import city.roles.CookRole;
import restaurant.interfaces.Waiter.Menu;
import restaurant.gui.CustomerGui;

/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface DeliveryMan {

	public abstract void msgTakeCustomer(CookRole c,MarketAgent m);
	
	public abstract void msgHereIsOrder(Map<String,Integer>choice);
	
	public abstract void msgHereIsPayment(double payment, CashierRole ca);

}