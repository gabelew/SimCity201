package city.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import bank.BankBuilding;
import market.interfaces.MarketCustomer;
import market.test.mock.MockClerk;
import market.test.mock.MockDeliveryMan;
import market.test.mock.MockMarket;
import market.test.mock.MockMarketCustomer;
import atHome.city.Residence;
import junit.framework.TestCase;
import city.MarketAgent;
import city.PersonAgent;
import city.PersonAgent.State;
import city.PersonAgent.Task;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.roles.MarketCustomerRole;
import city.roles.Role;
import city.test.mock.*;

public class PersonAgentTest extends TestCase{
	PersonAgent person, person2;
	MockDeliveryMan deliveryManRole;
	MockClerk clerkRole;
	
	BankBuilding bankBuilding = new BankBuilding(new Point(337,68));
	
	SimCityGui simCityGui= new SimCityGui();
	
	
	Residence h;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		

		h = new MockHome(new Point(297,68));
		person = new PersonAgent("personAgent",100, simCityGui, h );
		person2 = new PersonAgent("personAgent",20, simCityGui, h );

		PersonGui pgui = new PersonGui(person, simCityGui);
		person.setGui(pgui);
		pgui.setPresent(true);
		simCityGui.animationPanel.addGui(pgui);
		
		deliveryManRole = new MockDeliveryMan("personDeliveryManJob");
		clerkRole = new MockClerk("personClerkJob");

		for(MarketAgent m: simCityGui.getMarkets()){
			person.addMarket(m);
		}
		person.testing = true;
	}
	public void testMarketCustomerIntoMarket(){
		//setUp() runs first before this test!
		// check preconditions
		assertEquals("personAgent should have an empty event log before the personAgent's getFoodFromMarket is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());
		assertEquals("personAgent should have 0 items in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 0, person.taskList.size());
		
		//set up msgGetFoodFromMarket
		Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
		toOrderFromMarket.put("steak", 4);
		
		//call msgGetFoodFromMarket
		person.msgGetFoodFromMarket(toOrderFromMarket);

		assertTrue("personAgent should have logged \"Received msgGetFoodFromMarket added goToMarket to tasklist\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Received msgGetFoodFromMarket added goToMarket to tasklist"));
		
		assertEquals("personAgent should have 1 item in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 1, person.taskList.size());
		
		
		//Run Person agents scheduler
		person.pickAndExecuteAnAction();
		
		//Check post scheduler conditions and pre scheduler conditions
		assertTrue("personAgent should have logged \"Called goToMarket\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Called goToMarket from scheduler going to closest market"));
		
		assertEquals("personAgent should be going to closest market. Instead, the personAgents destination is: ("
				+ person.destination.x + ", " + person.destination.y + ")" , new Point(417, 68), person.destination);
		
		//Run Person agents scheduler
		person.pickAndExecuteAnAction();
		
		
		assertTrue("personAgent should have logged \"Received startShopping in MarketCustomer role.\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("received start shopping from person"));

		assertEquals("personAgent should be in state Shopping. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.shopping);
		int i =0;
		while(person.personGui.xPos != 417 || person.personGui.yPos != 68){
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
		
		assertEquals("personAgent should be at to closest market. Instead, the personAgents location is: ("
				+ person.destination.x + ", " + person.destination.y + ")" , new Point(417, 68), new Point(person.personGui.xPos,person.personGui.yPos) );
		
		MarketCustomerRole mcr = null;
		boolean hasMarketRole = false;
		for(Role r: person.roles){
			if(r instanceof MarketCustomerRole){
				mcr = (MarketCustomerRole) r;
				hasMarketRole = true;
			}
		}
		
		if(hasMarketRole){
			assertTrue("personAgent's Market customer role should be active. Instead it is false", mcr.active);
			assertTrue("personAgent's Market customer role gui should be present. Instead it is false", mcr.getMarketCustomerGui().isPresent());
		}else{
			assertTrue("No marketCustomer was added.", false);
		}
	}
	
	public void testBankCustomerIntoBankTest(){
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("personAgent should have an empty event log before the personAgent's getFoodFromMarket is called. Instead, the personAgent's event log reads: "
				+ person2.log.toString(), 0, person2.log.size());
		assertEquals("personAgent should have 0 items in task list. Instead, the personAgents task list has: "
				+ person2.taskList.size(), 0, person2.taskList.size());
		
		//set up msgGetFoodFromMarket
		Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
		toOrderFromMarket.put("steak", 4);
				
		//call msgGetFoodFromMarket
		person2.msgGetFoodFromMarket(toOrderFromMarket);

		//check post msg call and prescheduler conditions
		assertTrue("personAgent should have logged \"Received msgGetFoodFromMarket added goToMarket to tasklist\" but didn't. His log reads instead: " 
						+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("Received msgGetFoodFromMarket added goToMarket to tasklist"));
		
		assertEquals("personAgent should have 2 items in task list. Instead, the personAgents task list has: "
						+ person2.taskList.size(), 2, person2.taskList.size());
	
		
		//run scheduler
		person2.pickAndExecuteAnAction();
		
		//check post conditions and pre conditions
		assertEquals( "personAgent should be in state goingToBank", person2.state == State.goingToBank);
		
	}
	public void testPersonIntoAndOutOfHomeToRest() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("personAgent should have an empty event log before the personAgent's nextHour is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());
		assertEquals("personAgent should have 0 items in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 0, person.taskList.size());
		
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
