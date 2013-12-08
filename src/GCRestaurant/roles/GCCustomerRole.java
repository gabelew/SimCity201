package GCRestaurant.roles;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Waiter.Menu;
import restaurant.interfaces.Waiter.MenuItem;
import GCRestaurant.gui.GCCustomerGui;
import GCRestaurant.roles.GCCashierRole.Check;
import agent.Agent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant customer agent.
 */
public class GCCustomerRole extends Role implements Customer{
	final int TIMERCONST = 1000;
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	private int tableNumber;
	Timer timer = new Timer();
	private GCCustomerGui customerGui;
	private Semaphore busy = new Semaphore(0,true);
	private Semaphore atCashier = new Semaphore(0,true);
	// agent correspondents
	private GCHostRole host;
	private Waiter waiter;
	private Cashier cashier;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, thinkingAboutLeaving,WaitingInRestaurant, WaitingToSeat, Seated, ReadyToOrder, Ordering, Ordered, 
		Served, Eating, DoneEating, Paying, Leaving, ReorderFood, gotCheck};

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, doneLeaving};
	
	private String choice_;
	
	private Menu menu = null;
	private List<String> badChoices = new ArrayList<String>();
	private double cash;
	private Check check;
	
	//default states
	AgentEvent event = AgentEvent.none;
	public AgentState state = AgentState.DoingNothing;//The start state
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public GCCustomerRole(String name){
		super();
		this.name = name;
		if(name.equals("5")){ cash = 5;}
		else if(name.equals("6")){ cash = 6;}
		else{ cash = new Random().nextInt(20); }
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(GCHostRole host) {
		this.host = host;
	}
	//hack to connect waiter
	public void setWaiter(Waiter w) {
		this.waiter = w;
	}
	public void setCashier(GCCashierRole c) {
		this.cashier = c;
	}
	public String getCustomerName() {
		return name;
	}
/**************************************************
* Messages
**************************************************/
	public void gotHungry() {//from animation
		print("I'm hungry and have " + cash + " dollars");
		state=AgentState.DoingNothing;
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	// (1) seats at table
	public void msgSitAtTable(Menu m) {
		this.menu = m;
		print("Received msgSitAtTable");
		state = AgentState.WaitingToSeat;
		event = AgentEvent.followHost;
		stateChanged();
	}
	// (2) msg from waiter to give waiter order	
	public void tellMeOrderMsg()
	{
		state = AgentState.Ordering;
		stateChanged();
	}
	// (3) msg from waiter, been given food
	public void receivedFoodMsg()
	{
		state = AgentState.Eating;
		stateChanged();
	}
	// (4) Order out of stock
	public void anotherOrderMsg(String choice)
	{
		badChoices.add(choice);
		state = AgentState.ReorderFood;
		stateChanged();
	}
	// (5) Got Check from waiter
	public void receivedCheckMsg(Check c)
	{
		print("got check from waiter");
		state = AgentState.gotCheck;
		this.check = c;
		stateChanged();
	}
	
	public void receivedChangeMsg(double c)
	{
		print("Received Change, Thank You");
		cash += c;
	}
	
	public void restaurantFullMsg()
	{
		state = AgentState.thinkingAboutLeaving;
		stateChanged();
	}
/*********************************************
 * Actions
******************************************* */	
	// (1) Changes start to ready to order
	private void ReadyToOrderAction()
	{
		int decision = (new Random()).nextInt(4)+1;
		final GCCustomerRole temp = this;
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print("I'm ready to order");
				((GCWaiterRole)waiter).ReadyToOrderMsg(temp);//msg to waiter
				stateChanged();
			}
		},
		decision*TIMERCONST);
		
	}
	// (2) Msg to waiter giving order
	private void giveOrder()
	{
		if(cash == 0)
		{
			print("My choice is: steak");
			((GCWaiterRole)waiter).HereIsChoiceMsg(this, "Steak");
			customerGui.orderedFood();
			return;
		}
		if(canAffordOneItem())
		{
			print("~~~~~~~~~~~~~~~~~");
			for(MenuItem food : menu.menuItems)
			{
				if(cash >= food.cost)
				{
					this.choice_ = food.item;
					print("My choice is: " + choice_);
					((GCWaiterRole)waiter).HereIsChoiceMsg(this, choice_);
					customerGui.orderedFood();
					break;
				}
			}
		}
		else if(!canAffordMenu())
		{
			print("I can't afford anything");
			state = AgentState.Leaving;
			((GCWaiterRole)waiter).DoneEatingMsg(this);
			customerGui.DoExitRestaurant();
		}
		else
		{
			//Randomly Choose a Food
			if(name.equals("steak"))//hack to make customer order steak
			{
				print("My choice is: " + name);
				((GCWaiterRole)waiter).HereIsChoiceMsg(this, name);
			}
			else
			{
				int foodIndex = new Random().nextInt(menu.menuItems.size());
				this.choice_ = menu.menuItems.get(foodIndex).item;
				print("My choice is: " + choice_);
				((GCWaiterRole)waiter).HereIsChoiceMsg(this, choice_);
			}
			customerGui.orderedFood();
		}
		stateChanged();
	}
	private boolean canAffordOneItem()
	{
		int count = 0;
		for(MenuItem food : menu.menuItems)
		{
			if(cash < food.cost){ count++;}
		}
		if(menu.menuItems.size()-count == 1){ return true; }
		return false;
	}
	private boolean canAffordMenu()
	{
		for(MenuItem food : menu.menuItems)
		{
			if(cash >= food.cost){ return true;}
		}
		return false;
	}
	// (3) Timer to Eat food
	private void EatFoodAction() {
		customerGui.servedFood(choice_);
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
				print("Done eating, cookie = " + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		hungerLevel*TIMERCONST);//getHungerLevel() * 1000);//how long to wait before running task
	}
	// (4) Customer Leaves Restaurant, Sends Msg to Waiter
	private void LeaveTableAction() {
		Do("Leaving Table");
		customerGui.leftRest();
		customerGui.goToCashier();
		((GCWaiterRole)waiter).DoneEatingMsg(this);
		//customerGui.DoExitRestaurant();
	}
	// (6) Paying check
	public void PayingCheckAction()
	{
		customerGui.goToCashier();
		try {atCashier.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		print("Paying Bill Now");
		if(cash >= check.amountDue)
		{
			((GCCashierRole)cashier).msgPayment(this, check.amountDue);
		}
		else
		{
			print("I will pay you guys next time");
			((GCCashierRole)cashier).cannotPayCheckMsg(this, check.amountDue);
		}
		//replenishes money for next time
		//cash = 100;
	}
	// (5) reorders food
	private void newOrder()
	{
		if(canAffordOneItem())
		{
			print("I can't afford anything else");
			state = AgentState.Leaving;
			((GCWaiterRole)waiter).DoneEatingMsg(this);
			customerGui.DoExitRestaurant();
			stateChanged();
			return;
		}
		int foodIndex = new Random().nextInt(menu.menuItems.size());
		String choice = menu.menuItems.get(foodIndex).item;
		
		int count = 0;
		for(String c: badChoices)
		{
			if(c.equals(choice))
			{
				while(!choice.equals(c))
				{
					foodIndex = new Random().nextInt(menu.menuItems.size());
					choice = menu.menuItems.get(foodIndex).item;
					if(count >= menu.menuItems.size()){ break; }	
					break;
				}
			}
			count++;
		}
		
		print("My choice is: " + choice);
		((GCWaiterRole)waiter).HereIsChoiceMsg(this, choice);
		stateChanged();
	}
	
	private void RestaurantFullAction()
	{
		if(name.equals("leaving"))
		{
			print("the wait is too long, i'm leaving");
			state = AgentState.Leaving;
			host.waitingCustomerLeft(this);
			return;
		}
		int decidedToLeave = 2;
		int deciding = (new Random()).nextInt(5);
		if(deciding == decidedToLeave)
		{
			print("the wait is too long, i'm leaving");
			state = AgentState.Leaving;
			host.waitingCustomerLeft(this);
		}
		stateChanged();
	}
	public void msgActionDone()
	{
		busy.release();
		stateChanged();
	}
	public void msgAtCashier() {//from animation
		//print("msgAtTable() called");
		atCashier.release();// = true;
		stateChanged();
	}
/*******************************
 * Scheduler.  Determine what action is called for, and do it.
*************************** */
	public boolean pickAndExecuteAnAction() 
	{
		try
		{
			//	CustomerAgent is a finite state machine
			if(state == AgentState.thinkingAboutLeaving)
			{
				state = AgentState.WaitingToSeat;
				RestaurantFullAction();
				return true;
			}
			
			if (state==AgentState.DoingNothing && event == AgentEvent.gotHungry ){
				state = AgentState.WaitingInRestaurant;
				goToRestaurant();
				return true;
			}
			//Seating Animation
			if (state == AgentState.WaitingToSeat && event == AgentEvent.followHost ){
				state = AgentState.Seated;
				SitDown();
				return true;
			}
			//if current state is seated then change state to ReadyToOrder
			if(state == AgentState.Seated && event == AgentEvent.seated)
			{
				state = AgentState.ReadyToOrder;
				ReadyToOrderAction();
				return true;
			}
			
			if(state == AgentState.ReorderFood)
			{
				state = AgentState.Ordered;
				newOrder();
				return true;
			}
			
			if(state == AgentState.Ordering)
			{
				state = AgentState.Ordered;
				giveOrder();
				return true;
			}
			
			if(state == AgentState.Eating)
			{
				state = AgentState.DoneEating;
				EatFoodAction();
				return true;
			}
			
			if(state == AgentState.gotCheck && event == AgentEvent.doneEating )
			{
				state = AgentState.Paying;
				LeaveTableAction();
				return true;
			}
			
			if(state == AgentState.Paying)
			{
				state = AgentState.DoingNothing;
				PayingCheckAction();
				return true;
			}
			
			return false;
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}

/*********************************************
 * Animation Methods
******************************************* */	
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
	//tells the host to add him to the list
	private void goToRestaurant() {
		Do("Going to restaurant");
		customerGui.enterRestaurant();
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNumber);
		stateChanged();
	}

	// Accessors, etc.
	public void setTableNumber(int tn)
	{
		tableNumber = tn;
	}
	public String getName() {
		return name;
	}
	
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

	public void setGui(GCCustomerGui g) {
		customerGui = g;
	}

	public Gui getGui() {
		return (Gui) customerGui;
	}

	@Override
	public void setGui(Gui waiterGuiFactory) {
		// TODO Auto-generated method stub
		
	}
}
