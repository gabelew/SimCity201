package EBRestaurant.test;

import java.util.Timer;

import CMRestaurant.roles.CMWaiterRole.CustomerState;
import CMRestaurant.roles.CMWaiterRole.MyCustomer;
import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBNormalWaiterRole;
import EBRestaurant.roles.EBRevolvingStandMonitor;
import EBRestaurant.roles.EBSharedWaiterRole;
import EBRestaurant.roles.EBWaiterRole;
import EBRestaurant.roles.EBWaiterRole.customerState;
import junit.framework.TestCase;
import restaurant.Restaurant;
import restaurant.interfaces.Waiter.Menu;
import city.PersonAgent;

public class EBWaiterRevolingStandTest  extends TestCase{
	EBWaiterRole waiterShared;
	EBWaiterRole waiterNormal;
	EBRevolvingStandMonitor EBrevolvingStand;
	EBCookRole cook;
	PersonAgent person;
	PersonAgent person2;
	EBCustomerRole customer;
	EBCustomerRole customer2;
	Restaurant restaurant;
	Timer timer;
	Menu menu = new Menu();
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("Emily", 500, 500);
		person2 = new PersonAgent("Sam",500,500);
		EBrevolvingStand = new EBRevolvingStandMonitor();
		cook = new EBCookRole(5);
		cook.setRevolvingStand(EBrevolvingStand);
		timer = new Timer();
		restaurant = new Restaurant(null, null, cook, menu, "EBCustomerRole", "EBRestaurant", null, null, "EBWaiterRole");
		waiterShared = new EBSharedWaiterRole(person, restaurant);
		waiterNormal = new EBNormalWaiterRole(person, restaurant);
		customer = new EBCustomerRole(person2,restaurant);
		customer2 = new EBCustomerRole(person2,restaurant);
		
		waiterShared.testingMonitor = true;
		cook.testingMonitor = true;
	}	
	
	/**
	 * First test is empty revolving stand, waiter puts one order on the stand succesfully, and the coo removes it.
	 */
	public void testSharedWaiterNormal() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertTrue("Revolving stand should have no orders in it. It does.", 0 == EBrevolvingStand.getCount());
		assertEquals("watierShared should have an empty event log. Instead, the watierShared's event log reads: "
				+ waiterShared.log.toString(), 0, waiterShared.log.size());
		
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(0).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(0).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(0).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(0).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(0).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(0).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be waitForFood",waiterShared.Customers.get(0).S,customerState.waitForFood);
		assertEquals("watierShared should have one entry in log. The watierShared's event log reads: "
				+ waiterShared.log.toString(), 1, waiterShared.log.size());
		assertEquals("Revolving stand should have 1 order in it. It does.", 1,EBrevolvingStand.getCount());
		
		/**
		 * Seats 2nd customer
		 */
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer2, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(1).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(1).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer2);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(1).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(1).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer2);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(1).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be waitForFood",waiterShared.Customers.get(1).S,customerState.waitForFood);
		assertEquals("watierShared should have one entry in log. The watierShared's event log reads: "
				+ waiterShared.log.toString(), 2, waiterShared.log.size());
		assertEquals("Revolving stand should have 2 order in it. It does.", 2,EBrevolvingStand.getCount());
		
		//Customer gets food then leaves....
		waiterShared.msgLeavingTable(customer2);
		assertEquals("waiterShared customer state should now be done",waiterShared.Customers.get(1).S,customerState.done);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		
		/**
		 * Seats 3rd customer
		 */
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer2, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(1).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(1).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer2);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(1).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(1).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer2);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(1).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be waitForFood",waiterShared.Customers.get(1).S,customerState.waitForFood);
		assertEquals("watierShared should have three entries in log. The watierShared's event log reads: "
				+ waiterShared.log.toString(), 3, waiterShared.log.size());
		assertEquals("Revolving stand should have 3 orders in it. It does.", 3,EBrevolvingStand.getCount());
		
		//Customer gets food then leaves....
		waiterShared.msgLeavingTable(customer2);
		assertEquals("waiterShared customer state should now be done",waiterShared.Customers.get(1).S,customerState.done);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		

		/**
		 * Seats 4th customer
		 */
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer2, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(1).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(1).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer2);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(1).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(1).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer2);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(1).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be waitForFood",waiterShared.Customers.get(1).S,customerState.waitForFood);
		assertEquals("watierShared should have four entries in log. The watierShared's event log reads: "
				+ waiterShared.log.toString(), 4, waiterShared.log.size());
		assertEquals("Revolving stand should have 4 orders in it. It does.", 4,EBrevolvingStand.getCount());
		
		//Customer gets food then leaves....
		waiterShared.msgLeavingTable(customer2);
		assertEquals("waiterShared customer state should now be done",waiterShared.Customers.get(1).S,customerState.done);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		

		/**
		 * Seats 5th customer
		 */
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer2, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(1).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(1).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer2);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(1).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(1).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer2);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(1).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be waitForFood",waiterShared.Customers.get(1).S,customerState.waitForFood);
		assertEquals("watierShared should have five entries in log. The watierShared's event log reads: "
				+ waiterShared.log.toString(), 5, waiterShared.log.size());
		assertEquals("Revolving stand should have 5 orders in it. It does.", 5,EBrevolvingStand.getCount());
		
		//Customer gets food then leaves....
		waiterShared.msgLeavingTable(customer2);
		assertEquals("waiterShared customer state should now be done",waiterShared.Customers.get(1).S,customerState.done);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		
		/**
		 * Step 6: Sixth customer order. Attempt to put order in revolving stand but fails.
		 */
		//Waiter seats customer
		waiterShared.msgSeatCustomer(customer2, 1);
		assertEquals("waiterShared customer state should be waiting",waiterShared.Customers.get(1).S,customerState.waiting);
		//Scheduler should return true, will seat customer
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be seated",waiterShared.Customers.get(1).S,customerState.seated);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
						
		//Customer is ready to order
		waiterShared.msgReadyToOrder(customer2);
		assertEquals("waiterShared customer state should now be ready to order",waiterShared.Customers.get(1).S,customerState.readyToOrder);
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should now be asked",waiterShared.Customers.get(1).S,customerState.asked);
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		
		//Customer orders steak
		waiterShared.msgHereIsMyOrder("steak", customer2);
		assertEquals("waiterShared customer state should now be ordered",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("waiterShared customer should have choice steak",waiterShared.Customers.get(1).choice,"steak");
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared customer state should still be ordered since stand is full",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("watierShared should have six entries in log. The watierShared's event log reads: "
						+ waiterShared.log.toString(), 6, waiterShared.log.size());
		assertEquals("Revolving stand should have 5 orders in it. It does.", 5,EBrevolvingStand.getCount());
		
		// check postconditions for step 6 and preconditions for step 7
		assertEquals("Customer state should be ordered since stand is full.",waiterShared.Customers.get(1).S,customerState.ordered);
		assertEquals("Revolving stand should have 5 orders in it. It does.", 5 , EBrevolvingStand.getCount());
		

		/**
		 * Cook takes an order out of the revolving stand.
		 */
		cook.checkRevolving();
		assertFalse("watierShared scheduler should return false.", waiterShared.pickAndExecuteAnAction());
		int i =0;
		while(!waiterShared.checked){
			if(i==5){
				assertTrue("checked is never is set back to true" , false);
			}
			i++;
			try {
			    Thread.sleep(7010);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertTrue("check is now is set back to true" ,waiterShared.checked);
		
		/**
		 * Call the waiter's schedule again to try and put order on stand.
		 */
		assertTrue("watierShared scheduler should return true.", waiterShared.pickAndExecuteAnAction());
		
		// check postconditions for step 8
		assertEquals("Customer state should be wait for food now that order is on stand.",waiterShared.Customers.get(1).S,customerState.waitForFood);
		assertEquals("Revolving stand should have 1 orders in it. It does.", 1,EBrevolvingStand.getCount());	
	}
	
}