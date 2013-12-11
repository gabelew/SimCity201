package EBRestaurant.roles;

import EBRestaurant.gui.EBCustomerGui;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.*;

/**
 * EB Restaurant customer role.
 */
public class EBCustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	private int food;
	private int tableNumber;
	Timer timer = new Timer();
	private EBCustomerGui ebcustomerGui;
	public String choice;
	private Waiter waiter;
	private int eatTime=5000;
	private float amountOwed;
	Random generator;
	private String outOf;
	private int leaving;
	private int waitY;
	private boolean responsible;
	public Restaurant restaurant;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant,WaitingInArea,inArea,full,staying,BeingSeated, Seated,WaitingToOrder,Ordered,reOrdered,waitingForFood, Eating,Finishing, DoneEating,gotBill,paying,payed, Leaving};
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
	
	public void setResponsible(boolean resp){
		responsible=resp;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe(Waiter w, int Number){
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
		state=AgentState.Eating;
		stateChanged();
	}
	
	public void msgBill(float amount){
		myPerson.hungerLevel = 0;
		state=AgentState.gotBill;
		amountOwed=amount;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier(){
		state=AgentState.payed;
		stateChanged();
	}
	public void msgRestaurantClosed() {
		state=AgentState.payed;
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
		if(state==AgentState.payed){
			state=AgentState.Leaving;
			leave();
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			Done();
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
		ebcustomerGui.DoGoToWaitingArea(waitY);
		state=AgentState.inArea;
	}

	private void goToRestaurant() {
		if(((EBHostRole)restaurant.host).myPerson != null){
			state = AgentState.WaitingInRestaurant;
			restaurant.host.msgIWantToEat(this);//send our instance, so he can respond to us	
		}
		else{
			state = AgentState.payed;
			stateChanged();
		}
	}
	
	private void readyToOrder(){
		timer.schedule(new TimerTask() {
			public void run() {
				Order();
			}
		},
		5000);
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Ready to Order");

	}

	private void Order(){
		((EBWaiterRole) waiter).msgReadyToOrder(this);
	}
	
	private void SitDown() {
		ebcustomerGui.DoGoToSeat(tableNumber);
		state=AgentState.Seated;
		stateChanged();
		
	}
	
	private void giveOrder(){
		if(myPerson.cashOnHand<5.99){
			Do("Everything too expensive. Leaving");
			((EBWaiterRole) waiter).msgLeavingTable(this);
			state=AgentState.DoingNothing;
			ebcustomerGui.DoExitRestaurant();
			stateChanged();
		}
		else{
		generator=new Random();
		food=generator.nextInt(4);
		if ((food==2&&(myPerson.cashOnHand>5.99||!responsible))||(myPerson.cashOnHand<8.98))
			((EBWaiterRole) waiter).msgHereIsMyOrder("salad",this);
		else if (food==0&&(myPerson.cashOnHand>15.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("steak",this);
		else if (food==1&&(myPerson.cashOnHand>10.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("chicken",this);
		else if (food==3&&(myPerson.cashOnHand>8.99||!responsible))
			((EBWaiterRole) waiter).msgHereIsMyOrder("cookie",this);
		else{
			if(food==0)
				giveOrderAgain("steak");
			else if(food==1)
				giveOrderAgain("chicken");
			else if(food==2)
				giveOrderAgain("salad");
			else
				giveOrderAgain("cookie");
		}
		}
	}
	
	private void giveOrderAgain(String choice){
		generator=new Random();
		food=generator.nextInt(4);
		if (food==0&&(myPerson.cashOnHand>15.99||!responsible))
		{
			if(choice=="steak"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("steak",this);
		}
		else if (food==1&&(myPerson.cashOnHand>=10.99||!responsible))
		{
			if(choice=="chicken"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("chicken",this);
		}
		else if (food==2&&(myPerson.cashOnHand>=5.99||!responsible))
		{
			if(choice=="salad"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("salad",this);
		}
		else if (food==3&&(myPerson.cashOnHand>=8.99||!responsible))
		{
			if(choice=="cookie"){
				giveOrder();
			}
			else
				((EBWaiterRole) waiter).msgHereIsMyOrder("cookie",this);
		}
		else{
			((EBWaiterRole) waiter).msgLeavingTable(this);
			state=AgentState.DoingNothing;
			ebcustomerGui.DoExitRestaurant();
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
				//isHungry = false;
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		eatTime);
		AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Done Eating");
	}
	
	private void shouldLeave(){
		generator=new Random();
		leaving=generator.nextInt(2);
		if(leaving==1)
		{
			state=AgentState.payed;
			((EBHostRole)restaurant.host).msgLeavingRestaurant(this);
			stateChanged();
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Leaving early; restaurant full");

		}
		else{
			((EBHostRole) restaurant.host).msgStaying(this);
			state=AgentState.WaitingInArea;
			stateChanged();
		}
	}
	
	private void wantBill(){
		((EBWaiterRole) waiter).msgWantBill(this);
	}

	private void leaveTable() {
		state=AgentState.Leaving;
		((EBWaiterRole) waiter).msgLeavingTable(this);
		if (myPerson.cashOnHand>amountOwed)
		{
			((EBCashierRole) restaurant.cashier).msgPaying(amountOwed, tableNumber,true);
			myPerson.cashOnHand=myPerson.cashOnHand-amountOwed;
			ebcustomerGui.DoGoToCashier();
			stateChanged();
			AlertLog.getInstance().logMessage(AlertTag.REST_CUSTOMER, this.getName(), "Paying the cashier "+amountOwed);

		}
		else
		{
			if(myPerson.cashOnHand>0)
			{
				((EBCashierRole) restaurant.cashier).msgAddToTab(amountOwed-myPerson.cashOnHand,this);
				((EBCashierRole) restaurant.cashier).msgPaying(myPerson.cashOnHand, tableNumber,false);
				ebcustomerGui.DoGoToCashier();
				stateChanged();
			}
			else
			{
				((EBCashierRole) restaurant.cashier).msgAddToTab(amountOwed,this);
				ebcustomerGui.DoGoToCashier();
				stateChanged();
			}
		}
	}
	
	private void leave(){
		ebcustomerGui.DoExitRestaurant();
	}
	
	private void Done(){
		state = AgentState.DoingNothing;
		myPerson.msgDoneEatingAtRestaurant();
		restaurant.insideAnimationPanel.removeGui(ebcustomerGui);
	}

	// Accessors, etc.
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public void setGui(EBCustomerGui g) {
		ebcustomerGui = g;
	}

	public EBCustomerGui getGui() {
		return ebcustomerGui;
	}
	
	@Override
	public void setGui(Gui g) {
		ebcustomerGui = (EBCustomerGui) g;
	}

}

