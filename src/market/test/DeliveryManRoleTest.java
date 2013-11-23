package market.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import city.MarketAgent;
import city.roles.DeliveryManRole;
import city.roles.DeliveryManRole.orderState;
import market.test.mock.*;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the MarketAgent's basic interaction
 * with Cashiers,Cooks, customers, and the Delivery man.
 *
 * @author Emily Bernstein
 */
public class DeliveryManRoleTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	MarketAgent market;
	MockMarketCustomer customer, customer2, customerDitch, customerRich;
	MockClerk clerk;
	DeliveryManRole deliveryMan;
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
		clerk = new MockClerk("clerk");
		deliveryMan=new DeliveryManRole();
		customer = new MockMarketCustomer("mockcustomer");
		customer2 = new MockMarketCustomer("mockcustomer2");
		customerDitch = new MockMarketCustomer("mockcustomerditch");
		customerRich = new MockMarketCustomer("mockcustomerrich");
		market= new MarketAgent(location,"Market");
		choice= new HashMap<String,Integer>();
		cook= new MockCook("cook");


		
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
		assertEquals("Delivey man cook customer should be null",deliveryMan.cook, null);
		assertEquals("Delivery man order state should be no order",deliveryMan.o.s,orderState.noOrder);
		
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
		deliveryMan.msgTakeCustomer(cook, market);
		//check post-conditions of message
		assertTrue("Delivery Man should have logged \"Received msgTakeCustomer\". His log reads: " 
				+ deliveryMan.log.getLastLoggedEvent().toString(), deliveryMan.log.containsString("Received msgTakeCustomer from Market."));
		assertEquals("Delivery man cook customer should be equal to cook",deliveryMan.cook, cook);
		assertEquals("Delivery order state should be asked for order",deliveryMan.o.s,orderState.askedForOrder);
		// run the Delivery man's scheduler
		assertTrue("Delivery man's scheduler should have returned true (needs to react to ask cook for order).", 
				deliveryMan.pickAndExecuteAnAction());
		//check post scheduler
		assertEquals("Delivery order state should be waiting for order",deliveryMan.o.s,orderState.waitingForOrder);
		// run the clerks's scheduler
		assertFalse("Delivery man's scheduler should have returned false (needs to wait for order).", 
				deliveryMan.pickAndExecuteAnAction());
		//customer sends message to clerk
		deliveryMan.msgHereIsOrder(choice);
		//check post-conditions of message
		assertEquals("order choices should equal choice",deliveryMan.o.Choices,choice);
		assertEquals("Delivery order state should be waiting",deliveryMan.o.s,orderState.waiting);
		//run the clerk's scheduler
		assertTrue("Delivery Man's scheduler should have returned true (needs to react to place order from customer).", 
				deliveryMan.pickAndExecuteAnAction());
		//check post scheduler
		assertEquals("amount owed should be amount time price",deliveryMan.o.amountOwed,normAmount);
		assertEquals("Market inventory should go down",market.Inventory.get("steak"),amountLeft);
		assertEquals("Market inventory should go down",market.Inventory.get("cookie"),amountLeft);
		assertEquals("Market inventory should go down",market.Inventory.get("salad"),amountLeft);
		assertEquals("Delivery order state should be ordered",deliveryMan.o.s,orderState.ordered);
		assertTrue("Dellvery man's scheduler should have returned false (waiting for payment).", 
				deliveryMan.pickAndExecuteAnAction());
		assertEquals("Delivery order state should be waiting for payment",deliveryMan.o.s,orderState.waitingForPayment);
		//customer sends payment message
		deliveryMan.msgHereIsPayment(normAmount,cashier);
		//check post-conditions of message
		assertEquals("Delivery order state should be payed",deliveryMan.o.s,orderState.payed);
		//run the delivery's scheduler
		assertTrue("Delivery man's scheduler should have returned true (needs to react to payment).", 
				deliveryMan.pickAndExecuteAnAction());
		//check post-conditions of scheduler
		assertEquals("Delivery order state should be done",deliveryMan.o.s,orderState.noOrder);
		assertEquals("cook should be null",deliveryMan.cook,null);
		//run the clerk's scheduler
		assertFalse("Delivery Man's scheduler should have returned false.", 
				deliveryMan.pickAndExecuteAnAction());
	}


}