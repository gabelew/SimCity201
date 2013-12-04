package restaurant.test.mock;

import restaurant.Restaurant;
import restaurant.interfaces.*;

public class MockWaiter extends Mock implements Waiter{

	public EventLog log = new EventLog();
	
	public Cashier cashier;
	
	public MockWaiter(String name) {
		super(name);

	}

	public void msgHereIsCheck(Customer c, double check) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = "+ check));
		
	}

	public void msgSitAtTable(Customer c, int table) {
		log.add(new LoggedEvent("Received msgHereIsCheck from host. Table number = "+ table));
	}

	public void msgImReadyToOrder(Customer c) {
		log.add(new LoggedEvent("Received msgImReadyToOrder from customer."));
		
	}

	public void msgHereIsMyOrder(Customer c, String choice) {
		log.add(new LoggedEvent("Received msgHereIsMyOrder from customer. Choice = " + choice));
	}

	public void msgDoneEatingAndLeaving(Customer c) {
		log.add(new LoggedEvent("Received msgDoneEatingAndLeaving from customer."));
		
	}

	public void msgOutOfOrder(String choice, int table){
		log.add(new LoggedEvent("Received msgOutOfOrder from cook."));
	}
	 
	public void msgOrderIsReady(String choice, int table){
		log.add(new LoggedEvent("Received msgOrderIsReady from cook."));
	}
	
	public void msgGoOnBreak(){
		log.add(new LoggedEvent("Received msgGoOnBreak from host."));
	}
	
	public void msgDontGoOnBreak(){
		log.add(new LoggedEvent("Received msgDontGoOnBreak from host."));
	}

	public String getName(){
		log.add(new LoggedEvent("Received getName from host."));
		return "name";
	}

	public void msgAtEntrance() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeftTheRestaurant() {
		// TODO Auto-generated method stub
		
	}

	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Restaurant getRestaurant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void goesToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAskForBreak() {
		// TODO Auto-generated method stub
		
	}
}
