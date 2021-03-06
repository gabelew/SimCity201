package GCRestaurant.roles;

import GCRestaurant.gui.GCHostGui;
import GCRestaurant.gui.GCAnimationPanel;
import restaurant.Restaurant;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;
import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Host Agent
 */

public class GCHostRole extends Role implements Host 
{
	static final int NTABLES = 4;
	//lists 
	public List<myWaiter> waiters = Collections.synchronizedList(new ArrayList<myWaiter>());
	public List<Customer> waitingCustomers = Collections.synchronizedList(new ArrayList<Customer>());
	public Collection<Table> tableList;
	//enums
	private enum WaiterState{askedForBreak, onBreak, Working};
	enum State {none, goToWork, working, leaving, releaveFromDuty, aboutToClose, closing};
	State state = State.none;
	//backend animation
	public Restaurant restaurant;
	PersonAgent replacementPerson = null;
	public GCHostGui hostGui = null;
	//animation variables
	private Semaphore waitingResponse = new Semaphore(0,true);
	boolean restaurantClosed = false;

	//default constructor
	public GCHostRole() 
	{
		super();
		tableList = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tableList.add(new Table(ix));//how you add to a collections
		}
	}

	public Gui getGui() 
	{
		return (Gui) hostGui;
	}
	public void setRestaurant(Restaurant r) 
	{
		this.restaurant = r;
		((GCAnimationPanel) restaurant.insideAnimationPanel).setHost(this);
	}
	public List getWaitingCustomers() 
	{
		return waitingCustomers;
	}

	public Collection getTables() 
	{
		return tableList;
	}
	public void setGui(Gui GuiFactory) 
	{
		hostGui = (GCHostGui) GuiFactory;	
	}


	/**************************************************
	 * Messages
	 **************************************************/
	
	public void msgIWantToEat(Customer cust) {
		waitingCustomers.add(cust);
		((GCAnimationPanel) restaurant.insideAnimationPanel).addCustomerToList(((GCCustomerRole) cust).myPerson.getName());
		stateChanged();
	}
	// msg from waiter, add a free table to lit
	public void tableFreeMsg(Customer c) 
	{
		for (Table table : tableList) {
			if (table.getOccupant() == c) {
				print(c + " leaving " + table);
				((GCAnimationPanel) restaurant.insideAnimationPanel).removeCustomerFromList(((GCCustomerRole) c).myPerson.getName());
				table.setUnoccupied();
				table.waiter.customers--;
				stateChanged();
			}
		}
	}
	
	public void breakIsOverMsg(Waiter waiter)
	{
		for(myWaiter w: waiters)
		{
			if( w.w == waiter)
			{
				w.state = WaiterState.Working; 
				((GCAnimationPanel) restaurant.insideAnimationPanel).setWaiterWorking(((GCWaiterRole)w.w).myPerson.getName());
			}
		}
	}
	
	public void msgLeavingRestaurant(Customer cust) {
		synchronized(waitingCustomers)
		{
			print("Customer: " + ((GCCustomerRole)cust).getName() + ", decided to leave");
			for (Customer c: waitingCustomers) 
			{
				if(c == cust)
				{
					((GCAnimationPanel) restaurant.insideAnimationPanel).removeCustomerFromList(((GCCustomerRole) c).myPerson.getName());
					waitingCustomers.remove(c);
					break;
				}
			}
		}
		this.stateChanged();
	}
	
	public void msgReadyToWork(Waiter w) 
	{
		boolean addWaiter = true;
			for(myWaiter onListWaiter: waiters)
			{
				if(onListWaiter.w == w){addWaiter = false;}
			}
		myWaiter newWaiter = new myWaiter(w);
		if(addWaiter)
		{
			waiters.add(newWaiter);
			((GCAnimationPanel) restaurant.insideAnimationPanel).addWaiterToList(((GCWaiterRole) w).myPerson.getName());
		}
	}
	
	public void msgReleaveFromDuty(PersonAgent p) 
	{
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	//needed for interface, not needed for GCRestaurant
	public void msgCanIBreak(String name) 
	{
		for(myWaiter w: waiters)
		{
			if( ((GCWaiterRole)w.w).myPerson.getName().equals(name))
			{
				w.state = WaiterState.askedForBreak; 
				break;
			}		
		}
		
		stateChanged();
	}

	public void msgDoneWorking(Waiter waiter) 
	{
		for(myWaiter w: waiters){
			if(w.w.equals(waiter))
			{
				((GCAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((GCWaiterRole) w.w).myPerson.getName());
				waiters.remove(w);
			}
		}
		
	}
	public void msgAnimationHasLeftRestaurant() 
	{
		state = State.releaveFromDuty;
		waitingResponse.release();
	}

	public void msgCloseRestaurant() 
	{
		restaurantClosed = true;
		state = State.aboutToClose;
		stateChanged();
	}

	public void msgOpenRestaurant() 
	{
		restaurantClosed = false;
		state = State.none;
		stateChanged();
	}
	/****************************************************
	 * Actions
	 ***************************************************/

	//Selects a waiter and has that waiter seat customer
	/*
	 * Seating will go sequentially, from Waiter 1 - Waiter N
	 * then start over again after N
	 */
	private void ActionSeatCustomer(myWaiter waiter, Customer customer, Table table) 
	{
		if(waitingCustomers.size() > 0)
		{
			//increases customer count
			waiter.customers++;
			//Sends Messages
			print("telling waiter to seat " + ((GCCustomerRole)customer).getName());
			((GCWaiterRole) waiter.w).SeatCustomerMsg(customer, table);
			table.setOccupant(customer);
			table.waiter = waiter;
			((GCCustomerRole)customer).setWaiter(waiter.w);//hack
			waitingCustomers.remove(0); //pops first customer out of list
			stateChanged();
		}
	}

	private void grantBreakAction(myWaiter w)
	{
		print("processing break");
		int workingWaiters = 0;
		for(myWaiter waiter: waiters)
		{
			if(waiter.state == WaiterState.Working)
			{
				workingWaiters++;
			}
		}
		if(workingWaiters == 0)
		{
			w.state = WaiterState.Working;
			print(((GCWaiterRole)w.w).myPerson.getName() + ", you can't be on break!");
			w.w.msgDontGoOnBreak();
			((GCAnimationPanel) restaurant.insideAnimationPanel).setWaiterCantBreak(((GCWaiterRole)w.w).myPerson.getName());
		}
		else
		{
			print(((GCWaiterRole)w.w).myPerson.getName() + ", you can go on break...");
			w.state = WaiterState.onBreak;
			w.w.msgGoOnBreak();
		}
		stateChanged();
	}
	private boolean checkTablesFull()
	{
		int occupiedTables = 0;
		for(Table table: tableList)
		{
			if(table.isOccupied()){ occupiedTables++;}
		}
		if(occupiedTables == NTABLES)
		{
			return true;
		}
		return false;
	}
	/****************************************************
	 * Scheduler.  Determine what action is called for, and do it.
	 ***************************************************/
	public boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		try
		{
			/**
			 * Animation to enter restaurant ->> START
			 */
			if(state == State.releaveFromDuty){
				state = State.none;
				myPerson.releavedFromDuty(this);
				if(replacementPerson != null){
					replacementPerson.waitingResponse.release();
				}
			}
			if(state == State.goToWork){
				state = State.working;
				hostGui.DoEnterRestaurant();
				return true;
			}
			if(state == State.leaving){
				state = State.none;
				hostGui.DoLeaveRestaurant();
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return true;
			}
			//turns away customers if restaurant is closed
			if(restaurantClosed)
			{
				for(Customer c : waitingCustomers)
				{
					((GCCustomerRole)c).restaurantClosedMsg();
					waitingCustomers.remove(c);
				}
			}
			if(state == State.aboutToClose)
			{
				for(myWaiter w : waiters)
				{
					((GCWaiterRole)w.w).msgRestaurantClosing();
				}
				state = State.closing;
			}
			
			if(!restaurant.isOpen && waiters.size() == 0 )//state == State.closing
			{
				//print("^^^ CLOSING NOW");
				((GCCashierRole)restaurant.cashier).msgRestaurantClosing();
				((GCCookRole)restaurant.cook).msgRestaurantClosing();
				hostGui.DoLeaveRestaurant();
				try {waitingResponse.acquire();} 
				catch (InterruptedException e) { e.printStackTrace(); }
				
			}
			/**
			 * Animation to enter restaurant ->> END
			 */
			myWaiter w = null;
			//Keeps Scheduler active until waiter appears
			if(!(waiters.size() > 0))
			{
				return true; 
			}
	
			for(myWaiter wait: waiters)
			{
				if(wait.state == WaiterState.askedForBreak)
				{
					grantBreakAction(wait);
					return true;
				}
			}
			if(!waitingCustomers.isEmpty())
			{
				for(Customer c : waitingCustomers)
				{
					if(checkTablesFull())
					{
						((GCCustomerRole)c).restaurantFullMsg();
						return true;
					}
				}
			}
			
			for (Table table : tableList) 
			{
				if (!table.isOccupied() && !waitingCustomers.isEmpty()) 
				{
					for(myWaiter waiter : waiters)
					{
						if(waiter.state != WaiterState.onBreak)
						{
							if(waiter.customers==0)
							{
								w = waiter;
								break;
							}
							else if(w==null)
							{
								w = waiter;
							}
							else if(waiter.customers<w.customers)
							{
								w = waiter;
							}
						}
					}
					if(w!=null && !table.isOccupied() && waitingCustomers.size() != 0)
					{
						((GCCustomerRole)waitingCustomers.get(0)).setTableNumber(table.tableNumber);
						print("Seat customer at table: " + table.tableNumber);
						ActionSeatCustomer(w, waitingCustomers.get(0), table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
			return false;
			//we have tried all our rules and found
			//nothing to do. So return false to main loop of abstract agent
			//and wait.
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}

	//Utility class to manage breaks
	public class myWaiter
	{
		Waiter w;
		WaiterState state;
		int customers;
		public myWaiter(Waiter waiter)
		{
			customers = 0;
			this.w = waiter;
			state = WaiterState.Working;
		}
	}

	public class Table {
		Customer occupiedBy;
		myWaiter waiter;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer customer) {
			occupiedBy = customer;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	
	/**
	 * Animation Methods
	 */
	public void goesToWork() {
		state = State.goToWork;
		this.stateChanged();
	}

	//unused method for this implementation
	public void msgCanIBreak(Waiter w) {
		// TODO Auto-generated method stub
		
	}
	
}

