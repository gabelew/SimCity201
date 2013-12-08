package GCRestaurant.roles;

import GCRestaurant.gui.GCWaiterGui;
import GCRestaurant.roles.GCCashierRole.Check;
import GCRestaurant.roles.GCCookRole.Order;
import GCRestaurant.roles.GCHostRole.Table;
import agent.Agent;
import restaurant.Restaurant;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Waiter Agent
 */

public class GCWaiterRole extends Role implements Waiter{
	
	public enum CustomerState{Waiting, Seated, ReadyToOrder, ReorderFood, Ordering, 
		Ordered, FoodCooking, FoodDoneCooking, orderDone, Served, Leaving, Left, checkGiven}
	public enum WaiterEvent{onBreak, none,seatingCustomer, goingToCustomer,
		goingToCook, gettingFood, givingFood, giveCheckToCashier, AlmostOnBreak }
	private WaiterEvent event = WaiterEvent.none;
	public Collection<Table> tables;
	
	private Menu m = new Menu();
	private String name;
	private Semaphore busy = new Semaphore(0,true);
	public boolean onBreak = false;
	Timer timer = new Timer();
	
	//gui
	GCWaiterGui waiterGui = null;
	//link to other agents
	public List<MyCustomer> customers = new ArrayList<MyCustomer>();
	public GCHostRole host;
	private GCCookRole cook;
	public GCCashierRole cashier;
	private final int BREAKTIME = 10000;
	
	public GCWaiterRole(PersonAgent p, Restaurant r) {
		super();

		//this.name = name;
	}
	
	public void setHost(GCHostRole h)
	{
		this.host = h;
	}
	
	public void setCook(GCCookRole c)
	{
		this.cook = c;
	}
	public void setGui(GCWaiterGui wg)
	{
		this.waiterGui = wg;
	}
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}

	public Collection getTables() {
		return tables;
	}
	
/*********************************************
* Messages
 ***********************************************/
	
	//(1) Message from host to seat customer
	public void SeatCustomerMsg(GCCustomerRole customer, GCRestaurant.roles.GCHostRole.Table table)
	{
		print("Received msg SeatCustomer");
		customers.add(new MyCustomer(customer, table));
		stateChanged();
	}
	
	//(2) Message from customer to waiter
	public void ReadyToOrderMsg(GCCustomerRole c)
	{
		for(MyCustomer customer: customers)
		{
			if(customer.c == c)
			{
				customer.state = CustomerState.ReadyToOrder; 
			}
		}
		stateChanged();
	}
	
	// (3) Msg from Customer giving Waiter choice
	public void HereIsChoiceMsg(GCCustomerRole c, String choice)
	{
		for(MyCustomer customer: customers)
		{
			if(customer.c == c)
			{
				customer.choice = choice;
				customer.state = CustomerState.Ordered;
				break;
			}
		}
		stateChanged();
	}
	
	// (4) Msg from Cook that Food is done
	public void getFoodFromCookMsg(Order o)
	{
		try
		{
			for(MyCustomer c: customers)
			{
				if(c.c == o.customer)
				{
					c.state = CustomerState.FoodDoneCooking;
				}
			}
			event = WaiterEvent.givingFood;
			stateChanged();
		}
		catch(Exception e)
		{
			return;
		}
		
	}
	
	// (5) Msg from Customer, Done Eating, Leaving Table
	public void DoneEatingMsg(GCCustomerRole c)
	{
		for(MyCustomer customer : customers)
		{
			if( customer.c == c )
			{
				customer.state = CustomerState.Leaving;
				host.waitingCustomerLeft(c);
				break;
			}
		}
		stateChanged();
	}
	
	// (5) Go on Break
	public void msgAskForBreak() 
	{
		print("I want to go on Break");
		event = WaiterEvent.AlmostOnBreak;
		stateChanged();
	}
	@Override
	public void msgGoOnBreak() {
		// TODO Auto-generated method stub
		
	}

	// (6) Refuses to go on break
	public void msgDontGoOnBreak()
	{
		onBreak = false;
	}
	
	// (7) Food Out of Stock
	public void OutOfStockMsg(GCCustomerRole c)
	{
		for(MyCustomer customer: customers)
		{
			if(customer.c == c)
			{
				customer.state = CustomerState.ReorderFood;
				stateChanged();
			}
		}
	}
	
	// (8) Cashier to Waiter
	public void gotCustomerCheck(Check ck)
	{
		for(MyCustomer customer: customers)
		{
			if(customer.c == ck.customer)
			{
				print("got check from cashier");
				customer.check = ck;
				//stateChanged();
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		busy.release();// = true;
		stateChanged();
	}
/*********************************************
 * Actions
******************************************* */	
	// (1) brings customer to table
		public void FollowMeAction(MyCustomer customer)
		{
			print("Seating Customer " + customer.c.getName());
			waiterGui.getWaitingCustomer();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			customer.c.msgSitAtTable(m);
			customer.table.setOccupant(customer.c);
			seatCustomerAnimation(customer.c, customer.table.tableNumber);
			//Waits for semaphore
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
		
	// (2) Goes to Customer Table
		public void TellMeChoiceAction(MyCustomer c)
		{
			goToCustomerAnimation(c.table.tableNumber);
			//Waits for semaphore
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			print("taking " + c.c.getName() +"'s order" );
			c.c.tellMeOrderMsg();
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
	// (3) gives order to cook
		public void HereIsOrderCookAction(MyCustomer c)
		{
			print("giving order to cook");
			
			//animation details
			waiterGui.goToCook();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			
			//sends msg to cook
			cook.HereIsOrderMsg(this, c.c, c.table, c.choice);
			stateChanged();
		}

	// (4) Gives food to customer
		public void HereIsFoodAction(MyCustomer c)
		{
			print("Getting Table " + c.table.tableNumber +" order");
			waiterGui.goToCook();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			cook.gotFoodMsg();
			print("giving food to table: " + c.table.tableNumber);
			waiterGui.bringFood(c.choice);
			goToCustomerAnimation(c.table.tableNumber);
			//Waits for semaphore
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			c.c.receivedFoodMsg();
			waiterGui.DoLeaveCustomer();
			waiterGui.servedFood();
			stateChanged();
		}
		
		// (5) Gives and Gets Check
		public void giveCheckAction(MyCustomer c)
		{
			waiterGui.goToCashier();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			cashier.msgProduceCheck(this, c.c,c.choice);
			
			waiterGui.goToTable(c.table.tableNumber);
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			c.c.receivedCheckMsg(c.check);
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
		public void onBreakAction()
		{
				//host.takingBreakMsg(this);
				print("I'm going on break");
				timer.schedule(new TimerTask(){
					public void run()
					{		
						onBreak = false;  
						host.breakIsOverMsg(GCWaiterRole.this);
						print("Back from Break");
						stateChanged();
					}
				}, BREAKTIME);
				stateChanged();
		}
		//out of stock
		public void AnotherChoiceAction(MyCustomer c)
		{
			goToCustomerAnimation(c.table.tableNumber);
			//Waits for semaphore
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			print("taking " + c.c.getName() +"'s order" );
			c.c.anotherOrderMsg(c.choice);
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
		
/*********************************************
 * Animation Functions
******************************************* */
		private void seatCustomerAnimation(GCCustomerRole customer, int tableNumber)
		{
			waiterGui.DoBringToTable(customer, tableNumber);
		}
		private void goToCustomerAnimation(int tableNumber)
		{
			//print("GOING TO TABLE");
			waiterGui.goToTable(tableNumber);
		}
/*********************************************
 * Scheduler.  Determine what action is called for, and do it.
******************************************* */
	public boolean pickAndExecuteAnAction() 
	{	
		try
		{
			if(event == WaiterEvent.AlmostOnBreak && customers.size() == 0)
			{
				event = WaiterEvent.onBreak;
				onBreakAction();
				return true; 
			}
			// (1) Seats Waiting Customers
			for (MyCustomer customer : customers) 
			{
				if ( customer.state == CustomerState.Waiting )//&& waiterGui.atStartPos()) 
				{
					event = WaiterEvent.seatingCustomer;
					FollowMeAction(customer);//the action
					customer.state = CustomerState.Seated;
					return true;
				}
			}
			
			// (2) Gets Order from Customer
			for (MyCustomer customer : customers)
			{
				//if there exists a customer whose state is readyToOrder
				//then take the customer's order
				if( customer.state == CustomerState.ReadyToOrder)
				{
					event = WaiterEvent.goingToCustomer;
					customer.state = CustomerState.Ordering;
					TellMeChoiceAction(customer);
					return true;
				}
			}
			// Order out of Stock -> Reorder food
			for (MyCustomer customer : customers)
			{
				//if there exists a customer whose state is readyToOrder
				//then take the customer's order
				if( customer.state == CustomerState.ReorderFood)
				{
					event = WaiterEvent.goingToCustomer;
					customer.state = CustomerState.Ordering;
					AnotherChoiceAction(customer);
					return true;
				}
			}
			
			// (3) Gives Order to Cook
			for (MyCustomer customer : customers)
			{
				//if there exists a customer whose state is ordered
				//give the order to the cook
				if( customer.state == CustomerState.Ordered)
				{
					customer.state = CustomerState.FoodCooking;
					HereIsOrderCookAction(customer);
					return true;
				}
			}
			
			// (4) Gives Order to Customer
			if(event == WaiterEvent.givingFood)
			{
				for (MyCustomer customer : customers)
				{
					//if there exists a customer whose state is ordered
					//give the order to the cook
					if( customer.state == CustomerState.FoodDoneCooking)
					{
						customer.state = CustomerState.Served;
						HereIsFoodAction(customer);
						return true;
					}
				}
			}
			// (6) Handles the check
				for (MyCustomer customer : customers)
				{
					//if there exists a customer whose state is ordered
					//give the order to the cook
					if( customer.state == CustomerState.Served)
					{
						customer.state = CustomerState.checkGiven;
						giveCheckAction(customer);
						return true;
					}
				}
			// (5) Frees Table when customer leaves
			for (MyCustomer customer : customers)
			{
				//if there exists a customer whose state is leaving
				//then send message to host about free table
				if( customer.state == CustomerState.Leaving)
				{
					customer.state = CustomerState.Left;
					host.tableFreeMsg(customer.c);
					customers.remove(customer);
					//onBreakAction();
					return true;
				}
			}
			return false;
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}

	public class MyCustomer 
	{
		public GCCustomerRole c;
		public GCRestaurant.roles.GCHostRole.Table table;
		public CustomerState state;
		public String choice;
		public Check check;
		//public Order order;
		MyCustomer(GCCustomerRole customer, Table t)
		{
			this.c = customer;
			this.table = t;
			this.state = CustomerState.Waiting;
			this.choice = "";
		}
		
	}

	

	@Override
	public void msgLeftTheRestaurant() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Restaurant getRestaurant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void goesToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGui(Gui waiterGuiFactory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Gui getGui() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
		
}

