package city;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import bank.BankBuilding;
import bank.gui.BankCustomerGui;
import restaurant.Restaurant;
import restaurant.gui.CustomerGui;
import restaurant.gui.WaiterGui;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import agent.Agent;
import atHome.city.AtHomeGui;
import atHome.city.Home;
import atHome.city.Residence;
import city.BankAgent.BankAccount;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.interfaces.Bus;
import city.interfaces.Person;
import city.roles.*;

public class PersonAgent extends Agent implements Person
{
/********************************************************
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 *                       DATA 
 *>>>>>>>>>>>>>>>>                <<<<<<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
	public List<Role> roles = new ArrayList<Role>();
	private List<Restaurant> restaurants = new ArrayList<Restaurant>();//hacked in upon creation 
	private List<BankBuilding> banks = new ArrayList<BankBuilding>(); 
	private List<MarketAgent> markets = new ArrayList<MarketAgent>(); 
	public List<Task> taskList = new ArrayList<Task>(); 
	public Residence myHome;
	public Semaphore waitingResponse = new Semaphore(0,true);
	public PersonGui personGui;
	public EventLog log = new EventLog();
	public boolean testing = false;
	
	//Various States
	public enum Task {goToMarket, goEatFood, goToWork, goToBank, goToBankNow, doPayRent, doPayEmployees, offWorkBreak, onWorkBreak, goToHomeWithFood};
	public enum State { doingNothing, goingOutToEat, goingHomeToEat, eating, goingToWork, working, goingToMarket, shopping, goingToBank, banking, onWorkBreak, offWorkBreak, inHome, leavingHome };
	public enum Location { AtHome, AtWork, AtMarket, AtBank, InCity, AtRestaurant};
	enum TransportState { none, GoingToBus, WaitingForBus, OnBus, GettingOffBus, GettingOnBus};
	public boolean isRenter;
	public boolean isManager;
	
	public String name;
	public boolean car = false;
	public Bus busLeft;
	public Bus busRight;
	public MyJob job;
	public State state = State.doingNothing;
	public Location location = Location.InCity;
	private TransportState transportState = TransportState.none;
	public Point destination;
	
	Map<String, Integer> toOrderFromMarket = new HashMap<String, Integer>();
	Map<String, Integer> toPutInFridge = null;
	
	public int currentHour;
	public String dayOfWeek;
	
	public int hungerLevel = 51;
	public double cashOnHand = 0, businessFunds = 0;
	public BankAccount personalAccount = null;
	public BankAccount businessAccount = null;
	
	public SimCityGui simCityGui; 
	
	private static final int halfScreen = 417;
	
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
	public class MyJob{
		public Point location;
		public String type;
		public Shift shift;
		//public Role role;
		
		public MyJob(Point l, String type, Shift s){
			this.location = l;
			this.type = type;
			this.shift = s;
		}
	}

/***********************
 *  UTILITY CLASSES END
 ***********************/
	
	public enum Shift {day, night, none}
	
	/**
	 * Constructor
	 */
	
	// for unit testing purposes, where gui is not needed
	public PersonAgent(String name, double cash, double business) {
		this.name = name;
		this.cashOnHand = cash;
		this.businessFunds = business;
	    if(this.name.toLowerCase().contains("car") || this.name.toLowerCase().contains("deliveryman")){
	    	car = true;
	    }
	}
	// for unit testing purposes, where gui is not needed
	public PersonAgent(String name, double cash, Residence h) {
		this.name = name;
		this.cashOnHand = cash;
	    if(this.name.toLowerCase().contains("car") || this.name.toLowerCase().contains("deliveryman")){
	    	car = true;
	    }

		this.myHome = h;
	}
	
	public PersonAgent(String name, double cash, SimCityGui simCityGui,Residence h) {
	    this.name = name;
	    this.cashOnHand = cash;
	    this.simCityGui = simCityGui;
	    if(this.name.toLowerCase().contains("car") || this.name.toLowerCase().contains("deliveryman")){
	    	car = true;
	    }
	    
	    this.busLeft = this.simCityGui.animationPanel.busLeft;
	    this.busRight = this.simCityGui.animationPanel.busRight;
	    
		this.myHome = h;
		BankCustomerRole bc = new BankCustomerRole(this);
		roles.add(bc);
		bc.active = false;
	}
	public void addAtHomeRole(){
		AtHomeRole role = null;
		if(name.toLowerCase().contains("nofood")){
			role = new AtHomeRole(this,0);
		}else if(name.toLowerCase().contains("lowsteak")){
			role = new AtHomeRole(this,1);
		}else{
			role = new AtHomeRole(this,3);
		}
		
	    role.active = false;
	    roles.add(role);
		AtHomeGui ahGui = new AtHomeGui(this, role);
		role.setGui(ahGui);
		ahGui.setPresent(false);
		if(!testing){
			myHome.insideAnimationPanel.addGui(ahGui);
		}
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
	public void addMarket(MarketAgent m) {
		markets.add(m);
		
	}
	public void addBank(BankBuilding b) {
		banks.add(b);
		
	}
	public void setBusinessAccount(BankAccount b) {
		this.businessAccount = b;
	}
	public void setPersonalAccount(BankAccount p) {
		this.personalAccount = p;
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
	
	public void msgDoneAtBank() {
		state = State.doingNothing;
		for(Role role: roles) {
    		if (role instanceof BankCustomerRole) {
    			role.active = false;
    		}
		}
		stateChanged();
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
		
		if(job!=null){
			if((job.shift == Shift.day && location != Location.AtWork && (currentHour >= 22 || currentHour <= 5)) ||
					(job.shift == Shift.night && location != Location.AtWork && (currentHour >= 10 && currentHour < 17))){
				boolean inList = false;
				for(Task t: taskList){
					if(t == Task.goToWork)
						inList = true;
				}
				if(!inList){
					taskList.add(Task.goToWork);
				}
			}
			
		}
		if(currentHour == 23 && isRenter){
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
			if(youAreRich() && cashOnHand < 100.00){
				for(Task t: taskList){
					if(t == Task.goToBankNow)
						inList = true;
				}
				if(!inList){
					taskList.add(Task.goToBankNow);
				}
			}
			
			inList = false;
			for(Task t: taskList){
				if(t == Task.goEatFood)
					inList = true;
			}
			if(!inList){
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
	public void msgAtYourStop(int xPos, int yPos){
		personGui.xPos = xPos;

		if(personGui.xPos < halfScreen){
			personGui.yPos = yPos+16;
		}else{
			personGui.yPos = yPos-24;
		}
		transportState = TransportState.GettingOffBus;
		stateChanged();
	}
/***************************
 * TRANSPORTATION RELATED MESSAGES END
 ***************************/	
	
	public void msgDoneEatingAtRestaurant() {
		state = State.doingNothing;
		
		stateChanged();

	}
/***************************
 * ATHOME MESSAGES START
 ***************************/
	public void msgDoneEatingAtHome(){
		state = State.inHome;
		stateChanged();
	}
	
	public void msgHasLeftHome(){
    	for(Role r: roles){
			if(r instanceof AtHomeRole){
				r.active = false;
				((AtHomeRole) r).getGui().setPresent(false);
			}
    	}
		location = Location.InCity;
		state = State.doingNothing;
		stateChanged();

		log.add(new LoggedEvent("Received msgHasLeftHome from atHome Role."));
	}
	
	//RepairMan to Person that appliance is fixed
	public void ApplianceFixed(String appliance, double price)
	{
		for(Role r: roles)
		{
			//find at home role
			//send the ApplianceFixedMsg()
		}
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

		boolean inList = false;
		if(cashOnHand < 100.00){
			for(Task t: taskList){
				if(t == Task.goToBank)
					inList = true;
			}
			if(!inList){
				taskList.add(Task.goToBank);
			}
		}
		
		inList = false;
		for(Task t: taskList){
			if(t == Task.goToMarket)
				inList = true;
		}
		if(!inList){
			taskList.add(Task.goToMarket);
		}
		
		log.add(new LoggedEvent("Received msgGetFoodFromMarket added goToMarket to tasklist"));
		stateChanged();
	}
	
	public void msgNoMoreFood()
	{
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

/***************************
 * MARKET CUSTOMER MESSAGES END
 ***************************/
	public void doneShopping(Map<String,Integer> purchasedFood,MarketAgent m){
		//restockFidge with purchased food
		toPutInFridge = purchasedFood;
		
		MarketCustomerRole removeRole = null;
		for(Role r: roles){
			if(r instanceof MarketCustomerRole){
				m.insideAnimationPanel.removeGui(((MarketCustomerRole) r).getMarketCustomerGui());
		    	((MarketCustomerRole) r).getMarketCustomerGui().setPresent(false);
				r.active = false;
				removeRole = (MarketCustomerRole) r;
			}
		}
		roles.remove(removeRole);
		
		state = State.doingNothing;
		location = Location.InCity;
		stateChanged();
	}
	public void marketNotStocked(MarketAgent market){
		markets.remove(market);
	}
/***************************
 * MARKET CUSTOMER MESSAGES END
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
        	
        	if(state == State.inHome && taskList.isEmpty() == false){
        		state = State.leavingHome;
        		leaveHouse();
        	}
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
        	}*/
        	
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
        	}
        
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
        
        	for(Task t:taskList){
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
			if(transportState == TransportState.GettingOffBus){
				getOffBus();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingToWork){
				finishGoingToWork();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingOutToEat){
				finishGoingToRestaurant();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingHomeToEat){
				finishGoingToHomeToEat();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingToBank){
				finishGoingToBank();
				return true;
			}
			if(transportState == TransportState.none && state == State.goingToMarket){
				finishGoingToMarket();
				return true;
			}	
        	
	        for(Role r : roles) {
	        	if( r.isActive() ) {
	        		boolean tempBool = r.pickAndExecuteAnAction();
	        		if(tempBool){
	        			return true;
	        		}
	        	}
	        }
	        
	        if(transportState == TransportState.none && state == State.doingNothing){
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
    
    private void getOffBus() {
    	personGui.doGetOffBus();
    	transportState = TransportState.none;
    }

    private void getOnBus() {
    	personGui.doGetOnBus();
    	try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	transportState = TransportState.OnBus;

		if(personGui.xPos < halfScreen){
			busLeft.msgComingAboard(this, new Point(110, destination.y+10));
		}else{
			busRight.msgComingAboard(this, new Point(490, destination.y+10));
		}
    }
    
    private void doPayRent(){
    	//bank.msgTransferFunds(this, landlord, "personal","rent");
    }
    private void goToWork(){
    	state = State.goingToWork;
    	destination = job.location;
    	if(car == true || destination.y == personGui.yPos){
	    	personGui.DoWalkTo(destination);
	    	try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}else{
    		goToBusStop();
    	}
    }
    
	private void finishGoingToWork() {
		personGui.DoWalkTo(destination);
    	try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	location = Location.AtWork;
    	state = State.eating;
    	
    	if(job.type.equalsIgnoreCase("waiter") || job.type.equalsIgnoreCase("host") || job.type.equalsIgnoreCase("cook")
    			|| job.type.equalsIgnoreCase("cashier")){
    		Restaurant r = findRestaurant(destination);
    		if(job.type.equalsIgnoreCase("waiter")){
    			WaiterRole role = (WaiterRole) SimCityGui.waiterFactory(this, r);
            	role.setGui(new WaiterGui(role));
            	roles.add(role);
            	role.active = true;
            	r.insideAnimationPanel.addGui(role.getGui());
            	role.getGui().setPresent(true);
            	role.goesToWork();	
    		}else if(job.type.equalsIgnoreCase("host")){
    			HostRole role = (HostRole)(r.host);
    			if(role.getPerson() != null){
    				role.msgReleaveFromDuty(this);
    				try {
    					waitingResponse.acquire();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			roles.add(role);
    			role.setPerson(this);
            	role.active = true;
            	role.getGui().setPresent(true);
            	role.goesToWork();
    		}else if(job.type.equalsIgnoreCase("cook")){
    			CookRole role = (CookRole)(r.cook);
    			if(role.getPerson() != null){
    				role.msgRelieveFromDuty(this);
    				try {
    					waitingResponse.acquire();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			roles.add(role);
    			role.setPerson(this);
            	role.active = true;
            	role.getGui().setPresent(true);
            	role.goesToWork();
    		}else if(job.type.equalsIgnoreCase("cashier")){
    			CashierRole role = (CashierRole)(r.cashier);
    			if(role.getPerson() != null){
    				role.msgReleaveFromDuty(this);
    				try {
    					waitingResponse.acquire();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
    			roles.add(role);
    			role.setPerson(this);
            	role.active = true;
            	role.getGui().setPresent(true);
            	role.goesToWork();
            	role.active = true;
    		}
    	}else if(job.type.equalsIgnoreCase("clerk") || job.type.equalsIgnoreCase("deliveryMan")){
    		MarketAgent ma = findMarket(destination);
    		if(job.type.equalsIgnoreCase("clerk")){
		    		ClerkRole role = new ClerkRole();
		    		role.Market = ma;
		    		role.setPerson(this);
		    		roles.add(role);
		           	role.active = true;
		    		ma.insideAnimationPanel.addGui(role.getClerkGui());
		           	role.getClerkGui().setPresent(true);
		           	role.goesToWork();	
			}else if(job.type.equalsIgnoreCase("deliveryMan")){
					DeliveryManRole role = new DeliveryManRole(this);
		    		role.Market = ma;
		    		role.setPerson(this);
		    		roles.add(role);
		            role.active = true;
		    		ma.insideAnimationPanel.addGui(role.getDeliveryManGui());
		            role.getDeliveryManGui().setPresent(true);
		            role.goesToWork();		
			}
    	}
    }

	private MarketAgent findMarket(Point p) {
		for(MarketAgent ma: markets){
			if(ma.location.equals(p)){
				return ma;
			}
		}
		return null;
	}

	//Remember to add functionality so person can decide to eat at home
    private void goEatFood() {
		if(youAreRich()){
	    	state = State.goingOutToEat;
    		goToRestaurant();
		}else{
			state = State.goingHomeToEat;
			destination = myHome.location;
			goToHouseToEat();
		}
    }
    
    private void goToHouseToEat() {
    	location = Location.AtHome;

    	destination = myHome.location;
    	if(car == true || destination.y == personGui.yPos){
    		personGui.DoWalkTo(destination);
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}else{
    		goToBusStop();
    	}		
	}

	private void finishGoingToHomeToEat() {
		print("eatin at home");
		personGui.DoWalkTo(destination);
    	try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	state = State.eating;
		for(Role r:roles){
			if(r instanceof AtHomeRole){
				r.active = true;
				((AtHomeRole)r).getGui().setPresent(true);
				((AtHomeRole)r).ImHungry();
				if(toPutInFridge != null)
					((AtHomeRole)r).restockFridge(this.toPutInFridge);
				toPutInFridge = null;
				break;
			}
		}
		
    	/*
		role.setGui(new CustomerGui(role));
    	roles.add(role);
    	role.active = true;
    	r.insideAnimationPanel.addGui(role.getGui());
    	role.getGui().setPresent(true);
    	role.gotHungry();
		 
		 */
		
	}
	private boolean youAreRich() {
		if(personalAccount.currentBalance > 200){
			return true;
		}
		return false;
	}

	private void goToRestaurant() {
    	// DoGoToRestaurant animation
    	location = Location.AtRestaurant;
    	
    	Restaurant mr = restaurants.get(randInt(0,restaurants.size() - 1));
    	//Restaurant mr = restaurants.get(randInt(0,0));
    	destination = mr.location;
    	if(car == true || destination.y == personGui.yPos){
    		personGui.DoWalkTo(destination);
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}else{
    		goToBusStop();
    	}
    }
    private void finishGoingToRestaurant(){
    	personGui.DoWalkTo(destination);
    	try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
    
    private void goToBank() {
    	state = State.goingToBank;
	    BankBuilding m  = chooseClosestBank();
	    destination = m.location;
	    
	    if(car == true || destination.y == personGui.yPos){
    		personGui.DoWalkTo(destination);
    		if(!testing){
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}else{
    		goToBusStop();
    	} 
    }
    
    private void finishGoingToBank(){
    	state  = State.banking;
    	BankBuilding b = findBank(destination);
    	personGui.DoWalkTo(destination); //animationStub
    	if(!testing){
        	try {
    			waitingResponse.acquire();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	for(Role role: roles) {
    		if (role instanceof BankCustomerRole) {
    			role.active = true;
    			((BankCustomerRole) role).setGui(new BankCustomerGui((BankCustomerRole) role));
    	    	((BankCustomerRole) role).getGui().setPresent(true);
    	    	b.insideAnimationPanel.addGui(((BankCustomerRole) role).getGui());
    	    	((BankCustomerRole) role).bank = b;
    	    	((BankCustomerRole) role).goingToBank();
    	    	
    		}
    	}
    	
    }

	private void goToMarket(){
    	state = State.goingToMarket;
	    MarketAgent m  = chooseClosestMarket();
	    destination = m.location;
	    
	    if(car == true || destination.y == personGui.yPos){
    		personGui.DoWalkTo(destination);
    		
    		if(!testing){
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}else{
    		goToBusStop();
    	} 
	    
	    log.add(new LoggedEvent("Called goToMarket from scheduler going to closest market"));
    }
    
    private void finishGoingToMarket(){
    	state  = State.shopping;
    	MarketAgent m = findMarket(destination);
    	personGui.DoWalkTo(destination); //animationStub
    	if(!testing){
        	try {
    			waitingResponse.acquire();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	MarketCustomerRole role = new MarketCustomerRole(this);
    	roles.add(role);
    	role.active = true;
    	m.insideAnimationPanel.addGui(role.getMarketCustomerGui());
    	role.getMarketCustomerGui().setPresent(true);
    	role.startShopping(m, toOrderFromMarket);
    }

    private MarketAgent chooseClosestMarket() {
    	MarketAgent closestMa = markets.get(0);
    	for(MarketAgent m: markets){
			if(Math.abs(closestMa.location.y - personGui.yPos) > Math.abs(m.location.y - personGui.yPos)){
				if(Math.abs(closestMa.location.x - personGui.xPos) > Math.abs(m.location.x - personGui.xPos)){
					closestMa = m;
				}
			}
		}
		return closestMa;
	}

    private BankBuilding chooseClosestBank() {
    	BankBuilding closestBa = banks.get(0);
    	for(BankBuilding b: banks){
			if(Math.abs(closestBa.location.y - personGui.yPos) > Math.abs(b.location.y - personGui.yPos)){
				if(Math.abs(closestBa.location.x - personGui.xPos) > Math.abs(b.location.x - personGui.xPos)){
					closestBa = b;
				}
			}
		}
		return closestBa;
	}
    
	private void goToBusStop() {
    	personGui.doGoToBus();
		this.transportState = TransportState.GoingToBus;	
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(personGui.xPos < halfScreen){
			busLeft.msgWaitingForBus(this, new Point(personGui.xPos, personGui.yPos));
		}else{
			busRight.msgWaitingForBus(this, new Point(personGui.xPos, personGui.yPos));
		}
		this.transportState = TransportState.WaitingForBus;	
	}

	private Restaurant findRestaurant(Point d) {
		for(Restaurant r: restaurants){
			if(r.location.equals(d)){
				return r;
			}
		}
		return null;
	}
    
	private BankBuilding findBank(Point d) {
		for(BankBuilding b: banks){
			if(b.location.equals(d)){
				return b;
			}
		}
		return null;
	}
    
	public void msgAnimationFinshed() {
		waitingResponse.release();	
	}

	public void msgWalkingHomeAnimationFinshed() {
		waitingResponse.release();	
		stateChanged();
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

/********************************************************
 *>>>>>>>>>>>>                        <<<<<<<<<<<<<<<<<<
 *                ANIMATION METHODS 
 *>>>>>>>>>>>>                        <<<<<<<<<<<<<<<<<<
 ******************^^^^^^^^^^^^^^^^*********************/
	    private void goHome() 
	    {	
	    		destination = myHome.location;
				if(car == true || Math.abs(destination.y - personGui.yPos) <= 40){
					personGui.doWalkToHome();
					if(!testing){
			        	try {
			    			waitingResponse.acquire();
			    		} catch (InterruptedException e) {
			    			e.printStackTrace();
			    		}
			    	}
					location = Location.AtHome;
					state = State.inHome;
					for(Role r: roles){
						if(r instanceof AtHomeRole){
							r.active = true;
							((AtHomeRole) r).getGui().setPresent(true);
							((AtHomeRole)r).goToHomePos();
							if(toPutInFridge != null)
								((AtHomeRole)r).restockFridge(this.toPutInFridge);
							toPutInFridge = null;
						}
					}
		    	}else{
		    		goToBusStop();
		    	} 

		}
	    private void leaveHouse() {
	    	for(Role r: roles){
				if(r instanceof AtHomeRole){
					((AtHomeRole)r).msgGoLeaveHome();
				}
	    	}
	    }
	    
	    //public void msgReenablePerson(){
			//gui.setPresent();
		//}

		public void releavedFromDuty(Role role) {
			Role removeRole = null;
			for(Role r:roles){
				if(role == r){
					removeRole = r;
				}
			}
			if(removeRole!=null){
				roles.remove(removeRole);
				state = State.doingNothing;
				location = Location.InCity;
			}
			stateChanged();
		}


}