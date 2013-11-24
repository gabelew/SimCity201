package city.test;

import java.util.Timer;
import java.util.TimerTask;

import restaurant.RevolvingStandMonitor;
import restaurant.gui.WaiterGui;
import restaurant.test.mock.MockCustomer;
import city.PersonAgent;
import city.roles.CookRole;
import city.roles.SharedDataWaiterRole;
import city.roles.SharedDataWaiterRole.CustomerState;
import junit.framework.TestCase;

public class SharedDataWaiterRoleTest extends TestCase{
	SharedDataWaiterRole waiter;
	RevolvingStandMonitor revolvingStand;
	CookRole cook;
	PersonAgent person;
	MockCustomer customer;
	Timer timer;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("Alan", 500, 500);
		waiter = new SharedDataWaiterRole(person);
		customer = new MockCustomer("James");
		revolvingStand = new RevolvingStandMonitor();
		cook = new CookRole();
		timer = new Timer();
	}	
	
	public void testPutInOrder() {
		//setUp() runs first before this test!
		waiter.setRevolvingStand(revolvingStand);
		cook.setRevolvingStand(revolvingStand);
		
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
		 * Step 6: Sixth customer order. Attempt to put order in revolving stand.
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
		
		// check postconditions for step 7
		assertTrue("Revolving stand should have 4 orders in it. It doesn't.", 4 == revolvingStand.getCount());
		
		/**
		 * Step 8: Call the waiter's schedule again for it to reattempt putting order in stand.
		 */
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(5).s));
		assertTrue("Revolving stand should have 5 orders in it. It doesn't.", 5 == revolvingStand.getCount());
		
		
	}
	
}
