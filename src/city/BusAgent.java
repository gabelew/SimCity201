package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;

public class BusAgent extends Agent{
	public List<MyBusStop> busStops = Collections.synchronizedList(new ArrayList<MyBusStop>());
	
	class MyBusStop{
		Point location;
		List<MyPassenger> passengers = Collections.synchronizedList(new ArrayList<MyPassenger>());
	}
	
	class MyPassenger{
		PersonAgent person;
		StopEvent stopEvent;
		
		MyPassenger(PersonAgent p, StopEvent se){
			person = p;
			stopEvent = se;
		}
	}
	
	enum StopEvent {pickUp, dropOff}
	MyBusStop atStop = null;
	
	
	BusAgent(){

	}
	
	
	//Messages
	
	public void msgWaitingForBus(PersonAgent p, Point busStop){
		for(MyBusStop b : busStops){
			if(b.location == busStop){
				b.passengers.add(new MyPassenger(p, StopEvent.pickUp));
			}
		}
		
		stateChanged();
	}
	
	public void msgComingAboard(PersonAgent p, Point Destination){
		for(MyBusStop b : busStops){
			if(b.location == Destination){
				b.passengers.add(new MyPassenger(p, StopEvent.dropOff));
			}
		}
		
		stateChanged();
	}
	
	public void msgAtStop(Point busStop){ //from animation
		for(MyBusStop b : busStops){
			if(b.location == busStop){
				atStop = b;
			}
		}	
		
		stateChanged();
	}
	
	//Scheduler
	
	protected boolean pickAndExecuteAnAction() {
		
		if(atStop != null){
			transferPeople();
		}
		
		
		return false;
	}
	
	
	
	//Actions
	
	private void transferPeople(){
		
	}
	
	
	
	
}
