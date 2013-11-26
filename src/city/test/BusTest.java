package city.test;

import java.awt.Point;

import city.BusAgent;
import city.test.mock.MockPerson;
import junit.framework.TestCase;

public class BusTest extends TestCase {
	BusAgent bus;
	MockPerson person;
	
	public void setUp() throws Exception{
		super.setUp();
		bus = new BusAgent('B');
		person = new MockPerson("Bob");
	}
	
	
	public void testOnePersonPickUpAndDropOff(){
		//check preconditions
		assertEquals("Bus should have 4 busStops in it. It doesn't.", bus.getBusStops().size() , 4);
		assertEquals("MockPerson's log should be empty before Bus' scheduler is called. Instead, it reads: " + person.log.toString(), 0, person.log.size());

		//the person sends the bus a msg that hey are waiting at a BusStop
		bus.msgWaitingForBus(person, new Point(67, 85));
		
		assertEquals("The Bus' 4th BusStop should have a person in it. It doesn't.", bus.getBusStops().get(3).getPassengers().size() , 1);
		//assertEquals("The person's state should be pickUp. It isn't", bus.getBusStops().get(3).getPassengers().get(0).getStopEvent() , "pickUp");

		//animation sends the bus a msg when it is at the particular bustop
		bus.msgAtStop(new Point(30, 65));
		
		assertTrue("Bus scheduler should return true since the gui sent it a messege that it is at the bus stop. It doesn't.", bus.pickAndExecuteAnAction());
		assertTrue("MockPerson should have logged \"Received msgBusIsHere\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgBusIsHere from the BusAgent"));
		assertEquals("The Bus' 4th BusStop should have no persons in it. It does.", bus.getBusStops().get(3).getPassengers().size(), 0);
		
		//the person sends a msg to the bus when the bus has arrived and is coming aboard
		bus.msgComingAboard(person, new Point(67,165));
		
		/*assertEquals("The Bus' 2nd BusStop should have a person in it. It doesn't", bus.getBusStops().get(2).getPassengers().size(), 1);
		assertTrue("MockPerson should have logged \"Received msgAtYourStop\" but didn't. His log reads instead: " 
				+ person.log.getLastLoggedEvent().toString(), person.log.containsString("Recieved msgAtYourStop from the BusAgent"));
		
		bus.msgAtStop(new Point(30, 145));*/

		
		
	}
}
