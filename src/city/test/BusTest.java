package city.test;

import java.awt.Point;

import city.BusAgent;
import city.test.mock.MockPerson;
import junit.framework.TestCase;

public class BusTest extends TestCase {
	BusAgent bus;
	MockPerson person;
	MockPerson person2;
	
	public void setUp() throws Exception{
		super.setUp();
		bus = new BusAgent('B');
		bus.testing = true;
		person = new MockPerson("Bob");
		person2 = new MockPerson("Fred");
	}
	
	
	public void testOnePersonPickUpAndDropOff(){
		//check preconditions
		assertEquals("Bus should have 4 busStops in it. It doesn't.", bus.getBusStops().size() , 4);
		assertEquals("MockPerson's log should be empty before Bus' scheduler is called. Instead, it reads: " + person.log.toString(), 0, person.log.size());

		//the person sends the bus a msg that hey are waiting at a BusStop
		bus.msgWaitingForBus(person, new Point(67, 85));
		
		assertEquals("The Bus' 4th BusStop should have a person in it. It doesn't.", bus.getBusStops().get(3).getPassengers().size() , 1);

		//animation sends the bus a msg when it is at the particular bustop
		bus.msgAtStop(new Point(30, 65));
		
		assertTrue("Bus scheduler should return true since the gui sent it a messege that it is at the bus stop. It doesn't.", bus.pickAndExecuteAnAction());
		assertTrue("MockPerson should have logged \"Received msgBusIsHere\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgBusIsHere from the BusAgent"));
		assertEquals("The Bus' 4th BusStop should have no persons in it. It does.", bus.getBusStops().get(3).getPassengers().size(), 0);
		
		//the person sends a msg to the bus when the bus has arrived and is coming aboard
		bus.msgComingAboard(person, new Point(67,165));
		
		assertEquals("The Bus' 3rd BusStop should have a person in it. It doesn't", bus.getBusStops().get(2).getPassengers().size(), 1);
		
		//animation sends the bus a msg when it is at the particular bustop
		bus.msgAtStop(new Point(30, 145));
		
		assertTrue("Bus scheduler should return true since the gui sent it a messege that it is at the bus stop. It doesn't.", bus.pickAndExecuteAnAction());
		assertTrue("MockPerson should have logged \"Received msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop from the BusAgent"));
		assertEquals("The Bus' 3rd BusStop should have no person in it. It does.", bus.getBusStops().get(2).getPassengers().size(), 0);
		
	}
	
	public void testTwoPersonsSameStopPickUpAndDropOff(){
		//check preconditions
		assertEquals("Bus should have 4 busStops in it. It doesn't.", bus.getBusStops().size() , 4);
		assertEquals("MockPerson's log should be empty before Bus' scheduler is called. Instead, it reads: " + person.log.toString(), 0, person.log.size());
		assertEquals("MockPerson's log should be empty before Bus' scheduler is called. Instead, it reads: " + person2.log.toString(), 0, person2.log.size());

		//the person sends the bus a msg that hey are waiting at a BusStop
		bus.msgWaitingForBus(person, new Point(67, 85));
		bus.msgWaitingForBus(person2, new Point(67, 85));
				
		assertEquals("The Bus' 4th BusStop should have two persons in it. It doesn't.", bus.getBusStops().get(3).getPassengers().size() , 2);

		//animation sends the bus a msg when it is at the particular bustop
		bus.msgAtStop(new Point(30, 65));
				
		assertTrue("Bus scheduler should return true since the gui sent it a messege that it is at the bus stop. It doesn't.", bus.pickAndExecuteAnAction());
		assertTrue("MockPerson should have logged \"Received msgBusIsHere\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgBusIsHere from the BusAgent"));
		assertTrue("MockPerson should have logged \"Received msgBusIsHere\" but didn't. His log reads instead: " 
				+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("Recieved msgBusIsHere from the BusAgent"));
		assertEquals("The Bus' 4th BusStop should have no persons in it. It does.", bus.getBusStops().get(3).getPassengers().size(), 0);
				
		//the person sends a msg to the bus when the bus has arrived and is coming aboard
		bus.msgComingAboard(person, new Point(67,165));
		bus.msgComingAboard(person2, new Point(67,165));
				
		assertEquals("The Bus' 3rd BusStop should have two persons in it. It doesn't", bus.getBusStops().get(2).getPassengers().size(), 2);
				
		//animation sends the bus a msg when it is at the particular bustop
		bus.msgAtStop(new Point(30, 145));
				
		assertTrue("Bus scheduler should return true since the gui sent it a messege that it is at the bus stop. It doesn't.", bus.pickAndExecuteAnAction());
		assertTrue("MockPerson should have logged \"Received msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop from the BusAgent"));
		assertTrue("MockPerson should have logged \"Received msgAtYourStop\" but didn't. His log reads instead: " 
				+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("Recieved msgAtYourStop from the BusAgent"));
		assertEquals("The Bus' 3rd BusStop should have no persons in it. It does.", bus.getBusStops().get(2).getPassengers().size(), 0);
			
		
	}

}
