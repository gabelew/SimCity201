package market.interfaces;

import java.util.Map;

import city.MarketAgent;
import city.roles.MarketCustomerRole;
import restaurant.interfaces.Waiter.Menu;
import restaurant.gui.CustomerGui;

/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface Clerk {

public abstract void msgTakeCustomer(MarketCustomer mcr,MarketAgent m);

public abstract void msgPlaceOrder(Map<String,Integer> choice);

public abstract void msgHereIsPayment(double money);
}