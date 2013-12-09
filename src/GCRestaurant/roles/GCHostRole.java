package GCRestaurant.roles;

import GCRestaurant.gui.GCHostGui;
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

	public List<Customer> waitingCustomers = Collections.synchronizedList(new ArrayList<Customer>());
	public Collection<Table> tableList;
	private Semaphore waitingResponse = new Semaphore(0,true);
	public Restaurant restaurant;
	PersonAgent replacementPerson = null;
	public GCHostGui hostGui = null;
	public List<myWaiter> waiters = Collections.synchronizedList(new ArrayList<myWaiter>());
	private enum WaiterState{askedForBreak, onBreak, Working, deniedBreak};
	enum State {none, goToWork, working, leaving, releaveFromDuty};
	State state = State.none;

	public GCHostRole() 
	{
		super();
		// make some tables
		tableList = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tableList.add(new Table(ix));//how you add to a collections
		}
	}

	public Gui getGui() {
		return (Gui) hostGui;
	}

	public void setRestaurant(Restaurant r) {
		this.restaurant = r;
	}
	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tableList;
	}

	/**************************************************
	 * Messages
	 **************************************************/
	
	public void msgIWantToEat(Customer cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}
	// msg from waiter, add a free table to lit
	public void tableFreeMsg(Customer c) 
	{
		for (Table table : tableList) {
			if (table.getOccupant() == c) {
				print(c + " leaving " + table);
				table.setUnoccupied();
				table.waiter.customers--;
				stateChanged();
			}
		}
	}

	public void takingBreakMsg(Waiter waiter)
	{
		print(waiter + "asked for a break");
		for(myWaiter w: waiters)
		{
			if( w.w == waiter){ w.state = WaiterState.askedForBreak; }		
		}
		stateChanged();
	}
	
	public void breakIsOverMsg(Waiter waiter)
	{
		for(myWaiter w: waiters)
		{
			if( w.w == waiter){ w.state = WaiterState.Working; }
		}
	}
	
	public void msgLeavingRestaurant(Customer cust) {
		synchronized(waitingCustomers)
		{
			print("Customer: " + ((GCCustomerRole)cust).getName() + ", decided to leave");
			for (Customer c: waitingCustomers) 
			{
				if(c == cust){
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
		if(addWaiter){waiters.add(newWaiter);}
	}
	
	public void msgReleaveFromDuty(PersonAgent p) 
	{
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	//needed for interface, not needed for GCRestaurant
	public void msgCanIBreak(Waiter w) {}

	@Override
	public void msgDoneWorking(Waiter waiter) 
	{
		for(myWaiter w: waiters){
			if(w.w.equals(waiter))
			{
				waiters.remove(w);
			}
		}
		//((GCRestaurantAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((CMWaiterRole) waiter).getName());
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
		if(waiters.size() == 1)
		{
			w.state = WaiterState.deniedBreak;
			print(((GCWaiterRole)w.w).getName() + ", you can't be on break!");
			w.w.msgDontGoOnBreak();
		}
		else
		{
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
					if(w!=null && !table.isOccupied())
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

	public void setGui(Gui GuiFactory) {
		hostGui = (GCHostGui) GuiFactory;
		
	}

	public void msgAnimationHasLeftRestaurant() {
		state = State.releaveFromDuty;
		waitingResponse.release();
	}

	@Override
	public void msgCloseRestaurant() {
		// TODO Auto-generated method stub
		
	}

	
}

