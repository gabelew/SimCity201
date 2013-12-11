package CMRestaurant.test;

import java.util.Timer;

import junit.framework.TestCase;
import restaurant.Restaurant;
import restaurant.RevolvingStandMonitor;
import restaurant.interfaces.Waiter.Menu;
import restaurant.test.mock.MockCustomer;
import CMRestaurant.roles.CMCookRole;
import CMRestaurant.roles.CMNormalWaiterRole;
import CMRestaurant.roles.CMSharedWaiterRole;
import CMRestaurant.roles.CMWaiterRole;
import CMRestaurant.roles.CMWaiterRole.CustomerState;
import city.PersonAgent;

public class CMSharedAndNormalWaiterAndRevolingStandTest  extends TestCase{
	CMWaiterRole waiterShared;
	CMWaiterRole waiterNormal;
	RevolvingStandMonitor revolvingStand;
	CMCookRole cook;
	PersonAgent person;
	MockCustomer customer;
	Restaurant restaurant;
	Timer timer;
	Menu menu = new Menu();
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("Alan", 500, 500);
		revolvingStand = new RevolvingStandMonitor();
		cook = new CMCookRole();
		cook.setRevolvingStand(revolvingStand);
		timer = new Timer();
		restaurant = new Restaurant(null, null, cook, menu, "RestaurantCMCustomerRole", "RestaurantCM", null, null, "RestaurantCMWaiterRole");
		waiterShared = new CMSharedWaiterRole(person, restaurant);
		waiterNormal = new CMNormalWaiterRole(person, restaurant);
		customer = new MockCustomer("James");
		
		waiterShared.testingRevolvingMonitor = true;
	}	
	
	/**
	 * The test starts with an empty revolving stand. Waiter fills it to capacity and tries to add
	 * another order but should not be able to until the cook has removed the order.
	 */
	public void testWaiterPutInOrderAtMaximumCapacity() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("watierShared should have an empty event log. Instead, the watierShared's event log reads: "
				+ waiterShared.log.toString(), 0, waiterShared.log.size());
		
		/**
		 * Step 1: Customer has ordered. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 1, CustomerState.ordered, "chicken"));
		
		// check postconditions of step 1 and preconditions for step 2
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(0).s));
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
		
		/**
		 * Step 2: Second customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 2, CustomerState.ordered, "steak"));
		
		// check postconditions for step 2 and preconditions for step 3
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(1).s));
		assertTrue("Revolving stand should have 2 orders in it. It doesn't.", 2 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
				
		/**
		 * Step 3: Third customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 3, CustomerState.ordered, "cookie"));
		
		// check postconditions for step 3 and preconditions for step 4
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(2).s));
		assertTrue("Revolving stand should have 3 orders in it. It doesn't.", 3 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
		
		/**
		 * Step 4: Fourth customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 4, CustomerState.ordered, "salad"));
		
		// check postconditions for step 4 and preconditions for step 5
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(3).s));
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 4 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
		
		/**
		 * Step 5: Fifth customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 5, CustomerState.ordered, "pizza"));
		
		// check postconditions for step 5 and preconditions for step 6
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(4).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
		
		
		/**
		 * Step 6: Sixth customer order. Attempt to put order in revolving stand but fails.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 6, CustomerState.ordered, "chicken"));
		
		// check postconditions for step 6 and preconditions for step 7
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be ordered since stand is full.", CustomerState.ordered.equals(waiterShared.customers.get(5).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());
		assertTrue("watierShared should have logged \"Check revoliving stand and satand was full\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and satand was full"));
		
		/**
		 * Step 7: Cook takes an order out of the revolving stand.
		 */
		cook.checkRevolvingStand();
		
		// check postconditions for step 7 and preconditions for step 8
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 0 == revolvingStand.getCount());


		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		int i =0;
		while(!waiterShared.haveNotRecentlyCheckedStand){
			if(i==5){
				assertTrue("haveNotRecentlyCheckedStand is never is set back to true" , false);
			}
			i++;
			try {
			    Thread.sleep(7010);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		/**
		 * Step 8: Call the waiter's schedule again for it to reattempt putting order in stand.
		 */
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		
		// check postconditions for step 8
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(5).s));
		assertTrue("Revolving stand should have 1 orders in it. It doesn't.", 1 == revolvingStand.getCount());	
		assertTrue("watierShared should have logged \"Check revoliving stand and put in order\" but didn't. His log reads instead: " 
				+ waiterShared.log.getLastLoggedEvent().toString(), waiterShared.log.containsString("Check revoliving stand and put in order"));
		
	}
	
	/**
	 * Cook tries to remove an order from an empty revolving stand but cannot until waiter has put in an order.
	 */
	public void testCookRemoveOrderWhenEmpty() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		/**
		 * Step 0: Cook should check inventory
		 * **/
		assertTrue("Cook should check inventory", cook.pickAndExecuteAnAction());
		
		//check pre/post conditions
		assertTrue("cook should have logged \"Preformed orderFoodFromMarket\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Preformed orderFoodFromMarket"));
		
		
		/**
		 * Step 1: Cook tries to take an order from the revolving stand but moves on because there is nothing to take
		 */
		assertTrue("Cook should check revoling stand", cook.pickAndExecuteAnAction());
		
		// check postconditions of step 1 and preconditions of step 2
		assertTrue("Revolving stand should have no orders in it. It doesn't.", 0 == revolvingStand.getCount());
		assertTrue("cook should have logged \"Checked Revolving Stand and it was empty.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked Revolving Stand and it was empty."));
		
		assertFalse("Cook should not recheck stand.", cook.pickAndExecuteAnAction());
		
		int i =0;
		while(!cook.checkStand){
			if(i==5){
				assertTrue("checkStand is never is set back to true" , false);
			}
			i++;
			try {
			    Thread.sleep(4010);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		/**
		 * Step 2: Waiter adds an order
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 5, CustomerState.ordered, "pizza"));
		
		// check postconditions for step 2 and preconditions for step 3
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		
		/**
		 * Step 3: Cook removes the order successfully
		 */
		assertTrue("Cook should check revoling stand", cook.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertTrue("cook should have logged \"Checked Revolving Stand and it had orders in it.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked Revolving Stand and it had orders in it."));
	}
	
	/**
	 * Cook removes all orders from a full revolving stand.
	 */
	public void testCookRemoveAllOrdersFromFullStand() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		assertEquals("Cook should have an empty order list. Instead, the cook's order list has: "
				+ cook.orders.size(), 0, cook.orders.size());
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		
		/**
		 * Step 0: Cook should check inventory
		 * **/
		assertTrue("Cook should check inventory", cook.pickAndExecuteAnAction());
		assertTrue("cook should have logged \"Preformed orderFoodFromMarket\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Preformed orderFoodFromMarket"));
		
		/**
		 * Step 1: Customer has ordered. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 1, CustomerState.ordered, "chicken"));
		
		// check postconditions of step 1 and preconditions for step 2
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(0).s));
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		
		/**
		 * Step 2: Second customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 2, CustomerState.ordered, "steak"));
		
		// check postconditions for step 2 and preconditions for step 3
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(1).s));
		assertTrue("Revolving stand should have 2 orders in it. It doesn't.", 2 == revolvingStand.getCount());
				
		/**
		 * Step 3: Third customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 3, CustomerState.ordered, "cookie"));
		
		// check postconditions for step 3 and preconditions for step 4
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(2).s));
		assertTrue("Revolving stand should have 3 orders in it. It doesn't.", 3 == revolvingStand.getCount());
	
		/**
		 * Step 4: Fourth customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 4, CustomerState.ordered, "salad"));
		
		// check postconditions for step 4 and preconditions for step 5
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(3).s));
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 4 == revolvingStand.getCount());
		
		/**
		 * Step 5: Fifth customer order. Put order in revolving stand.
		 */
		waiterShared.customers.add(waiterShared.new MyCustomer(customer, 5, CustomerState.ordered, "pizza"));
		
		// check postconditions for step 5 and preconditions for step 6
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiterShared.customers.get(4).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());

		
		/**
		 * Step 6: Cook takes an order from the revolving stand
		 */
		assertTrue("Cook should check revoling stand", cook.pickAndExecuteAnAction());
		
		// check postconditions of step 6
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertFalse("checkStand should be set to false" , cook.checkStand);
		assertTrue("cook should have logged \"Checked Revolving Stand and it had orders in it.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked Revolving Stand and it had orders in it."));
		assertEquals("Cook should have an empty order list. Instead, the cook's order list has: "
				+ cook.orders.size(), 5, cook.orders.size());
		
	}

	/**
	 * Cook removes all orders from a full revolving stand.
	 */
	public void testNormalWaiterGivesOrderToCook() {
		//setUp() runs first before this test!
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("WaiterNormal should have an empty event log. Instead, the waiterNormal's event log reads: "
						+ waiterNormal.log.toString(), 0, waiterNormal.log.size());
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		
		/**
		 * Step 1: Gets order from customer
		 * **/
		waiterNormal.customers.add(waiterShared.new MyCustomer(customer, 1, CustomerState.ordered, "chicken"));
		
		//check step 1 post conditions and step 2 pre conditions
		assertEquals("waiterNormal should have 1 customer in list.", waiterNormal.customers.size(), 1);
		assertEquals("waiterNormal should have 1 customer in list in state ordered.", waiterNormal.customers.get(0).s, CustomerState.ordered);
		assertEquals("WaiterNormal should have an empty event log. Instead, the waiterNormal's event log reads: "
				+ waiterNormal.log.toString(), 0, waiterNormal.log.size());
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		
		/**
		 * Step 2: Waiter normal gives cook order.
		 * **/
		assertTrue("waiterNormal scheduler should return true.", waiterNormal.pickAndExecuteAnAction());
		
		//check step 2 post conditions
		assertEquals("waiterNormal should have 1 customer in list.", waiterNormal.customers.size(), 1);
		assertEquals("waiterNormal should have 1 customer in list in state orderPlaced.", waiterNormal.customers.get(0).s, CustomerState.orderPlaced);
		assertTrue("waiterNormal should have logged \"Gave cook order directly\" but didn't. His log reads instead: " 
				+ waiterNormal.log.getLastLoggedEvent().toString(), waiterNormal.log.containsString("Gave cook order directly"));
		assertTrue("cook should have logged \"Recieved msgHereIsOrder\" but didn't. His log reads instead: "
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Recieved msgHereIsOrder"));
	}
}
