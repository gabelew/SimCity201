package market.test.mock;

import java.util.List;
import java.util.Map;

import CMRestaurant.gui.CMCustomerGui;
import city.MarketAgent;
import city.PersonAgent;
import restaurant.RoleOrder;
import restaurant.interfaces.Waiter.Menu;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
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
public class MockCook extends Mock implements Cook {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockCook(String name) {
		super(name);

	}
	
	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market){
		log.add(new LoggedEvent("Received msgNeverOrderFromMarketAgain from cashier."));
	}
	
	@Override
	public void msgCanIHelpYou(DeliveryMan Dm, MarketAgent m){
		log.add(new LoggedEvent("Received msgCanIHelpYou from delivery man."));
	}

	public void msgHereIsOrder(Waiter W,String choice, int table) {
		log.add(new LoggedEvent("Received msgHereIsOrder from waiter."));
	}
	
	public void msgFoodDone(RoleOrder o){
		log.add(new LoggedEvent("Received msgFoodDone."));
	}
	
	public void msgAnimationFinishedAtFidge(){
		log.add(new LoggedEvent("Received msgAnimationFinishedAtFidge."));
	}
	
	public void msgAnimationFinishedPutFoodOnGrill(){
		log.add(new LoggedEvent("Received msgAnimationFinishedPutFoodOnGrill."));
	}

	public void msgAnimationFinishedWaiterPickedUpFood(){
		log.add(new LoggedEvent("Received msgAnimationFinishedWaiterPickedUpFood."));
	}
	
	public void msgAnimationFinishedPutFoodOnPickUpTable(RoleOrder o){
		log.add(new LoggedEvent("Received msgAnimationFinishedPutFoodOnPickUpTable."));
	}
	
	public void badSteaks(){
		log.add(new LoggedEvent("Received bad steaks"));
	}
	
	public void cookieMonster(){
		log.add(new LoggedEvent("Received cookie monster"));
	}
	
	public void setSteaksAmount(int i){
		log.add(new LoggedEvent("Recieved set steaks amount"));
	}
	
	public void msgHereIsOrderFromMarket(DeliveryMan Dm, Map<String,Integer> choices, double amount){
		log.add(new LoggedEvent("Recieved msgHereIsOrderFromMarket"));
	}

	@Override
	public void msgRelieveFromDuty(PersonAgent p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMarket(MarketAgent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIncompleteOrder(DeliveryMan deliveryMan, List<String> outOf) {
		// TODO Auto-generated method stub
		
	}
}
