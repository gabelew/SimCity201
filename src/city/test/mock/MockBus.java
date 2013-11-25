package city.test.mock;

import java.awt.Point;

import city.PersonAgent;
import city.gui.BusGui;
import city.interfaces.Bus;
import city.interfaces.Person;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import restaurant.test.mock.Mock;

public class MockBus extends Mock implements Bus{

	public EventLog log = new EventLog();
	
	public MockBus(String name) {
		super(name);
	}

	@Override
	public void msgWaitingForBus(Person p, Point location) {
		log.add(new LoggedEvent("Recieved msgWaitingForBus from the PersonAgent at locatoin " + location));
		
	}

	@Override
	public void msgComingAboard(Person p, Point Destination) {
		log.add(new LoggedEvent("Recieved msgComingAboard from the PersonAgent with destination " + Destination));
		
	}

	@Override
	public void msgAtStop(Point busStop) {
		log.add(new LoggedEvent("Recieved msgAtStop from the BusGui, at Busstop " + busStop));
		
	}

	@Override
	public BusGui getBusGui() {
		// TODO Auto-generated method stub
		return null;
	}

}
