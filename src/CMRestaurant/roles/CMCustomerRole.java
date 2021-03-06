package CMRestaurant.roles;

import restaurant.Restaurant;
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

import CMRestaurant.gui.CMCustomerGui;
import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;

/**
 * Restaurant customer agent.
 */
public class CMCustomerRole extends Role implements Customer {
	private String choice; 				// will hold the customers food choice
	private MyMenu myMenu;	
	Timer timer = new Timer();
	private CMCustomerGui customerGui;
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
	static final int MENUINDEXEND_DEFAULT = 3;
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
	public CMCustomerRole(PersonAgent p, Restaurant r){
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

	public void msgRestaurantIsClosed() {
		state = AgentState.WaitigForTable;
		event = AgentEvent.leavingEarly;
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

		if(myPerson.name.equalsIgnoreCase("Steak") && myPerson.cashOnHand >= CMCashierRole.STEAK_COST && isAvailable("Steak")){
			choice = "Steak";
		}
		if(myPerson.name.equalsIgnoreCase("Chicken") && myPerson.cashOnHand >=  CMCashierRole.CHICKEN_COST && isAvailable("Chicken")){
			choice = "Chicken";
		}
		if(myPerson.name.equalsIgnoreCase("Salad") && myPerson.cashOnHand >=  CMCashierRole.SALAD_COST && isAvailable("Salad")){
			choice = "Salad";
		}
		if(myPerson.name.equalsIgnoreCase("Burger") && myPerson.cashOnHand >=  CMCashierRole.BURGER_COST && isAvailable("Burger")){
			choice = "Burger";
		}
		if(myPerson.name.equalsIgnoreCase("Cookie") && myPerson.cashOnHand >=  CMCashierRole.COOKIE_COST && isAvailable("Cookie")){
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
		
		myMenu.remove(c);
		
		if(MENUINDEXEND < 0)
		{
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "There is no more food that I can afford!");
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
		myPerson.msgDoneEatingAtRestaurant();
		restaurant.insideAnimationPanel.removeGui(customerGui);
	}
	private void goToRestaurant() {
		DoGoToRestaurant();
		if(((CMHostRole)restaurant.host).myPerson != null){
			restaurant.host.msgIWantToEat(this);//send our instance, so he can respond to us
			DoWaitForTable();
		}else{
			state = AgentState.WaitigForTable;
			event = AgentEvent.leavingEarly;
		}
	}
	
	private void WaitForTable() {
		timer.schedule(
			new TimerTask() {
				public void run() {
					if(!myPerson.name.equalsIgnoreCase("wait")){
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
			PersonAgent.randInt(QUICKEST_CHOICE_TIME, SLOWEST_CHOICE_TIME)
		);			
	}
	
	private void LeaveEarly() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Leaving Early");
		if(waiter != null)
		{
			((CMWaiterRole) waiter).msgDoneEatingAndLeaving(this);
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
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Being seated. Going to table");
		customerGui.DoGoToSeat();
	}

	private void makeChoice(){
		List<MenuItem> removeMenuItems = new ArrayList<MenuItem>();
		for(MenuItem m: myMenu.menuItems){
			if(myPerson.cashOnHand < m.cost && !myPerson.name.equalsIgnoreCase("Mahdi") && !myPerson.name.equalsIgnoreCase("ditch") )
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
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Everything is too expensive. I'm out of here.");	
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
		((CMWaiterRole) waiter).msgImReadyToOrder(this);
		customerGui.orderFood(choice);
	}
	
	private void giveOrder(){
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "I want to order " + choice);
		((CMWaiterRole) waiter).msgHereIsMyOrder(this, choice);
		customerGui.doneOrderingFood();
	}

	private void eatFood() {
		customerGui.foodIsHere(choice);
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Eating Food");
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
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), 	"Done eating, " + choice);
		customerGui.doneEatingFood();
		((CMWaiterRole) waiter).msgDoneEatingAndLeaving(this);
		waiter = null;
		myPerson.hungerLevel = 0;
		doGoToCashier();
		
		if((myPerson.cashOnHand - check) > 0){
			myPerson.cashOnHand = myPerson.cashOnHand - check;
			((CMCashierRole) restaurant.cashier).msgPayment(this, check);
		}else{
			((CMCashierRole) restaurant.cashier).msgPayment(this, myPerson.cashOnHand);
			myPerson.cashOnHand = 0;
		}
	}
	
	private void leaveRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Leaving.");
		recievedCheck = false;
		customerGui.DoExitRestaurant(); // animation Stub
	}
	
	private void DoGoToRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Going to restaurant");
		customerGui.DoEnterRestaurant();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void doGoToCashier() {
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Going to Cashier");
		customerGui.doGoToCashier();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void DoWaitForTable(){
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, getName(), "Waiting to be seatted");
		customerGui.DoWaitForTable();
	}

	// Accessors, etc.
	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CMCustomerGui g) {
		customerGui = g;
		
	}

	public CMCustomerGui getGui() {
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

	@Override
	public void setGui(Gui g) {
		customerGui = (CMCustomerGui) g;
		
	}



}

