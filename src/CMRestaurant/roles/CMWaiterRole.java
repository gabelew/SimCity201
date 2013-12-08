package CMRestaurant.roles;


import java.util.*;
import java.util.concurrent.Semaphore;

import CMRestaurant.gui.CMWaiterGui;
import city.PersonAgent;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.RevolvingStandMonitor;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public abstract class CMWaiterRole extends Role implements Waiter{
	CMWaiterGui waiterGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	public List<MyCustomer> customers	=  Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Restaurant restaurant;
	public enum CustomerState {waiting, seated, askedToOrder, asked, ordered, orderPlaced, 
		orderReady, servingOrder, orderServed, needsCheck, hasCheck, leaving, outOfOrder};
	public enum AgentEvent {none, gotToWork, goingToAskForBreak, askedToBreak, goingOnBreak, onBreak, relieveFromDuty};
	AgentEvent event = AgentEvent.none;
	Timer timer = new Timer();
	private final int THIRTY_SECONDS = 30000;
	protected RevolvingStandMonitor revolvingStand;
	boolean haveNotRecentlyCheckedStand = true;
	
	class MyCustomer{
		private Customer c;
		int table;
		CustomerState s;
		String choice;
		private double check;

		MyCustomer(Customer nc, int t, CustomerState ns, String nchoice){
			c = nc;
			table = t;
			s = ns;
			choice = nchoice;
		}

	}

	public CMWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant = r;
	}

	public void goesToWork(){ //from gui
		event = AgentEvent.gotToWork;
		stateChanged();
	}

	public void msgSitAtTable(Customer c, int table){
		customers.add(new MyCustomer(c, table, CustomerState.waiting, null));
		stateChanged();
	}

	public void msgImReadyToOrder(Customer c){
		MyCustomer mc = findCustomer(c);
		mc.s = CustomerState.askedToOrder;
		stateChanged();
	}

	public void msgHereIsMyOrder(Customer c, String choice){
		MyCustomer mc = findCustomer(c);
		mc.choice = choice;
		mc.s = CustomerState.ordered;
		waitingResponse.release();// = true;
		stateChanged();
	}

	public void msgOrderIsReady(String choice, int table){
		MyCustomer mc = findCustomer(table);
		mc.s = CustomerState.orderReady;
		stateChanged();
	}

	public void msgDoneEatingAndLeaving(Customer c){
		MyCustomer mc = findCustomer(c);
		mc.s = CustomerState.leaving;		
		stateChanged();
	}
	public void msgAskForBreak() {//from gui
		event = AgentEvent.goingToAskForBreak;
		stateChanged();
	}
	
	
	public void msgGoOnBreak() {
		event = AgentEvent.goingOnBreak;
		stateChanged();		
	}
	
	public void msgDontGoOnBreak() {
		event = AgentEvent.none;
		stateChanged();		
	}

	public void msgOutOfOrder(String choice, int table) {
		MyCustomer mc = findCustomer(table);
		mc.s = CustomerState.outOfOrder;
		stateChanged();
	}
	public void msgHereIsCheck(Customer c, double check) {
		MyCustomer mc = findCustomer(c);
		mc.check = check;
		mc.s = CustomerState.needsCheck;
		stateChanged();
	}
	
	//GUI animation msgs
	public void msgAtEntrance(){//from animation
		waitingResponse.release();// = true;
		stateChanged();	
	}

	public void msgAtTable() {//from animation
		waitingResponse.release();// = true;
		stateChanged();
	}
	public void msgAtKitchen(){//from animation
		waitingResponse.release();// = true;
		stateChanged();	
	}
	public void msgAtCashier() {//from animation
		waitingResponse.release();// = true;
		stateChanged();
	}	
	
	public void setGui(CMWaiterGui g) {
		waiterGui = g;
	}

	public CMWaiterGui getGui() {
		return waiterGui;
	}

	private MyCustomer findCustomer(Customer c)
	{
		synchronized(customers){
			for (MyCustomer mc : customers)
			{
				if(mc.c == c)
					return mc;
			}
		}
		return null;
	}

	private MyCustomer findCustomer(int table)
	{

		synchronized(customers){
			for (MyCustomer mc : customers)
			{
				if(mc.table == table)
					return mc;
			}
		}
		return null;
	}

	public boolean pickAndExecuteAnAction() {
		if(event == AgentEvent.relieveFromDuty){
			event = AgentEvent.none;
			myPerson.releavedFromDuty(this);
			restaurant.insideAnimationPanel.removeGui(waiterGui);
			return true;
		}
		
		if(customers.size() == 0 && (
				(getName().toLowerCase().contains("day") && myPerson.currentHour >= 11 && myPerson.currentHour <=21) ||
				(getName().toLowerCase().contains("night") && myPerson.currentHour < 10 || myPerson.currentHour >=22))){
			leaveWork();
			return true;
		}
		if(event == AgentEvent.gotToWork)
		{
			event = AgentEvent.none;
			tellHost();
			return true;
		}
		if(event == AgentEvent.goingToAskForBreak)
		{
			event = AgentEvent.askedToBreak;
			askForBreak();
			return true;
		}
		try{
			if(revolvingStand == null || haveNotRecentlyCheckedStand){
				for (MyCustomer c : customers)
				{
					if(c.s == CustomerState.ordered)
					{
						putInOrder(c);				
						return true;
					}
				}
			}
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.servingOrder)
				{
					serveOrder(c);
					return true;
				}
			}
			
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.needsCheck)
				{
					dropOffCheck(c);
					return true;
				}
			}
	
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.orderReady)
				{
					pickUpOrder(c);
					return true;
				}
			}
			
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.outOfOrder)
				{
					tellBadNews(c);
					return true;
				}
			}
	
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.askedToOrder)
				{
					takeOrder(c);	
					return true;
				}
			}
			
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.waiting)
				{
					seatCustomer(c);
					return true;
				}
			}
			
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.leaving)
				{
					tableFree(c);
					return true;
				}
			}
		}catch(ConcurrentModificationException e){
			return false;
		}
		
		if(event == AgentEvent.goingOnBreak && customers.isEmpty())
		{
			event = AgentEvent.onBreak;
			doGoToBreakPos();
			
			timer.schedule(new TimerTask() {
				public void run() {
					//goesToWork();
					if(event == AgentEvent.onBreak){
						((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setWaiterBackFromBreak(getName());
						stateChanged();
					}
				}
			}, THIRTY_SECONDS);
			
			return false;
		}

		if(waiterGui.waitingSeatNumber < 0){
			doGoToRestPos();
		}
		
		return false;
	}
	
	private void leaveWork() {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "I am leaving Work.");
		waiterGui.DoLeaveRestaurant();
		restaurant.host.msgDoneWorking(this);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}

	private void tellHost() {
		restaurant.host.msgReadyToWork(this);
	}

	private void seatCustomer(MyCustomer c){
			doGoToEntrance();
			
			((CMCustomerRole) c.c).msgFollowMeToTable(((Waiter)this), new Menu());
			
			DoSeatCustomer(c, c.table);
			
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				
			}
			
			c.s = CustomerState.seated;
	}

	private void takeOrder(MyCustomer c) {
		doGoToTable(c.table);
		
		((CMCustomerRole) c.c).msgWhatWouldYouLike();
		c.s = CustomerState.asked;
		
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}

	protected abstract void putInOrder(MyCustomer c);
	
	private void serveOrder(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.orderServed;
		((CMCustomerRole) c.c).msgHereIsYourFood();
		waiterGui.doneServingOrder();
		//doGoToCashier();
		((CMCashierRole) restaurant.cashier).msgProduceCheck(this, c.c, c.choice);
	}

	private void pickUpOrder(MyCustomer c){
		doGoToKitchen(c);
		waiterGui.servingFood(this, c.choice, c.table);
		c.s = CustomerState.servingOrder;
		stateChanged();
	}

	private void tableFree(MyCustomer c) {
		((CMHostRole) restaurant.host).msgTableIsFree(this, c.table);
		customers.remove(c);
	}
	
	public void askForBreak(){
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Can I take a break?");
		restaurant.host.msgCanIBreak(this);
	}
	private void tellBadNews(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.seated;
		((CMCustomerRole) c.c).msgOutOfOrder(c.choice);
	}
	
	private void dropOffCheck(MyCustomer c) {
		c.s = CustomerState.hasCheck;
		doGoToCashier();
		doGoToTable(c.table);
		((CMCustomerRole) c.c).msgHereIsCheck(c.check);
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(MyCustomer customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		waiterGui.DoBringToTable(customer.c, table); 
	}
	
	private void doGoToEntrance(){
		waiterGui.doGoToEntrance();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	
	private void doGoToRestPos(){
		if(event == AgentEvent.onBreak){
			waiterGui.doGoToBreakPos();
		}else{
			waiterGui.doGoToRestPos();
		}
	}

	private void doGoToBreakPos() {
		waiterGui.doGoToBreakPos();
	}
	
	protected void doGoToKitchen(MyCustomer c){
		if(c.s == CustomerState.ordered){
			waiterGui.grabbingFood(c.choice);
			waiterGui.doGoDropOffOrderAtKitchen();
		}else{
			waiterGui.doGoPickUpOrderAtKitchen();
		}
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	
	private void doGoToTable(int table){
		waiterGui.DoBringToTable(table);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	private void doGoToCashier() {
		waiterGui.doGoToCashier();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}

	public void msgLeftTheRestaurant() {
		waitingResponse.release();
		event = AgentEvent.relieveFromDuty;
	}

	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}
	@Override
	public void setGui(Gui g) {
		waiterGui = (CMWaiterGui) g;
	}
}