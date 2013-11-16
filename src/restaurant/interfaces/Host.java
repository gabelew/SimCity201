package restaurant.interfaces;

import java.util.List;

import restaurant.CookAgent.Order;
import restaurant.MarketAgent;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Host {
	
	public abstract void msgReadyToWork(Waiter w);
	
	public abstract void msgIWantToEat(Customer c);
	
	public abstract void msgLeavingRestaurant(Customer c);
	
	public abstract void msgTableIsFree(Waiter w, int msgerT);
	
	public abstract void msgCanIBreak(Waiter w);
	
	public abstract String getName();

	
}
