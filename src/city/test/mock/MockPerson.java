package city.test.mock;

import city.interfaces.Person;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import restaurant.test.mock.Mock;


public class MockPerson extends Mock implements Person {
	
	public EventLog log = new EventLog();

	public MockPerson(String name) {
		super(name);
	}

	@Override
	public void msgBusIshere() {
		log.add(new LoggedEvent("Recieved msgBusIsHere from the BusAgent"));
		
	}

	@Override
	public void msgAtYourStop(int xPos, int yPos) {
		log.add(new LoggedEvent("Recieved msgAtYourStop from the BusAgent"));
		
	}

}
