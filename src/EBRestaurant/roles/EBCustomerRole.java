package EBRestaurant.roles;

import EBRestaurant.gui.EBCustomerGui;
import agent.Agent;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import city.PersonAgent;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.*;

/**
 * Restaurant customer agent.
 */
public class EBCustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	private int food;
	private int tableNumber;
	private float money;
	Timer timer = new Timer();
	private EBCustomerGui customerGui;
	public String choice;
	// agent correspondents
	private Host host;
	private Waiter waiter;
	private int eatTime=5000;
	private float amountOwed;
	Random generator;
	private String outOf;
	private Cashier cashier;
	private int leaving;
	private int waitY;
	private boolean responsible;
	public Restaurant restaurant;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant,WaitingInArea,inArea,full,staying,BeingSeated, Seated,WaitingToOrder,Ordered,reOrdered,waitingForFood, Eating,Finishing, DoneEating,gotBill,paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public EBCustomerRole(PersonAgent p, Restaurant r){
		super(p);
		myPerson=p;
		restaurant=r;
		responsible=true;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Host host) {
		this.host = host;
	}
	
	public void setAmount(float amount){
		money=amount;
	}
	
	public void setResponsible(boolean resp){
		responsible=resp;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe(Waiter w, int Number){
		print("Received msgSitAtTable");
		waiter=w;
		tableNumber=Number;
		event = AgentEvent.followHost;
		stateChanged();
	}
	
	public void msgWhatDoYouWant(){
		state=AgentState.Ordered;
		stateChanged();
	}
	
	public void msgWhatDoYouWantAgain(String choice){
		outOf=choice;
		state=AgentState.reOrdered;
		stateChanged();
	}
	
	public void msgHereIsOrder(String choice){
		Do("Eating my "+choice);
		state=AgentState.Eating;
		stateChanged();
	}
	
	public void msgBill(float amount){
		Do("Received bill");
		state=AgentState.gotBill;
		amountOwed=amount;
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
	
	public void msgFull(){
		state=AgentState.full;
		stateChanged();
	}
	
	public void msgWaitHere(int yIndex){
		waitY=yIndex;
		state=AgentState.WaitingInArea;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		try{
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			goToRestaurant();
			return true;
		}
		if (state ==AgentState.WaitingInArea){
			waitingArea();
			return true;
		}
		if(state==AgentState.full){
			shouldLeave();
			return true;
		}
		if ((state == AgentState.WaitingInRestaurant||state==AgentState.inArea) && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if(state==AgentState.Seated && event==AgentEvent.seated)
		{
			state=AgentState.WaitingToOrder;
			readyToOrder();
			return true;
		}
		if (state==AgentState.Ordered)
		{
			state=AgentState.waitingForFood;
			giveOrder();
			return true;
		}
		
		if (state==AgentState.reOrdered)
		{
			state=AgentState.waitingForFood;
			giveOrderAgain(outOf);
			return true;
		}
		
		if(state==AgentState.Eating){
			state=AgentState.Finishing;
			EatFood();
			return true;
		}

		if (state == AgentState.DoneEating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			wantBill();
			return true;
		}
		if (state ==AgentState.gotBill){
			state=AgentState.paying;
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			Done();
			//no action
			return true;
		}
		return false;
		}
		catch(Exception e){
			return false;
		}
	}

	// Actions
	
	private void waitingArea(){
		customerGui.DoGoToWaitingArea(waitY);
		state=AgentState.inArea;
	}

	private void goToRestaurant() {
		state = AgentState.WaitingInRestaurant;
		Do("Going to restaurant");
		((EBHostRole) host).msgIWantToEat(this);//send our instance, so he can respond to us
	}
	
	private void readyToOrder(){
		timer.schedule(new TimerTask() {
			public void run() {
				print("Ready to order");
				Order();
			}
		},
		5000);
	}

	private void Order(){
		((EBWaiterRole) waiter).msgReadyToOrder(this);
	}
	
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNumber);
		state=AgentState.Seated;
		stateChanged();
		
	}
	
	private void giveOrder(){
		if(money<5.99){
			Do("Everything too expensive. Leaving");
			((EBWaiterRole) waiter).msgLeavingTable(this);
			state=AgentState.DoingNothing;
			customerGui.DoExitRestaurant();
			stateChanged();
		}
		else{
		generator=new Random();
		food=generator.nextInt(4);
		if ((food==2&&(money>5.99||!responsible))||(money<8.98))
			((EBWaiterRole) waiter).msgHereIsMyOrder("Salad",this);
		else if (food==0&&(money>15.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("Steak",this);
		else if (food==1&&(money>10.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("Chicken",this);
		else if (food==3&&(money>8.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("Pizza",this);
		else{
			if(food==0)
				giveOrderAgain("Steak");
			else if(food==1)
				giveOrderAgain("Chicken");
			else if(food==2)
				giveOrderAgain("Salad");
			else
				giveOrderAgain("Pizza");
		}
		}
	}
	
	private void giveOrderAgain(String choice){
		generator=new Random();
		food=generator.nextInt(4);
		if (food==0&&(money>15.99||!responsible))
		{
			if(choice=="Steak"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("Steak",this);
		}
		else if (food==1&&(money>=10.99||!responsible))
		{
			if(choice=="Chicken"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("Chicken",this);
		}
		else if (food==2&&(money>=5.99||!responsible))
		{
			if(choice=="Salad"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("Salad",this);
		}
		else if (food==3&&(money>=8.99||!responsible))
		{
			if(choice=="Pizza"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("Pizza",this);
		}
		else{
			Do("Can't afford choice. Leaving");
			((EBWaiterRole) waiter).msgLeavingTable(this);
			state=AgentState.DoingNothing;
			customerGui.DoExitRestaurant();
			stateChanged();
		}
	}

	private void EatFood() {
		state=AgentState.DoneEating;
		stateChanged();
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
				print("Done eating");
				//isHungry = false;
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		eatTime);
	}
	
	private void shouldLeave(){
		generator=new Random();
		leaving=generator.nextInt(2);
		if(leaving==1)
		{
			state=AgentState.staying;
			Do("Leaving because restaurant is full");
			((EBHostRole) host).msgLeavingRestaurant(this);
			stateChanged();
		}
		else{
			((EBHostRole) host).msgStaying(this);
			Do("Staying and waiting");
			state=AgentState.WaitingInArea;
			stateChanged();
		}
	}
	
	private void wantBill(){
		((EBWaiterRole) waiter).msgWantBill(this);
	}

	private void leaveTable() {
		state=AgentState.Leaving;
		Do("Paying Cashier");
		((EBWaiterRole) waiter).msgLeavingTable(this);
		if (money>amountOwed)
		{
			Do("I owe "+amountOwed);
			((EBCashierRole) cashier).msgPaying(amountOwed, tableNumber,true);
			money=money-amountOwed;
			NumberFormat formatter=NumberFormat.getCurrencyInstance();
			String moneys = formatter.format(money);
			Do("I now have "+moneys+" left");
			customerGui.DoExitRestaurant();
			event=AgentEvent.doneLeaving;
			stateChanged();
		}
		else
		{
			if(money>0)
			{
				((EBCashierRole) cashier).msgAddToTab(amountOwed-money,this);
				((EBCashierRole) cashier).msgPaying(money, tableNumber,false);
				customerGui.DoExitRestaurant();
				event=AgentEvent.doneLeaving;
				stateChanged();
			}
			else
			{
				((EBCashierRole) cashier).msgAddToTab(amountOwed,this);
				customerGui.DoExitRestaurant();
				event=AgentEvent.doneLeaving;
				stateChanged();
			}
		}
	}
	
	private void Done(){
		state = AgentState.DoingNothing;
		stateChanged();
	}

	// Accessors, etc.

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

	public void setGui(EBCustomerGui g) {
		customerGui = g;
	}

	public EBCustomerGui getGui() {
		return customerGui;
	}
	
	public void setCashier(Cashier c){
		cashier=c;
	}
	/*public void pauseIt(){
		pause();
	}
	
	public void resumeIt(){
		resume();
	}*/
}

