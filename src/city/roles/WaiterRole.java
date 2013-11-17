package city.roles;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.roles.CustomerRole;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Host;

public class WaiterRole extends Role implements Waiter{
	WaiterGui waiterGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	List<MyCustomer> customers	=  Collections.synchronizedList(new ArrayList<MyCustomer>());
	private Host host;
	private Cook cook;
	private Cashier cashier;
	public enum CustomerState {waiting, seated, askedToOrder, asked, ordered, orderPlaced, 
		orderReady, servingOrder, orderServed, needsCheck, hasCheck, leaving, outOfOrder};
	public enum AgentEvent {none, gotToWork, goingToAskForBreak, askedToBreak, goingOnBreak, onBreak};
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
	/*public class Menu{
		public List<MenuItem> menuItems = new ArrayList<MenuItem>();

		Menu(){
			menuItems.add(new MenuItem("Salad", Cashier.SALAD_COST));
			menuItems.add(new MenuItem("Steak", Cashier.STEAK_COST));
			menuItems.add(new MenuItem("Chicken", Cashier.CHICKEN_COST));
			menuItems.add(new MenuItem("Burger", Cashier.BURGER_COST));
			menuItems.add(new MenuItem("Cookie", Cashier.COOKIE_COST));
		}
	}
	public class MenuItem{
		public String item;
		public double cost;
		MenuItem(String i, double d){
			item = i;
			cost = d;
		}
	}*/

	public WaiterRole(PersonAgent p) {
		super(p);
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
	
	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	/**
	 * hack to establish connection to Cook agent.
	 */
	public void setCook(Cook cook) {
		this.cook = cook;
		waiterGui.setCookGui(cook.cookGui);
	}
	
	/**
	 * hack to establish connection to Cook agent.
	 */
	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
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
		doGoToRestPos();
		return false;
	}


	private void tellHost() {
		print("Reporting for Duty");
		host.msgReadyToWork(this);
	}

	private void seatCustomer(MyCustomer c){
			doGoToEntrance();
			
			StringBuilder msg = new StringBuilder("Follow me " + c.c.getName());
			print(msg.toString());
			c.c.msgFollowMeToTable(((Waiter)this), new Menu());
			
			DoSeatCustomer(c, c.table);
			
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			c.s = CustomerState.seated;
	}

	private void takeOrder(MyCustomer c) {
		doGoToTable(c.table);
		
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.asked;
		print("What would you like to order?");
		
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void putInOrder(MyCustomer c) {
		doGoToKitchen(c);
		print("\t\t HERHER IM IN DA KITCH");
		c.s = CustomerState.orderPlaced;
		print("\t\t BEFORE THE PAUSE msgHereIsOrder");
		cook.msgHereIsOrder(this, c.choice, c.table);
		print("\t\t msgHereIsOrder");
		waiterGui.placedOrder();
		print("\t\t waiterGui.placedOrder");
	}

	private void serveOrder(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.orderServed;
		print("serving food");
		c.c.msgHereIsYourFood();
		waiterGui.doneServingOrder();
		//doGoToCashier();
		cashier.msgProduceCheck(this, c.c, c.choice);
	}

	private void pickUpOrder(MyCustomer c){
		print("Waiter grabbing food");
		doGoToKitchen(c);
		waiterGui.servingFood(this, c.choice, c.table);
		c.s = CustomerState.servingOrder;
		stateChanged();
	}

	private void tableFree(MyCustomer c) {
		host.msgTableIsFree(this, c.table);
		customers.remove(c);
	}
	
	public void askForBreak(){
		print(host.getName() + ", can I go on break?");
		host.msgCanIBreak(this);
	}
	private void tellBadNews(MyCustomer c) {
		doGoToTable(c.table);
		c.s = CustomerState.seated;
		c.c.msgOutOfOrder(c.choice);
		print(c.c.getName() + ", we are out of " + c.choice);
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
		StringBuilder msg = new StringBuilder("Seating " + customer + " at " + table);
		print(msg.toString());
		waiterGui.DoBringToTable(customer.c, table); 
	}
	
	private void doGoToEntrance(){
		waiterGui.doGoToEntrance();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	
	private void doGoToTable(int table){
		waiterGui.DoBringToTable(table);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void doGoToCashier() {
		waiterGui.doGoToCashier();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}