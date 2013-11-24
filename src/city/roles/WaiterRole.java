package city.roles;


import java.util.*;
import java.util.concurrent.Semaphore;
import city.PersonAgent;
import restaurant.Restaurant;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class WaiterRole extends Role implements Waiter{
	WaiterGui waiterGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	public List<MyCustomer> customers	=  Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Restaurant restaurant;
	public enum CustomerState {waiting, seated, askedToOrder, asked, ordered, orderPlaced, 
		orderReady, servingOrder, orderServed, needsCheck, hasCheck, leaving, outOfOrder};
	public enum AgentEvent {none, gotToWork, goingToAskForBreak, askedToBreak, goingOnBreak, onBreak, relieveFromDuty};
	AgentEvent event = AgentEvent.none;
	
	private class MyCustomer{
		private Customer c;
		private int table;
		private CustomerState s;
		private String choice;
		private double check;

		MyCustomer(Customer nc, int t, CustomerState ns, String nchoice){
			c = nc;
			table = t;
			s = ns;
			choice = nchoice;
		}

	}

	public WaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant = r;
	}

	public void goesToWork(){ //from gui
		event = AgentEvent.gotToWork;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	public void msgSitAtTable(Customer c, int table){
		customers.add(new MyCustomer(c, table, CustomerState.waiting, null));
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	public void msgImReadyToOrder(Customer c){
		MyCustomer mc = findCustomer(c);
		mc.s = CustomerState.askedToOrder;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	public void msgHereIsMyOrder(Customer c, String choice){
		MyCustomer mc = findCustomer(c);
		mc.choice = choice;
		mc.s = CustomerState.ordered;
		waitingResponse.release();// = true;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	public void msgOrderIsReady(String choice, int table){
		MyCustomer mc = findCustomer(table);
		mc.s = CustomerState.orderReady;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	public void msgDoneEatingAndLeaving(Customer c){
		MyCustomer mc = findCustomer(c);
		mc.s = CustomerState.leaving;		
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}
	public void msgAskForBreak() {//from gui
		event = AgentEvent.goingToAskForBreak;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}
	
	
	public void msgGoOnBreak() {
		event = AgentEvent.goingOnBreak;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}		
	}
	
	public void msgDontGoOnBreak() {
		event = AgentEvent.none;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}		
	}

	public void msgOutOfOrder(String choice, int table) {
		MyCustomer mc = findCustomer(table);
		mc.s = CustomerState.outOfOrder;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}
	public void msgHereIsCheck(Customer c, double check) {
		MyCustomer mc = findCustomer(c);
		mc.check = check;
		mc.s = CustomerState.needsCheck;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}
	
	//GUI animation msgs
	public void msgAtEntrance(){//from animation
		waitingResponse.release();// = true;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}	
	}

	public void msgAtTable() {//from animation
		waitingResponse.release();// = true;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}
	public void msgAtKitchen(){//from animation
		waitingResponse.release();// = true;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}	
	}
	public void msgAtCashier() {//from animation
		waitingResponse.release();// = true;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}	
	
	public void setGui(WaiterGui g) {
		waiterGui = g;
	}

	public WaiterGui getGui() {
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
			for (MyCustomer c : customers)
			{
				if(c.s == CustomerState.ordered)
				{
					putInOrder(c);				
					return true;
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
			return false;
		}

		if(waiterGui.waitingSeatNumber < 0){
			doGoToRestPos();
		}
		
		return false;
	}


	private void leaveWork() {
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
			
			c.c.msgFollowMeToTable(((Waiter)this), new Menu());
			
			DoSeatCustomer(c, c.table);
			
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				
			}
			
			c.s = CustomerState.seated;
	}

	private void takeOrder(MyCustomer c) {
		doGoToTable(c.table);
		
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.asked;
		
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}

	private void putInOrder(MyCustomer c) {
		doGoToKitchen(c);
		c.s = CustomerState.orderPlaced;
		restaurant.cook.msgHereIsOrder(this, c.choice, c.table);
		waiterGui.placedOrder();
	}

	private void serveOrder(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.orderServed;
		c.c.msgHereIsYourFood();
		waiterGui.doneServingOrder();
		//doGoToCashier();
		restaurant.cashier.msgProduceCheck(this, c.c, c.choice);
	}

	private void pickUpOrder(MyCustomer c){
		doGoToKitchen(c);
		waiterGui.servingFood(this, c.choice, c.table);
		c.s = CustomerState.servingOrder;
		if(myPerson.getStateChangePermits()==0){
			stateChanged();	
		}
	}

	private void tableFree(MyCustomer c) {
		restaurant.host.msgTableIsFree(this, c.table);
		customers.remove(c);
	}
	
	public void askForBreak(){
		restaurant.host.msgCanIBreak(this);
	}
	private void tellBadNews(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.seated;
		c.c.msgOutOfOrder(c.choice);
	}
	
	private void dropOffCheck(MyCustomer c) {
		c.s = CustomerState.hasCheck;
		doGoToCashier();
		doGoToTable(c.table);
		c.c.msgHereIsCheck(c.check);
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
	
	private void doGoToKitchen(MyCustomer c){
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
	
}