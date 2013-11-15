package city;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.sql.Time;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import restaurant.CashierAgent;
import restaurant.HostAgent;
import restaurant.gui.CustomerGui;
import agent.Agent;
import city.gui.PersonGui;
import city.roles.*;

public class PersonAgent extends Agent {
	private List<Role> roles = new ArrayList<Role>();
	//hacked in upon creation
	private List<MyRestaurant> restaurants = new ArrayList<MyRestaurant>(); 
	private List<MyBank> banks = new ArrayList<MyBank>(); 
	private List<MyMarket> markets = new ArrayList<MyMarket>(); 
	private List<MyBusStop> busStops = new ArrayList<MyBusStop>(); 
	private List<Task> taskList = new ArrayList<Task>(); 
	private Semaphore waitingResponse = new Semaphore(0,true);
	private PersonGui personGui;
	

	enum Task {goToMarket, goEatFood, goToWork, goToBank, goToBankNow, doPayRent, doPayEmployees, offWorkBreak, onWorkBreak};
	enum State { doingNothing, goingOutToEat, goingHomeToEat, eating, goingToWork, working, goingToMarket, shopping, goingToBank, banking, onWorkBreak, offWorkBreak };
	enum Location { AtHome, AtWork, AtMarket, AtBank, InCity, AtRestaurant};
	enum TransportState { none, GoingToBus, WaitingForBus, OnBus, GettingOffBus, GettingOnBus};
	boolean isRenter;
	boolean isManager;
	
	private String name;
	public BufferedImage car = null;
	//Bus busLeft;
	//Bus busRight;
	MyJob job;
	private State state = State.doingNothing;
	private Location location = Location.InCity;
	private TransportState transportState = TransportState.none;
	private Point destination;
	
	Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
	
	//Time currentTime;
	int currentHour;
	String dayOfWeek;
	
	public int hungerLevel = 51;
	public double cashOnHand, businessFunds;
	
	class MyRestaurant {
		//Restaurant r; 
		HostAgent h;
		CashierAgent c;
		Point location;
		String type;
		String name;
		CustomerRole cr;
		MyRestaurant(HostAgent h, CashierAgent c, Point location, String type, String name) {
			this.h = h;
			this.c = c;
			this.location = location;
			this.type = type;
			this.name = name;
		}
	}
	class MyBank {
		Point location;
		String name;
		//BankCustomerRole bcr;
	}
	class MyMarket {
		Point location;
		String type;
		String name;
		//List<item> inventory;
	}
	class MyBusStop {
		Point location;
	}
	class MyJob{
		Point location;
		String type;
		Shift shift;
		Role role;
	}
	enum Shift {day, night}
	
	public PersonAgent(String name, double cash) {
	    this.name = name;
	    this.cashOnHand = cash;
	}
	
	public void addRole(Role r) {
        roles.add(r);
        r.setPerson(this);
	}
	
	public void addRestaurant(HostAgent h, CashierAgent c, Point location, String type, String name) {
		restaurants.add(new MyRestaurant(h, c, location, type, name));
	}
	
	public void setName(String name){
        this.name = name;
	}
	
	public String getName(){
       return name;
	}
	
	// messages
	public void msgTransferSuccessful(PersonAgent recipient, double amount, String purpose) {

	}
	public void msgTransferFailure(PersonAgent recipient, double amount, String purpose) {
		// get a loan, attempt transfer again
	}
	public void msgTransferCompleted(PersonAgent sender, double amount, String purpose) {

	}
	
	public void msgHereIsBalance(double amount, String accountType) {
		
	}

	public void msgNextHour(int hour, String dayOfWeek) {
		this.currentHour = hour;
		this.dayOfWeek = dayOfWeek;
		this.hungerLevel += 1;
		/*
		if((job.shift == Shift.day && location != Location.AtWork && (currentHour >= 20 || currentHour <= 11)) ||
				(job.shift == Shift.night && location != Location.AtWork && (currentHour >= 12 || currentHour <= 3))){
			boolean inList = false;
			for(Task t: taskList){
				if(t == Task.goToWork)
					inList = true;
			}
			if(!inList){
				taskList.add(Task.goToWork);
			}
		}*/
		print("newhour");
		if(hour == 23 && isRenter){
			boolean inList = false;
			for(Task t: taskList){
				if(t == Task.doPayRent)
					inList = true;
			}
			if(!inList){
				taskList.add(Task.doPayRent);
			}
		}
		
		if(hour == 23 && isManager){
			boolean inList = false;
			for(Task t: taskList){
				if(t == Task.doPayEmployees)
					inList = true;
			}
			if(!inList){
				taskList.add(Task.doPayEmployees);
			}
		}
		
		if(hungerLevel > 50 && state != State.goingOutToEat && state != State.eating && state != State.goingHomeToEat){
			boolean inList = false;
			for(Task t: taskList){
				if(t == Task.goEatFood)
					inList = true;
			}
			if(!inList){
				print("\t\t\t\tAHHHHHHHHHHHHHHHHH");
				taskList.add(Task.goEatFood);
			}
		} 
		stateChanged();
	}
	
	public void msgGoBackToWork(){
		boolean inList = false;
		for(Task t: taskList){
			if(t == Task.offWorkBreak)
				inList = true;
		}
		if(!inList){
			taskList.add(Task.offWorkBreak);
		}
	}

	
	public void msgTakingBreak(){
		boolean inList = false;
		for(Task t: taskList){
			if(t == Task.onWorkBreak)
				inList = true;
		}
		if(!inList){
			taskList.add(Task.onWorkBreak);
		}
		stateChanged();
	}
	
	public void msgNoMoreFood(){
		boolean inList = false;
		for(Task t: taskList){
			if(t == Task.goToMarket)
				inList = true;
		}
		if(!inList){
			taskList.add(Task.goToMarket);
		}
		stateChanged();
	}
	
	public void msgGetFoodFromMarket(Map<String,Integer> toOrderFromMarket){
		if(this.toOrderFromMarket != null){
        	Iterator<Entry<String, Integer>> it = toOrderFromMarket.entrySet().iterator();
        	while (it.hasNext()) {
        		Entry<String, Integer> pairs = it.next();
        		if(this.toOrderFromMarket.get(pairs.getKey()) == null){
        			this.toOrderFromMarket.put(pairs.getKey(), pairs.getValue());
        		}else{
        			Integer temp = this.toOrderFromMarket.get(pairs.getKey());
        			this.toOrderFromMarket.put(pairs.getKey(), (pairs.getValue() + temp));
        		}
		            it.remove(); // avoids a ConcurrentModificationException
		        }
		}else{
			this.toOrderFromMarket = toOrderFromMarket;
		}
		stateChanged();
	}
	
	public void msgDepositBusinessCash(){
		boolean inList = false;
		for(Task t: taskList){
			if(t == Task.goToBankNow)
				inList = true;
		}
		if(!inList){
			taskList.add(Task.goToBankNow);
		}
		stateChanged();
	}
	public void msgReenablePerson(){
		//gui.setPresent();
	}
	public void msgBusIshere(){
		transportState = TransportState.GettingOnBus;
		stateChanged();
	}
	public void msgAtYourStop(){
		transportState = TransportState.GettingOffBus;
		stateChanged();
	}
	public void msgDoneWorking(){
		state = State.doingNothing;
		/*Role temp = null;
		for(Role r: roles){
			if(r instanceof job.role){
				
			}
		}
		
		*/
		stateChanged();
	}
	
	public void msgDoneEatingAtRestaurant() {
		print("msgDoneEatingAtRestaurant");
		state = State.doingNothing;
		for(Task t: taskList){
			print(t.toString());
		}

		print(state.toString());
		
		stateChanged();

	}
	
    public boolean pickAndExecuteAnAction() {
        try {
        	Task temp = null;
/*
        	for(Task t:taskList){
        		if(t == Task.doPayRent){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		doPayRent();
        		taskList.remove(temp);
        		return true;
        	}
        	
        	for(Task t:taskList){
        		if(state == State.doingNothing && t == Task.goToWork){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		goToWork();
        		taskList.remove(temp);
        		return true;
        	}
        
        	for(Task t:taskList){
        		if(state == State.doingNothing && t == Task.goToBankNow){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		goToBank();
        		taskList.remove(temp);
        		return true;
        	}*/
        
        	for(Task t:taskList){
        		if(state == State.doingNothing && t == Task.goEatFood){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		goEatFood();
        		taskList.remove(temp);
        		return true;
        	}
        
        	/*for(Task t:taskList){
        		if(state == State.doingNothing && t == Task.goToBank){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		goToBank();
        		taskList.remove(temp);
        		return true;
        	}
        
        	for(Task t:taskList){
        		if(state == State.doingNothing && t == Task.goToMarket){
        			temp = t;
        		}
        	}
        	
        	if(temp != null){
        		goToMarket();
        		taskList.remove(temp);
        		return true;
        	}
        	
        	

			if(transportState == TransportState.GettingOnBus){
				getOnBus();
				return true;
			}
			if(transportState = TransportState.GettingOffBus){
				getOffBus();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingToWork){
				finishGoingToWork();
				return true;
			}*/
			if(transportState == TransportState.none && state == State.goingOutToEat){
				finishGoingToRestaurant();
				return true;
			}
			/*if(transportState == TransportState.none && state == State.goingHomeToEat){
				finishGoingToHome();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingToBank){
				finishGoingToBank();
				return true;
			}*/
        	
        	
	        for(Role r : roles) {
	        	if( r.isActive() ) {
	        		boolean tempBool = r.pickAndExecuteAnAction();
	        		if(tempBool){
	        			return true;
	        		}
	        	}
	        }
	        
	        if(state == State.doingNothing){
	        	System.out.println("goingHome");
	        	goHome();
        	}
	        
	        return false;
        } catch(ConcurrentModificationException e){ return false; }
	}
    
    private void goHome() {
		personGui.DoWalkTo(new Point(75,103)); ///CHange to special go home method and remove semaphore
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//actions
    private void doPayRent(){
    	//bank.msgTransferFunds(this, landlord, "personal","rent");
    }
    private void goToWork(){
    	state = State.goingToWork;
    	
    }
    private void goEatFood() {
    	state = State.goingOutToEat;
    	goToRestaurant();
    }
    
    private void goToRestaurant() {
    	// DoGoToRestaurant animation
    	location = Location.AtRestaurant;
    	MyRestaurant mr = restaurants.get(0); // hack for first restaurant for now
    	destination = mr.location;

    	personGui.DoWalkTo(destination);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	//RestaurantCustomerRole rcr = roles.get(0); // hack 
    	//mr.h.msgIWantToEat(rcr);
    }
    private void finishGoingToRestaurant(){
    	print("finishGoingToRestaurant");
    	state = State.eating;
    	
    	/*MyRestaurant mr = restaurants.get(0); // hack for first restaurant for now
    	CustomerRole role = new CustomerRole(this, name, cashOnHand);
    	role.setGui(new CustomerGui(role));
    	roles.add(role);
    	role.active = true;
    	role.setHost(mr.h);
    	role.setCashier(mr.c);
    	personGui.gui.restaurantAnimationPanel.addGui(role.getGui());
    	role.getGui().setPresent(true);
    	role.gotHungry();*/
    }

	public void msgAnimationFinshed() {
    	print("msgAnimationFinshed");
		waitingResponse.release();	
	}

	public PersonGui getGui() {
		return personGui;
	}

	public void setHost(HostAgent host) {
		// TODO Auto-generated method stub
		
	}

	public void setCashier(CashierAgent cashier) {
		// TODO Auto-generated method stub
		
	}

	public void setGui(PersonGui g) {
		personGui = g;
		
	}


}