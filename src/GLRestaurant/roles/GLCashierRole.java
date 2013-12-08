package GLRestaurant.roles;

import agent.Agent;
import GLRestaurant.gui.GLCashierGui;
import GLRestaurant.roles.GLCookRole.State;
import CMRestaurant.roles.CMCashierRole.Bill;
import CMRestaurant.roles.CMCashierRole.BillState;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.LoggedEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import market.interfaces.DeliveryMan;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Cashier Agent
 */

public class GLCashierRole extends Role implements Cashier {
	
	private double currentBalance = 70; // starting amount of money at restaurant
	private final double STEAKPRICE = 15.99;
	private final double CHICKENPRICE = 10.99;
	private final double SALADPRICE = 5.99;
	private final double COOKIEPRICE = 8.99;
	private final double STEAKCOST = 8.00;
	private final double CHICKENCOST = 5.00;
	private final double SALADCOST = 1.00;
	private final double PIZZACOST = 3.00;
	enum State {none, goToWork, working, leaving, relieveFromDuty};
	State state = State.none;
	PersonAgent replacementPerson = null;
	private Semaphore waitingResponse = new Semaphore(0,true);
	
	public class Bill{
		public DeliveryMan deliveryMan;
		public double bill;
		public BillState state;
		
		Bill(DeliveryMan DMR, double b, BillState s){
			deliveryMan=DMR;
			bill = b;
			state = s;
		}
	}
	public enum BillState {requested, payed, informed};
	
	public class Check {
		Waiter w;
		String choice;
		public Customer c;
		public checkState cs;
		public double amount;
		public double paid;
		Check(Waiter w, Customer c, String choice, checkState cs) {
			this.w = w;
			this.choice = choice;
			this.c = c;
			this.cs = cs;
		}
	}
	public Restaurant restaurant;
	public enum checkState {pending, preparing, unpaid, paying, processingPayment, paid, debt};
//	public enum BillState {pending, paying, debt, paid};
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	//public List<MarketBill> bills = Collections.synchronizedList(new ArrayList<MarketBill>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	Map<String, Double> menu = new ConcurrentHashMap<String, Double>();
	
	public GLCashierGui cashierGui = null;

	public GLCashierRole() {
		super();

		menu.put("steak", STEAKPRICE);
		menu.put("chicken", CHICKENPRICE);
		menu.put("salad", SALADPRICE);
		menu.put("cookie", COOKIEPRICE);
	}

	// Messages
	
//	public void msgHereIsBill(Market mkt, String choice, int amount) {
//		bills.add(new MarketBill(mkt, choice, amount, BillState.pending));
//		stateChanged();
//	}
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
	/**
	 * Invoice from Cook to confirm price
	 * @param price
	 * @param DMR
	 */
	public void msgHereIsInvoice(double price,DeliveryMan DMR) {
		bills.add(new Bill(DMR,price,BillState.informed));
		stateChanged();
	}
	
	/**
	 * Bill from Market's DeliveryMan
	 */
	@Override
	public void msgHereIsBill(DeliveryMan DM, double bill) {
		bills.add( new Bill(DM, bill, BillState.requested));
		stateChanged();	
	}
	
	public void msgProduceCheck(Waiter w, Customer c, String choice) {
		checks.add(new Check(w, c, choice, checkState.pending));
		stateChanged();
	}

	public void msgHereIsMoney(Customer c, double amount) {
		Check check = findCheck(c);
		check.paid = amount;
		check.cs = checkState.paying;
		stateChanged();
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
			cashierGui.DoEnterRestaurant();
			return true;
		}
		if(state == State.leaving){
			state = State.none;
			cashierGui.DoLeaveRestaurant();
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		synchronized(checks) {
			for (Check c : checks) {
				if (checkState.pending == c.cs) {
					c.cs = checkState.preparing;
					produceCheck(c);
					return true;
				}
			}
		}

		synchronized(checks) {
			for (Check c : checks) {
				if (checkState.paying == c.cs) {
					c.cs = checkState.processingPayment;
					fulfillCheck(c);
					return true;
				}
			}
		}
		
//		synchronized(bills) {
//			for (MarketBill b : bills) {
//				if (BillState.pending == b.bs) {
//					b.bs = BillState.paying;
//					payBill(b);
//					return true;
//				}
//			}
//		}
//		
//		synchronized(bills) {
//			for (MarketBill b : bills) {
//				if (BillState.debt == b.bs && currentBalance > b.cost) {
//					b.bs = BillState.pending;
//					return true;
//				}
//			}
//		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	
	private void produceCheck(Check c) {
		//Do ("Producing check for " + c.c.getName());
		double previousAmount = 0;
		synchronized(checks) {
			for (Check ch : checks) {
				if(checkState.debt == ch.cs && c.c == ch.c) {
					previousAmount = ch.amount;
					ch.cs = checkState.paid;
					//print (c.c.getName() + " owes us " + previousAmount + ". Added to current bill.");
				}
			}
		}
		double calculatedAmount = menu.get(c.choice) + previousAmount;
		print("The check comes out to " + calculatedAmount);
		c.amount = calculatedAmount;
		GLWaiterRole waiter = (GLWaiterRole)c.w;
		waiter.msgHereIsCheck(c.c, c.amount);
		c.cs = checkState.unpaid;
	}

	private void fulfillCheck(Check c) {
		//Do (c.c.getName() + " paid " + c.paid);
		if(c.paid < c.amount) {
			c.amount -= c.paid;
			currentBalance += c.paid;
			c.paid = 0;
			c.cs = checkState.debt;
			//print(c.c.getName() + " owes us " + c.amount);
		} else {
			c.cs = checkState.paid;
			currentBalance += c.paid;
		}
		GLCustomerRole customer = (GLCustomerRole) c.c;
		customer.msgHereIsReceipt(c.paid);
	}
	
//	private void payBill(MarketBill b) {
//		if("steak".equals(b.choice)) {
//			b.cost = STEAKCOST * b.amount;
//		} else if("chicken".equals(b.choice)) {
//			b.cost = CHICKENCOST * b.amount;
//		} else if("salad".equals(b.choice)) {
//			b.cost = SALADCOST * b.amount;
//		} else if("pizza".equals(b.choice)) {
//			b.cost = PIZZACOST * b.amount;
//		}
//		if(currentBalance < b.cost) {
//			Do("We owe " + b.m.getName() + " $" + b.cost);
//			b.bs = BillState.debt;
//		} else {
//			Do("Paying " + b.choice + " bill to " + b.m.getName());
//			currentBalance -= b.cost;
//			print("Current bal: " + currentBalance);
//			//b.m.msgHereIsMoney(b.cost);
//			//b.m.msgPayment(this, b.cost);
//			b.bs = BillState.paid;
//		}
//	}
	


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
	
	public void setGui(GLCashierGui gui) {
		cashierGui = gui;
	}
	
	public GLCashierGui getGui() {
		return cashierGui;
	}

	@Override
	public void setGui(Gui waiterGuiFactory) {
		this.cashierGui = (GLCashierGui) waiterGuiFactory;
	}
	
	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

}

