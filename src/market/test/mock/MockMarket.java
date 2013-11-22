package market.test.mock;


import restaurant.interfaces.Cashier;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;
/**
 * A sample MockCustomer built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockMarket(String name) {
		super(name);

	}

	
	public void msgPlaceOrder(MarketCustomer CR){
		log.add(new LoggedEvent("Received msgPlaceOrder from MarketCustomer."));
	}

	public void msgPlaceDeliveryOrder(Cook cook){
		log.add(new LoggedEvent("Received msgPlaceDeliveryOrder from CookCustomer."));
	}
	
	public void msgClerkDone(){
		log.add(new LoggedEvent("Received msgClerkDone from clerk."));
	}
	
	public void msgDeliveryDone(){
		log.add(new LoggedEvent("Received msgDeliveryDone from deliveryMan."));
	}
	
}
