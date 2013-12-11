package GLRestaurant.test;

import java.util.Timer;

import junit.framework.TestCase;
import GLRestaurant.roles.GLRevolvingStandMonitor;
import restaurant.Restaurant;
import restaurant.interfaces.Waiter.Menu;
import GLRestaurant.roles.GLCookRole;
import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLNormalWaiterRole;
import GLRestaurant.roles.GLSharedWaiterRole;
import GLRestaurant.roles.GLWaiterRole;
import GLRestaurant.roles.GLWaiterRole.customerState;
import city.PersonAgent;

public class GLRevolvingMonitorTest extends TestCase {
	GLWaiterRole normalWaiter, sharedWaiter;
	GLCookRole cook;
	GLCustomerRole customer;
	PersonAgent person, person2, person3;
	Restaurant restaurant;
	Timer timer;
	Menu menu = new Menu();
	GLRevolvingStandMonitor revolvingStand;
	
	public void setUp() throws Exception {
		super.setUp();
		revolvingStand = new GLRevolvingStandMonitor();
		cook = new GLCookRole(50);
		cook.setRevolvingStand(revolvingStand);
		restaurant = new Restaurant(null, null, cook, menu, "RestaurantGLCustomerRole", "RestaurantGL", null, null, "RestaurantGLWaiterRole");
		person = new PersonAgent("Timothy", 800, 1500);
		person2 = new PersonAgent("Wade", 400, 2000);
		person3 = new PersonAgent("Andy", 500, 0);
		sharedWaiter = new GLSharedWaiterRole(person, restaurant);
		normalWaiter = new GLNormalWaiterRole(person2, restaurant);
		customer = new GLCustomerRole(person3, restaurant);

		timer = new Timer();
	}
	
	public void testSharedWaiterPutInOrderAtMaxCapacity() {
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("sharedWaiter should have an empty event log. Instead, the event log reads: "
				+ sharedWaiter.log.toString(), 0, sharedWaiter.log.size());
		
		/**
		 * Step 1: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood", customerState.waitingForFood == sharedWaiter.customers.get(0).cs);
		assertTrue("Revolving stand should have 1 order(s) in it. It doesn't.", 1 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		/**
		 * Step 2: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood", customerState.waitingForFood == sharedWaiter.customers.get(1).cs);
		assertTrue("Revolving stand should have 2 order(s) in it. It doesn't.", 2 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		/**
		 * Step 3: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));

		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood", customerState.waitingForFood == sharedWaiter.customers.get(2).cs);
		assertTrue("Revolving stand should have 3 order(s) in it. It doesn't.", 3 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		/**
		 * Step 4: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood", customerState.waitingForFood == sharedWaiter.customers.get(3).cs);
		assertTrue("Revolving stand should have 4 order(s) in it. It doesn't.", 4 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		/**
		 * Step 5: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood.", customerState.waitingForFood == sharedWaiter.customers.get(4).cs);
		assertTrue("Revolving stand should have 5 order(s) in it. It doesn't.", 5 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		
		/**
		 * Step 6: Customer has ordered. Revolving stand is full.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be ordered.", customerState.ordered == sharedWaiter.customers.get(5).cs);
		assertTrue("Revolving stand should have 5 order(s) in it. It doesn't.", 5 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Revolving stand is full. Try again later.\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Revolving stand is full. Try again later."));
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		/**
		 * Step 7: Cook removes orders from revolving stand.
		 */
		cook.checkRevolvingStand();
		//check postconditions are true
		assertTrue("cook should have logged \"Revolving stand has orders in it.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Revolving stand has orders in it."));
		assertTrue("Revolving stand should have 0 order(s) in it. It doesn't.", 0 == revolvingStand.getCount());
		
		/**
		 * Step 8: Waiter puts order in revolving stand successfully now.
		 */
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood.", customerState.waitingForFood == sharedWaiter.customers.get(5).cs);
		assertTrue("Revolving stand should have 5 order(s) in it. It doesn't.", 1 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		
	}
	
	public void testCookTakeOrderWhenEmpty() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		
		/**
		 * Step 1: Cook checks revolving stand for orders.
		 */
		cook.checkRevolvingStand();
		//check postconditions are true
		assertTrue("cook should have logged \"Revolving stand is empty.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Revolving stand is empty."));
		assertTrue("Revolving stand should have 0 order(s) in it. It doesn't.", 0 == revolvingStand.getCount());
		assertEquals("sharedWaiter should have an empty event log. Instead, the event log reads: "
				+ sharedWaiter.log.toString(), 0, sharedWaiter.log.size());
		
		/**
		 * Step 2: Customer has ordered. Put order in revolving stand.
		 */
		sharedWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood.", customerState.waitingForFood == sharedWaiter.customers.get(0).cs);
		assertTrue("Revolving stand should have 1 order(s) in it. It doesn't.", 1 == revolvingStand.getCount());
		assertTrue("sharedWaiter should have logged \"Put order into the revolving stand.\" but didn't. His log reads instead: " 
				+ sharedWaiter.log.getLastLoggedEvent().toString(), sharedWaiter.log.containsString("Put order into the revolving stand."));
		/**
		 * Step 3: Cook removes orders from revolving stand.
		 */
		cook.checkRevolvingStand();
		//check postconditions are true
		assertTrue("cook should have logged \"Revolving stand has orders in it.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Revolving stand has orders in it."));
		assertTrue("Revolving stand should have 0 order(s) in it. It doesn't.", 0 == revolvingStand.getCount());
		
	}
	
	public void testNormalWaiterSendsCookOrder() {
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == revolvingStand.getCount());
		assertEquals("Cook should have an empty event log. Instead, the cook's event log reads: "
				+ cook.log.toString(), 0, cook.log.size());
		assertEquals("normalWaiter should have an empty event log. Instead, the event log reads: "
				+ normalWaiter.log.toString(), 0, normalWaiter.log.size());
		

		/**
		 * Step 1: Customer has ordered. Send order to cook electronically.
		 */
		normalWaiter.customers.add(sharedWaiter.new MyCustomer(customer,customerState.ordered,"steak"));
		//check postconditions are true
		assertTrue("Waiter scheduler returns true.", normalWaiter.pickAndExecuteAnAction());
		assertTrue("Customer state should be waitingForFood.", customerState.waitingForFood == normalWaiter.customers.get(0).cs);
		assertTrue("normalWaiter should have logged \"Electronically sent order to cook.\" but didn't. His log reads instead: " 
				+ normalWaiter.log.getLastLoggedEvent().toString(),normalWaiter.log.containsString("Electronically sent order to cook."));
		assertTrue("cook should have logged \"Received msgHereIsOrder.\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsOrder."));
	}
}
