package EBRestaurant.roles;

import CMRestaurant.gui.CMCustomerGui;
import EBRestaurant.gui.EBHostGui;
import EBRestaurant.gui.EBWaiterGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.*;

/**
 * Restaurant Waiter Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the WaiterAgent. A W is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EBWaiterRole extends Role implements Waiter {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	private Cashier Cashier;
	public Restaurant restaurant;
	private EBWaiterGui waiterGui;
	List<MyCustomer>Customers=new ArrayList<MyCustomer>();
	public class MyCustomer{
		Customer C;
		int tableNumber;
		String choice;
		customerState S;
		float amountOwed;
		boolean check;
		boolean billReady;
		public MyCustomer(Customer customer, int tableNumber2,
				customerState waiting) {
			C=customer;
			tableNumber=tableNumber2;
			S=waiting;
		}
	}
	public enum customerState{waiting,seated,readyToOrder,asked,ordered,reOrder,waitForFood,foodReady,eating,wantBill,gaveBill,done};
	public enum state{none, gotToWork, goingToAskForBreak, askedToBreak, goingOnBreak, onBreak, relieveFromDuty};
	state waiterState;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String outOf;
	private Semaphore atTable = new Semaphore(0,true);

	public EBHostGui hostGui = null;
	private Host host;
	private Cook cook;
	public boolean atStart=true;
	private boolean atCook=false;
	private boolean requestBreak=false;
	
	public EBWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant=r;
	}


	public void setCook(Cook cook) {
		this.cook = cook;
	}
	
	public void setHost(Host host){
		this.host=host;
	}
	
	public void setCashier(Cashier cashier){
		this.Cashier=cashier;
	}


	// Messages

	public void msgSeatCustomer(Customer Customer, int tableNumber){
	Customers.add(new MyCustomer(Customer, tableNumber,customerState.waiting));
	stateChanged();
	}
	
	public void msgReadyToOrder(Customer cust) {
		for(MyCustomer c: Customers)
		{
			if(c.C==cust)
			{
				c.S=customerState.readyToOrder;
				stateChanged();
			}
		}
	}

	public void msgLeavingTable(Customer cust) {
		for (MyCustomer C: Customers){
			if (C.C==cust)
			{
				C.S=customerState.done;
				stateChanged();
				waiterGui.setChoice("", C.tableNumber);
			}
		}
	}

	public void msgHereIsMyOrder(String choice, Customer cust){
		for(MyCustomer c: Customers)
		{
			if(c.C==cust)
			{
				c.S=customerState.ordered;
				c.choice=choice;
				waiterGui.setChoice(c.choice+"?", c.tableNumber);
				stateChanged();
			}
		}
	}
	
	public void msgOrderIsReady(String choice, int tableNumber)
	{
		for (MyCustomer c: Customers){
			if(c.tableNumber==tableNumber){
				c.S=customerState.foodReady;
				stateChanged();
			}
		}
	}
	
	public void msgCheckCreated(float Amount,int tableNumber){
		for (MyCustomer c: Customers){
			if(c.tableNumber==tableNumber){
				c.billReady=true;
				c.amountOwed=Amount;
				stateChanged();
			}
		}
	}
	
	public void msgAtTable() {//from animation
		atTable.release();// = true;
		stateChanged();
		atStart=false;
	}
	
	public void msgAtStart(){
		atStart=true;
		stateChanged();
	}
	
	public void msgAtCook(){
		atCook=true;
		stateChanged();
	}
	
	public void msgOutOfOrder(String choice,int tableNumber){
		for (MyCustomer c: Customers){
			if(c.tableNumber==tableNumber){
				outOf=choice;
				c.S=customerState.reOrder;
				stateChanged();
			}
		}
	}
	
	public void msgWantBill(Customer cust){
		for (MyCustomer c: Customers){
			if(c.C==cust){
				c.S=customerState.wantBill;
				stateChanged();
			}
		}
	}
	
	public void msgDontGoOnBreak(){
		waiterGui.breakDenied();
	}
	
	public void msgGoOnBreak(){
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
			if(waiterState == state.relieveFromDuty){
				waiterState = state.none;
				myPerson.releavedFromDuty(this);
				restaurant.insideAnimationPanel.removeGui(waiterGui);
				return true;
			}
			
			if(Customers.size() == 0 && (
					(getName().toLowerCase().contains("day") && myPerson.currentHour >= 11 && myPerson.currentHour <=21) ||
					(getName().toLowerCase().contains("night") && myPerson.currentHour < 10 || myPerson.currentHour >=22))){
				leaveWork();
				return true;
			}
			if(waiterState == state.gotToWork)
			{
				waiterState = state.none;
				tellHost();
				return true;
			}
			try{
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.foodReady&&atCook)
				{
					giveOrderToCustomer(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.foodReady&&atStart)
				{
					goPickUpFood();
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.readyToOrder)
				{
					goTakeOrder(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.reOrder)
				{
					goReTakeOrder(cust,outOf);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.ordered&&atCook)
				{
					giveOrderToCook(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.wantBill&&cust.billReady&&atStart)
				{
					giveBillToCustomer(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.done){
					tellHostCustomerLeft(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.waiting&&atStart)
				{
					seatCustomer(cust);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.check)
				{
					createCheck(cust);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
				}
			}
			if (requestBreak){
				askForBreak();
			}
		
		return false;
		}
		catch(ConcurrentModificationException e){
			return false;
		}
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void askForBreak(){
		((EBHostRole) host).msgCanIBreak(this);
	}
	
	private void goPickUpFood(){
		waiterGui.DoGoToCook();
	}
	
	private void leaveWork() {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "I am leaving Work.");
		waiterGui.DoLeaveRestaurant();
		restaurant.host.msgDoneWorking(this);
		myPerson.msgDoneEatingAtRestaurant();
		restaurant.insideAnimationPanel.removeGui(waiterGui);
	}

	private void tellHost() {
		restaurant.host.msgReadyToWork(this);
	}
	
	private void seatCustomer(MyCustomer mc) {
		((EBCustomerRole) mc.C).msgFollowMe(this, mc.tableNumber);
		waiterGui.DoBringToTable(mc.C, mc.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mc.S=customerState.seated;
		waiterGui.DoLeaveCustomer();
	}

	public void wantBreak(){
		((EBHostRole) host).msgCanIBreak(this);
	}
	
	public void backFromBreak(){
		((EBHostRole) host).msgBackFromBreak(this);
	}
	private void goTakeOrder(MyCustomer mc){
		waiterGui.DoBringToTable(mc.C, mc.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((EBCustomerRole) mc.C).msgWhatDoYouWant();
		mc.S=customerState.asked;
		waiterGui.DoGoToCook();
	}
	
	private void goReTakeOrder(MyCustomer mc,String outOf){
		waiterGui.DoBringToTable(mc.C, mc.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((EBCustomerRole) mc.C).msgWhatDoYouWantAgain(outOf);
		mc.S=customerState.asked;
		waiterGui.DoLeaveCustomer();
	}
	
	private void giveOrderToCook(MyCustomer mc){
		mc.S=customerState.waitForFood;
		atCook=false;
		((EBCookRole) restaurant.cook).msgHereIsOrder(mc.choice, mc.tableNumber,this);
		waiterGui.DoLeaveCustomer();
	}
	
	private void giveBillToCustomer(MyCustomer mc){
		mc.S=customerState.gaveBill;
		waiterGui.DoBringToTable(mc.C,mc.tableNumber);//animation
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("Here is bill.Please go to Cashier");
		((EBCustomerRole) mc.C).msgBill(mc.amountOwed);
		waiterGui.DoLeaveCustomer();
	}
	
	private void giveOrderToCustomer(MyCustomer mc){
		atCook=false;
		((EBCookRole) restaurant.cook).msgAnimationTakingFood(mc.tableNumber);
		waiterGui.DoBringToTable(mc.C,mc.tableNumber);//animation
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("Here is your "+mc.choice);
		waiterGui.setChoice(mc.choice, mc.tableNumber);
		mc.S=customerState.eating;
		((EBCustomerRole) mc.C).msgHereIsOrder(mc.choice);
		waiterGui.DoLeaveCustomer();
		mc.check=true;
	}
	
	private void tellHostCustomerLeft(MyCustomer mc){
		Do("Done Eating and Leaving");
		((EBHostRole) restaurant.host).msgTableEmpty(mc.tableNumber);
		waiterGui.setChoice("", mc.tableNumber);
		waiterGui.DoLeaveCustomer();
		Customers.remove(mc);
	}
	
	private void createCheck(MyCustomer mc){
		mc.check=false;
		Do("Cashier please create check");
		((EBCashierRole) restaurant.cashier).msgHereIsCheck(this,mc.choice, mc.tableNumber);
	}

	//utilities


	public void setGui(EBWaiterGui g) {
		waiterGui = g;
	}

	public EBWaiterGui getGui() {
		return waiterGui;
	}
	
	/*public void pauseIt(){
		pause();
	}
	
	public void resumeIt(){
		resume();
	}*/


	public void msgLeftTheRestaurant() {
		waiterState = state.relieveFromDuty;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void goesToWork() {
		waiterState = state.gotToWork;
		print("www");
		stateChanged();
	}

	public void msgAskForBreak() {
		requestBreak=true;
	}

	public void setGui(Gui g) {
		waiterGui = (EBWaiterGui) g;
	}
}

