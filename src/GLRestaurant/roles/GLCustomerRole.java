package GLRestaurant.roles;



import GLRestaurant.roles.GLWaiterRole.customerState;
import GLRestaurant.gui.GLCustomerGui;
import restaurant.Restaurant;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Waiter.Menu;
import agent.Agent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;

/**
 * Restaurant customer agent.
 */
public class GLCustomerRole extends Role implements Customer{
	private final int EATINGTIME = 5000;
	private final int DECIDINGORDERTIME = 4000;
	private String choice;
	private int seatnumber = 0;
	private double amountPayable;
	Timer timer = new Timer();
	public GLCustomerGui customerGui;
	
	private class MyWaiter {
		GLWaiterRole w;
		MyWaiter(GLWaiterRole waiter) {
			w = waiter;
		}
	}
	public Restaurant restaurant;
	private MyWaiter waiter;
	Map<String, Double> menu = new ConcurrentHashMap<String, Double>();
	private Random generator = new Random();

	public enum AgentState
	{DoingNothing, GoingToRestaurant, WaitingInRestaurant, BeingSeated, Seated, Reorder, Ordering, Ordered, Eating, DoneEating, WaitingForCheck, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, atWaitingArea, tiredOfWaiting, followWaiter, seated, reorder, order, eating, doneEating, checkReceived, paid, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public GLCustomerRole(PersonAgent p, Restaurant r){
		super(p);
		myPerson = p;
		restaurant = r;
	}


	// Messages

	public void gotHungry() {//from animation
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "I'm hungry.");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgRestaurantClosed() {
		event = AgentEvent.tiredOfWaiting;
		stateChanged();
	}
	
	public void msgRestaurantFull() {
		int randomChoice = generator.nextInt(2);
		if(0 == randomChoice) {
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "There are no seats. I am leaving.");
			event = AgentEvent.tiredOfWaiting;
		}
		stateChanged();
	}

	public void msgFollowMe(GLWaiterRole w, int seatNum, Map<String, Double> menu) {
		this.menu = new ConcurrentHashMap<String, Double>(menu);
		setWaiter(w); // hack to let it know which waiter to order with later
		seatnumber = seatNum;
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgPleaseReorder() {
		event = AgentEvent.reorder;
		customerGui.reorder();
		stateChanged();
	}

	public void msgChooseFood() {
		event = AgentEvent.order;
		stateChanged();
	}

	public void msgHereIsFood(String choice) {
		event = AgentEvent.eating;
		stateChanged();
	}
	
	public void msgHereIsCheck(double amount) {
		event = AgentEvent.checkReceived;
		amountPayable = amount;
		stateChanged();
	}
	
	public void msgHereIsReceipt(double paid) {
		amountPayable = 0;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoingToCashier() {
		event = AgentEvent.paid;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoingToWaitingArea() {
		event = AgentEvent.atWaitingArea;
		stateChanged();
	}
	
	public void msgOutOfFood(String item) {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "I guess I can't order the " + item + ".");
		removeItemFromMenu(item);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.GoingToRestaurant;
			goToRestaurant();
			return true;
		}
		
		if(state == AgentState.GoingToRestaurant && event == AgentEvent.atWaitingArea) {
			state = AgentState.WaitingInRestaurant;
			tellHost();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.tiredOfWaiting ){
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}

		if (state == AgentState.BeingSeated && event == AgentEvent.seated) {
			state = AgentState.Ordering;
			decideOrder(); 
			return true;
		}
		
		if (state == AgentState.Ordered && event == AgentEvent.reorder) {
			state = AgentState.Ordering;
			decideOrder(); 
			return true;
		}

		if (state == AgentState.Ordering && event == AgentEvent.order){
			state = AgentState.Ordered;
			orderFood();
			return true;
		}

		if (state == AgentState.Ordered && event == AgentEvent.eating){
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.WaitingForCheck;
			askForCheck();
			return true;
		}
		
		if (state == AgentState.WaitingForCheck && event == AgentEvent.checkReceived){
			state = AgentState.Paying;
			payCashier();
			return true;
		}
		
		if (state == AgentState.Paying && event == AgentEvent.paid){
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			reactivatePerson();
			return true;
		}
		return false;
	}

	// Actions
	
	private void reactivatePerson() {
		myPerson.msgDoneEatingAtRestaurant();
		restaurant.insideAnimationPanel.removeGui(customerGui);
	}

	private void goToRestaurant() {
		customerGui.DoGoToWait();
	}
	
	public void tellHost() {
		if(((GLHostRole)restaurant.host).myPerson != null) {
			restaurant.host.msgIWantToEat(this);
		} else {
			state = AgentState.WaitingInRestaurant;
			event = AgentEvent.tiredOfWaiting;
		}
	}

	private void SitDown() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Being seated. Going to table.");
		customerGui.DoGoToSeat(seatnumber); //might change to take no parameters but get WaiterGui to tell CustomerGui where to go
	}

	private void decideOrder() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Deciding  what to order.");
		timer.schedule(new TimerTask() {
			public void run() {
				callWaiter();
			}
		},
		DECIDINGORDERTIME);
	}

	private void orderFood() {
//		if("steak".equals(this.name)) {
//			choice = "steak";
//		} else if("chicken".equals(this.name)) {
//			choice = "chicken";
//		} else if("cookie".equals(this.name)) {
//			choice = "cookie";
//		} else if("salad".equals(this.name)) {
//			choice = "salad";
//		} else if("runner".equals(this.name)) {
//			List<String> keys = new ArrayList<String>(menu.keySet());
//			int randomIndex = generator.nextInt(keys.size());
//			choice = keys.get(randomIndex);
//		} else {
			// Removes items from the menu that customer cannot afford
			Iterator<String> it1 = menu.keySet().iterator();
			while(it1.hasNext()) {
				String key = it1.next();
				double foodPrice = menu.get(key);
				int priceDifference = Double.compare(foodPrice, myPerson.cashOnHand);
				if(priceDifference > 0) {
					menu.remove(key);
				}
			}
			if (menu.size() > 0) {
				List<String> keys = new ArrayList<String>(menu.keySet());
				int randomIndex = generator.nextInt(keys.size());
				choice = keys.get(randomIndex);
			} else {
				AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "There is nothing that I can order.");
				choice = null;
				event = AgentEvent.paid;
				state = AgentState.Paying;
			}		
		
		if (choice != null) {
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Ordering " + choice);
			waiter.w.msgHereIsChoice(this, choice);
			customerGui.ordered(choice);
		}
	}

	private void EatFood() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Eating yummy food.");
		customerGui.eating();
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		EATINGTIME);
	}
	
	private void askForCheck() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Asking for check.");
		myPerson.hungerLevel = 0;
		waiter.w.msgEatingDone(this);
	}
	
	private void payCashier() {
		customerGui.DoGoToCashier();
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Paying cashier.");
		double amount;
		if (myPerson.cashOnHand >= amountPayable) {
			amount = amountPayable;
			myPerson.cashOnHand -= amountPayable;
		} else {
			amount = myPerson.cashOnHand;
		}
		((GLCashierRole)restaurant.cashier).msgHereIsMoney(this, amount);
	}

	private void leaveRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Leaving restaurant " + restaurant.type);
		if(AgentState.Leaving == state && AgentEvent.paid == event)
			waiter.w.msgIAmLeaving(this);
		else if(AgentState.Leaving == state && AgentEvent.tiredOfWaiting == event) 
			restaurant.host.msgLeavingRestaurant(this);
		customerGui.DoExitRestaurant();
	}
	
	private void callWaiter() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Ready to order.");
		waiter.w.msgReadyToOrder(this);
	}

	// Accessors, etc.
	
	private void setWaiter(GLWaiterRole w) {
		waiter = new MyWaiter(w);
	}
	
	private void removeItemFromMenu(String item) {
		Iterator<String> it1 = menu.keySet().iterator();
		while(it1.hasNext()) {
			String key = it1.next();
			if(item.equals(key)) {
				menu.remove(key);
			}
		}
	}
	

	// Utilities
	
	public String toString() {
		return "customer " + getName();
	}

	public Gui getGui() {
		return this.customerGui;
	}

	public void setGui(Gui g) {
		this.customerGui = (GLCustomerGui)g;
	}
}