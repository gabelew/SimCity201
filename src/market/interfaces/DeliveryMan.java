package market.interfaces;

import java.util.Map;

import city.MarketAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;


/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface DeliveryMan {

	public abstract void msgTakeCustomer(Cook c,MarketAgent m);
	
	public abstract void msgHereIsOrder(Map<String,Integer>choice);
	
	public abstract void msgHereIsPayment(double payment, Cashier ca);

}