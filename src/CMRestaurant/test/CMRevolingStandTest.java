package CMRestaurant.test;

import java.util.Timer;

import junit.framework.TestCase;
import restaurant.Restaurant;
import restaurant.RevolvingStandMonitor;
import restaurant.interfaces.Waiter.Menu;
import restaurant.test.mock.MockCustomer;
import CMRestaurant.roles.CMCookRole;
import CMRestaurant.roles.CMSharedWaiterRole;
import CMRestaurant.roles.CMWaiterRole.CustomerState;
import city.PersonAgent;

public class CMRevolingStandTest  extends TestCase{
	CMSharedWaiterRole waiter;
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
		waiter = new CMSharedWaiterRole(person, restaurant);
		customer = new MockCustomer("James");
	}	
	
	/**
	 * The test starts with an empty revolving stand. Waiter fills it to capacity and tries to add
	 * another order but should not be able to until the cook has removed the order.
	 */
	public void testWaiterPutInOrderAtMaximumCapacity() {
		//setUp() runs first before this test!
		waiter.testingRevolvingMonitor = true;
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		
		/**
		 * Step 1: Customer has ordered. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 1, CustomerState.ordered, "chicken"));
		
		// check postconditions of step 1 and preconditions for step 2
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(0).s));
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		
		/**
		 * Step 2: Second customer order. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 2, CustomerState.ordered, "steak"));
		
		// check postconditions for step 2 and preconditions for step 3
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(1).s));
		assertTrue("Revolving stand should have 2 orders in it. It doesn't.", 2 == revolvingStand.getCount());
				
		/**
		 * Step 3: Third customer order. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 3, CustomerState.ordered, "cookie"));
		
		// check postconditions for step 3 and preconditions for step 4
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(2).s));
		assertTrue("Revolving stand should have 3 orders in it. It doesn't.", 3 == revolvingStand.getCount());
	
		/**
		 * Step 4: Fourth customer order. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 4, CustomerState.ordered, "salad"));
		
		// check postconditions for step 4 and preconditions for step 5
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(3).s));
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 4 == revolvingStand.getCount());
		
		/**
		 * Step 5: Fifth customer order. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 5, CustomerState.ordered, "pizza"));
		
		// check postconditions for step 5 and preconditions for step 6
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(4).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());

		
		/**
		 * Step 6: Sixth customer order. Attempt to put order in revolving stand but fails.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 6, CustomerState.ordered, "chicken"));
		
		// check postconditions for step 6 and preconditions for step 7
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be ordered since stand is full.", CustomerState.ordered.equals(waiter.customers.get(5).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());
		
		/**
		 * Step 7: Cook takes an order out of the revolving stand.
		 */
		cook.checkRevolvingStand();
		
		// check postconditions for step 7 and preconditions for step 8
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 4 == revolvingStand.getCount());


		assertFalse("Waiter scheduler should return false.", waiter.pickAndExecuteAnAction());
		int i =0;
		while(!waiter.haveNotRecentlyCheckedStand){
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
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		
		// check postconditions for step 8
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(5).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());	
		
	}
	
	/**
	 * Cook tries to remove an order from an empty revolving stand but cannot until waiter has put in an order.
	 */
	public void testCookRemoveOrderWhenEmpty() {
		//setUp() runs first before this test!
		waiter.testingRevolvingMonitor = true;
		cook.setRevolvingStand(revolvingStand);
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertTrue("Cook should check inventory", cook.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Cook tries to take an order from the revolving stand but moves on because there is nothing to take
		 */
		cook.checkRevolvingStand();
		
		// check postconditions of step 1 and preconditions of step 2
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
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
		waiter.customers.add(waiter.new MyCustomer(customer, 5, CustomerState.ordered, "pizza"));
		
		// check postconditions for step 2 and preconditions for step 3
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		
		/**
		 * Step 3: Cook removes the order successfully
		 */
		cook.checkRevolvingStand();
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
	}
}
