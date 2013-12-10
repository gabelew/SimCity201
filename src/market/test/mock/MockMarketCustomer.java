package market.test.mock;

import java.util.List;
import java.util.Map;

import city.MarketAgent;
import city.roles.ClerkRole;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;
/**
 * A sample MockCustomer built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public class MockMarketCustomer extends Mock implements MarketCustomer {

	public boolean goToATM = false;
	public EventLog log = new EventLog();
	
	public MockMarketCustomer(String name) {
		super(name);

	}
	public void msgCanIHelpYou(ClerkRole clerk){
		log.add(new LoggedEvent("Received msgCanIHelpYou from clerk"));
	}
	
	public void msgHereIsPrice(double amount){
		log.add(new LoggedEvent("Received msgHereIsPrice from clerk"));
	}
	
	public void msgHereIsOrder(Map<String,Integer>choice,List<String>outOf){
		log.add(new LoggedEvent("Received msgHereIsOrder from clerk"));
	}
	@Override
	public void startShopping(MarketAgent m,
			Map<String, Integer> toOrderFromMarket) {
		log.add(new LoggedEvent("Received startShopping"));
	}
	@Override
	public void msgMarketClosed() {
		log.add(new LoggedEvent("Received msgMarketClosed from market"));
	}

	
	
}
