package market.test.mock;

import city.MarketAgent;
import market.interfaces.*;
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
public class MockCashier extends Mock implements Cashier {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockCashier(String name) {
		super(name);

	}

	@Override
	public void msgProduceCheck(Waiter w, Customer c, String choice) {
		log.add(new LoggedEvent("Received msgProduceCheck from waiter."));
	}
	
	@Override
	public void msgHereIsBill(MarketAgent marketAgent,double bill){
		log.add(new LoggedEvent("Received msgHereIsBill from market."));
	}
	
	@Override
	public void msgPayment(Customer c, double cash){
		log.add(new LoggedEvent("Received msgPayment from customer."));
	}
	
}
