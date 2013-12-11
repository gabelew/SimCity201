package GLRestaurant.roles;

import GLRestaurant.roles.GLCashierRole.Check;
import GLRestaurant.roles.GLCashierRole.checkState;
import GLRestaurant.roles.GLCookRole.Food;
import GLRestaurant.gui.GLCustomerGui;
import GLRestaurant.gui.GLWaiterGui;
import restaurant.Restaurant;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CopyOnWriteArrayList;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;

/**
 * Restaurant Waiter Agent
 */
public abstract class GLWaiterRole extends Role implements Waiter{
	//Notice that we implement customers using ArrayList, but type it
	//with List semantics.
	private List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	protected class MyCustomer {
		GLCustomerRole c;
		customerState cs;
		Table t;
		String choice;
		int platex;
		int platey;
		MyCustomer(GLCustomerRole cust, int tableNum, customerState state) {
			this.c = cust;
			this.t = new Table(tableNum);
			this.cs = state;
		}
	}
	class Check {
		String choice;
		GLCustomerRole c;
		checkState cs;
		double amount;
		Check(GLCustomerRole c, String choice, checkState cs) {
			this.choice = choice;
			this.c = c;
			this.cs = cs;
		}
	}
	public Restaurant restaurant;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atOrigin = new Semaphore(0,true);
	private Semaphore atCustomer = new Semaphore(0,true);
	private Semaphore atPlate = new Semaphore(0,true);
	protected Semaphore waitingResponse = new Semaphore(0,true);
	public enum customerState {waiting, seated, askedToOrder, ordering, ordered, reorder, waitingForFood, readyToEat, eating, eatingDone, waitingForCheck, checkReceived, leaving, leftRestaurant};
	public enum agentEvent {none, goOnBreak, onBreak, relieveFromDuty, goToWork, leaveWork};
	public enum checkState {pending, preparing, finished};
	agentEvent event = agentEvent.none;
	public GLWaiterGui waiterGui = null;
	private boolean WantToGoOnBreak = false;
	private boolean finishedServing = false;
	protected GLRevolvingStandMonitor revolvingStand;
	private String unstockedFood;
	Map<String, Double> menu = new ConcurrentHashMap<String, Double>();
	private final double STEAKPRICE = 15.99;
	private final double CHICKENPRICE = 10.99;
	private final double SALADPRICE = 5.99;
	private final double COOKIEPRICE = 8.99;
	private boolean restaurantClosed = false;
	
	/**
	 * Constructor for WaiterAgent class.
	 * @param name
	 */
	public GLWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant = r;
		menu.put("steak", STEAKPRICE);
		menu.put("chicken", CHICKENPRICE);
		menu.put("salad", SALADPRICE);
		menu.put("cookie", COOKIEPRICE);
	}

	public List getCustomers() {
		return customers;
	}

	// Messages
	
	public void msgRestaurantClosed() {
		event = agentEvent.leaveWork;
		stateChanged();
	}
	
	public void msgWantToGoOnBreak() { //from gui
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Want to go on break.");
		WantToGoOnBreak = true;
		stateChanged();
	}

	public void msgReturnedFromBreak() {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Finished break.");
		((GLHostRole)restaurant.host).msgFinishedBreak(this);
		stateChanged();
	}
	
	public void msgBreakRequestReviewed(boolean canGoOnBreak) {
		if (canGoOnBreak) {
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Break request approved.");
			event = agentEvent.goOnBreak;
		} else {
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Break request denied.");
			event = agentEvent.none;
			waiterGui.noBreak();
		}
		stateChanged();
	}
	
	public void msgSitAtTable(GLCustomerRole cust, int table) {
		customers.add(new MyCustomer(cust, table, customerState.waiting));
		stateChanged();
	}
	
	public void msgReadyToOrder(GLCustomerRole c) {
		MyCustomer mc = findCustomer(c);
		mc.cs = customerState.askedToOrder;
		stateChanged();
	}
	
	public void msgIAmLeaving(GLCustomerRole c) {
		MyCustomer mc = findCustomer(c);
		mc.cs = customerState.leaving;
		stateChanged();
	}
	
	public void msgHereIsChoice(GLCustomerRole c, String choice) {
		MyCustomer mc = findCustomer(c);
		mc.cs = customerState.ordered;
		mc.choice = choice;
		waiterGui.takenOrder(choice); // tells gui to display icon for order
		stateChanged();
	}
	
	public void msgOutOfFood(GLCustomerRole c, String choice) {
		MyCustomer mc = findCustomer(c);
		unstockedFood = choice;
		mc.cs = customerState.reorder;
		waiterGui.finishedWithOrder(choice);
		stateChanged();
	}
	
	public void msgOrderDone(GLCustomerRole c, String choice, int x, int y) {
		MyCustomer mc = findCustomer(c);
		mc.platex = x;
		mc.platey = y;
		mc.cs = customerState.readyToEat;
		stateChanged();
	}
	
	public void msgHereIsCheck(Customer c, double amount) {
		Check check = findCheck(c);
		check.amount = amount;
		check.cs = checkState.finished;
		stateChanged();
	}

	public void msgEatingDone(GLCustomerRole cust) {
		MyCustomer mc = findCustomer(cust);
		mc.cs = customerState.eatingDone;
		stateChanged();
	}

	public void msgAtTable() {//from animation
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgAtOrigin() {
		atOrigin.release();
		stateChanged();
	}

	public void msgAtCustomer() {
		atCustomer.release();
		stateChanged();
	}
	
	public void msgAtPlate() {
		atPlate.release();
		stateChanged();
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(event == agentEvent.relieveFromDuty) {
			event = agentEvent.none;
			myPerson.releavedFromDuty(this);
			AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Finished shift.");
			restaurant.insideAnimationPanel.removeGui(waiterGui);
			return true;
		}
		if(customers.isEmpty() && (
				(getName().toLowerCase().contains("day") && myPerson.currentHour >= 11 && myPerson.currentHour <=21) ||
				(getName().toLowerCase().contains("night") && myPerson.currentHour < 10 || myPerson.currentHour >=22))){
			leaveWork();
			return true;
		}
		
		if(event == agentEvent.leaveWork) {
			closedLeaving();
			return true;
		}
		
		if(event == agentEvent.goToWork)
		{
			event = agentEvent.none;
			tellHost();
			return true;
		}
		
		try {
			// First rule: Seats any waiting customers on the list
			for (MyCustomer mc : customers) {
				if(customerState.waiting == mc.cs){
					seatCustomer(mc);
					return true;
				}
			}
			
			// Second rule: Takes order from customer
			for (MyCustomer mc : customers) {
				if(customerState.askedToOrder == mc.cs){
					takeOrder(mc);
					return true;
				}
			}
			
			// Third rule: Sends order to the cook to prepare
			for (MyCustomer mc : customers) {
				if(customerState.ordered == mc.cs){
					sendOrderToCook(mc);
					return true;
				}
			}
			
			for (MyCustomer mc : customers) {
				if(customerState.reorder == mc.cs) {
					getCustomerToReorder(mc);
					return true;
				}
			}
			
			// Fourth rule: Brings food to customer
			for (MyCustomer mc : customers) {
				if(customerState.readyToEat == mc.cs){		
					bringFoodToTable(mc, mc.choice);
					return true;
				}
			}
			
			for (MyCustomer mc : customers) {
				if(customerState.eatingDone == mc.cs) {
					Check check = findCheck(mc.c);
					if(checkState.finished == check.cs) {
						bringCustomerCheck(mc, check);
						return true;
					}
				}
			}
			
			// Prepares table for new customer
			for (MyCustomer mc : customers) {
				if(customerState.leaving == mc.cs){
					clearTable(mc);
					return true;
				}
			}
			
			if(WantToGoOnBreak) {
				requestHostForBreak();
				return true;
			}
			
			if (agentEvent.goOnBreak == event) {
				finishedServing = true;
				for(MyCustomer mc : customers) {
					if (customerState.leftRestaurant != mc.cs) {
						finishedServing = false;
					}
				}
				if(finishedServing)
					goOnBreak();
				return true;
			}		
			
		} catch(ConcurrentModificationException cme) {
			return false;
		}
		if (event != agentEvent.onBreak) 
			waiterGui.DoLeaveCustomer();
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void requestHostForBreak() {
		WantToGoOnBreak = false;
		((GLHostRole)restaurant.host).msgIWantToGoOnBreak(this);
	}

	private void seatCustomer(MyCustomer mc) {
		mc.c.customerGui.setWaiterGui(waiterGui);
		waiterGui.GoToCustomer(mc.c.customerGui.getXPos(), mc.c.customerGui.getYPos());	
		waiterGui.ifAtCustomer(mc.c.customerGui.getXPos(), mc.c.customerGui.getYPos());
		try {
			atCustomer.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.c.msgFollowMe(this, mc.t.tableNumber, this.menu);
		DoSeatCustomer(mc.c, mc.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.cs = customerState.seated;
		waiterGui.DoLeaveCustomer();	
	}
	
	private void takeOrder(MyCustomer mc){
		waiterGui.GoToTable(mc.c, mc.t.tableNumber);
		try {
				atTable.acquire();
		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Taking customer " + mc.c.getName() + "'s order.");
		mc.cs = customerState.ordering;
		mc.c.msgChooseFood();
	}
	
	protected abstract void sendOrderToCook(MyCustomer mc);
	
	private void getCustomerToReorder(MyCustomer mc) {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Customer " + mc.c.getName() + " ordered choice we are out of. Letting them reorder.");
		mc.cs = customerState.seated;
		waiterGui.GoToTable(mc.c, mc.t.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.c.msgOutOfFood(unstockedFood);
		mc.c.msgPleaseReorder();
	}
	
	private void bringFoodToTable(MyCustomer mc, String choice) {
		waiterGui.DoLeaveCustomer();
		waiterGui.GoToPlate(mc.platex, mc.platey);
		waiterGui.ifAtPlate(mc.platex, mc.platey);
		try {
			atPlate.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((GLCookRole)restaurant.cook).msgGotPlate(this, mc.c);
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Gave customer " + mc.c.getName() + choice);
		waiterGui.servingFood(choice);
		waiterGui.GoToTable(mc.c, mc.t.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.c.msgHereIsFood(choice);
		checks.add(new Check(mc.c, choice, checkState.pending));
		((GLCashierRole)restaurant.cashier).msgProduceCheck(this, mc.c, choice);
		mc.cs = customerState.eating;
		waiterGui.finishedWithOrder(choice);
		waiterGui.DoLeaveCustomer();
	}
	
	private void bringCustomerCheck(MyCustomer mc, Check check) {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Gave customer " + mc.c.getName() + " check.");
		mc.c.msgHereIsCheck(check.amount);
		mc.cs = customerState.checkReceived;
	}
	
	private void clearTable(MyCustomer mc) {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Customer " + mc.c.getName() + " is leaving.");
		((GLHostRole)restaurant.host).msgTableAvailable(this, mc.c, mc.t.tableNumber);
		mc.cs = customerState.leftRestaurant;
		customers.remove(mc);
	}
	
	private void goOnBreak() {
		Do("Going on break.");
		event = agentEvent.onBreak;
		waiterGui.goOnBreak(); // waiter goes to origin
	}
	
	private void leaveWork() {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "I am leaving work.");
		waiterGui.DoLeaveRestaurant();
		restaurant.host.msgDoneWorking(this);
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	
	private void closedLeaving() {
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Restaurant has closed. I am leaving work early.");
		waiterGui.DoLeaveRestaurant();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			
		}
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(GLCustomerRole customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		AlertLog.getInstance().logMessage(AlertTag.REST_WAITER, this.getName(), "Seating customer " + customer.getName() + " at table: " + table.tableNumber);
		waiterGui.GoToTable(customer, table.tableNumber); 
	}
	
	//utilities
	
	public Check findCheck(Customer c) {
		Check check = null;
		synchronized(checks) {
			for (Check ch : checks) {
				if (c.equals(ch.c)) {
					check = ch;
				}
			}
		}
		return check;
	}
	
	public MyCustomer findCustomer(GLCustomerRole c) {
		MyCustomer mc = null;
		synchronized(customers) {
			for (MyCustomer cust : customers) {
				if (c == cust.c)
					mc = cust;
			}	
		}
		return mc;
	}
	
	public void setGui(GLWaiterGui gui) {
		waiterGui = gui;
	}

	public GLWaiterGui getGui() {
		return waiterGui;
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
			return (null != occupiedBy);
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}

	@Override
	public void msgGoOnBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDontGoOnBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeftTheRestaurant() {
		waitingResponse.release();
		if(!restaurantClosed)	
			event = agentEvent.relieveFromDuty;
		restaurantClosed = false;
	}
	
	public void msgAtStand() {
		waitingResponse.release();
	}

	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}

	@Override
	public void goesToWork() {
		if(event != agentEvent.leaveWork) {
			event = agentEvent.goToWork;
			stateChanged();
		}
	}
	
	public void setRevolvingStand(GLRevolvingStandMonitor r) {
		this.revolvingStand = r;
	}
	
	public void tellHost() {
		restaurant.host.msgReadyToWork(this);
	}

	@Override
	public void msgAskForBreak() {
		msgWantToGoOnBreak();
	}

	@Override
	public void setGui(Gui waiterGuiFactory) {
		this.waiterGui = (GLWaiterGui) waiterGuiFactory;
	}
}

