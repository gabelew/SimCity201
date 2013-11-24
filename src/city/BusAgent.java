package city;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

import city.gui.BusGui;
import agent.Agent;

public class BusAgent extends Agent{
	public List<MyBusStop> busStops = Collections.synchronizedList(new ArrayList<MyBusStop>());
	private BusGui busGui;
	private char type;
	public Semaphore waitingResponse = new Semaphore(0,true);
	Timer timer = new Timer();
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
	
	enum StopEvent {pickUp, onBus, dropOff}	
	enum State {none, atStop0, atStop1, atStop2, atStop3, goingToStop}
	private State state = State.none;
	
	public BusAgent(char type){
		this.type = type;
		
		
		
												//where customers will pile up
		Point busStation3 = new Point(30,65);	//(67, 85+80*0)
		Point busStation2 = new Point(30,145);	//(67, 85+80*1)
		Point busStation1 = new Point(30,225);	//(67, 85+80*2)
		Point busStation0 = new Point(30,305);	//(67, 85+80*3)
		Point busStation4 = new Point(825,65);	//(797, 85+80*0)
		Point busStation5 = new Point(825,145);	//(797, 85+80*1)
		Point busStation6 = new Point(825,225);	//(797, 85+80*2)
		Point busStation7 = new Point(825,305);	//(797, 85+80*3)
		
		if(this.type == 'B'){
			busStops.add(new MyBusStop(busStation0,0));
			busStops.add(new MyBusStop(busStation1,1));
			busStops.add(new MyBusStop(busStation2,2));
			busStops.add(new MyBusStop(busStation3,3));
		}else{
			busStops.add(new MyBusStop(busStation4,0));
			busStops.add(new MyBusStop(busStation5,1));
			busStops.add(new MyBusStop(busStation6,2));
			busStops.add(new MyBusStop(busStation7,3));
		}
		
		
	}
	
	public List<MyBusStop> getBusStops(){
		return busStops;
	}
	
	//Messages
	
	public void msgWaitingForBus(PersonAgent p, Point location){
		//for(MyBusStop b : busStops){
			//if()
			//if(b.location.getY() == busStop.getY()){
		MyBusStop b = findBusStop(location);	
		b.passengers.add(new MyPassenger(p, StopEvent.pickUp));
			//}
		///}
		if(getStateChangePermits()==0)
			stateChanged();
	}
	
	private MyBusStop findBusStop(Point location) {
		
		if(location.x < 115 && location.y < 115){
			return busStops.get(3);
		}else if(location.x < 115+80 && location.y < 115+80){
			return busStops.get(2);
		}else if(location.x < 115+80*2 && location.y < 115+80*2){
			return busStops.get(1);
		}else if(location.x < 115+80*3 && location.y < 115+80*3){
			return busStops.get(0);
		}else if(location.y < 115){
			return busStops.get(0);
		}else if(location.y < 115+80*1){
			return busStops.get(1);
		}else if(location.y < 115+80*2){
			return busStops.get(2);
		}else {//if(location.y < 115+80*3){
			return busStops.get(3);
		}
	}
	public void msgComingAboard(PersonAgent p, Point Destination){

		MyBusStop b = findBusStop(Destination);	
		
		b.passengers.add(new MyPassenger(p, StopEvent.dropOff));
		state = State.none;
		if(getStateChangePermits()==0)
			stateChanged();
		
	}
	
	public void msgAtStop(Point busStop){ //from animation
		for(MyBusStop b : busStops){
			if(b.location.equals(busStop)){
				
				if(b.stopnumber == 0){
					print("msg at stop 0");
					state = State.atStop0;
				}
				else if(b.stopnumber == 1){
					print("msg at stop 1");
					state = State.atStop1;
				}
				else if(b.stopnumber == 2){
					print("msg at stop 2");
					state = State.atStop2;
				}
				else if(b.stopnumber == 3){
					print("msg at stop 3");
					state = State.atStop3;
				}
			}
		}

		if(getStateChangePermits()==0)
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
			if(!b.passengers.isEmpty() && state != State.goingToStop){
				state = State.goingToStop;
				GoToBusStop(b);
				return true;
			}
		}
		
		if(state == State.none)
			goToRest();
		return false;
	}
	
	
	
	//Actions
	
	private void goToRest() {
		busGui.doGoToRest();
		
	}
	private void GoToBusStop(MyBusStop mbs){
		DoGoToBusStop(mbs.location);
	}
	
	private void DoGoToBusStop(Point p){
		busGui.GoToBusStop(p);
	}
	
	private void transferPeople(MyBusStop ms){
		timer.schedule(new TimerTask() {
			public void run() {
				waitingResponse.release();
			}
		}, 
		4000);
		List<MyPassenger> removePs = Collections.synchronizedList(new ArrayList<MyPassenger>());
		for(MyPassenger p : ms.passengers){
			if(p.stopEvent == StopEvent.dropOff){
				p.person.msgAtYourStop(busGui.xPos, busGui.yPos);
				removePs.add(p);
			}
		}	
		
		
		for(MyPassenger p : ms.passengers){
			if(p.stopEvent == StopEvent.pickUp){
				p.person.msgBusIshere();
				removePs.add(p);
			}
		}	
		
		for(MyPassenger p: removePs){
			 ms.passengers.remove(p);
		}
		
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = State.none;
	}
	
	
	
}
