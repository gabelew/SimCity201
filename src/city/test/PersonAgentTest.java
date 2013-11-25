package city.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import bank.BankBuilding;
import market.test.mock.MockClerk;
import market.test.mock.MockDeliveryMan;
import market.test.mock.MockMarket;
import market.test.mock.MockMarketCustomer;
import atHome.city.Residence;
import junit.framework.TestCase;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.test.mock.*;

public class PersonAgentTest extends TestCase{
	MockMarket market;
	PersonAgent person;
	MockBankCustomer bankCustomer;
	MockMarketCustomer marketCustomer;
	MockAtHomeRole atHomeRole;
	MockDeliveryMan deliveryManRole;
	MockClerk clerkRole;
	
	BankBuilding bankBuilding = new BankBuilding(new Point(337,68));
	
	SimCityGui simCityGui= new SimCityGui();
	
	
	Residence h;
	//MockPerson person
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		

		h = new MockHome(new Point(297,68));
		market = new MockMarket("mockMarket");
		person = new PersonAgent("personAgent",100, simCityGui, h );

		PersonGui pgui = new PersonGui(person, simCityGui);
		person.setGui(pgui);
		
		marketCustomer = new MockMarketCustomer("personMarketCustomeRole");
		bankCustomer = new MockBankCustomer("personBankCustomerRole");
		atHomeRole = new MockAtHomeRole("personAtHomeRole");
		deliveryManRole = new MockDeliveryMan("personDeliveryManJob");
		clerkRole = new MockClerk("personClerkJob");

		for(MarketAgent m: simCityGui.getMarkets()){
			person.addMarket(m);
		}
		person.testing = true;
	}
	public void testMarketCustomerIntoandOutOfMarketTest(){
		//setUp() runs first before this test!
		// check preconditions
		assertEquals("personMarketCustomeRole should have an empty event log before the personAgents's getFoodFromMarket is called. Instead, the personMarketCustomeRole's event log reads: "
				+ marketCustomer.log.toString(), 0, marketCustomer.log.size());

		assertEquals("personAgent should have an empty event log before the personAgent's getFoodFromMarket is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());
		
		Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
		toOrderFromMarket.put("steak", 4);
		
		person.msgGetFoodFromMarket(toOrderFromMarket);

		assertEquals("personMarketCustomeRole should have an empty event log before the personAgents's getFoodFromMarket is called. Instead, the personMarketCustomeRole's event log reads: "
				+ marketCustomer.log.toString(), 0, marketCustomer.log.size());

		assertTrue("personAgent should have logged \"Received msgGetFoodFromMarket added goToMarket to tasklist\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Received msgGetFoodFromMarket added goToMarket to tasklist"));
		
		assertEquals("personAgent should have 1 item in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 1, person.taskList.size());
		
		//Run Person agents scheduler
		person.pickAndExecuteAnAction();

		assertEquals("personMarketCustomeRole should have an empty event log after the personAgents's scheduler is called. Instead, the personMarketCustomeRole's event log reads: "
				+ marketCustomer.log.toString(), 0, marketCustomer.log.size());
		
		assertTrue("personAgent should have logged \"Called goToMarket\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Called goToMarket from scheduler going to closest market"));
		
		assertEquals("personAgent should be going to closest market. Instead, the personAgents destination is: ("
				+ person.destination.x + ", " + person.destination.y + ")" , new Point(417, 68), person.destination);
		

		//Run Person agents scheduler
		person.pickAndExecuteAnAction();

	}
	public void testBankCustomerIntoandOutOfBankTest(){
		//setUp() runs first before this test!
		
		// check preconditions
		
	}
	public void testPersonIntoAndOutOfHomeToRest() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("personAtHomeRole should have an empty event log before the personAgents's nextHour is called. Instead, the personAtHomeRole's event log reads: "
				+ atHomeRole.log.toString(), 0, atHomeRole.log.size());

		assertEquals("personAgent should have an empty event log before the personAgent's nextHour is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());

		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		
		
		/**
		 * Step 2: Bank gets a message from a customer to check their balance
		 */
		
		
	}
	
	/*public void testOneCustomerOpenAccountAndCheckBalance() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", person.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler is called. Instead, it reads: " + bankCustomer.log.toString(), 0, bankCustomer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", person.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", person.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
	/*	person.msgOpenAccount(bankCustomer, 480.55, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", person.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", person.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", bankCustomer.equals(person.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(person.accounts.get(0).accountType));
		assertTrue("Account balance should contain 480.55 in it. It doesn't.", 0 == Double.compare(480.55, person.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", person.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler is called. Instead, it reads: " + bankCustomer.log.toString(), 0, bankCustomer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to check their balance
		 */
		/*person.msgCheckBalance(bankCustomer, "personal");
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", person.transactions.size(), 1);
		assertTrue("Transaction should have a state checkBalance. It doesn't.", TransactionState.checkBalance.equals(person.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", bankCustomer.equals(person.transactions.get(0).bankCustomer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to return customer's balance. It doesn't.", person.pickAndExecuteAnAction());
		
		assertTrue("MockBankCustomer should have logged \"Received msgHereIsBalance\" but didn't. His log reads instead: " 
				+ bankCustomer.log.getLastLoggedEvent().toString(), bankCustomer.log.containsString("Received msgHereIsBalance from "
						+ "Bank for account type: personal Current balance: 480.55"));
		assertEquals("Bank should have no transactions in it. It does.", person.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", person.pickAndExecuteAnAction());	
	}*/
	
}
