package city.test;

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
	//MockCook cook;
	CookRole cook;
	PersonAgent person;
	MockCustomer customer;
	
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
		
	}	
	
	public void testPutInOrder() {
		//setUp() runs first before this test!
		waiter.setRevolvingStand(revolvingStand);
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		
		/**
		 * Step 1: Customer has ordered. Put order in revolving stand.
		 */
		waiter.customers.add(waiter.new MyCustomer(customer, 1, CustomerState.ordered, "chicken"));
		
		assertTrue("Waiter scheduler should return true.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be order placed.", CustomerState.orderPlaced.equals(waiter.customers.get(0).s));
		assertTrue("Revolving stand should have 1 order in it. It doesn't.", 1 == revolvingStand.getCount());
		
	}
	
}
