package city.interfaces;

import java.awt.Point;

import city.PersonAgent;

public interface Bus {
	
	public abstract void msgWaitingForBus(PersonAgent p, Point location);
	
	public abstract void msgComingAboard(PersonAgent p, Point Destination);
	
	public abstract void msgAtStop(Point busStop);

}
