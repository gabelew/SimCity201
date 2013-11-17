package city;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import restaurant.Restaurant;
import restaurant.gui.CustomerGui;
import agent.Agent;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.roles.*;

public class PersonAgent extends Agent 
{
/********************************************************
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 *                       DATA 
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
	private List<Role> roles = new ArrayList<Role>();
	private List<Restaurant> restaurants = new ArrayList<Restaurant>();//hacked in upon creation 
	private List<MyBank> banks = new ArrayList<MyBank>(); 
	private List<MyMarket> markets = new ArrayList<MyMarket>(); 
	private List<MyBusStop> busStops = new ArrayList<MyBusStop>(); 
	private List<Task> taskList = new ArrayList<Task>(); 
	private Semaphore waitingResponse = new Semaphore(0,true);
	private PersonGui personGui;
	
	//Various States
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
	
/***********************
 *  UTILITY CLASSES START
 ***********************/
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
/***********************
 *  UTILITY CLASSES END
 ***********************/
	
	enum Shift {day, night}
	
	/**
	 * Constructor
	 */
	public PersonAgent(String name, double cash) {
	    this.name = name;
	    this.cashOnHand = cash;
	}
	
/***********************
 *  ACCESSOR METHODS START
 ***********************/
	public void addRole(Role r) {
        roles.add(r);
        r.setPerson(this);
	}
	
	public void addRestaurant(Restaurant r) {
		restaurants.add(r);
	}
	
	public void setName(String name){
        this.name = name;
	}
	
	public String getName(){
       return name;
	}
/***********************
 *  ACCESSOR METHODS END
 ***********************/
	
/********************************************************
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 *                     MESSAGES 
  *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
	
/***************************
 * BANKING MESSAGES START
 ***************************/
	public void msgTransferSuccessful(PersonAgent recipient, double amount, String purpose) {

	}
	public void msgTransferFailure(PersonAgent recipient, double amount, String purpose) {
		// get a loan, attempt transfer again
	}
	public void msgTransferCompleted(PersonAgent sender, double amount, String purpose) {

	}
	
	public void msgHereIsBalance(double amount, String accountType) {
		
	}
/***************************
 * BANKING MESSAGES END
 ***************************/

/***************************
 * TIME SENSITIVE MESSAGES START
 ***************************/
	public void msgNextHour(int hour, String dayOfWeek) 
	{
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

		/**********
		 * GETS FOOD IF HUNGRY
		 *************/
		if(hungerLevel > 50 && state != State.goingOutToEat && state != State.eating && state != State.goingHomeToEat)
		{
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
/***************************
 * TIME SENSITIVE MESSAGES END
 ***************************/

/***************************
 * WORK RELATED MESSAGES START
 ***************************/
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
	
	public void msgDoneWorking()
	{
		state = State.doingNothing;
		/*Role temp = null;
		for(Role r: roles){
			if(r instanceof job.role){
				
			}
		}
		
		*/
		stateChanged();
	}
/***************************
 * WORK RELATED MESSAGES END
 ***************************/
	
/***************************
 * MANAGER WORK RELATED MESSAGES START
 ***************************/	
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
/***************************
 * MANAGER WORK RELATED MESSAGES END
 ***************************/	

/***************************
 * TRANSPORTATION RELATED MESSAGES START
 ***************************/	
	public void msgBusIshere(){
		transportState = TransportState.GettingOnBus;
		stateChanged();
	}
	public void msgAtYourStop(){
		transportState = TransportState.GettingOffBus;
		stateChanged();
	}
/***************************
 * TRANSPORTATION RELATED MESSAGES END
 ***************************/	
	
	public void msgDoneEatingAtRestaurant() {
		print("msgDoneEatingAtRestaurant");
		state = State.doingNothing;

		for(Task t: taskList){
			print(t.toString());
		}

		print(state.toString());
		
		stateChanged();

	}
/***************************
 * ATHOME MESSAGES START
 ***************************/
	
	//RepairMan to Person that appliance is fixed
	public void ApplianceFixed(String appliance, double price)
	{
		
	}
	
	//Role to Itself to get food
	public void msgGetFoodFromMarket(Map<String,Integer> toOrderFromMarket)
	{
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
/***************************
 * ATHOME MESSAGES END
 ***************************/
	
/********************************************************
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 *                    SCHEDULER 
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
    public boolean pickAndExecuteAnAction() 
    {
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
	        	print("goingHome");
	        	goHome();
        	}
	        
	        return false;
        } catch(ConcurrentModificationException e){ return false; }
	}
    


/********************************************************
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 *                     ACTIONS 
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
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
    	Restaurant mr = restaurants.get(randInt(0,restaurants.size() - 1));
    	destination = mr.location;

    	personGui.DoWalkTo(destination);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    private void finishGoingToRestaurant(){
    	print("finishGoingToRestaurant");
    	state = State.eating;
    	
    	Restaurant r = findRestaurant(destination);
    	CustomerRole role = (CustomerRole) SimCityGui.customerFactory(this, r);
    	role.setGui(new CustomerGui(role));
    	roles.add(role);
    	role.active = true;
    	r.insideAnimationPanel.addGui(role.getGui());
    	role.getGui().setPresent(true);
    	role.gotHungry();
    }

	private Restaurant findRestaurant(Point d) {
		for(Restaurant r: restaurants){
			if(r.location.equals(d)){
				return r;
			}
		}
		return null;
	}

	public void msgAnimationFinshed() {
    	print("msgAnimationFinshed");
		waitingResponse.release();	
	}

	public PersonGui getGui() {
		return personGui;
	}
	public void setGui(PersonGui g) {
		personGui = g;
		
	}
	public static int randInt(int min, int max) {
	    Random i = new Random();
	    return i.nextInt((max - min) + 1) + min;
	}
	public void print(String msg)
	{
		System.out.println(name + ": " + msg);
	}
/********************************************************
 *>>>>>>>>>>>>                        <<<<<<<<<<<<<<<<<<
 *                ANIMATION METHODS 
 *>>>>>>>>>>>>                        <<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
	    private void goHome() 
	    {
			personGui.DoWalkTo(new Point(75,103)); //CHange to special go home method and remove semaphore
			try {waitingResponse.acquire();} 
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	    
	    public void msgReenablePerson(){
			//gui.setPresent();
		}

}