package city;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import city.gui.BusGui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.interfaces.Bus;
import city.interfaces.Person;
import agent.Agent;

public class BusAgent extends Agent implements Bus{
	public List<MyBusStop> busStops = Collections.synchronizedList(new ArrayList<MyBusStop>());
	private BusGui busGui;
	private char type;
	public Semaphore waitingResponse = new Semaphore(0,true);
	Timer timer = new Timer();
	public String name;
	public EventLog log = new EventLog();
	public boolean testing = false;
	
    static final int B_BUS_STATION_X = 30;
    static final int F_BUS_STATION_X = 825;
    static final int BUS_STATION0_Y = 305;
    static final int BUS_STATION1_Y = 225;
    static final int BUS_STATION2_Y = 145;
    static final int BUS_STATION3_Y = 65;





	
	public BusGui getBusGui() {
		return busGui;
	}
	public void setBusGui(BusGui bg){
		this.busGui = bg;
	}
	public class MyBusStop{
		Point location;
		int stopnumber;
		public List<MyPassenger> passengers = Collections.synchronizedList(new ArrayList<MyPassenger>());
	
		MyBusStop(Point p, int sn){
			location = p;
			stopnumber = sn;
		}
		
		public Point getLocation(){
			return location;
		}

		public List<MyPassenger> getPassengers() {
			return passengers;
		}	
	}
	
	public class MyPassenger{
		Person person;
		public StopEvent stopEvent;
		
		MyPassenger(Person p, StopEvent se){
			person = p;
			stopEvent = se;
		}
		
		public StopEvent getStopEvent(){
			return stopEvent;
		}
	}
	
	public enum StopEvent {pickUp, onBus, dropOff}	
	enum State {none, atStop0, atStop1, atStop2, atStop3, goingToStop}
	private State state = State.none;
	
	public BusAgent(char type){
		this.type = type;
		
		if(type=='B'){
			this.name = "busLeft";
		}else if(type=='F'){
			this.name = "busRight";
		}
		
												//where customers will pile up
		Point busStation3 = new Point(B_BUS_STATION_X,BUS_STATION3_Y);	//(67, 85+80*0)
		Point busStation2 = new Point(B_BUS_STATION_X,BUS_STATION2_Y);	//(67, 85+80*1)
		Point busStation1 = new Point(B_BUS_STATION_X,BUS_STATION1_Y);	//(67, 85+80*2)
		Point busStation0 = new Point(B_BUS_STATION_X,BUS_STATION0_Y);	//(67, 85+80*3)
		Point busStation4 = new Point(F_BUS_STATION_X,BUS_STATION3_Y);	//(797, 85+80*0)
		Point busStation5 = new Point(F_BUS_STATION_X,BUS_STATION2_Y);	//(797, 85+80*1)
		Point busStation6 = new Point(F_BUS_STATION_X,BUS_STATION1_Y);	//(797, 85+80*2)
		Point busStation7 = new Point(F_BUS_STATION_X,BUS_STATION0_Y);	//(797, 85+80*3)
		
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
	
	public String getName(){
	       return name;
	}
	
	public List<MyBusStop> getBusStops(){
		return busStops;
	}
	
	//Messages
	
	public void msgWaitingForBus(Person p, Point location){
		AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msgWaitingForBus recieved from " +p.getName());
		log.add(new LoggedEvent("msgWaitingForBus recieved from " +p.getName()));
		MyBusStop b = findBusStop(location);	
		b.passengers.add(new MyPassenger(p, StopEvent.pickUp));
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
	public void msgComingAboard(Person p, Point Destination){
		AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msgComingAboard recieved from " +p.getName());
		log.add(new LoggedEvent("msgComingAboard recieved from " +p.getName()));
		MyBusStop b = findBusStop(Destination);	
		
		b.passengers.add(new MyPassenger(p, StopEvent.dropOff));
		state = State.none;
		if(getStateChangePermits()==0)
			stateChanged();
		
	}
	
	public void msgAtStop(Point busStop){ //from animation
		AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msgAtStop recieved");
		for(MyBusStop b : busStops){
			if(b.location.equals(busStop)){
				
				if(b.stopnumber == 0){
					AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msg at stop 0");
					state = State.atStop0;
				}
				else if(b.stopnumber == 1){
					AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msg at stop 1");
					state = State.atStop1;
				}
				else if(b.stopnumber == 2){
					AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msg at stop 2");
					state = State.atStop2;
				}
				else if(b.stopnumber == 3){
					AlertLog.getInstance().logMessage(AlertTag.BUS, getName(), "msg at stop 3");
					state = State.atStop3;
				}
			}
		}

		if(getStateChangePermits()==0)
			stateChanged();
	}
	
	//Scheduler
	
	public boolean pickAndExecuteAnAction() {
		
		if(state == State.atStop0 && !(busStops.get(0).passengers.isEmpty())){
			transferPeople(busStops.get(0));
			return true;
		}
		
		if(state == State.atStop1 && !(busStops.get(1).passengers.isEmpty())){
			transferPeople(busStops.get(1));
			return true;
		}	
		
		if(state == State.atStop2 && !(busStops.get(2).passengers.isEmpty())){
			transferPeople(busStops.get(2));
			return true;
		}	
		
		if(state == State.atStop3 && !(busStops.get(3).passengers.isEmpty())){
			transferPeople(busStops.get(3));
			return true;
		}
		
		
		for(MyBusStop b : busStops){
			if(!b.passengers.isEmpty() && state != State.goingToStop){
				state = State.goingToStop;
				GoToNextBusStop();
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
	private void GoToNextBusStop(){
		busGui.GoToNextBusStop();
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
				if(!testing){
					p.person.msgAtYourStop(busGui.xPos, busGui.yPos);
				}else{
					p.person.msgAtYourStop(30,145);
				}
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
