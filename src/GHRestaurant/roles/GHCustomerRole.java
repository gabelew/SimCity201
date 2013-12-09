package GHRestaurant.roles;

import GHRestaurant.gui.*;
import restaurant.Restaurant;
import restaurant.interfaces.*;
import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.interfaces.Person;
import city.roles.Role;

/**
 * Restaurant customer agent.
 */
public class GHCustomerRole extends Role implements Customer{
	//private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private GHCustomerGui customerGui;

	private int tableNumber; 
	private String choice;
	private double money;				

	private Random generator = new Random();
	private Semaphore atDestination = new Semaphore(0,true);
	private Check check;
	private Restaurant restaurant = null;
	
	// agent correspondents
	private Host host;
	private Cashier cashier;
	private Waiter waiter;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, WaitingForFood, Eating, DoneEating, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, ordered, reordered, seated, eating, doneEating, atCashier, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public GHCustomerRole(PersonAgent p, Restaurant r){
		super(p);
		//this.name = name;
		this.restaurant = r;
		
		money = 50;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Host host) {
		this.host = host;
	}
	
	public void setCashier(Cashier ca){
		this.cashier = ca;
	}
	
	public double getMoney(){
		return money;
	}
	
	public void setMoney(double m){
		money = m;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(int tablenumber, Waiter w) {
		print("Received msgFollowMeToTable");
		tableNumber = tablenumber;
		waiter = w;
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike(){
		print("Received msgWhatWouldYouLike");
		
		int c = generator.nextInt(4);
		
		switch(c){
		case 0: choice = "Steak";
		break;
		
		case 1: choice = "Chicken";
		break;
		
		case 2: choice = "Salad";
		break;
		
		case 3: choice = "Pizza";
		break;
		}
				
		event = AgentEvent.ordered;
		stateChanged();
	  
	  }
	
	public void msgOutOfChoiceReorder(String ch){
		print("Received msgOutOfChoiceReorder");
				
		while(true){
			
			int c = generator.nextInt(4);
			
			switch(c){
			case 0: choice = "Steak";
			break;
			
			case 1: choice = "Chicken";
			break;
			
			case 2: choice = "Salad";
			break;
			
			case 3: choice = "Pizza";
			break;
			}
			
			if(!(choice == ch)){break;}
		}
		
		event = AgentEvent.reordered;
		stateChanged();
		
	}
	  
	public void msgHereIsYourOrder(){
	  print("Recieved msgeHereIsYourOrder");
	  event = AgentEvent.eating;
	  stateChanged();
	  }
	
	public void msgHeresCheck(String choice, double co){
		print("Recieved the check");
		check = new Check(choice, co);
		stateChanged();
	}
	 

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier(){
		//from animation
		event = AgentEvent.atCashier;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown(tableNumber);
			return true;
		}
		
		if (state == AgentState.BeingSeated && event == AgentEvent.seated ){
			state = AgentState.Seated;
			SignalWaiter();
			return true;
		}
		
		if (state == AgentState.Seated && event == AgentEvent.ordered){
			state = AgentState.WaitingForFood;
			Order();
			return true;
		}
		
		if (state == AgentState.WaitingForFood && event == AgentEvent.reordered){
			state = AgentState.WaitingForFood;
			Order();
			return false;
		}
		
		if (state == AgentState.WaitingForFood && event == AgentEvent.eating){
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Paying;
			leaveTable();
			return true;
		}
		
		if (state == AgentState.Paying && event == AgentEvent.atCashier){
			state = AgentState.Leaving;
			PayBill();
			return true;
		}
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action 
			return true;
		}
				
		return false;
	}

	// Actions

	private void goToRestaurant() {
	    print("Going to restaurant");
		((GHHostRole) restaurant.host).msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown(int tablenumber) {
		print("Being seated. Going to table " + tablenumber);
		customerGui.DoGoToSeat(tablenumber);
	}
	
	private void SignalWaiter(){
		print("Signaling Waiter "+ waiter);
		((GHWaiterRole) waiter).msgImReadyToOrder(this);
	}
	
	private void Order(){
		print("My choice is " + choice);
		((GHWaiterRole) waiter).msgHereIsMyOrder(this, choice);	
	}

	private void EatFood() {
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTable() {
		print("Leaving.");
		((GHWaiterRole) waiter).msgDoneEatingandLeaving(this);
		customerGui.DoGoToCashier();
	}
	
	private void PayBill(){
		print("Going to pay bill");
		((GHCashierRole) restaurant.cashier).msgCustomerPaying(this,check.choice,check.cost,tableNumber);
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	public AgentState getState() {
		return state;
	}
	
	/*public String getName() {
		return name;
	}*/
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(GHCustomerGui g) {
		customerGui = g;
	}

	public GHCustomerGui getGui() {
		return customerGui;
	}
	
	private class Check {
		String choice;
		double cost;
		
		Check(String c, double co){		
			choice = c;
			cost = co;
		}	
	}

	@Override
	public void setGui(Gui g) {
		customerGui = (GHCustomerGui) g;
	}
}

