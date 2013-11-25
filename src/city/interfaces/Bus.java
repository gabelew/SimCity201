package city.interfaces;

import java.awt.Point;

import city.PersonAgent;
import city.gui.BusGui;

public interface Bus {
	
	public abstract void msgWaitingForBus(Person p, Point location);
	
	public abstract void msgComingAboard(Person p, Point Destination);
	
	public abstract void msgAtStop(Point busStop);
	
	public abstract BusGui getBusGui();

}
