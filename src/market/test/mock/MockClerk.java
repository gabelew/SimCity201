package market.test.mock;

import java.util.Map;

import CMRestaurant.gui.CMCustomerGui;
import city.MarketAgent;
import city.roles.MarketCustomerRole;
import restaurant.interfaces.Waiter.Menu;
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
public class MockClerk extends Mock implements Clerk {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockClerk(String name) {
		super(name);

	}

	@Override
	public void msgTakeCustomer(MarketCustomer mcr, Market m){
		log.add(new LoggedEvent("Received msgTakeCustomer from market"));
	}
	@Override
	public void msgPlaceOrder(Map<String,Integer> choice){
		log.add(new LoggedEvent("Received msgPlaceOrder from market customer"));
	}
	@Override
	public void msgHereIsPayment(double money){
		log.add(new LoggedEvent("Received msgHereIsPaymet from market customer"));
	}

	@Override
	public void msgDoneWithShift() {
		log.add(new LoggedEvent("Received msgDoneWithShift from market customer"));
	}
}
