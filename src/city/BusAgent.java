package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;

public class BusAgent extends Agent{
	public List<MyBusStop> busStops = Collections.synchronizedList(new ArrayList<MyBusStop>());
	
	public class MyBusStop{
		Point location;
		int stopnumber;
		List<MyPassenger> passengers = Collections.synchronizedList(new ArrayList<MyPassenger>());
	
		MyBusStop(Point p, int sn){
			location = p;
			stopnumber = sn;
		}
		
		public Point getLocation(){
			return location;
		}
		
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
	
	
	public BusAgent(){
		
		Point busStation1 = new Point(30,65);
		Point busStation2 = new Point(30,145);
		Point busStation3 = new Point(30,225);
		Point busStation4 = new Point(30,305);
		Point busStation5 = new Point(825,65);
		Point busStation6 = new Point(825,145);
		Point busStation7 = new Point(825,225);
		Point busStation8 = new Point(825,305);
		
		busStops.add(new MyBusStop(busStation1,1));
		busStops.add(new MyBusStop(busStation2,2));
		busStops.add(new MyBusStop(busStation3,3));
		busStops.add(new MyBusStop(busStation4,4));
		busStops.add(new MyBusStop(busStation5,5));
		busStops.add(new MyBusStop(busStation6,6));
		busStops.add(new MyBusStop(busStation7,7));
		busStops.add(new MyBusStop(busStation8,8));
	}
	
	public List<MyBusStop> getBusStops(){
		return busStops;
	}
	
	//Messages
	
	public void msgWaitingForBus(PersonAgent p, Point busStop){
		for(MyBusStop b : busStops){
			if(b.location.getY() == busStop.getY()){
				b.passengers.add(new MyPassenger(p, StopEvent.pickUp));
			}
		}
		
		stateChanged();
	}
	
	public void msgComingAboard(PersonAgent p, Point Destination){
		for(MyBusStop b : busStops){
			if(b.location.getY() == Destination.getY()){
				b.passengers.add(new MyPassenger(p, StopEvent.dropOff));
			}
		}
		
		stateChanged();
	}
	
	public void msgAtStop(Point busStop){ //from animation
		//print("msg at stop");
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
			return true;
		}
		
		
		return false;
	}
	
	
	
	//Actions
	
	private void transferPeople(){
		MyBusStop s = atStop;
		atStop = null;
		
		for(MyPassenger p : s.passengers){
			if(p.stopEvent == StopEvent.dropOff){
				p.person.msgAtYourStop();
			}
		}	
		
		for(MyPassenger p : s.passengers){
			if(p.stopEvent == StopEvent.pickUp){
				p.person.msgBusIshere();
			}
		}	
		
	}
	
	
	
	
}
