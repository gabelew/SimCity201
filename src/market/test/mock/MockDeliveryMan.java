package market.test.mock;

import java.util.Map;

import city.MarketAgent;
import city.roles.CashierRole;
import city.roles.CookRole;
import restaurant.interfaces.Waiter.Menu;
import restaurant.gui.CustomerGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;
/**
 * A sample MockCustomer built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public class MockDeliveryMan extends Mock implements DeliveryMan {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockDeliveryMan(String name) {
		super(name);

	}

	public void msgTakeCustomer(Cook c,MarketAgent m){
		log.add(new LoggedEvent("Received msgTakeCustomer from market"));
	}
	
	public void msgHereIsOrder(Map<String,Integer>choice){
		log.add(new LoggedEvent("Received msgHereIsOrder from cook"));
	}
	
	public void msgHereIsPayment(double payment, CashierRole ca){
		log.add(new LoggedEvent("Received msgHereIsPayment from cook"));
	}
	
	
}