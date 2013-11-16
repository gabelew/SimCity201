package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter{

	public EventLog log = new EventLog();
	
	public CashierAgent cashier;
	
	public MockWaiter(String name) {
		super(name);

	}

	@Override
	public void msgHereIsCheck(Customer c, double check) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = "+ check));
		
	}

	@Override
	public void msgSitAtTable(Customer c, int table) {
		log.add(new LoggedEvent("Received msgHereIsCheck from host. Table number = "+ table));
	}

	@Override
	public void msgImReadyToOrder(Customer c) {
		log.add(new LoggedEvent("Received msgImReadyToOrder from customer."));
		
	}

	@Override
	public void msgHereIsMyOrder(Customer c, String choice) {
		log.add(new LoggedEvent("Received msgHereIsMyOrder from customer. Choice = " + choice));
	}

	@Override
	public void msgDoneEatingAndLeaving(Customer c) {
		log.add(new LoggedEvent("Received msgDoneEatingAndLeaving from customer."));
		
	}

	@Override
	public void msgOutOfOrder(String choice, int table){
		log.add(new LoggedEvent("Received msgOutOfOrder from cook."));
	}
	
	@Override 
	public void msgOrderIsReady(String choice, int table){
		log.add(new LoggedEvent("Received msgOrderIsReady from cook."));
	}
	
	@Override
	public void msgGoOnBreak(){
		log.add(new LoggedEvent("Received msgGoOnBreak from host."));
	}
	
	@Override
	public void msgDontGoOnBreak(){
		log.add(new LoggedEvent("Received msgDontGoOnBreak from host."));
	}

	@Override
	public String getName(){
		log.add(new LoggedEvent("Received getName from host."));
		return "name";
	}
}
