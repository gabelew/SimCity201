package GCRestaurant.roles;

import GCRestaurant.gui.GCAnimationPanel;
import GCRestaurant.gui.GCWaiterGui;
import GCRestaurant.roles.GCCashierRole.Check;
import GCRestaurant.roles.GCHostRole.Table;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;

/**
 * Restaurant Waiter Agent
 */

public abstract class GCWaiterRole extends Role implements Waiter{
	
	public enum CustomerState{Waiting, Seated, ReadyToOrder, ReorderFood, Ordering, 
		Ordered, FoodCooking, FoodDoneCooking, orderDone, Served, Leaving, Left, checkGiven}
	public enum WaiterEvent{onBreak, none,seatingCustomer, goingToCustomer,
		goingToCook, gettingFood, givingFood, giveCheckToCashier, AlmostOnBreak, gotToWork, relieveFromDuty }
	boolean restaurantClosed = false;
	private WaiterEvent event = WaiterEvent.none;
	public Collection<Table> tables;
	
	private Menu m = new Menu();
	private String name;
	protected Semaphore busy = new Semaphore(0,true);
	public boolean onBreak = false;
	Timer timer = new Timer();
	GCRevolvingStandMonitor orderStand;
	boolean checkStand = false;
	
	//gui
	public Restaurant restaurant;
	GCWaiterGui waiterGui = null;
	//link to other agents
	public List<MyCustomer> customers = new ArrayList<MyCustomer>();
	public Host host;
	protected Cook cook;
	public Cashier cashier;
	private final int BREAKTIME = 7000;
	
	public GCWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		this.restaurant = r;
		this.host = r.host;
		this.cook = r.cook;
		this.cashier = r.cashier;
		this.orderStand = ((GCCookRole)restaurant.cook).orderStand;
	}
	
	public void setHost(GCHostRole h)
	{
		this.host = h;
	}
	
	public void setCook(GCCookRole c)
	{
		this.cook = c;
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
	public void setGui(Gui waiterGuiFactory) 
	{
		waiterGui = (GCWaiterGui) waiterGuiFactory;
	}
	public Gui getGui() 
	{
		return (Gui) waiterGui;
	}
	public Restaurant getRestaurant() 
	{
		return restaurant;
	}
	
/*********************************************
* Messages
 ***********************************************/
	
	//(1) Message from host to seat customer
	public void SeatCustomerMsg(Customer customer, Table table)
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
	public void getFoodFromCookMsg(GCOrder o)
	{
			for(MyCustomer c: customers)
			{
				if(c.c == o.customer)
				{
					c.order = o;
					c.state = CustomerState.FoodDoneCooking;
					event = WaiterEvent.givingFood;
				}
			}
			stateChanged();
	}
	 
	// (5) Msg from Customer, Done Eating, Leaving Table
	public void DoneEatingMsg(Customer c)
	{
		for(MyCustomer customer : customers)
		{
			if( customer.c == c )
			{
				customer.state = CustomerState.Leaving;
				((GCHostRole)host).msgLeavingRestaurant(c);
				break;
			}
		}
		stateChanged();
	}
	
	public void msgAskForBreak() 
	{
		print("I want to go on Break");
		event = WaiterEvent.AlmostOnBreak;
		stateChanged();
	}
	// (5) Go on Break
	public void msgGoOnBreak() 
	{
		onBreak = true;
	}

	// (6) Refuses to go on break
	public void msgDontGoOnBreak()
	{
		onBreak = false;
	}
	
	// (7) Food Out of Stock
	public void OutOfStockMsg(Customer c)
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
	
	// (9) Restaurant closes, leave once finished tasks
	public void msgRestaurantClosing() 
	{
		restaurantClosed = true;
		stateChanged();
	}

	//Empty Function, inherited, not needed in this implementation
	public void msgLeftTheRestaurant()
	{
		
	}
/*********************************************
 * Actions
******************************************* */	
	// (1) brings customer to table
		public void FollowMeAction(MyCustomer customer)
		{
			print("Seating Customer " + ((GCCustomerRole)customer.c).getName());
			waiterGui.getWaitingCustomer();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			((GCCustomerRole)customer.c).msgSitAtTable(m);
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
			print("taking " + ((GCCustomerRole)c.c).getName() +"'s order" );
			((GCCustomerRole)c.c).tellMeOrderMsg();
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
	// (3) gives order to cook
		protected abstract void HereIsOrderCookAction(MyCustomer c);
	
	// (4) Gives food to customer
		public void HereIsFoodAction(MyCustomer c)
		{
			print("Getting Table " + c.table.tableNumber +" order");
			waiterGui.goToCook();
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			((GCCookRole)cook).gotFoodMsg(c.order);
			print("giving food to table: " + c.table.tableNumber);
			waiterGui.bringFood(c.choice);
			goToCustomerAnimation(c.table.tableNumber);
			//Waits for semaphore
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			((GCCustomerRole)c.c).receivedFoodMsg();
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
			((GCCashierRole)cashier).msgProduceCheck(this, c.c,c.choice);
			
			waiterGui.goToTable(c.table.tableNumber);
			try {busy.acquire();} 
			catch (InterruptedException e) { e.printStackTrace();}
			((GCCustomerRole)c.c).receivedCheckMsg(c.check);
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
		public void onBreakAction()
		{
			final GCWaiterRole wr = this;
				print("I'm going on break");
				timer.schedule(new TimerTask(){
					public void run()
					{		
						onBreak = false;  
						event = WaiterEvent.none;
						((GCHostRole)host).breakIsOverMsg(wr);
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
			print("taking " + ((GCCustomerRole)c.c).getName() +"'s order" );
			((GCCustomerRole)c.c).anotherOrderMsg(c.choice);
			waiterGui.DoLeaveCustomer();
			stateChanged();
		}
		
		private void tellHostImHere() {
			((GCAnimationPanel)restaurant.insideAnimationPanel).addWaiter(this);
			waiterGui.setHomePosition();
			waiterGui.enterRestaurant();
			restaurant.host.msgReadyToWork(this);
		}
		
		private void shiftOver() 
		{
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "I am leaving Work.");
			((GCAnimationPanel)restaurant.insideAnimationPanel).removeWaiter(this);
			waiterGui.DoLeaveRestaurant();
			restaurant.host.msgDoneWorking(this);
			try {busy.acquire();} 
			catch (InterruptedException e) {}
		}
/*********************************************
 * Animation Functions
******************************************* */
		public void goesToWork() {
			event = WaiterEvent.gotToWork;
			stateChanged();
		}
		private void seatCustomerAnimation(Customer c, int tableNumber)
		{
			waiterGui.DoBringToTable(c, tableNumber);
		}
		private void goToCustomerAnimation(int tableNumber)
		{
			//print("GOING TO TABLE");
			waiterGui.goToTable(tableNumber);
		}
		public void msgDoneWorkingLeave() {
			busy.release();
			event = WaiterEvent.relieveFromDuty;
		}
		public void msgAnimationDone() 
		{
			busy.release();// = true;
			stateChanged();
		}
/*********************************************
 * Scheduler.  Determine what action is called for, and do it.
******************************************* */
	public boolean pickAndExecuteAnAction() 
	{	
		try
		{
			
			if(event == WaiterEvent.relieveFromDuty){
				event = WaiterEvent.none;
				myPerson.releavedFromDuty(this);
				restaurant.insideAnimationPanel.removeGui(waiterGui);
				return true;
			}
			
			if(customers.size() == 0 && (
					((myPerson.getName()).toLowerCase().contains("day") && myPerson.currentHour >= 11 && myPerson.currentHour <=21) ||
					((myPerson.getName()).toLowerCase().contains("night") && myPerson.currentHour < 10 || myPerson.currentHour >=22))){
				shiftOver();
				return true;
			}
			
			if(event == WaiterEvent.gotToWork)
			{
				event = WaiterEvent.none;
				tellHostImHere();
				return true;
			}
			
			if(event == WaiterEvent.onBreak && onBreak)
			{
				return true;
			}
			if(event == WaiterEvent.AlmostOnBreak && customers.size() == 0)
			{
				event = WaiterEvent.onBreak;
				onBreakAction();
				return true; 
			}
			
			if(!restaurant.isOpen&& customers.size() == 0)
			{
				shiftOver();
				return true;
			}
			
			// (4) Gives Order to Customer
			for (MyCustomer customer : customers)
			{
				//if there exists a customer whose state is ordered
				//give the order to the cook
				if( customer.state == CustomerState.FoodDoneCooking)
				{
					HereIsFoodAction(customer);
					customer.state = CustomerState.Served;
					return true;
				}
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
					HereIsOrderCookAction(customer);
					return true;
				}
			}
			
			
			// (6) Handles the check
				for (MyCustomer customer : customers)
				{
					//if there exists a customer whose state is ordered
					//give the order to the cook
					if( customer.state == CustomerState.Served)
					{
						giveCheckAction(customer);
						customer.state = CustomerState.checkGiven;
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
					((GCHostRole)host).tableFreeMsg(customer.c);
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

	/*********************************************
	 * INNER CLASSES
	******************************************* */
	public class MyCustomer 
	{
		public Customer c;
		public Table table;
		public CustomerState state;
		public String choice;
		public Check check;
		public GCOrder order;
		MyCustomer(Customer customer, Table t)
		{
			this.c = customer;
			this.table = t;
			this.state = CustomerState.Waiting;
			this.choice = "";
		}
		
	}
	
}

