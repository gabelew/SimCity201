package GCRestaurant.test;

import java.awt.Point;
import java.util.Timer;

import EBRestaurant.gui.EBCookGui;
import EBRestaurant.gui.EBWaiterGui;
import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBNormalWaiterRole;
import EBRestaurant.roles.EBRevolvingStandMonitor;
import EBRestaurant.roles.EBSharedWaiterRole;
import EBRestaurant.roles.EBWaiterRole;
import EBRestaurant.roles.EBWaiterRole.customerState;
import GCRestaurant.roles.GCCookRole;
import GCRestaurant.roles.GCCustomerRole;
import GCRestaurant.roles.GCRevolvingStandMonitor;
import GCRestaurant.roles.GCSharedWaiterRole;
import GCRestaurant.roles.GCWaiterRole.CustomerState;
import GCRestaurant.roles.GCWaiterRole.MyCustomer;
import junit.framework.TestCase;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter.Menu;
import city.PersonAgent;
import city.animationPanels.InsideAnimationPanel;

public class GCWaiterRevolingStandTest  extends TestCase
{
	GCCustomerRole customer;
	GCCustomerRole customer1;
	GCCustomerRole customer2;
	GCCustomerRole customer3;
	GCRevolvingStandMonitor orderStand;
	GCCookRole cook;
	GCSharedWaiterRole waiter;
	PersonAgent testCust;
	PersonAgent testWaiter;
	Restaurant restaurant;
	Menu menu = new Menu();
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception
	{
		
		testCust = new PersonAgent("testcust1", 100, 100);
		testWaiter = new PersonAgent("testwaiter1",100,100);
		orderStand = new GCRevolvingStandMonitor();
		
		cook = new GCCookRole();
		cook.orderStand = orderStand;
		
		restaurant = new Restaurant(null, null, cook, menu, "GCCustomerRole", "GCRestaurant", null, null, "GCWaiterRole");
		waiter = new GCSharedWaiterRole(testWaiter, restaurant);
		customer = new GCCustomerRole(testCust, restaurant);
		customer1 = new GCCustomerRole(testCust, restaurant);
		customer2 = new GCCustomerRole(testCust, restaurant);
		customer3 = new GCCustomerRole(testCust, restaurant);
		super.setUp();			
	}	
	
	/**
	 * First test is empty revolving stand, waiter puts 3 orders on the stand successfully, 6th fails. 
	 * Cook removes orders, waiter puts on the one.
	 */
	public void testSharedWaiterNormal() 
	{
		waiter.unitTesting = true;
		cook.unitTesting = true;
		MyCustomer testCust = waiter.makeCustomer(customer);
		//preconditions
		assertTrue("Revolving stand should have no orders in it. ", 0 == orderStand.getCount());
		assertTrue("cook has no orders", 0 == cook.orders.size());
	
		//insert order
		waiter.customers.add(testCust);
		waiter.HereIsOrderCookAction(testCust);
		//postconditions, added one order
		assertTrue("Revolving stand should have 1 order in it. ", 1 == orderStand.getCount());
		assertTrue("waiter has 1 customer ", 1 == waiter.customers.size());
		assertTrue("cook has no orders", 0 == cook.orders.size());
		
		//cook checks for order
		cook.pickAndExecuteAnAction();
	
		//postconditions cook has one order, revolving stand no orders
		assertTrue("cook has one order", 1 == cook.orders.size());
		assertTrue("revolving stand has no orders", 0 == cook.orderStand.getCount());
		
		//waiter has one order to pick up
		cook.pickAndExecuteAnAction();
		
		//post condition, waiter has order to give
		assertTrue("waiter has one completed order to serve", waiter.customers.get(0).state == CustomerState.FoodDoneCooking);
	
	}
	
	/*
	 * Waiter tries to order 
	 */
	public void testSharedWaiterStandFull() 
	{
		waiter.unitTesting = true;
		cook.unitTesting = true;
		MyCustomer testCust = waiter.makeCustomer(customer);
		MyCustomer testCust1 = waiter.makeCustomer(customer1);
		MyCustomer testCust2 = waiter.makeCustomer(customer2);
		MyCustomer testCust3 = waiter.makeCustomer(customer3);
		
		//preconditions
		assertTrue("Revolving stand should have no orders in it. ", 0 == orderStand.getCount());
		assertTrue("cook has no orders", 0 == cook.orders.size());
	
		//insert 3 orders, making the stand full
		waiter.customers.add(testCust);
		waiter.customers.add(testCust1);
		waiter.customers.add(testCust2);
		waiter.customers.add(testCust3);
		waiter.HereIsOrderCookAction(testCust);
		waiter.HereIsOrderCookAction(testCust1);
		waiter.HereIsOrderCookAction(testCust2);
		
		//postconditions
		assertTrue("Revolving stand should have 3 orders in it. ", 3 == orderStand.getCount());
		assertTrue("waiter has 4 customers ", 4 == waiter.customers.size());
		assertTrue("cook has no orders", 0 == cook.orders.size());
		
		//waiter tries to add one more order, it does not go through
		waiter.HereIsOrderCookAction(testCust3);
		//postconditions
		assertTrue("Revolving stand should have 3 orders in it. ", 3 == orderStand.getCount());
		assertTrue("waiter has 4 customer ", 4 == waiter.customers.size());
		assertTrue("cook has no orders", 0 == cook.orders.size());
		
		//cook takes all orders
		cook.pickAndExecuteAnAction();
	
		//postconditions cook has 3 orders, revolving stand no orders
		assertTrue("cook has three orders", 3 == cook.orders.size());
		assertTrue("revolving stand has no orders", 0 == cook.orderStand.getCount());
		
		//waiter tries to add one more order
		waiter.HereIsOrderCookAction(testCust3);
		//postconditions
		assertTrue("Revolving stand should have 1 orders in it. ", 1 == orderStand.getCount());
		assertTrue("waiter has 4 customer ", 4 == waiter.customers.size());
		assertTrue("cook has three orders", 3 == cook.orders.size());
		
		//waiter has one order to pick up
		cook.pickAndExecuteAnAction();
		cook.pickAndExecuteAnAction();
		cook.pickAndExecuteAnAction();
		
		//postconditions
		assertTrue("Revolving stand should have 1 orders in it. ", 1 == orderStand.getCount());
		assertTrue("waiter has 4 customer ", 4 == waiter.customers.size());
		assertTrue("cook has no orders", 0 == cook.orders.size());
		
		//cook gets order from order stand
		cook.checkOrderStand = true;
		cook.pickAndExecuteAnAction();
		
		//postconditions
		assertTrue("Revolving stand should have 0 orders in it. ", 0 == orderStand.getCount());
		assertTrue("waiter has 1 customer ", 4 == waiter.customers.size());
		assertTrue("cook has 1 orders", 1 == cook.orders.size());
		
		//cook gives order to waiter
		cook.checkOrderStand = false;
		cook.pickAndExecuteAnAction();
		
		//post condition, waiter has 4 complete orders of food
		assertTrue("cook has no orders", 0 == cook.orders.size());
		assertTrue("waiter has one completed order to serve", waiter.customers.get(0).state == CustomerState.FoodDoneCooking);
		assertTrue("waiter has one completed order to serve", waiter.customers.get(1).state == CustomerState.FoodDoneCooking);
		assertTrue("waiter has one completed order to serve", waiter.customers.get(2).state == CustomerState.FoodDoneCooking);
		assertTrue("waiter has one completed order to serve", waiter.customers.get(3).state == CustomerState.FoodDoneCooking);
	
	}
	
	
}