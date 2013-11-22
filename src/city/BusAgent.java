package city;

import java.awt.Point;
import java.util.*;

import city.gui.BusGui;
import agent.Agent;

public class BusAgent extends Agent{
	public List<MyBusStop> busStops = Collections.synchronizedList(new ArrayList<MyBusStop>());
	private BusGui busGui;
	private char type;
	public BusGui getBusGui() {
		return busGui;
	}
	public void setBusGui(BusGui bg){
		this.busGui = bg;
	}
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
	enum State {none, atStop0, atStop1, atStop2, atStop3}
	private State state = State.none;
	
	public BusAgent(char type){
		this.type = type;
		
		
		
												//where customers will pile up
		Point busStation0 = new Point(30,65);	//(67, 85+80*0)
		Point busStation1 = new Point(30,145);	//(67, 85+80*1)
		Point busStation2 = new Point(30,225);	//(67, 85+80*2)
		Point busStation3 = new Point(30,305);	//(67, 85+80*3)
		Point busStation4 = new Point(825,65);	//(797, 85+80*0)
		Point busStation5 = new Point(825,145);	//(797, 85+80*1)
		Point busStation6 = new Point(825,225);	//(797, 85+80*2)
		Point busStation7 = new Point(825,305);	//(797, 85+80*3)
		
		busStops.add(new MyBusStop(busStation0,0));
		busStops.add(new MyBusStop(busStation1,1));
		busStops.add(new MyBusStop(busStation2,2));
		busStops.add(new MyBusStop(busStation3,3));
		busStops.add(new MyBusStop(busStation4,4));
		busStops.add(new MyBusStop(busStation5,5));
		busStops.add(new MyBusStop(busStation6,6));
		busStops.add(new MyBusStop(busStation7,7));
		
		
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
				
				if(b.stopnumber == 0 || b.stopnumber == 4){
					state = State.atStop0;
				}
				else if(b.stopnumber == 1 || b.stopnumber == 5){
					state = State.atStop1;
				}
				else if(b.stopnumber == 2 || b.stopnumber == 6){
					state = State.atStop2;
				}
				else if(b.stopnumber == 3 || b.stopnumber == 7){
					state = State.atStop3;
				}
				
			}
		}
		
		stateChanged();
	}
	
	//Scheduler
	
	protected boolean pickAndExecuteAnAction() {
		
		if(state == State.atStop0){
			transferPeople(busStops.get(0));
			return true;
		}
		
		if(state == State.atStop1){
			transferPeople(busStops.get(1));
			return true;
		}	
		
		if(state == State.atStop2){
			transferPeople(busStops.get(2));
			return true;
		}	
		
		if(state == State.atStop3){
			transferPeople(busStops.get(3));
			return true;
		}
		
		
		
		for(MyBusStop b : busStops){
			if(!b.passengers.isEmpty()){
				GoToBusStop(b);
				return true;
			}
		}
		
		
		return false;
	}
	
	
	
	//Actions
	
	private void GoToBusStop(MyBusStop mbs){
		print("Going to busStop " + mbs.stopnumber );
		DoGoToBusStop(mbs.location);
	}
	
	private void DoGoToBusStop(Point p){
		busGui.GoToBusStop(p);
	}
	
	private void transferPeople(MyBusStop ms){
		for(MyPassenger p : ms.passengers){
			if(p.stopEvent == StopEvent.dropOff){
				p.person.msgAtYourStop();
			}
		}	
		
		for(MyPassenger p : ms.passengers){
			if(p.stopEvent == StopEvent.pickUp){
				p.person.msgBusIshere();
			}
		}	
		
	}
	
	
	
}
