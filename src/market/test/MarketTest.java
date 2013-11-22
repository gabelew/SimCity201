package market.test;

import java.awt.Point;

import city.MarketAgent;
import city.MarketAgent.cookState;
import city.MarketAgent.customerState;
import city.roles.CashierRole;
import city.roles.MarketCustomerRole;
import city.roles.ClerkRole;
import city.roles.DeliveryManRole;
import city.roles.CookRole;
import restaurant.interfaces.*;
import market.test.mock.*;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the MarketAgent's basic interaction
 * with Cashiers,Cooks, customers, and the Delivery man.
 *
 * @author Emily Bernstein
 */
public class MarketTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	MarketAgent market;
	MockMarketCustomer customer, customer2, customerDitch, customerRich;
	MockClerk clerk;
	MockDeliveryMan deliveryMan;
	MockCook cook,cook2,cook3;
	MockCashier cashier,cashier2,cashier3;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		Point location=new Point(5, 5);
		customer = new MockMarketCustomer("mockcustomer");
		customer2 = new MockMarketCustomer("mockcustomer2");
		customerDitch = new MockMarketCustomer("mockcustomerditch");
		customerRich = new MockMarketCustomer("mockcustomerrich");
		clerk = new MockClerk("Clerk");
		deliveryMan = new MockDeliveryMan("DeliveryMan");
		market= new MarketAgent(clerk,deliveryMan,location,"market");
		cook = new MockCook("cook");
		cook2 = new MockCook("cook2");
		cook3 = new MockCook("cook3");
		cashier = new MockCashier("cashier");
		cashier2 = new MockCashier("cashier2");
		cashier3 = new MockCashier("cashier3");
		
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer will pay the exact check amount.
	 */
	public void testOneNormalCustomerScenario()
	{
		/*
		 * Normal one customer coming into store.
		 */
		//pre-initializing checks
		assertEquals("Market should have 0 customers in it. It doesn't.",market.MyCustomers.size(), 0);
		assertEquals("Market should have 0 cook customers in it. It doesn't.",market.MyCooks.size(), 0);
		assertEquals(
				"MockClerk should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ clerk.log.toString(), 0, clerk.log.size());
		assertEquals(
				"MockCook should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals(
				"MockCook2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook2.log.toString(), 0, cook2.log.size());
		assertEquals(
				"MockCook3 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook3.log.toString(), 0, cook3.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockCustomerRich should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		//customer sends initial message to Market
		market.msgPlaceOrder(customer);
		//check post-conditions of message
		assertTrue("Market should have logged \"Received msgPlaceOrder\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPlaceOrder from MarketCustomer."));
		assertTrue("Clerk should be free before being assigned a customer",market.clerkFree);
		assertEquals("Market should have 1 customer in it.",market.MyCustomers.size(), 1);
		assertTrue("The customer hsould have state waiting",market.MyCustomers.get(0).state==customerState.waiting);
		assertTrue("The Market's customer should be set to customer." , 
				market.MyCustomers.get(0).MC==customer);
		assertTrue("The market's stateChange semaphore should have positive permit." +  market.getStateChangePermits(), 
				market.getStateChangePermits() > 0);
		
		
		
		// run the market's scheduler
		assertTrue("Market's scheduler should have returned true (needs to react to customer's msgPlaceOrder).", 
				market.pickAndExecuteAnAction());
		
		//check post scheduler
		assertFalse("Clerk should be no longer be free",market.clerkFree);
		assertEquals(
				"MockClerk should not have an empty event log after the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ clerk.log.toString(), 1, clerk.log.size());
		assertEquals("Market should now have 0 customers in it.",market.MyCustomers.size(), 0);
		//when done, Clerk sends message saying he is free
		market.msgClerkDone();
		assertTrue("Market should have logged \"Received msgClerkDone\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgClerkDone from clerk."));
		assertTrue("Clerk should now be free",market.clerkFree);
		assertFalse("Market's scheduler should have returned false (doesn't have anything to do).", 
				market.pickAndExecuteAnAction());
	}
	
	public void testOneNormalCookScenario()
	{
		/*
		 * Normal one cook asking for delivery.
		 */
		//pre-initializing checks
		assertEquals("Market should have 0 customers in it. It doesn't.",market.MyCustomers.size(), 0);
		assertEquals("Market should have 0 cook customers in it. It doesn't.",market.MyCooks.size(), 0);
		assertEquals(
				"MockClerk should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ clerk.log.toString(), 0, clerk.log.size());
		assertEquals(
				"MockCook should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals(
				"MockCook2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook2.log.toString(), 0, cook2.log.size());
		assertEquals(
				"MockCook3 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook3.log.toString(), 0, cook3.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockCustomerRich should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		//customer sends initial message to Market
		market.msgPlaceDeliveryOrder(cook);
		//check post-conditions of message
		assertTrue("Market should have logged \"Received msgPlaceDeliveryOrder\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPlaceDeliveryOrder from CookCustomer."));
		assertTrue("DeliveryMan should be free before being assigned a customer",market.deliveryFree);
		assertEquals("Market should have 1 cook customer in it.",market.MyCooks.size(), 1);
		assertTrue("The cook customer should have state waiting",market.MyCooks.get(0).cookstate==cookState.waiting);
		assertTrue("The Market's cook customer should be set to cook." , 
				market.MyCooks.get(0).cook==cook);
		assertTrue("The market's stateChange semaphore should have positive permit." +  market.getStateChangePermits(), 
				market.getStateChangePermits() > 0);
		
		
		
		// run the market's scheduler
		assertTrue("Market's scheduler should have returned true (needs to react to customer's msgPlaceOrder).", 
				market.pickAndExecuteAnAction());
		
		//check post scheduler
		assertFalse("Delivery Man should be no longer be free",market.deliveryFree);
		assertTrue("Clerk should still be free",market.clerkFree);
		assertEquals(
				"MockDeliveryMan should not have an empty event log after the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ deliveryMan.log.toString(), 1, deliveryMan.log.size());
		assertEquals("Market should now have 0 cook customers in it.",market.MyCooks.size(), 0);
		assertFalse("Delivery Man should still not be free",market.deliveryFree);
		//when done, DeliveryMan sends message saying he is free
		market.msgDeliveryDone();
		assertTrue("Market should have logged \"Received msgDeliveryDone\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgDeliveryDone from deliveryMan."));
		assertTrue("Delivery Man should now be free",market.deliveryFree);
		assertFalse("Market's scheduler should have returned false (doesn't have anything to do).", 
				market.pickAndExecuteAnAction());
		
	}
	public void testOneNormalCookCustomerScenario()
	{
		/*
		 * Normal one cook delivery and customer coming into store.
		 */
		//pre-initializing checks
		assertEquals("Market should have 0 customers in it. It doesn't.",market.MyCustomers.size(), 0);
		assertEquals("Market should have 0 cook customers in it. It doesn't.",market.MyCooks.size(), 0);
		assertEquals(
				"MockClerk should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ clerk.log.toString(), 0, clerk.log.size());
		assertEquals(
				"MockCook should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals(
				"MockCook2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook2.log.toString(), 0, cook2.log.size());
		assertEquals(
				"MockCook3 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ cook3.log.toString(), 0, cook3.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockCustomerRich should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		//customer sends initial message to Market
		market.msgPlaceDeliveryOrder(cook);
		//check post-conditions of message
		assertTrue("Market should have logged \"Received msgPlaceDeliveryOrder\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPlaceDeliveryOrder from CookCustomer."));
		assertTrue("DeliveryMan should be free before being assigned a customer",market.deliveryFree);
		assertEquals("Market should have 1 cook customer in it.",market.MyCooks.size(), 1);
		assertTrue("The cook customer should have state waiting",market.MyCooks.get(0).cookstate==cookState.waiting);
		assertTrue("The Market's cook customer should be set to cook." , 
				market.MyCooks.get(0).cook==cook);
		assertTrue("The market's stateChange semaphore should have positive permit." +  market.getStateChangePermits(), 
				market.getStateChangePermits() > 0);
		
		
		
		// run the market's scheduler
		assertTrue("Market's scheduler should have returned true (needs to react to customer's msgPlaceOrder).", 
				market.pickAndExecuteAnAction());
		
		//check post scheduler
		assertFalse("Delivery Man should be no longer be free",market.deliveryFree);
		assertTrue("Clerk should still be free",market.clerkFree);
		assertEquals(
				"MockDeliveryMan should not have an empty event log after the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ deliveryMan.log.toString(), 1, deliveryMan.log.size());
		assertEquals("Market should now have 0 cook customers in it.",market.MyCooks.size(), 0);
		assertFalse("Delivery Man should still not be free",market.deliveryFree);
		//when done, DeliveryMan sends message saying he is free
		market.msgDeliveryDone();
		assertTrue("Market should have logged \"Received msgDeliveryDone\". His log reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgDeliveryDone from deliveryMan."));
		assertTrue("Delivery Man should now be free",market.deliveryFree);
		assertFalse("Market's scheduler should have returned false (doesn't have anything to do).", 
				market.pickAndExecuteAnAction());
		
	}

}
