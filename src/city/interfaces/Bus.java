package city.interfaces;

import java.awt.Point;

import city.PersonAgent;
import city.gui.BusGui;

public interface Bus {
	
	public abstract void msgWaitingForBus(PersonAgent p, Point location);
	
	public abstract void msgComingAboard(PersonAgent p, Point Destination);
	
	public abstract void msgAtStop(Point busStop);
	
	public abstract BusGui getBusGui();

}
