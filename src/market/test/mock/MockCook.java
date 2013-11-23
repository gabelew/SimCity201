package market.test.mock;

import java.util.List;
import java.util.Map;

import city.MarketAgent;
import city.roles.CookRole.RoleOrder;
import restaurant.interfaces.Waiter.Menu;
import restaurant.gui.CustomerGui;
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
	public void msgIncompleteOrder(DeliveryMan Dm,List<String> outOf){
		log.add(new LoggedEvent("Received msgIncompleteOrder from delivery man."));
	}
	
	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market){
		log.add(new LoggedEvent("Received msgNeverOrderFromMarketAgain from cashier."));
	}
	
	@Override
	public void msgCanIHelpYou(DeliveryMan Dm, MarketAgent m){
		log.add(new LoggedEvent("Received msgCanIHelpYou from delivery man."));
	}

	@Override
	public void msgHereIsOrder(Waiter W,String choice, int table) {
		log.add(new LoggedEvent("Received msgHereIsOrder from waiter."));
	}
	
	@Override
	public void msgFoodDone(RoleOrder o){
		log.add(new LoggedEvent("Received msgFoodDone."));
	}
	
	@Override
	public void msgAnimationFinishedAtFidge(){
		log.add(new LoggedEvent("Received msgAnimationFinishedAtFidge."));
	}
	
	@Override
	public void msgAnimationFinishedPutFoodOnGrill(){
		log.add(new LoggedEvent("Received msgAnimationFinishedPutFoodOnGrill."));
	}
	
	@Override
	public void msgAnimationFinishedWaiterPickedUpFood(){
		log.add(new LoggedEvent("Received msgAnimationFinishedWaiterPickedUpFood."));
	}
	
	@Override
	public void msgAnimationFinishedPutFoodOnPickUpTable(RoleOrder o){
		log.add(new LoggedEvent("Received msgAnimationFinishedPutFoodOnPickUpTable."));
	}
	
	@Override
	public void badSteaks(){
		log.add(new LoggedEvent("Received bad steaks"));
	}
	
	@Override
	public void cookieMonster(){
		log.add(new LoggedEvent("Received cookie monster"));
	}
	
	@Override
	public void setSteaksAmount(int i){
		log.add(new LoggedEvent("Recieved set steaks amount"));
	}
	
	@Override
	public void msgHereIsOrderFromMarket(DeliveryMan Dm, Map<String,Integer> choices, List<String> outOf, double amount){
		log.add(new LoggedEvent("Recieved msgHereIsOrderFromMarket"));
	}
}
