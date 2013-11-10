package restaurant.test;

import restaurant.CashierAgent;
import restaurant.CashierAgent.BillState;
import restaurant.CashierAgent.OrderState;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the market.
 *
 * @author Chad Martin
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer, customer2, customerDitch, customerRich;
	MockMarket market, market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");
		customerDitch = new MockCustomer("mockcustomerditch");
		customerRich = new MockCustomer("mockcustomerrich");
		waiter = new MockWaiter("mockwaiter");	
		market = new MockMarket("mockmarket");	
		market2 = new MockMarket("mockmarket2");
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer will pay the exact check amount.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customer, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		assertEquals("Cashier should have 1 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 1);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));
		
		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
	
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		//send message to customer from waiter
		customer.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 15.99"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Order should contain cashIn = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 15.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
	
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 orders.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 15.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 15.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));
	
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 15.99, - o.check, 15.99"));
		
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end one normal customer scenario
	
	/**
	 * This tests the cashier under the following terms: two customers will pay the exact check amount.
	 */
	public void testTwoNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.
		customer2.cashier = cashier;
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customer, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		//Send the second message to cashier
		cashier.msgProduceCheck(waiter, customer2, "chicken");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler

		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = chicken"));
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
		"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
		"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to chicken. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
	
		
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to printing check. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));
		
		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-2).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
				assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
						cashier.pickAndExecuteAnAction());
				
				//Check post scheduler and pre timer conditions
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
				
				assertTrue("The order choice should be set to steak. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).customer == customer);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
				
				assertTrue("The order state should be set to deliver check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
						+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
				
				assertTrue("The order choice should be set to chicken. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).customer == customer2);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
				
				assertTrue("The order state should be set to printing check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
				
				assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 10.99"));
				
				i = 0;	
				while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
					if(i==100){
						assertTrue("We never recieved mgCheckPrinted.", false);
					}
					i++;
					try {
					    Thread.sleep(100);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
				
				//check post timer conditions and pre scheduler conditions
				assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
				
				assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 10.99"));
				
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
						cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
				
		//send message to customer from waiter
		customer.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 15.99"));
		
		assertEquals("We should have 2 order", 2, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("Order should contain cashIn = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).cashIn == 15.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.paymentRecieved);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 1 order.", 1, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 15.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 15.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 15.99, - o.check, 15.99"));

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 10.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer2. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);
		
		//send message to customer from waiter
		customer2.msgHereIsCheck(10.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 10.99"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);
		
		assertTrue("Order should contain cashIn = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 10.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
		
		assertTrue("MockCustomer2 should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 orders.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 10.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 10.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));
	
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 10.99, - o.check, 10.99"));

		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end two normal customer scenario
	
	/**
	 * This tests the cashier under the following terms: two customers will pay the exact check amount, and one market will be payed the exact bill amount .
	 */
	public void testTwoNormalCustomersOneMarketScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.
		customer2.cashier = cashier;
		waiter.cashier = cashier;
		market.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customer, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		//Send the second message to cashier
		cashier.msgProduceCheck(waiter, customer2, "chicken");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler

		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = chicken"));
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
		"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
		"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to chicken. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);		

		//Send the initial message, msgHereIsBill, to cashier
		cashier.msgHereIsBill(market, 275.00);
		
		// check post msgHereIsBill conditions and pre scheduler conditions

		assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 275.0"));
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
		"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
		"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to chicken. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to printing check. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-2).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		// run the cashier's scheduler
				assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
						cashier.pickAndExecuteAnAction());
				
				//Check post scheduler and pre timer conditions
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
				
				assertTrue("The order choice should be set to steak. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).customer == customer);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
				
				assertTrue("The order state should be set to deliver check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
						+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
				
				assertTrue("The order choice should be set to chicken. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).customer == customer2);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
				
				assertTrue("The order state should be set to printing check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
				
				assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 10.99"));
		
				assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
				
				assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-1).market == market);
				
				assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
				
				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

				assertEquals(
						"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
								+ market.log.toString(), 0, market.log.size());

				i = 0;	
				while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
					if(i==100){
						assertTrue("We never recieved mgCheckPrinted.", false);
					}
					i++;
					try {
					    Thread.sleep(100);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
				
				//check post timer conditions and pre scheduler conditions
				assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
				
				assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 10.99"));
				
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
						cashier.getStateChangePermits() > 0);

				assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
				
				assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-1).market == market);
				
				assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
				
				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

				assertEquals(
						"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
								+ market.log.toString(), 0, market.log.size());

		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		//send message to customer from waiter
		customer.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 15.99"));
		
		assertEquals("We should have 2 order", 2, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("Order should contain cashIn = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).cashIn == 15.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.paymentRecieved);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 1 order.", 1, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 15.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 15.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 15.99, - o.check, 15.99"));

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 10.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer2. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		//send message to customer from waiter
		customer2.msgHereIsCheck(10.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 10.99"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);
		
		assertTrue("Order should contain cashIn = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 10.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
		
		assertTrue("MockCustomer2 should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 orders.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 10.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 10.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));
				
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 10.99, - o.check, 10.99"));

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		savedBankBalance = cashier.bank;
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
				cashier.pickAndExecuteAnAction());		
		
		// check post scheduler conditions
		assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 275.0) + ", = bank " + savedBankBalance + ", - b.bill, "+ 275));

		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);
		
		assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

		assertEquals("We should have " + (savedBankBalance - 275.0) + " in the bank. Instead, we have " + cashier.bank,
				savedBankBalance - 275.0, cashier.bank);

		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end two normal customer One Market scenario

	/**
	 * This tests the cashier under very simple terms: one market will be payed the exact bill amount.
	 */
	public void testOneNormalMarketScenario(){
		//setUp() runs first before this test!
		
		market.cashier = cashier;//You can do almost anything in a unit test.
				
		//check preconditions for msgHereIsBill
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//Send the initial message, msgHereIsBill, to cashier
		cashier.msgHereIsBill(market, 275.00);
		
		// check post msgHereIsBill conditions and pre scheduler conditions

		assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 275.0"));
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		double savedBankBalance = cashier.bank;
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
				cashier.pickAndExecuteAnAction());		
		
		// check post scheduler conditions
		assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 275.0) + ", = bank " + savedBankBalance + ", - b.bill, "+ 275));

		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	

		assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

		assertEquals("We should have " + (savedBankBalance - 275.0) + " in the bank. Instead, we have " + cashier.bank,
				savedBankBalance - 275.0, cashier.bank);

		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end one normal market-cashier scenario

	
	/**
	 * This tests the cashier under the following terms: one customer will not pay for meal and then return "eat" and pay for both meals.
	 */
	public void testOneDitchingCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customerDitch.cashier = cashier;//You can do almost anything in a unit test.
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customerDitch, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertEquals("Cashier should have 1 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 1);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));
		
		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
	
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		//send message to customer from waiter
		customerDitch.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 0.0"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Order should contain cashIn = $0.0. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 0.0);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
	
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customerDitch.log.getLastLoggedEvent().toString(), customerDitch.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
			
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 1 order.", 1, cashier.orders.size());
		
		assertEquals("We should have " + savedBankBalance + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgPayMelater\" with the correct balance, but his last event logged reads instead: " 
				+ customerDitch.log.getLastLoggedEvent().toString(), customerDitch.log.containsString("Received msgPayMeLater from cashier. Go to ATM."));
	
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, -15.99, = o.cashIn, 0.0, - o.check, 15.99"));
		
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
		


		/***************************************************************************************************************/
		/******************* Start the customers second round through the restaurant ***********************************/

		/***************************************************************************************************************/
		cashier.log.clear();
		customerDitch.log.clear();
		waiter.log.clear();
		
		//check preconditions
		assertEquals("Cashier should have 1 orders in it. It doesn't.",cashier.orders.size(), 1);		
		
		assertTrue("The order state should be set to inDebt. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.inDebt);	
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customerDitch, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's msgProduceCheck is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's msgProduceCheck is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.inDebt);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);

		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.inDebt);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));
		
		i = 0;	
		while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 31.98"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());

		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 31.98"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 31.98"));
	
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customerDitch.log.toString(), 0, customerDitch.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $31.98. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 31.98);
		
		//send message to customer from waiter
		customerDitch.msgHereIsCheck(31.98);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 31.98"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $31.98. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 31.98);
		
		assertTrue("Order should contain cashIn = $31.98. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 31.98);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerDitch);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
	
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customerDitch.log.getLastLoggedEvent().toString(), customerDitch.log.containsString("Received msgHereIsCheck from cashier. Total = 31.98"));
			
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 order.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 31.98) + "in the bank. Instead, we have " + cashier.bank,
				31.98 + savedBankBalance, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customerDitch.log.getLastLoggedEvent().toString(), customerDitch.log.containsString("Received msgChange from cashier. Change = 0.0"));
	
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 31.98, - o.check, 31.98"));
		
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
		
		
	}//end one ditching customer scenario
	
	
	/**
	 * This tests the cashier under the following terms: one customer will pay more than the check amount.
	 */
	public void testOneRichCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customerRich.cashier = cashier;//You can do almost anything in a unit test.
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customerRich, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerRich);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
			
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		
		assertEquals("Cashier should have 1 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 1);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerRich);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));
		
		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
	
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customerRich.log.toString(), 0, customerRich.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerRich);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		//send message to customer from waiter
		customerRich.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 16.0"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 15.99);
		
		assertTrue("Order should contain cashIn = $16.0. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 16.0);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customerRich);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customerRich.log.getLastLoggedEvent().toString(), customerRich.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));
	
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 orders.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 15.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 15.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customerRich.log.getLastLoggedEvent().toString(), customerRich.log.containsString("Received msgChange from cashier. Change = 0.0"));
	
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.01, = o.cashIn, 16.0, - o.check, 15.99"));
		
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end one rich customer scenario
	
	
	/**
	 * This tests the cashier under the following terms: two markets will be payed the exact bill amount.
	 */
	public void testTwoNormalMarketScenario(){
		//setUp() runs first before this test!
		
		market.cashier = cashier;//You can do almost anything in a unit test.
		market2.cashier = cashier;
			
		//check preconditions for msgHereIsBill
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());
		
		//Send the initial message, msgHereIsBill, to cashier
		cashier.msgHereIsBill(market, 275.00);
		
		// check post msgHereIsBill conditions and pre scheduler conditions

		assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 275.0"));
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertEquals(
				"MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());
		
		//Send the second message, msgHereIsBill, to cashier from market2
				cashier.msgHereIsBill(market2, 95.00);
				
				// check post msgHereIsBill conditions and pre scheduler conditions

				assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 95.0"));
				
				assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
				
				assertEquals(
						"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
								+ market.log.toString(), 0, market.log.size());
				assertEquals(
						"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
								+ market2.log.toString(), 0, market2.log.size());

				assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-2).market == market);
				
				assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
				
				assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-1).market == market2);
				
				assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
				
				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
				
				assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
						cashier.getStateChangePermits() > 0);
				
		double savedBankBalance = cashier.bank;
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
				cashier.pickAndExecuteAnAction());		

		// check post scheduler conditions
		assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 275.0) + ", = bank " + savedBankBalance + ", - b.bill, "+ 275));


		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);	

		assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());
		
		assertEquals("We should have " + (savedBankBalance - 275.0) + " in the bank. Instead, we have " + cashier.bank,
				savedBankBalance - 275.0, cashier.bank);

		// run the cashier's scheduler
				assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
						cashier.pickAndExecuteAnAction());		


			// check post scheduler conditions
			assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
					+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 95.0 - 275.0) + ", = bank " + (savedBankBalance - 275.0) + ", - b.bill, "+ 95.0));

			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	

			assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

			assertTrue("MockMarket2 should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
					+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Received msgPayment from cashier. Total: 95.0"));
			
			assertEquals("We should have " + (savedBankBalance - 275.0 - 95.0) + " in the bank. Instead, we have " + cashier.bank,
					savedBankBalance - 275.0 - 95.0, cashier.bank);
	
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end two normal market-cashier scenario


	
	
	/**
	 * This tests the cashier under the following terms: two customers will pay the exact check amount, and one market will be payed the exact bill amount.
	 * And the second market will ask for payment in the middle the first customer of paying for a check.
	 */
	public void testTwoNormalCustomersTwoMarketsScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.
		customer2.cashier = cashier;
		waiter.cashier = cashier;
		market.cashier = cashier;
		market2.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProduceCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		//Send the initial message to cashier
		cashier.msgProduceCheck(waiter, customer, "steak");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler
		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = steak"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.orders.size(), 1);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		//Send the second message to cashier
		cashier.msgProduceCheck(waiter, customer2, "chicken");//send the message from a waiter

		//check postconditions for message reception of msgProduceCheck and preconditions for scheduler

		assertTrue("Cashier should have logged \"Received msgProduceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgProduceCheck from waiter. Choice = chicken"));
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
		"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
		"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to chicken. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);		

		//Send the initial message, msgHereIsBill, to cashier
		cashier.msgHereIsBill(market, 275.00);
		
		// check post msgHereIsBill conditions and pre scheduler conditions

		assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 275.0"));
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);
		
		assertEquals("Cashier should have 2 orders in it. It doesn't.", cashier.orders.size(), 2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
		"MockCustomer should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
		"MockCustomer2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.requested);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("The order choice should be set to chicken. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Check post scheduler and pre timer conditions
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("The order state should be set to printing check. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.printingCheck);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 15.99"));

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		int i = 0;	
		while(cashier.orders.get(cashier.orders.size()-2).state != OrderState.deliverCheck){
			if(i==100){
				assertTrue("We never recieved mgCheckPrinted.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		//check post timer conditions and pre scheduler conditions
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
		
		assertTrue("The order state should be set to deliverCheck. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
		
		assertTrue("The order state should be set to requested. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.requested);
		
		assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 15.99"));
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());

		assertEquals(
				"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());	
		
		// run the cashier's scheduler
				assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's msgProduceCheck), but didn't.", 
						cashier.pickAndExecuteAnAction());
				
				//Check post scheduler and pre timer conditions
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertEquals("Cashier should have 2 order in it. Instead it has " + cashier.orders.size(), cashier.orders.size(), 2);
				
				assertTrue("The order choice should be set to steak. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).choice, cashier.orders.get(cashier.orders.size()-2).choice == "steak");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).customer == customer);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
				
				assertTrue("The order state should be set to deliver check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
						+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
				
				assertTrue("The order choice should be set to chicken. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

				assertTrue("The order's customer should be set to customer. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).customer == customer2);

				assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
						cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
				
				assertTrue("The order state should be set to printing check. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.printingCheck);
				
				assertTrue("Cashier should have logged \"Performed produceCheck\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed produceCheck. Check = 10.99"));
		
				assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
				
				assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-1).market == market);
				
				assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
				
				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

				assertEquals(
						"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
								+ market.log.toString(), 0, market.log.size());
				assertEquals(
						"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
								+ market2.log.toString(), 0, market2.log.size());	
				
				i = 0;	
				while(cashier.orders.get(cashier.orders.size()-1).state != OrderState.deliverCheck){
					if(i==100){
						assertTrue("We never recieved mgCheckPrinted.", false);
					}
					i++;
					try {
					    Thread.sleep(100);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
				
				//check post timer conditions and pre scheduler conditions
				assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-2).state, cashier.orders.get(cashier.orders.size()-2).state == OrderState.deliverCheck);
				
				assertTrue("The order state should be set to deliverCheck. Instead, it is " 
						+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);
				
				assertTrue("Cashier should have logged \"Received msgCheckPrinted\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgCheckPrinted from cashier. Total of check = 10.99"));
				
				assertEquals(
						"MockWaiter should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 0, waiter.log.size());
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				assertEquals(
						"MockCustomer2 should have an empty event log after the Cashier's timer is called for the first time. Instead, the MockCustomer2's event log reads: "
								+ customer2.log.toString(), 0, customer2.log.size());
				
				assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
						cashier.getStateChangePermits() > 0);

				assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
				
				assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
						cashier.bills.get(cashier.bills.size()-1).market == market);
				
				assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
						+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
				
				assertTrue("The bill state should be set to requested. Instead, it is " 
						+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

				assertEquals(
						"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
								+ market.log.toString(), 0, market.log.size());
				assertEquals(
						"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
								+ market2.log.toString(), 0, market2.log.size());
				
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 15.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 2 order from waiter", 2, cashier.orders.size());

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());
		
		//send message to customer from waiter
		customer.msgHereIsCheck(15.99);
		
		//check Post msgPayment conditions and Pre msgHereIsBill conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 15.99"));
		
		assertEquals("We should have 2 order", 2, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("Order should contain cashIn = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).cashIn == 15.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.paymentRecieved);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		
		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 275.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());	
		
		
		//Send the second message, msgHereIsBill, to cashier from market2
		cashier.msgHereIsBill(market2, 95.00);
		
		// check post msgHereIsBill conditions and pre scheduler conditions

		assertTrue("Cashier should have logged \"Received msgHereIsBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsBill from market. Total of Bill = 95.0"));
		
		assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-2).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
		
		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		
		assertEquals("We should have 2 order", 2, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);
		
		assertTrue("Order should contain cashIn = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).cashIn == 15.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).customer == customer);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-2).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-2).state == OrderState.paymentRecieved);
		
		assertTrue("Order should contain a order of price = $15.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-2).check, cashier.orders.get(cashier.orders.size()-2).check == 15.99);

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsCheck from cashier. Total = 15.99"));

		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		
		double savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 1 order.", 1, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 15.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 15.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));

		assertEquals(
				"MockCustomer2 should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
			
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 15.99, - o.check, 15.99"));

		assertTrue("The order state should be set to deliverCheck. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.deliverCheck);

		assertTrue("The order choice should be set to steak. Instead, it is " 
				+ cashier.orders.get(cashier.orders.size()-1).choice, cashier.orders.get(cashier.orders.size()-1).choice == "chicken");

		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-2).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
		
		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to cashiers's msgCheckPrinted), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check post scheduler conditions and pre msgPayment Conditions 
		assertTrue("The order state should be set to awaitingPayment. Instead, it is "
				+ cashier.orders.get(cashier.orders.size()-1).state, cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);
		
		assertTrue("Cashier should have logged \"Performed giveWaiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed giveWaiter. Total of check = 10.99"));
		
		assertTrue("MockWaiter should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertEquals(
				"MockCustomer2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer2's event log reads: "
						+ customer2.log.toString(), 0, customer2.log.size());
		
		assertEquals("We should have 1 order from waiter", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order with state == awaitingPayment. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.awaitingPayment);

		assertTrue("The order's customer should be set to customer2. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);
		
		assertTrue("Order should contain a order of price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);

		assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-2).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
		
		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		//send message to customer from waiter
		customer2.msgHereIsCheck(10.99);
		
		//check Post msgPayment conditions and Pre scheduler conditions
		assertTrue("Cashier should have logged \"Received msgPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Total cash in = 10.99"));
		
		assertEquals("We should have 1 order", 1, cashier.orders.size());
		
		assertTrue("Order should contain a order price = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).check == 10.99);
		
		assertTrue("Order should contain cashIn = $10.99. It contains something else instead: $" 
				+ cashier.orders.get(cashier.orders.size()-1).check, cashier.orders.get(cashier.orders.size()-1).cashIn == 10.99);
		
		assertTrue("The order's customer should be set to customer. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).customer == customer2);

		assertTrue("The order's waiter should be set to waiter. Instead, it is not." , 
				cashier.orders.get(cashier.orders.size()-1).waiter == waiter);

		assertTrue("Order should contain a order with state == paymentRecieved. It doesn't.",
				cashier.orders.get(cashier.orders.size()-1).state == OrderState.paymentRecieved);
		
		assertTrue("MockCustomer2 should have logged an event for receiving \"Received msgHereIsCheck\" with the correct balance, but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHereIsCheck from cashier. Total = 10.99"));
	
		assertTrue("The cashier's stateChange semaphore should have positive permit. Instead, it is has " +  cashier.getStateChangePermits(), 
				cashier.getStateChangePermits() > 0);

		assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-2).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
		
		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		
		
		savedBankBalance = cashier.bank;
		
		//run the cashier's scheduler
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		// check post scheduler conditions
		assertEquals("We should have 0 orders.", 0, cashier.orders.size());
		
		assertEquals("We should have " + (savedBankBalance + 10.99) + "in the bank. Instead, we have " + cashier.bank,
				savedBankBalance + 10.99, cashier.bank);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received msgChange\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgChange from cashier. Change = 0.0"));
				
		assertTrue("Cashier should have logged \"Performed processPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed processPayment. Cash out, 0.0, = o.cashIn, 10.99, - o.check, 10.99"));

		assertEquals("Cashier should have 2 bill in it. It doesn't.",cashier.bills.size(), 2);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-2).market == market);
		
		assertTrue("Bill should contain a bill of price = $275.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-2).bill, cashier.bills.get(cashier.bills.size()-2).bill == 275.0);

		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-2).state, cashier.bills.get(cashier.bills.size()-2).state == BillState.requested);
		
		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);
		
		
		savedBankBalance = cashier.bank;
		
		// run the cashier's scheduler
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
				cashier.pickAndExecuteAnAction());		
		
		// check post scheduler conditions
		assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 275.0) + ", = bank " + savedBankBalance + ", - b.bill, "+ 275));

		assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

		assertEquals("We should have " + (savedBankBalance - 275.0) + " in the bank. Instead, we have " + cashier.bank,
				savedBankBalance - 275.0, cashier.bank);

		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);
		
		assertEquals(
				"MockMarket2 should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockMarket2's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());

		assertTrue("The bill's waiter should be set to market2. Instead, it is not." , 
				cashier.bills.get(cashier.bills.size()-1).market == market2);
		
		assertTrue("Bill should contain a bill of price = $95.0. It contains something else instead: $" 
				+ cashier.bills.get(cashier.bills.size()-1).bill, cashier.bills.get(cashier.bills.size()-1).bill == 95.0);
		
		assertTrue("The bill state should be set to requested. Instead, it is " 
				+ cashier.bills.get(cashier.bills.size()-1).state, cashier.bills.get(cashier.bills.size()-1).state == BillState.requested);

		savedBankBalance = cashier.bank;
		
		// run the cashier's scheduler
				assertTrue("Cashier's scheduler should have returned true (needs to react to market's msgHereIsBill), but didn't.", 
						cashier.pickAndExecuteAnAction());		
				
				// check post scheduler conditions
				assertTrue("Cashier should have logged \"Performed payBill\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Performed payBill. new bank balance, " + (savedBankBalance - 95.0) + ", = bank " + (savedBankBalance) + ", - b.bill, "+ 95.0));

				assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
				assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);	

				assertTrue("MockMarket should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
						+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayment from cashier. Total: 275.0"));

				assertTrue("MockMarket2 should have logged an event for receiving \"Received msgPayment\" with the correct balance, but his last event logged reads instead: " 
						+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Received msgPayment from cashier. Total: 95.0"));
				
				assertEquals("We should have " + (savedBankBalance - 95.0) + " in the bank. Instead, we have " + cashier.bank,
						savedBankBalance - 95.0, cashier.bank);
				
		assertFalse("cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	}//end two normal customer One Market scenario


}
