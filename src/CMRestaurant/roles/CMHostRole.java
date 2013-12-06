package CMRestaurant.roles;

import restaurant.Restaurant;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import CMRestaurant.gui.*;
import city.PersonAgent;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.gui.SimCityGui;
import city.roles.Role;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CMHostRole extends Role implements Host {
	
	//a global for the number of tables.
	private int NTABLES = 0; // must start at zero -

	private Semaphore waitingResponse = new Semaphore(0,true);
	PersonAgent replacementPerson = null;
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());

	public Restaurant restaurant;
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	
	public Collection<Table> tables = Collections.synchronizedList(new ArrayList<Table>());
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	public CMHostGui hostGui = null;
	private SimCityGui simCityGui = null;

	public enum cState {waiting, waitingAndTold, eating, done};
	public enum wState {idle, working, askedForBreak}; 
	enum State {none, goToWork, working, leaving, releaveFromDuty};
	State state = State.none;
	
	static final int ZERO = 0;
	
	private class MyCustomer{
		private Customer c;
		private cState state;
		
		MyCustomer(Customer nc, cState s){
			c = nc;
			setState(s);
		}

		public cState getState() {
			return state;
		}

		public void setState(cState state) {
			this.state = state;
		}
	}
	public class MyWaiter{
		public Waiter w;
		private wState state;	
		private int tableCount;
		
		MyWaiter(Waiter nw, wState s, int t){
			w = nw;
			state = s;
			tableCount = t;
		}
	}
	
	public CMHostRole() {
		super();
	}


	// Messages
	public void goesToWork() {
		state = State.goToWork;
		this.stateChanged();
	}

	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	public void msgAnimationHasLeftRestaurant() {
		state = State.releaveFromDuty;
		waitingResponse.release();
	}
	public void msgReadyToWork(Waiter w) {
		/*** Prevents waiter from being added twice ***/
		boolean addWaiter = true;
		synchronized(waiters){
			for(MyWaiter onListWaiter: waiters){
				if(onListWaiter.w == w){
					addWaiter = false;
					onListWaiter.state = wState.working;
				}
			}
		}
		
		if(addWaiter){
			if(((CMWaiterRole) w).getName().toLowerCase().contains("day")){
				for(MyWaiter existingW: waiters){
					if(((CMWaiterRole) existingW.w).getName().toLowerCase().contains("night")){
						existingW.state = wState.idle;
					}
				}
			}else if(((CMWaiterRole) w).getName().toLowerCase().contains("night")){
				for(MyWaiter existingW: waiters){
					if(((CMWaiterRole) existingW.w).getName().toLowerCase().contains("day")){
						existingW.state = wState.idle;
					}
				}
			}

			((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).addWaiterToList(((CMWaiterRole) w).getName());
			waiters.add(new MyWaiter(w, wState.working, ZERO));
		}
		
		this.stateChanged();
	}
	
	public void msgIWantToEat(Customer cust) {
		/*** Prevents customer from being added twice ***/
		boolean addCustomer = true;
		synchronized(customers){
			for(MyCustomer onListCust: customers){
				if(onListCust.c == cust){
					addCustomer = false;
				}
			}
		}
		
		if(addCustomer){
			customers.add(new MyCustomer(cust, cState.waiting));
		}
		
		this.stateChanged();
	}

	public void msgLeavingRestaurant(Customer cust) {
		MyCustomer deleteC = null;
		
		synchronized(customers){
			for (MyCustomer c: customers) {
				if(c.c == cust){
					deleteC = c;
				}
			}
		}
		
		customers.remove(deleteC);
		this.stateChanged();
	}
	
	public void msgTableIsFree(Waiter waiter, int msgerT){
		synchronized(tables){		
		for (Table table : tables) {
			if (table.getTableNumber() == msgerT) {
				MyCustomer c = findCustomer(table);
				MyWaiter w = findWaiter(waiter);
				if(w!=null){
					w.tableCount--;
				}
				if(w.tableCount == 0){
					((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setWaiterBreakable(((CMWaiterRole) w.w).getName()); //enables break check box
				}
				customers.remove(c); //c.state = cState.done;

				table.setUnoccupied();

				((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setTableUnoccupied(table.getTableNumber());
			    	
			   
				this.stateChanged();
			}
		}		
		}
	}

	public void msgCanIBreak(Waiter w) {
		MyWaiter mw = findWaiter(w);
		mw.state = wState.askedForBreak;
		this.stateChanged();
	}

	private MyWaiter findWaiter(Waiter w) {
		synchronized(waiters){
			for(MyWaiter mw: waiters)
			{
				if(mw.w == w)
					return mw;
			}
		}
		return null;
	}
	
	private MyCustomer findCustomer(Table t){
		synchronized(customers){
		for(MyCustomer c: customers)
		{
			if(c.c == t.getOccupant())
			{
				return c;
			}
		}
		}
		return null;
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
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
		MyWaiter tempWaiter = null;
		
		synchronized(waiters){
			for(MyWaiter w: waiters)
			{
				if(w.state == wState.askedForBreak && tempWaiter == null)
				{
					tempWaiter = w;
				}
			}
		}
		
		if(tempWaiter != null){
			waiterBreak(tempWaiter);
			return true;
		}
		
		Table tempTable = null;
		MyCustomer tempCust = null;
		synchronized(tables){
			for (Table table : tables) {
				if (!table.isOccupied() && tempTable == null) {
					if (!customers.isEmpty()) {
						synchronized(customers){
							for(MyCustomer c: customers)
							{
								if(tempCust == null && (c.getState() == cState.waiting || c.getState() == cState.waitingAndTold))
								{
									if(!waiters.isEmpty())
									{
										tempTable = table;
										tempCust = c;
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(tempCust != null && tempTable !=null){
			assignWaiter(tempCust, tempTable);//the action
			return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		
		if (!customers.isEmpty()) {
			synchronized(customers){
				for(MyCustomer c: customers)
				{
					if(c.getState() == cState.waiting && tempCust == null)
					{
						tempCust = c;
					}
				}
			}
		}
		
		if(tempCust != null){
			tellCustNoTables(tempCust);	
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	
	// Actions
	private void tellCustNoTables(MyCustomer c) {
		c.state = cState.waitingAndTold;
		((CMCustomerRole) c.c).msgWaitForOpenTable();
	}
	
	private void waiterBreak(MyWaiter w){
		boolean canBreak = false;
		synchronized(waiters){
			for(MyWaiter w2: waiters)
			{
				if(w2.state == wState.working && w2 != w)
				{
					doLetWaiterBreak(w);
					canBreak = true;
				}
			}
		}
		if(!canBreak)
			dontLetWaiterBreak(w);
	}

	private void doLetWaiterBreak(MyWaiter w) {
		w.state = wState.idle;
		w.w.msgGoOnBreak();
		((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setWaiterOnBreak(((CMWaiterRole) w.w).getName());
	}

	private void dontLetWaiterBreak(MyWaiter w) {
		w.state = wState.working;
		w.w.msgDontGoOnBreak();
		((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setWaiterCantBreak(((CMWaiterRole) w.w).getName());
	}
	
	private void assignWaiter(MyCustomer c, Table t)
	{
		MyWaiter waiter = null;
		synchronized(waiters){		
			for(MyWaiter w: waiters)
			{
				if((waiter == null && w.state ==  wState.working) ||
						(w.state ==  wState.working && w.tableCount < waiter.tableCount)){
					waiter = w;
				}
			}
		}
		
		if(c != null){
			t.setOccupant(c.c);


			((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setTableOccupied(t.getTableNumber());
		 
			c.setState(cState.eating);
			((CMCustomerRole) c.c).msgTableIsReady();
			
			waiter.tableCount++;
			if(waiter.tableCount > 0)
			{
				((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).setWaiterUnbreakable(((CMWaiterRole) waiter.w).getName());
			}
			
			waiter.state = wState.working;
			((CMWaiterRole) waiter.w).msgSitAtTable(c.c, t.tableNumber);
		}
	}

	//utilities

	public void addNewTable() {
		tables.add(new Table(NTABLES++));
		this.stateChanged();
	}
	
	public void setGui(CMHostGui gui) {
		hostGui = gui;
	}

	public CMHostGui getGui() {
		return hostGui;
	}

	private class Table {
		private Customer occupiedBy;
		private int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}
		int getTableNumber(){
			return tableNumber;
		}
		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			StringBuilder msg = new StringBuilder("table " + tableNumber);
			return msg.toString();
		}
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
		
	}

	@Override
	public void msgDoneWorking(Waiter waiter) {
		MyWaiter removeW = null;
		for(MyWaiter w: waiters){
			if(w.w.equals(waiter)){
				removeW = w;
			}
		}
		if(removeW !=null){
			waiters.remove(removeW);
		}
		
		((CMRestaurantAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((CMWaiterRole) waiter).getName());
		
	}







}

