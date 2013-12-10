package GHRestaurant.roles;

import GHRestaurant.gui.GHHostGui;
import restaurant.Restaurant;
import restaurant.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class GHHostRole extends Role implements Host{
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
	private Restaurant restaurant;

	//private String name;
	private Semaphore atDestination = new Semaphore(0,true);

	public GHHostGui hostGui = null;
	private PersonAgent replacementPerson = null;
	
	private enum State {goToWork, leaving, releaveFromDuty, none, working}
	private State state;

	public GHHostRole() {
		super();

		//this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	/*public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}*/

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
				print("customer leaving " + table);
				table.setUnoccupied();
				//waitingCustomers.remove(cust);
				stateChanged();
			}
		}
	}
	
	public void msgCanIGoOnBreak(Waiter w){
		print("Recieved msgCanIGoOnBreak");
		
		if(waiters.size() > 1){
			((GHWaiterRole) w).setWantToGoOnBreak(false);
			((GHWaiterRole) w).setOnBreak(true);
			waiters.remove(w);		
			nextwaiter = (nextwaiter+1)%waiters.size();	
		}
		else{
			print("Cannot give break go back to work please");
			((GHWaiterRole) w).setWantToGoOnBreak(false);
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
				atDestination.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		
		if(!waitingCustomers.isEmpty()  && !waiters.isEmpty()){// && ((GHCashierRole) restaurant.cashier).getgui().getYPos() <= 30){
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
		((GHWaiterRole) waiters.get(nextwaiter)).msgSitAtTable(customer, table.tableNumber);
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

	@Override
	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();		
	}

	@Override
	public void msgReadyToWork(Waiter w) {
		//msgSetWaiter(w);		
	}

	@Override
	public void msgIWantToEat(Customer c) {
		msgIWantFood(c);
	}

	@Override
	public void msgLeavingRestaurant(Customer c) {
		msgLeavingTable(c);		
	}

	@Override
	public void msgCanIBreak(Waiter w) {
		 msgCanIGoOnBreak(w);		
	}

	@Override
	public void msgDoneWorking(Waiter w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goesToWork() {
		state = State.goToWork;
		this.stateChanged();		
	}

	@Override
	public void setGui(Gui g) {
		hostGui = (GHHostGui) g;
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

	@Override
	public void msgCloseRestaurant() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOpenRestaurant() {
		// TODO Auto-generated method stub
		
	}

	public void msgAnimationHasLeftRestaurant() {
		state = State.releaveFromDuty;
		atDestination.release();		
	}
}
