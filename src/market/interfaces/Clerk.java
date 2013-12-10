package market.interfaces;

import java.util.Map;

import CMRestaurant.gui.CMCustomerGui;
import city.MarketAgent;
import city.roles.MarketCustomerRole;
import restaurant.interfaces.Waiter.Menu;

/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface Clerk {

public abstract void msgTakeCustomer(MarketCustomer mcr,Market m);

public abstract void msgPlaceOrder(Map<String,Integer> choice);

public abstract void msgHereIsPayment(double money);

public abstract void msgDoneWithShift();

public abstract void msgMarketClosed();
}