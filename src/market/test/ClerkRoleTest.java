package market.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import city.MarketAgent;
import city.MarketAgent.cookState;
import city.MarketAgent.customerState;
import city.roles.CashierRole;
import city.roles.ClerkRole.orderState;
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
public class ClerkRoleTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	MarketAgent market;
	MockMarketCustomer customer, customer2, customerDitch, customerRich;
	ClerkRole clerk;
	MockDeliveryMan deliveryMan;
	MockCook cook,cook2,cook3;
	MockCashier cashier,cashier2,cashier3;
	Map<String,Integer> choice;
	double normAmount=30;
	Integer amountLeft=8;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();	
		Point location= new Point(5,5);
		clerk = new ClerkRole();
		deliveryMan=new MockDeliveryMan("deliveryMan");
		customer = new MockMarketCustomer("mockcustomer");
		customer2 = new MockMarketCustomer("mockcustomer2");
		customerDitch = new MockMarketCustomer("mockcustomerditch");
		customerRich = new MockMarketCustomer("mockcustomerrich");
		market= new MarketAgent(clerk,deliveryMan,location,"Market");
		choice= new HashMap<String,Integer>();


		
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer will pay the exact check amount.
	 */
	public void testOneNormalCustomerScenario()
	{
		//add choices to order
		choice.put("steak", 2);
		choice.put("cookie", 2);
		choice.put("salad",2);
		market.setInventory(10, 10, 10, 10, 10);
		/*
		 * Normal one customer coming into store.
		 */
		//pre-initializing checks
		assertEquals("Clerk customer should be null",clerk.MCR, null);
		assertEquals("Clerk order should be null",clerk.o, null);
		assertEquals(
				"MockCustomer should have an empty event log before the Clerks's scheduler is called for the first time. Instead, the event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockCustomerRich should have an empty event log before the Market's scheduler is called for the first time. Instead, the MockClerks's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		//market sends initial message of customer to clerk
		clerk.msgTakeCustomer(customer, market);
		//check post-conditions of message
		assertTrue("Clerk should have logged \"Received msgTakeCustomer\". His log reads: " 
				+ clerk.log.getLastLoggedEvent().toString(), clerk.log.containsString("Received msgTakeCustomer from Market."));
		assertEquals("Clerk customer should be equal to customer",clerk.MCR, customer);
		assertEquals("Clerk order state should be asked for order",clerk.o.s,orderState.askedForOrder);
		// run the clerks's scheduler
		assertTrue("Clerk's scheduler should have returned true (needs to react to ask customer for order).", 
				clerk.pickAndExecuteAnAction());
		//check post scheduler
		assertEquals("Clerk order state should be waiting for order",clerk.o.s,orderState.waitingForOrder);
		// run the clerks's scheduler
		assertFalse("Clerk's scheduler should have returned false (needs to wait for order).", 
				clerk.pickAndExecuteAnAction());
		//customer sends message to clerk
		clerk.msgPlaceOrder(choice);
		//check post-conditions of message
		assertEquals("order choices should equal choice",clerk.o.Choices,choice);
		assertEquals("Clerk order state should be waiting",clerk.o.s,orderState.waiting);
		//run the clerk's scheduler
		assertTrue("Clerk's scheduler should have returned true (needs to react to place order from customer).", 
				clerk.pickAndExecuteAnAction());
		//check post scheduler
		assertEquals("amount owed should be amount time price",clerk.o.amountOwed,normAmount);
		assertEquals("Market inventory should go down",market.Inventory.get("steak"),amountLeft);
	}
	public void testOneNormalCookCustomerScenario()
	{
		/*
		 * Normal one cook delivery and customer coming into store.
		 */
		//pre-initializing checks
		
		
	}

}
