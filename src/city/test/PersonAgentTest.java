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
import city.BusAgent;
import city.MarketAgent;
import city.PersonAgent;
import city.PersonAgent.MyJob;
import city.PersonAgent.State;
import city.PersonAgent.Task;
import city.PersonAgent.TransportState;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.roles.AtHomeRole;
import city.roles.BankCustomerRole;
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

		PersonGui pgui2 = new PersonGui(person2, simCityGui);
		person2.setGui(pgui2);
		pgui2.setPresent(true);
		simCityGui.animationPanel.addGui(pgui2);
		
		deliveryManRole = new MockDeliveryMan("personDeliveryManJob");
		clerkRole = new MockClerk("personClerkJob");
		
		person.testing = true;
		person.addAtHomeRole();
		
		for(MarketAgent m: simCityGui.getMarkets()){
			person.addMarket(m);
			person2.addMarket(m);
		}

		for(BankBuilding b: simCityGui.getBanks()){
			person.addBank(b);
			person2.addBank(b);
		}
		
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
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person.pickAndExecuteAnAction());
		
		//Check post scheduler conditions and pre scheduler conditions
		assertTrue("personAgent should have logged \"Called goToMarket\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Called goToMarket from scheduler going to closest market"));
		
		assertEquals("personAgent should be going to closest market. Instead, the personAgents destination is: ("
				+ person.destination.x + ", " + person.destination.y + ")" , new Point(417, 68), person.destination);
		
		//Run Person agents scheduler
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person.pickAndExecuteAnAction());
		
		//Check post scheduler conditions
		assertTrue("personAgent should have logged \"Received startShopping in MarketCustomer role.\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("received start shopping from person"));

		assertEquals("personAgent should be in state Shopping. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.shopping);
		int i =0;
		while(person.personGui.xPos != 417 || person.personGui.yPos != 68){
			if(i==200){
				assertTrue("We never recieved destination. (" + person.personGui.xPos +" , "+ person.personGui.yPos + ")" , false);
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
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person2.pickAndExecuteAnAction());
		
		//check post conditions and pre conditions
		assertEquals( "personAgent should be in state goingToBank", person2.state, State.goingToBank);
		
		assertEquals("personAgent should be at to closest bank. Instead, the personAgents location is: ("
				+ person2.destination.x + ", " + person2.destination.y + ")" , new Point(377, 68), new Point(person2.personGui.xPos,person.personGui.yPos) );
				
		//Run Person agents scheduler
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person2.pickAndExecuteAnAction());

		//Check post scheduler conditions
		assertTrue("personAgent should have logged \"Recieved goingToBank from person Agent.\" but didn't. His log reads instead: " 
				+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("Recieved goingToBank from person Agent."));

		assertEquals("personAgent should be in state banking. Instead, his state is " + person2.state.toString(), person2.state, PersonAgent.State.banking);
		int i =0;
		while(person2.personGui.xPos != 377 || person2.personGui.yPos != 68){
			if(i==100){
				assertTrue("We never reached destination.", false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
				
		assertEquals("personAgent should be at to closest bank. Instead, the personAgents location is: ("
				+ person2.destination.x + ", " + person2.destination.y + ")" , new Point(377, 68), new Point(person2.personGui.xPos,person2.personGui.yPos) );
				
		BankCustomerRole mcr = null;
		boolean hasBankRole = false;
		for(Role r: person2.roles){
			if(r instanceof BankCustomerRole){
				mcr = (BankCustomerRole) r;
				hasBankRole = true;
			}
		}
				
		if(hasBankRole){
			assertTrue("personAgent's Bank customer role should be active. Instead it is false", mcr.active);
			assertTrue("personAgent's Bank customer role gui should be present. Instead it is false", mcr.getGui().isPresent());
		}else{
			assertTrue("No bankCustomer was added.", false);
		}
		
	}
	public void testPersonIntoAndOutOfHomeToRest() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("personAgent should have an empty event log before the personAgent's nextHour is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());
		assertEquals("personAgent should have 0 items in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 0, person.taskList.size());
		
		//run the scheduler
		assertFalse("Person's scheduler should have returned false , but didn't.", 
				person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"Recieved goToHomePos from myPerson.\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved goToHomePos from myPerson."));

		assertEquals("personAgent should be in state inHome. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.inHome);
		assertEquals("personAgent should be in location atHome. Instead, his state is " + person.state.toString(), person.location, PersonAgent.Location.AtHome);

		AtHomeRole mcr = null;
		boolean hasAtHomeRole = false;
		for(Role r: person.roles){
			if(r instanceof AtHomeRole){
				mcr = (AtHomeRole) r;
				hasAtHomeRole = true;
			}
		}
				
		if(hasAtHomeRole){
			assertTrue("personAgent's atHome customer role should be active. Instead it is false", mcr.active);
			assertTrue("personAgent's atHome customer role gui should be present. Instead it is false", mcr.getGui().isPresent());
		}else{
			assertTrue("No atHomeRole was added.", false);
		}
		
		//set up msgGetFoodFromMarket
				Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
				toOrderFromMarket.put("steak", 4);
				
		//call msgGetFoodFromMarket to get person out of house
		person.msgGetFoodFromMarket(toOrderFromMarket);

		assertTrue("personAgent should have logged \"Received msgGetFoodFromMarket added goToMarket to tasklist\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Received msgGetFoodFromMarket added goToMarket to tasklist"));
		
		assertEquals("personAgent should have 1 item in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 1, person.taskList.size());

		assertEquals("personAgent should be in state inHome. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.inHome);
		assertEquals("personAgent should be in location atHome. Instead, his state is " + person.state.toString(), person.location, PersonAgent.Location.AtHome);

		if(hasAtHomeRole){
			assertTrue("personAgent's atHome customer role should be active. Instead it is false", mcr.active);
			assertTrue("personAgent's atHome customer role gui should be present. Instead it is false", mcr.getGui().isPresent());
		}else{
			assertTrue("No atHomeRole was added.", false);
		}
		
		//Run Person agents scheduler to reactivate person
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person.pickAndExecuteAnAction());
		
		assertTrue("personAgent should have logged \"Received msgHasLeftHome from atHome Role.\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Received msgHasLeftHome from atHome Role."));
	
		assertEquals("personAgent should have 1 item in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 1, person.taskList.size());

		assertEquals("personAgent should be in state doingNothing. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.doingNothing);
		assertEquals("personAgent should be in location InCity. Instead, his state is " + person.state.toString(), person.location, PersonAgent.Location.InCity);

		if(hasAtHomeRole){
			assertFalse("personAgent's atHome customer role should not be active. Instead it is false", mcr.active);
			assertFalse("personAgent's atHome customer role gui should not be present. Instead it is false", mcr.getGui().isPresent());
		}else{
			assertTrue("No atHomeRole was added.", false);
		}
		
	}
	
	public void testPersonGoToBusStop0Left() {
		//setUp() runs first before this test!

		person.job = person.new MyJob(simCityGui.getRestaurants().get(2).location , "waiter", PersonAgent.Shift.day);
		// check preconditions
		assertEquals("personAgent should have an empty event log before the personAgent's getFoodFromMarket is called. Instead, the personAgent's event log reads: "
				+ person.log.toString(), 0, person.log.size());
		assertEquals("personAgent should have 0 items in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 0, person.taskList.size());
		
		//send NextHour msg
		person.msgNextHour(1, "Monday");
	
		//check post msg conditions and pre scheduler conditions
		assertEquals("personAgent should have 2 items in task list. Instead, the personAgents task list has: "
				+ person.taskList.size(), 2, person.taskList.size());
		assertTrue("personAgent should have goToWork in task list, but it doesn't.", person.taskList.get(0)== PersonAgent.Task.goToWork );
		assertTrue("personAgent should have goEatFood in task list, but it doesn't.", person.taskList.get(1)== PersonAgent.Task.goEatFood );
		
		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", 
				person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preforming Go to bus stop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming Go to bus stop"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		
		int i =0;
		while(person.personGui.xPos != 67 || person.personGui.yPos != 85){
			if(i==100){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertEquals("personAgent should be in transportation state WaitingForBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.WaitingForBus);
		
		assertTrue("personAgent should have logged \"msgWaitingForBus recieved from personAgent\" but didn't. His log reads instead: " 
				+ ((BusAgent)person.busLeft).log.getLastLoggedEvent().toString(), ((BusAgent)person.busLeft).log.containsString("msgWaitingForBus recieved from personAgent"));
		
		
		i =0;
		while(person.transportState != TransportState.GettingOnBus){
			if(i==100){
				assertTrue("We never recieved msg bus is here." , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		
		assertTrue("personAgent should have logged \"Recieved bus is here msg\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved bus is here msg"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state GettingOnBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.GettingOnBus);
		
		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		
		assertTrue("personAgent should have logged \"Recieved bus is here msg\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved bus is here msg"));
		assertTrue("personAgent should have logged \"msgWaitingForBus recieved from personAgent\" but didn't. His log reads instead: " 
				+ ((BusAgent)person.busLeft).log.getLastLoggedEvent().toString(), ((BusAgent)person.busLeft).log.containsString("msgWaitingForBus recieved from personAgent"));
		
	}
}
