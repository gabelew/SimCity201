package GHRestaurant.roles;

import GHRestaurant.gui.GHHostGui;
import restaurant.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.roles.Role;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class GHHostAgent extends Role implements Host{
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<Customer> waitingCustomers
	= Collections.synchronizedList(new ArrayList<Customer>());
	public List<Waiter> waiters
	= Collections.synchronizedList(new ArrayList<Waiter>());
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	
	private int nextwaiter = 0;

	private String name;
	private Semaphore atDestination = new Semaphore(0,true);

	public GHHostGui hostGui = null;

	public GHHostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	// Messages

	public void msgIWantFood(Customer cust) {
		waitingCustomers.add(cust);
		print("msgIWantFood");
		stateChanged();
	}
	
	public void msgSetWaiter(Waiter wait){
		waiters.add(wait);
		print("msgGoingToWork");
		stateChanged();
	}

	public void msgLeavingTable(Customer cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void msgCanIGoOnBreak(Waiter w){
		print("Recieved msgCanIGoOnBreak");
		
		if(waiters.size() > 1){
			w.setWantToGoOnBreak(false);
			w.setOnBreak(true);
			waiters.remove(w);		
			nextwaiter = (nextwaiter+1)%waiters.size();	
		}
		else{
			print("Cannot give break go back to work please");
			w.setWantToGoOnBreak(false);
		}
		
		stateChanged();
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atDestination.release();// = true;
		stateChanged();
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

		
		if(!waitingCustomers.isEmpty() && !waiters.isEmpty()){
			for(Table table: tables){
				if(!table.isOccupied()){
					seatCustomer(waitingCustomers.get(0),table);
					return true;
				}
				
			}
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(Customer customer, Table table) {
		waiters.get(nextwaiter).msgSitAtTable(customer, table.tableNumber);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		nextwaiter = (nextwaiter+1)%waiters.size();	
	}

	//utilities

	public void setGui(GHHostGui gui) {
		hostGui = gui;
	}

	public GHHostGui getGui() {
		return hostGui;
	}

	private class Table {
		Customer occupiedBy;
		int tableNumber;

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

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}
