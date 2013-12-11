package GLRestaurant.roles;

import agent.Agent;
import CMRestaurant.roles.CMWaiterRole;
import CMRestaurant.roles.CMHostRole.MyWaiter;
import CMRestaurant.roles.CMHostRole.wState;
import GLRestaurant.gui.GLHostGui;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CopyOnWriteArrayList;

import city.PersonAgent;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.animationPanels.GLRestaurantAnimationPanel;
import city.gui.Gui;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class GLHostRole extends Role implements Host{
	static final int NTABLES = 3;//a global for the number of tables.
	private class MyWaiter {
		GLWaiterRole w;
		int customers;
		waiterState ws = waiterState.serving;
		MyWaiter(GLWaiterRole waiter) {
			this.w = waiter;
		}
	}

	private class MyCustomer {
		GLCustomerRole c;
		customerState cs;
		Table t;
		MyCustomer(GLCustomerRole cust, customerState state) {
			this.c = cust;
			this.cs = state;
		}
	}
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<Table> tables = Collections.synchronizedList(new ArrayList<Table>());
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public enum waiterState {serving, wantToGoOnBreak, onBreak, offDuty};
	public enum customerState {waiting, seated, done};
	enum State {none, goToWork, working, leaving, relieveFromDuty};
	State state = State.none;
	boolean firstRestock = false;
	private boolean closeRestaurant = false;
	private Semaphore waitingResponse = new Semaphore(0,true);

	PersonAgent replacementPerson = null;
	public Restaurant restaurant;

	public GLHostGui hostGui = null;

	public GLHostRole() {
		super();
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	public List getCustomers() {
		return customers;
	}

	public Collection getTables() {
		return tables;
	}
	// Messages

	public void goesToWork() {
		state = State.goToWork;
		stateChanged();
	}
	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	public void msgAnimationHasLeftRestaurant() {
		state = State.relieveFromDuty;
		waitingResponse.release();
	}
	
	public void msgIWantFood(GLCustomerRole cust) {
		customers.add(new MyCustomer(cust, customerState.waiting));
		((GLRestaurantAnimationPanel) restaurant.insideAnimationPanel).addCustomerToList(cust.myPerson.getName()); 

		int seatedCount = 0;
		for(MyCustomer mc : customers) {
			if(customerState.seated == mc.cs) {
				seatedCount++;
			}
		}
		if(seatedCount >= NTABLES) {
			cust.msgRestaurantFull();
		}
		stateChanged();
	}
	
	public void msgIAmLeaving(GLCustomerRole c) {
		MyCustomer mc = findCustomer(c);
		if(mc != null) {
			mc.cs = customerState.done;
			customers.remove(mc.c);
			((GLRestaurantAnimationPanel) restaurant.insideAnimationPanel).removeCustomerFromList(c.myPerson.getName()); 
		}
		stateChanged();
	}
	
	public void msgIWantToGoOnBreak(GLWaiterRole w) {
		MyWaiter mw = findWaiter(w);
		mw.ws = waiterState.wantToGoOnBreak;
		stateChanged();
	}
	
	public void msgFinishedBreak(GLWaiterRole w) {
		MyWaiter mw = findWaiter(w);
		mw.ws = waiterState.serving;
		stateChanged();
	}

	public void msgTableAvailable(GLWaiterRole w, GLCustomerRole c, int tableNum) {
		MyCustomer mc = findCustomer(c); 
		MyWaiter mw = findWaiter(w);
		Table table = findTable(tableNum);
		Do("Table " + table.tableNumber + " now available.");
		mc.cs = customerState.done;
		mw.customers--;
		mc.t = null;
		table.setUnoccupied();
		customers.remove(mc);
		((GLRestaurantAnimationPanel) restaurant.insideAnimationPanel).removeCustomerFromList(c.myPerson.getName()); 
		stateChanged();
	}
	
	public void msgCookHasRestocked() {
		Do("Cook has completed first restock. Customers can now be seated.");
		firstRestock = true;
		stateChanged();
	}
	
	@Override
	public void msgReadyToWork(Waiter w) {
		boolean addWaiter = true;
		synchronized(waiters) {
			for(MyWaiter onListWaiter: waiters) {
				if (w.equals(onListWaiter.w)) {
					addWaiter = false;
					onListWaiter.ws = waiterState.serving;
				}
			}
		}
		
		if(addWaiter) {
			if(((GLWaiterRole) w).getName().toLowerCase().contains("day")){
				for(MyWaiter existingW: waiters){
					if(((GLWaiterRole) existingW.w).getName().toLowerCase().contains("night")){
						existingW.ws = waiterState.offDuty;
					}
				}
			}else if(((GLWaiterRole) w).getName().toLowerCase().contains("night")){
				for(MyWaiter existingW: waiters){
					if(((GLWaiterRole) existingW.w).getName().toLowerCase().contains("day")){
						existingW.ws = waiterState.offDuty;
					}
				}
			}
			
			((GLRestaurantAnimationPanel) restaurant.insideAnimationPanel).addWaiterToList(((GLWaiterRole)w).myPerson.getName());
			waiters.add(new MyWaiter((GLWaiterRole)w));
		}
		
		stateChanged();
	}

	@Override
	public void msgIWantToEat(Customer c) {
		msgIWantFood((GLCustomerRole)c);
	}

	@Override
	public void msgLeavingRestaurant(Customer c) {
		msgIAmLeaving((GLCustomerRole)c);
	}

	@Override
	public void msgCanIBreak(Waiter w) {
		msgIWantToGoOnBreak((GLWaiterRole)w);	
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
			((GLRestaurantAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((GLWaiterRole)waiter).myPerson.getName());

		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state == State.relieveFromDuty){
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
		
		if(closeRestaurant && customers.size() > 0) {
			print("1 got claled dawg");
			synchronized(customers) {
				for(MyCustomer mc : customers) {
					if (mc.cs == customerState.waiting) {
						askCustomerToLeave(mc);
						return true;
					}
				}
			}
		} 
		
		if(closeRestaurant && customers.isEmpty()) {
			print("2 got called dawg!!");
			askEmployeesToLeave();
			return true;
		}
		
		
		synchronized(customers) {
			for(MyCustomer mc : customers) {
				if (mc.cs == customerState.waiting) {
					AssignCustomerToTable(mc);
					return true;
				}
			}
		}
		
		synchronized(waiters) {
			for(MyWaiter mw: waiters) {
				if (mw.ws == waiterState.wantToGoOnBreak) {
					ReviewBreakRequest(mw);
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
	
	private void askCustomerToLeave(MyCustomer mc) {
		mc.c.msgRestaurantClosed();
		customers.remove(mc);
	}
	
	private void askEmployeesToLeave() {
		print("WHAT IS HAPPENING?!");
		synchronized(waiters) {
			for(MyWaiter mw : waiters) {
				mw.w.msgRestaurantClosed();
			}
		}
		waiters.removeAll(waiters);
		((GLCashierRole) restaurant.cashier).msgRestaurantClosed();
		((GLCookRole) restaurant.cook).msgRestaurantClosed();
		state = State.leaving;
		closeRestaurant = false;
	}
	

	/** 
	 * Find an empty table and tell waiter to seat a waiting customer
	 */
	private void AssignCustomerToTable(MyCustomer mc) {
		//if(firstRestock) {
			MyWaiter mw = findWaiter();
			if (mw != null) {
				Table t = findOpenTable();
				if (t != null) {
					mw.w.msgSitAtTable(mc.c, t.tableNumber);
					mw.ws = waiterState.serving;
					mw.customers++;
					mc.t = t;
					t.setOccupant(mc.c);
					mc.cs = customerState.seated;
				}
			}
		//}
	}
	
	private void ReviewBreakRequest(MyWaiter mw) {
		Do("Reviewing break request from " + mw.w.getName());
		mw.ws = waiterState.serving;
		boolean answer = false;
		int waitersOnBreak = 0;
		if(waiters.size() > 1) {
			synchronized(waiters) {
				for(MyWaiter waiter : waiters) {
					if (waiter.ws == waiterState.onBreak)
						waitersOnBreak++;
				}
			}
			if(waitersOnBreak < waiters.size() - 1) {
				answer = true;
				mw.ws = waiterState.onBreak;
			}
		}
		mw.w.msgBreakRequestReviewed(answer);
	}

	//utilities

	public MyCustomer findCustomer(GLCustomerRole c) {
		MyCustomer mc = null;
		synchronized(customers) {
			for (MyCustomer cust : customers) {
				if (cust.c == c)
					mc = cust;
			}
		}
		return mc;
	}

	public MyWaiter findWaiter(GLWaiterRole w) {
		MyWaiter mw = null;
		synchronized(waiters) {
			for (MyWaiter waiter : waiters) {
				if (waiter.w == w)
					mw = waiter;
			}
		}
		return mw;
	}

	public MyWaiter findWaiter() {
		MyWaiter mw = null;
		if(!waiters.isEmpty()) {
			mw = waiters.get(0);
			synchronized(waiters) {
				for (MyWaiter waiter : waiters) {
					if (mw.ws == waiterState.onBreak && waiter.ws == waiterState.serving)
						mw = waiter;
					if (waiter.customers < mw.customers)
						mw = waiter;
				}
			}
		}
		return mw;
	}

	public Table findTable(int tableNum) {
		Table foundTable = null;
		for (Table table : tables) {
			if (table.tableNumber == tableNum) {
				foundTable = table;
			}
		}
		return foundTable;
	}

	public Table findOpenTable() {
		Table openTable = null;
		for (Table table : tables) {
			if (!table.isOccupied()) {
				openTable = table;
			}
		}
		return openTable;
	}

	public void setGui(GLHostGui gui) {
		hostGui = gui;
	}

	public GLHostGui getGui() {
		return hostGui;
	}
	
	public void addWaiter(GLWaiterRole waiter) {
		waiters.add(new MyWaiter(waiter));
	}

	private class Table {
		GLCustomerRole occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(GLCustomerRole cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		GLCustomerRole getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}
	
	@Override
	public void setGui(Gui waiterGuiFactory) {
		this.hostGui = (GLHostGui)waiterGuiFactory;
	}

	@Override
	public void msgCloseRestaurant() {
		closeRestaurant = true;
		stateChanged();
	}

	@Override
	public void msgOpenRestaurant() {
		state = State.none;
		closeRestaurant = false;
		stateChanged();
	}

}