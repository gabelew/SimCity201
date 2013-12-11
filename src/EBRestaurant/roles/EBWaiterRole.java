package EBRestaurant.roles;

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
import restaurant.test.mock.EventLog;

/**
 * EB Restaurant Waiter Role
 */
public abstract class EBWaiterRole extends Role implements Waiter {
	static final int NTABLES = 3;//a global for the number of tables.
	public Restaurant restaurant;
	public EventLog log = new EventLog();
	private boolean restaurantClosed=false;
	protected EBRevolvingStandMonitor revolvingStand;
	public boolean checked=false;
	public boolean testingMonitor=false;
	Timer timer=new Timer();
	protected EBWaiterGui waiterGui;
	public List<MyCustomer>Customers=new ArrayList<MyCustomer>();
	public class MyCustomer{
		Customer C;
		int tableNumber;
		public String choice;
		public customerState S;
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
	public enum customerState{waiting,seated,readyToOrder,asked,ordered,reOrder,waitForFood,foodReady,giveFood,eating,wantBill,gaveBill,done};
	public enum wState{none, gotToWork, goingToAskForBreak, askedToBreak, goingOnBreak, onBreak,leaving, relieveFromDuty};
	wState waiterState;
	private String outOf;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCook = new Semaphore(0,true);
	public EBHostGui hostGui = null;
	private Host host;
	public boolean atStart=true;
	private boolean requestBreak=false;
	
	public EBWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant=r;
	}

	public void setHost(Host host){
		this.host=host;
	}


	// Messages

	public void msgSeatCustomer(Customer Customer, int tableNumber){
		Customers.add(new MyCustomer(Customer, tableNumber,customerState.waiting));
		stateChanged();
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Seating customer");

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
				if(!testingMonitor){
				waiterGui.setChoice("", C.tableNumber);
				}
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
				if(!testingMonitor){
				waiterGui.setChoice(c.choice+"?", c.tableNumber);
				}
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
		atCook.release();
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
			if(waiterState == wState.relieveFromDuty&&Customers.size() == 0){
				waiterState = wState.none;
				myPerson.releavedFromDuty(this);
				restaurant.insideAnimationPanel.removeGui(waiterGui);
				return true;
			}
			if(waiterState==wState.leaving){
				waiterState=wState.none;
				workClosed();
				return true;
			}
			if(Customers.size() == 0 && (
					(getName().toLowerCase().contains("day") && myPerson.currentHour >= 11 && myPerson.currentHour <=21) ||
					(getName().toLowerCase().contains("night") && myPerson.currentHour < 10 || myPerson.currentHour >=22))){
				waiterState = wState.none;
				leaveWork();
				return true;
			}
			if(restaurantClosed&&Customers.size() == 0){
				waiterState = wState.leaving;
				return true;
			}
			if(waiterState == wState.gotToWork)
			{
				waiterState = wState.none;
				tellHost();
				return true;
			}
			try{
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.giveFood)
				{
					giveOrderToCustomer(cust);
					return true;
				}
			}
			for (MyCustomer cust : Customers)
			{
				if (cust.S==customerState.foodReady)
				{
					goPickUpFood(cust);
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
			if(revolvingStand == null || checked){
				for (MyCustomer cust : Customers)
				{
					if(cust.S == customerState.ordered)
					{
						giveOrderToCook(cust);
						return true;
					}
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
				if (cust.S==customerState.waiting)
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
		waiterGui.DoLeaveCustomer();
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
	
	private void goPickUpFood(MyCustomer c){
		waiterGui.DoGoToCook();
		if(!testingMonitor){
			try {
				atCook.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		c.S=customerState.giveFood;
		AlertLog.getInstance().logMessage(AlertTag.REST_COOK, this.getName(), "Pick up food from cook");

	}
	
	private void leaveWork() {
		waiterState=wState.none;
		restaurant.host.msgDoneWorking(this);
		waiterGui.DoLeaveRestaurant();
	}
	
	private void workClosed() {
		waiterState=wState.none;
		waiterGui.DoLeaveRestaurant();
	}

	private void tellHost() {
		restaurant.host.msgReadyToWork(this);
	}
	
	private void seatCustomer(MyCustomer mc) {
		mc.S=customerState.seated;
		((EBCustomerRole) mc.C).msgFollowMe(this, mc.tableNumber);
		if(!testingMonitor){
		waiterGui.DoBringToTable(mc.C, mc.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
		}
	}

	public void wantBreak(){
		((EBHostRole) host).msgCanIBreak(this);
	}
	
	public void backFromBreak(){
		((EBHostRole) host).msgBackFromBreak(this);
	}
	private void goTakeOrder(MyCustomer mc){
		if(!testingMonitor){
		waiterGui.DoBringToTable(mc.C, mc.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		((EBCustomerRole) mc.C).msgWhatDoYouWant();
		mc.S=customerState.asked;
		if(!testingMonitor){
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Taking customer's order");

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
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void giveOrderToCook(MyCustomer c);
	
	private void giveBillToCustomer(MyCustomer mc){
		mc.S=customerState.gaveBill;
		waiterGui.DoBringToTable(mc.C,mc.tableNumber);//animation
		if(!testingMonitor){
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		((EBCustomerRole) mc.C).msgBill(mc.amountOwed);
		waiterGui.DoLeaveCustomer();
	}
	
	private void giveOrderToCustomer(MyCustomer mc){
		((EBCookRole) restaurant.cook).msgAnimationTakingFood(mc.tableNumber);
		waiterGui.DoBringToTable(mc.C,mc.tableNumber);//animation
		if(!testingMonitor){
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waiterGui.setChoice(mc.choice, mc.tableNumber);
		mc.S=customerState.eating;
		((EBCustomerRole) mc.C).msgHereIsOrder(mc.choice);
		waiterGui.DoLeaveCustomer();
		mc.check=true;
	}
	
	private void tellHostCustomerLeft(MyCustomer mc){
		if(!testingMonitor){
		((EBHostRole) restaurant.host).msgTableEmpty(mc.tableNumber);
		waiterGui.setChoice("", mc.tableNumber);
		waiterGui.DoLeaveCustomer();
		}
		Customers.remove(mc);
	}
	
	private void createCheck(MyCustomer mc){
		mc.check=false;
		if(!testingMonitor)
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
		waiterState = wState.relieveFromDuty;
		stateChanged();
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void goesToWork() {
		restaurantClosed=false;
		waiterState = wState.gotToWork;
		stateChanged();
	}

	public void msgAskForBreak() {
		requestBreak=true;
	}

	public void setGui(Gui g) {
		waiterGui = (EBWaiterGui) g;
		if(getName().toLowerCase().contains("car")||getName().toLowerCase().contains("poor")){
			if(getName().toLowerCase().contains("night"))
				waiterGui.setWaitingPosition(50,-30);
			else
				waiterGui.setWaitingPosition(50,0);
		}
		else{
			if(getName().toLowerCase().contains("night"))
				waiterGui.setWaitingPosition(0,-30);
		}
	}


	public void msgClosed() {
		restaurantClosed=true;
		stateChanged();
	}
}

