package city.roles;

import restaurant.CashierAgent;
import restaurant.HostAgent;
import restaurant.Restaurant;
import restaurant.gui.CustomerGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Waiter.Menu;
import restaurant.interfaces.Waiter.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class CustomerRole extends Role implements Customer {
	private String name;
	private String choice; 				// will hold the customers food choice
	private MyMenu myMenu;	
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	private Semaphore leaveEarly = new Semaphore(1,true);
	boolean recievedCheck = false;
	boolean goToATM = false;
	double check;
	
	// agent correspondents
	private Waiter waiter;
	public Restaurant restaurant;
	
	static final int ATM_WITHDRAWAL_AMOUNT = 200; 
	static final int HUNGERLEVEL = 5000;
	static final int ONE = 1;
	static final int MENUINDEXSTART = 0;
	static final int MENUINDEXEND_DEFAULT = 4;
	int MENUINDEXEND = MENUINDEXEND_DEFAULT;
	static final int QUICKEST_CHOICE_TIME = 8000;
	static final int SLOWEST_CHOICE_TIME = 20000;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, WaitigForTable, BeingSeated, Seated, LookingAtMenu, ChoiceMade, Ordered, 
		Eating, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, noTables, followWaiter, seated, decided, askedToOrder, startEating, doneEating, doneLeaving,
		payedCheck, leavingEarly};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerRole class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerRole(PersonAgent p, Restaurant r){
		super(p);
		myPerson = p;
		restaurant = r;
	}
	
	// Messages

	public void gotHungry() {//from animation
		event = AgentEvent.gotHungry;
		MENUINDEXEND = MENUINDEXEND_DEFAULT;
		if(goToATM){
			goToATM = false;
			myPerson.cashOnHand += ATM_WITHDRAWAL_AMOUNT;
		}
		stateChanged();
	}

	public void msgWaitForOpenTable() {
		event = AgentEvent.noTables;
		stateChanged();
	}
	
	public void msgTableIsReady() {
		if(leaveEarly.tryAcquire()){
			state = AgentState.WaitingInRestaurant;
			leaveEarly.release();
		}
	}
	public void msgFollowMeToTable(Waiter w, Menu m){
		waiter = w;
		myMenu = new MyMenu(m);
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	public void msgAnimationFinishedDoEnterRestaurant() {
		//from animation
		waitingResponse.release();// = true;
	}	
	public void msgAnimationFinishedGoToCashier() {
		//from animation
		waitingResponse.release();// = true;
	}
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	private void msgChoiceMade(String c){
		choice = c;

		if(name.equalsIgnoreCase("Steak") && myPerson.cashOnHand >= CashierAgent.STEAK_COST && isAvailable("Steak")){
			choice = "Steak";
		}
		if(name.equalsIgnoreCase("Chicken") && myPerson.cashOnHand >=  CashierAgent.CHICKEN_COST && isAvailable("Chicken")){
			choice = "Chicken";
		}
		if(name.equalsIgnoreCase("Salad") && myPerson.cashOnHand >=  CashierAgent.SALAD_COST && isAvailable("Salad")){
			choice = "Salad";
		}
		if(name.equalsIgnoreCase("Burger") && myPerson.cashOnHand >=  CashierAgent.BURGER_COST && isAvailable("Burger")){
			choice = "Burger";
		}
		if(name.equalsIgnoreCase("Cookie") && myPerson.cashOnHand >=  CashierAgent.COOKIE_COST && isAvailable("Cookie")){
			choice = "Cookie";
		}
		
		event = AgentEvent.decided;
		stateChanged();
	}
	private boolean isAvailable(String choice) {
		for(MenuItem m: myMenu.menuItems)
		{
			if(m.item.equalsIgnoreCase(choice))
			{
				return true;
			}
		}
		return false;
	}

	public void msgWhatWouldYouLike(){
		event = AgentEvent.askedToOrder;
		stateChanged();
	}
	public void msgOutOfOrder(String c) {
		MENUINDEXEND--;

		print("msgOutOfOrder " + c + " MenuIndexEnd " + MENUINDEXEND);
		myMenu.remove(c);
		
		if(MENUINDEXEND < 0)
		{
			print("There is no more food that I can afford!");
			state = AgentState.WaitigForTable;
			event = AgentEvent.leavingEarly;
		}
		else{
			state = AgentState.BeingSeated;
			event = AgentEvent.seated;
		}
		stateChanged();
	}
	public void msgHereIsYourFood(){
		event = AgentEvent.startEating;
		stateChanged();
	}
	public void msgHereIsCheck(double check) {
		recievedCheck = true;
		this.check = check;
		stateChanged();	
	}
	public void msgFinishedEating(){
		event = AgentEvent.doneEating;
		stateChanged();
	}
	public void msgChange(double cashBack) {
		myPerson.cashOnHand += cashBack;
		event = AgentEvent.payedCheck;
		stateChanged();	
	}
	public void msgPayMeLater() {
		goToATM = true;
		event = AgentEvent.payedCheck;
		stateChanged();	
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerRole is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if ((state == AgentState.WaitingInRestaurant || state == AgentState.WaitigForTable) 
				&& event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.noTables){
			state = AgentState.WaitigForTable;
			WaitForTable();
			return true;
		}
		if (state == AgentState.WaitigForTable && event == AgentEvent.leavingEarly){
			state = AgentState.Leaving;
			LeaveEarly();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.LookingAtMenu;
			makeChoice();
			return true;
		}
		if(state == AgentState.LookingAtMenu && event == AgentEvent.decided){
			state = AgentState.ChoiceMade;
			tellWaiter();
			return true;
		}
		if(state == AgentState.ChoiceMade && event == AgentEvent.askedToOrder){
			state = AgentState.Ordered;
			giveOrder();
			return true;
		}
		if(state == AgentState.Ordered && event == AgentEvent.startEating){
			state = AgentState.Eating;
			eatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating && recievedCheck){
			state = AgentState.Paying;
			leaveTable();
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.payedCheck){
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
		print("reactivatePerson");
		myPerson.msgDoneEatingAtRestaurant();
		
	}
	private void goToRestaurant() {
		DoGoToRestaurant();
		restaurant.host.msgIWantToEat(this);//send our instance, so he can respond to us
		DoWaitForTable();
	}
	
	private void WaitForTable() {
		timer.schedule(
			new TimerTask() {
				public void run() {
					if(!name.equalsIgnoreCase("wait")){
						if(leaveEarly.tryAcquire()){
							if(state == AgentState.WaitigForTable){
								event = AgentEvent.leavingEarly;
								stateChanged();
							}else{
								leaveEarly.release();
							}
						}
					}
				}
			}, 
			myPerson.randInt(QUICKEST_CHOICE_TIME, SLOWEST_CHOICE_TIME)
		);			
	}
	
	private void LeaveEarly() {
		Do("Leaving Early.");
		if(waiter != null)
		{
			waiter.msgDoneEatingAndLeaving(this);
			waiter = null;
			customerGui.DoExitRestaurant();
			if(leaveEarly.availablePermits() == 0){
				leaveEarly.release();		
			}
		}else{
			restaurant.host.msgLeavingRestaurant(this);	
			customerGui.DoExitRestaurant();
			leaveEarly.release();		
		};
	}
	
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat();
	}

	private void makeChoice(){
		List<MenuItem> removeMenuItems = new ArrayList<MenuItem>();
		for(MenuItem m: myMenu.menuItems){
			if(myPerson.cashOnHand < m.cost && !name.equalsIgnoreCase("Mahdi") && !name.equalsIgnoreCase("ditch") )
			{
				removeMenuItems.add(m);
			}
		}
		for(MenuItem m: removeMenuItems){
			MENUINDEXEND--;
			myMenu.menuItems.remove(m);
		}
		removeMenuItems = null;
		
		if(myMenu.menuItems.isEmpty()){
			state = AgentState.WaitigForTable;
			event = AgentEvent.leavingEarly;
			print("Everything is too expensive. I'm out of here.");
		}
		else{
			timer.schedule(new TimerTask() {
				public void run() {
					msgChoiceMade(myMenu.getMenuItemName(PersonAgent.randInt(MENUINDEXSTART,MENUINDEXEND)));
				}}, 
					PersonAgent.randInt(QUICKEST_CHOICE_TIME, SLOWEST_CHOICE_TIME)
			);	
		}
	}
	

	
	private void tellWaiter(){
		waiter.msgImReadyToOrder(this);
		customerGui.orderFood(choice);
	}
	
	private void giveOrder(){
		StringBuilder msg = new StringBuilder("I want to order " + choice);
		print(msg.toString());
		waiter.msgHereIsMyOrder(this, choice);
		customerGui.doneOrderingFood();
	}

	private void eatFood() {
		customerGui.foodIsHere(choice);
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
			public void run() {
				msgFinishedEating();
			}
		}, 
		HUNGERLEVEL);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTable() {
		StringBuilder msg = new StringBuilder("Done eating, " + choice);
		print(msg.toString());
		customerGui.doneEatingFood();
		waiter.msgDoneEatingAndLeaving(this);
		waiter = null;
		myPerson.hungerLevel = 0;
		doGoToCashier();
		print(myPerson.cashOnHand - check + " = " + myPerson.cashOnHand + " - " + check);
		
		if((myPerson.cashOnHand - check) > 0){
			myPerson.cashOnHand = myPerson.cashOnHand - check;
			restaurant.cashier.msgPayment(this, check);
		}else{
			restaurant.cashier.msgPayment(this, myPerson.cashOnHand);
			myPerson.cashOnHand = 0;
		}
	}
	
	private void leaveRestaurant() {
		Do("Leaving.");
		recievedCheck = false;
		customerGui.DoExitRestaurant(); // animation Stub
	}
	
	private void DoGoToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoEnterRestaurant();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void doGoToCashier() {
		Do("Going to Cashier");
		customerGui.doGoToCashier();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void DoWaitForTable(){
		Do("Waiting to be seatted");
		customerGui.DoWaitForTable();
	}

	// Accessors, etc.
	public String getName() {
		return myPerson.getName();
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
		
	}

	public CustomerGui getGui() {
		return customerGui;
	}

	public class MyMenu{
		private List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MyMenu(Menu m){
			for(MenuItem i: m.menuItems){
				menuItems.add(i);
			}
		}
		
		public void remove(String c) {
			MenuItem removeIt = null;
			for(MenuItem i: menuItems){
				print("\t\t " + i.item + " = " + c);
				if(i.item.equalsIgnoreCase(c)){
					removeIt = i;
				}
			}
			if(removeIt != null){
				menuItems.remove(removeIt);
			}
		}
		
		public String getMenuItemName(int i){
			return menuItems.get(i).item;
		}
	}


}

