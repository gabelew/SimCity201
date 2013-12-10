package EBRestaurant.roles;

import EBRestaurant.gui.EBAnimationPanel;
import EBRestaurant.gui.EBHostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EBHostRole extends Role implements Host {
	public Restaurant restaurant;
	private boolean restaurantClosed=false;
	private Semaphore waitingResponse = new Semaphore(0,true);
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<Customers> waitingCustomers
	= new ArrayList<Customers>();
	PersonAgent replacementPerson = null;
	public class Customers{
		Customer cust;
		custState state;
		int waitArea;
		public Customers(Customer c2, custState first){
			cust=c2;
			state=first;
		}
	}
	public enum custState {staying,goToArea,assigned,waiting};
	public enum state {none,closed,goToWork,atWork,leaveWork,relieveDuty,done};
	state hostState;
	public List<MyWaiters> waiters
	= new ArrayList<MyWaiters>();
	public class MyWaiters{
		public Waiter w;
		Boolean onBreak;
		int numCustomers;
		public MyWaiters(Waiter w2, boolean b) {
			w=w2;
			onBreak=b;
		}
	}
	public Collection<Table> tables;
	public int numWaiter=0;
	public boolean isPaused=false;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private int yIndex;
	private String name;
	public EBHostGui hostGui = null;
	public boolean atStart=false;

	public EBHostRole() {
		super();

		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		yIndex=60;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	// Messages
	public void goesToWork(){
		hostState=state.goToWork;
		this.stateChanged();
	}
	
	public void msgIWantToEat(Customer cust) {
		waitingCustomers.add(new Customers(cust,custState.staying));
		stateChanged();
	}
	
	public void msgLeavingRestaurant(Customer cust){
		for (Customers c:waitingCustomers)
		{
			if(c.cust==cust)
			{
				waitingCustomers.remove(c);
				stateChanged();
				break;
			}
		}
	}

	public void msgTableEmpty(int tableNumber) {
		for (Table table : tables) {
			if (table.tableNumber == tableNumber) {
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtStart()
	{
		atStart=true;
		stateChanged();
	}
	
	public void msgStaying(Customer c){
		for (Customers cust: waitingCustomers){
			if (cust.cust==c){
				cust.state=custState.assigned;
				stateChanged();
			}
		}
	}
	
	public void msgCanIBreak(Waiter w){
		if(waiters.size()>1){
			for(MyWaiters waiter:waiters){
				if(waiter.w==w){
					waiters.remove(waiter);
					((EBWaiterRole) w).msgGoOnBreak();
					stateChanged();
					break;
				}
			
			}
		}
		else{
			((EBWaiterRole) w).msgDontGoOnBreak();
		}
	}
	
	public void msgBackFromBreak(Waiter w){
		waiters.add(new MyWaiters(w,false));
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
		try{
			if(waitingCustomers.size()==0&&restaurantClosed&&hostState==state.closed){
				int tableNum=0;
				for (Table table : tables) {
					if (!table.isOccupied()) {
						tableNum++;
					}
				}
				if(tableNum==tables.size()){
					hostState=state.none;
					tellStaff();
					return true;
				}
			}
			if(waitingCustomers.size()!=0&&restaurantClosed){
				for(Customers cust:waitingCustomers){
					tellClosed(cust);
					return true;
				}
			}
			if(hostState==state.relieveDuty){
				hostState=state.none;
				myPerson.releavedFromDuty(this);
				if(replacementPerson != null){
					replacementPerson.waitingResponse.release();
				}
				return true;
			}
			if(hostState==state.leaveWork){
				hostState = state.none;
				hostGui.DoLeaveRestaurant();
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hostState=state.relieveDuty;
				return true;
			}
			if(hostState == state.goToWork){
				hostState = state.atWork;
				hostGui.DoEnterRestaurant();
				return true;
			}
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty())	 {
						if (!waiters.isEmpty())
						{
							seatCustomer(waitingCustomers.get(0), table,pickWaiter());//the action
							return true;//return true to the abstract agent to reinvoke the scheduler.
						}
					}
				}
		
			}
			if(!waitingCustomers.isEmpty()){
				for(Customers c:waitingCustomers)
				{
					if(c.state==custState.staying)
					{
						c.state=custState.waiting;
						askToWait(c.cust);
						return true;
					}
				}
			}
			if(!waitingCustomers.isEmpty()){
				for(Customers c:waitingCustomers)
				{
					if(c.state==custState.assigned)
					{
						c.state=custState.goToArea;
						assignWaitingSpace(c);
						return true;
					}
				}
			}
			return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		}
		catch(ConcurrentModificationException e){
			return false;
		}
}

	private void tellClosed(Customers cust) {
		((EBCustomerRole) cust.cust).msgRestaurantClosed();
		waitingCustomers.remove(cust);
	}

	// Actions
	private void assignWaitingSpace(Customers c){
		((EBCustomerRole) c.cust).msgWaitHere(yIndex);
		yIndex=yIndex+30;
		if (yIndex>400){
			yIndex=60;
		}
	}
	private void askToWait(Customer cust){
		((EBCustomerRole) cust).msgFull();
	}
	private Waiter pickWaiter(){
			numWaiter++;
			if (numWaiter>=waiters.size())
			{
				numWaiter=0;
			}
			return waiters.get(numWaiter).w;

	}
	
	private void seatCustomer(Customers c, Table table,Waiter waiter) {
				((EBWaiterRole) waiter).msgSeatCustomer(c.cust, table.tableNumber);
				table.setOccupant(c.cust);
				waitingCustomers.remove(c);
	}
	
	private void tellStaff(){
		for(MyWaiters w: waiters){
			((EBAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((EBWaiterRole) w.w).getName());
		}
		for (MyWaiters w:waiters){
			((EBWaiterRole)w.w).msgClosed();
		}
		((EBCookRole) restaurant.cook).msgClosed();
		((EBCashierRole)restaurant.cashier).msgClosed();
		waiters.removeAll(waiters);
		hostState = state.leaveWork;
	}
	
	//utilities

	public void msgReadyToWork(Waiter w){
		restaurantClosed=false;
		waiters.add(new MyWaiters(w,false));
		((EBAnimationPanel) restaurant.insideAnimationPanel).addWaiterToList(((EBWaiterRole) w).getName());
		this.stateChanged();
	}
	
	public void setGui(EBHostGui gui) {
		hostGui = gui;
	}

	public EBHostGui getGui() {
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

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}


	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		hostState = state.leaveWork;
		this.stateChanged();
	}

	public void msgDoneWorking(Waiter wait) {
		for (MyWaiters waiter:waiters){
			if (waiter.w==wait){
				((EBAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((EBWaiterRole) wait).getName());
				waiters.remove(waiter);
			}
		}
		stateChanged();
	}
	
	public void msgLeavingEarly(Waiter wait){
		((EBAnimationPanel) restaurant.insideAnimationPanel).removeWaiterFromList(((EBWaiterRole) wait).getName());
	}

	public void setRestaurant(Restaurant r) {
		restaurant=r;
	}

	@Override
	public void setGui(Gui g) {
		hostGui = (EBHostGui) g;
	}

	public void msgLeft() {
		hostState=state.relieveDuty;
		waitingResponse.release();
	}

	public void msgCloseRestaurant() {
		hostState=state.closed;
		restaurantClosed=true;
		stateChanged();
	}

	public void msgOpenRestaurant() {
		hostState=state.none;
		restaurantClosed=false;
		stateChanged();
	}
	
	/*public void pauseIt(){
		pause();
	}
	
	public void resumeIt(){
		resume();
	}*/
}

