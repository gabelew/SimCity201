package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;
import restaurant.gui.RestaurantGui;

import java.util.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent {
	
	//a global for the number of tables.
	private static int NTABLES = 0; // must start at zero -
	
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;

	public HostGui hostGui = null;
	private RestaurantGui restGui = null;

	public enum cState {waiting, waitingAndTold, eating, done};
	public enum wState {idle, working, askedForBreak}; 
	
	static final int ZERO = 0;
	
	private class MyCustomer{
		private CustomerAgent c;
		private cState state;
		
		MyCustomer(CustomerAgent nc, cState s){
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
	private class MyWaiter{
		private WaiterAgent w;
		private wState state;	
		private int tableCount;
		
		MyWaiter(WaiterAgent nw, wState s, int t){
			w = nw;
			state = s;
			tableCount = t;
		}
	}
	
	public HostAgent(String name) {
		super();
		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	// Messages
	public void msgReadyToWork(WaiterAgent w) {
		
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
			waiters.add(new MyWaiter(w, wState.working, ZERO));
		}
		
		stateChanged();
	}
	
	public void msgIWantToEat(CustomerAgent cust) {
		
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
		
		stateChanged();
	}

	public void msgLeavingRestaurant(CustomerAgent cust) {
		print("\t\t msgLeavingRestaurant");
		MyCustomer deleteC = null;
		
		synchronized(customers){
			for (MyCustomer c: customers) {
				if(c.c == cust){
					deleteC = c;
				}
			}
		}
		
		customers.remove(deleteC);
		stateChanged();
	}
	
	public void msgTableIsFree(WaiterAgent waiter, int msgerT){
		synchronized(tables){		
		for (Table table : tables) {
			if (table.getTableNumber() == msgerT) {
				MyCustomer c = findCustomer(table);
				MyWaiter w = findWaiter(waiter);
				w.tableCount--;
				/*if(w.tableCount == 0){
					restGui.setWaiterBreakable(w.w.getName()); //enables break check box
				}*/
				customers.remove(c); //c.state = cState.done;

				table.setUnoccupied();
				restGui.setTableUnoccupied(table.getTableNumber());
				stateChanged();
			}
		}		
		}
	}

	public void msgCanIBreak(WaiterAgent w) {
		MyWaiter mw = findWaiter(w);
		mw.state = wState.askedForBreak;
		stateChanged();
	}

	private MyWaiter findWaiter(WaiterAgent w) {
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
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
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
		c.c.msgWaitForOpenTable();
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
		restGui.setWaiterOnBreak(w.w.getName());
		print(w.w.getName() + ", go on break.");
	}

	private void dontLetWaiterBreak(MyWaiter w) {
		w.state = wState.working;
		w.w.msgDontGoOnBreak();
		restGui.setWaiterCantBreak(w.w.getName());
		print(w.w.getName() + ", we don't have enough working waiters to break.");
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
			restGui.setTableOccupied(t.tableNumber);
	
			c.setState(cState.eating);
			c.c.msgTableIsReady();
			
			waiter.tableCount++;
			/*if(waiter.tableCount > 0)
			{
				restGui.setWaiterUnbreakable(waiter.w.getName());
			}*/
			
			waiter.state = wState.working;
			waiter.w.msgSitAtTable(c.c, t.tableNumber);
		}
	}

	//utilities

	public void addNewTable() {
		tables.add(new Table(NTABLES++));
	}
	
	public void setGui(HostGui gui, RestaurantGui rGui) {
		hostGui = gui;
		restGui = rGui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Table {
		private CustomerAgent occupiedBy;
		private int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
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

}

