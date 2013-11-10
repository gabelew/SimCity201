package restaurant.test.mock;

import java.util.List;
import restaurant.CookAgent;
import restaurant.CookAgent.Food;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Market;

public class MockMarket extends Mock implements Market {
	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	
	public EventLog log = new EventLog(); 
		
	public MockMarket(String name) {
		super(name);
		
	}

	
	@Override
	public void msgHereIsOrder(CookAgent c, Cashier cashier, List<Food> orderList) {
		log.add(new LoggedEvent("Received msgHereIsOrder from cook."));
	}

	@Override
	public void msgPayment(Cashier c, double payment) {
		log.add(new LoggedEvent("Received msgPayment from cashier. Total: " + payment));
	}

}
