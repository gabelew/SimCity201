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
import city.BusAgent.StopEvent;
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

public class PersonAgentBusLeftTest extends TestCase{
	PersonAgent person, person2;
	
	BankBuilding bankBuilding = new BankBuilding(new Point(337,68));
	
	SimCityGui simCityGui= new SimCityGui(true);
	
	
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
	
	public void testPersonGoToBusStop3LeftThenDroppedOfAtBusStop2() {
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
			if(i==1000){
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

		assertEquals("BusAgent's bus stop 0 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.size(), 1);
		
		assertEquals("BusAgent's bus stop 0 should have passenger where state is pickup. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent, StopEvent.pickUp);
		
		
		i =0;
		while(person.transportState != TransportState.GettingOnBus){
			if(i==1000){
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

		
		assertTrue("personAgent should have logged \"preforming get on Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get on Bus"));
		assertTrue("busAgent should have logged \"msgComingAboard recieved from personAgent\" but didn't. His log reads instead: " 
				+ ((BusAgent)person.busLeft).log.getLastLoggedEvent().toString(), ((BusAgent)person.busLeft).log.containsString("msgComingAboard recieved from personAgent"));
		
		
		i =0;
		while(person.personGui.xPos != 35 || person.personGui.yPos != 85){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	
		assertEquals("personAgent should be in transportation state OnBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.OnBus);
		
		assertEquals("BusAgent's bus stop 2 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(2).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(2).passengers.size(), 1);
		
		assertEquals("BusAgent's bus stop 2 should have passenger where state is dropOff. Instead it has " + ((BusAgent)person.busLeft).busStops.get(2).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(2).passengers.get(0).stopEvent, StopEvent.dropOff);
		
		

		i =0;
		while(person.transportState != TransportState.GettingOffBus){
			if(i==200){
				assertTrue("We never recieved msg bus is here." , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertTrue("personAgent should have logged \"Recieved msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state GettingOffBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.GettingOffBus);
		
		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preforming get off Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get off Bus"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);

		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preformed finish going to work\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preformed finish going to work"));
		
		i =0;
		while(person.personGui.xPos != 337 || person.personGui.yPos != 148){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertEquals("personAgent should be in state working. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.working);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);
		assertEquals("personAgent should be in location state atWork. Instead, his state is " + person.transportState.toString(), person.location, PersonAgent.Location.AtWork);
	}

	public void testPersonGoToBusStop3LeftThenDroppedOfAtBusStop0() {
		//setUp() runs first before this test!

		person.job = person.new MyJob(simCityGui.getRestaurants().get(4).location , "waiter", PersonAgent.Shift.day);
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
			if(i==1000){
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

		assertEquals("BusAgent's bus stop 0 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.size(), 1);
		
		assertEquals("BusAgent's bus stop 0 should have passenger where state is pickup. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent, StopEvent.pickUp);
		
		
		i =0;
		while(person.transportState != TransportState.GettingOnBus){
			if(i==1000){
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

		
		assertTrue("personAgent should have logged \"preforming get on Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get on Bus"));
		assertTrue("busAgent should have logged \"msgComingAboard recieved from personAgent\" but didn't. His log reads instead: " 
				+ ((BusAgent)person.busLeft).log.getLastLoggedEvent().toString(), ((BusAgent)person.busLeft).log.containsString("msgComingAboard recieved from personAgent"));
		
		
		i =0;
		while(person.personGui.xPos != 35 || person.personGui.yPos != 85){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	
		assertEquals("personAgent should be in transportation state OnBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.OnBus);
		
		assertEquals("BusAgent's bus stop 0 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(0).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(0).passengers.size(), 1);
		
		assertEquals("BusAgent's bus stop 0 should have passenger where state is dropOff. Instead it has " + ((BusAgent)person.busLeft).busStops.get(0).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(0).passengers.get(0).stopEvent, StopEvent.dropOff);
		
		

		i =0;
		while(person.transportState != TransportState.GettingOffBus){
			if(i==200){
				assertTrue("We never recieved msg bus is here." , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertTrue("personAgent should have logged \"Recieved msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state GettingOffBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.GettingOffBus);
		
		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preforming get off Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get off Bus"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);

		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preformed finish going to work\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preformed finish going to work"));
		
		i =0;
		while(person.personGui.xPos != 337 || person.personGui.yPos != 308){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertEquals("personAgent should be in state working. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.working);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);
		assertEquals("personAgent should be in location state atWork. Instead, his state is " + person.transportState.toString(), person.location, PersonAgent.Location.AtWork);
		

	}
	
	public void testPersonGoToBusStop3LeftThenDroppedOfAtBusStop1() {
		//setUp() runs first before this test!

		person.job = person.new MyJob(simCityGui.getMarkets().get(2).location , "clerk", PersonAgent.Shift.day);
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
			if(i==1000){
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
		
		assertEquals("BusAgent's bus stop 0 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.size(), 1);
		
		assertEquals("BusAgent's bus stop 0 should have passenger where state is pickup. Instead it has " + ((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(3).passengers.get(0).stopEvent, StopEvent.pickUp);
		
		
		i =0;
		while(person.transportState != TransportState.GettingOnBus){
			if(i==1000){
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

		
		assertTrue("personAgent should have logged \"preforming get on Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get on Bus"));
		assertTrue("busAgent should have logged \"msgComingAboard recieved from personAgent\" but didn't. His log reads instead: " 
				+ ((BusAgent)person.busLeft).log.getLastLoggedEvent().toString(), ((BusAgent)person.busLeft).log.containsString("msgComingAboard recieved from personAgent"));
		
		
		i =0;
		while(person.personGui.xPos != 35 || person.personGui.yPos != 85){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	
		assertEquals("personAgent should be in transportation state OnBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.OnBus);

		assertEquals("BusAgent's bus stop 1 should have 1 passenger. Instead it has " + ((BusAgent)person.busLeft).busStops.get(1).passengers.size(),
				((BusAgent)person.busLeft).busStops.get(1).passengers.size(), 1);
		assertEquals("BusAgent's bus stop 1 should have passenger where state is dropOff. Instead it has " + ((BusAgent)person.busLeft).busStops.get(1).passengers.get(0).stopEvent.toString(),
				((BusAgent)person.busLeft).busStops.get(1).passengers.get(0).stopEvent, StopEvent.dropOff);
		
		

		i =0;
		while(person.transportState != TransportState.GettingOffBus){
			if(i==200){
				assertTrue("We never recieved msg bus is here." , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertTrue("personAgent should have logged \"Recieved msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state GettingOffBus. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.GettingOffBus);
		
		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preforming get off Bus\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preforming get off Bus"));
		assertEquals("personAgent should be in state going to work. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.goingToWork);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);

		//run scheduler 
		assertTrue("Person's scheduler should have returned true , but didn't.", person.pickAndExecuteAnAction());

		assertTrue("personAgent should have logged \"preformed finish going to work\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("preformed finish going to work"));
		
		i =0;
		while(person.personGui.xPos != 337 || person.personGui.yPos != 228){
			if(i==1000){
				assertTrue("We never reached destination. Instead, the current position is : (" + person.personGui.xPos + ", " + person.personGui.yPos + ")" , false);
			}
			i++;
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		assertEquals("personAgent should be in state working. Instead, his state is " + person.state.toString(), person.state, PersonAgent.State.working);
		assertEquals("personAgent should be in transportation state none. Instead, his state is " + person.transportState.toString(), person.transportState, PersonAgent.TransportState.none);
		assertEquals("personAgent should be in location state atWork. Instead, his state is " + person.transportState.toString(), person.location, PersonAgent.Location.AtWork);
		

	}
}
