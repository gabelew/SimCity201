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
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.DeliveryManRole;
import city.roles.Role;

/**
 * Restaurant Cashier Agent
 */

public class GLCashierRole extends Role implements Cashier {
	
	private double bank = 5000; // starting amount of money at restaurant
	private final double STEAKPRICE = 15.99;
	private final double CHICKENPRICE = 10.99;
	private final double SALADPRICE = 5.99;
	private final double COOKIEPRICE = 8.99;
	enum State {none, goToWork, working, leaving, relieveFromDuty};
	State state = State.none;
	private boolean restaurantClosed = false;
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
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
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
	public void msgRestaurantClosed() {
		restaurantClosed = true;
		stateChanged();
	}
	public void goesToWork() {
		if(!restaurantClosed) {
			state = State.goToWork;
			stateChanged();
		}
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
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Finished shift.");
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
			if(!"Saturday".equals(myPerson.dayOfWeek) && !"Sunday".equals(myPerson.dayOfWeek) && myPerson.aBankIsOpen())
				DepositBusinessCash();
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
		
		Bill temp2 = null;
		Bill temp3 = null;
		synchronized(bills) {
			for(Bill b: bills) {
				if(b.state == BillState.requested && temp2 == null) {
					for(Bill b2 : bills) {
						if(b2.state == BillState.informed && b2.deliveryMan == b.deliveryMan) {
							temp2 = b;
							temp3 = b2;
						}
					}
				}
			}
		}
		if(temp2 != null) {
			payBill(temp2, temp3);
			return true;
		}
		
		boolean checksPaid = true;
		synchronized(checks) {
			for (Check c : checks) {
				if (checkState.debt != c.cs && checkState.paid != c.cs) {
					checksPaid = false;
					return true;
				}
			}
		}
		
		if(restaurantClosed && bills.isEmpty() && checksPaid) {
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Restaurant is closed. Leaving work early.");
			state = State.leaving;
			restaurantClosed = false;
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
		
	private void payBill(Bill billFromDman, Bill billFromCook) {
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Bill from Delivery Man was $" + billFromDman.bill + ". Invoice from Cook was $" + billFromCook.bill);
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Paid market $" + billFromDman.bill);
		if(billFromDman.bill == billFromCook.bill){
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Bills match.");
			bank = bank - billFromDman.bill;
			billFromDman.deliveryMan.msgHereIsPayment(billFromDman.bill, this);
			bills.remove(billFromDman);
			bills.remove(billFromCook);
		}else{
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Bills do not match. We are never ordering from this market again.");
			bank = bank - billFromDman.bill;
			billFromDman.deliveryMan.msgHereIsPayment(billFromDman.bill, this);
			//tell cook to put market on naughty list
			restaurant.cook.msgNeverOrderFromMarketAgain(((DeliveryManRole)billFromDman.deliveryMan).Market);
			bills.remove(billFromDman);
			bills.remove(billFromCook);
		}
	}
	
	private void DepositBusinessCash() {
		double cash = bank - 1500;
		cash = (Math.round(100*cash) / ((double)100));
		int balance = Double.compare(cash, 0);
		if(1 == balance) {
			bank -= cash;
			myPerson.businessFunds += cash;
			myPerson.msgDepositBusinessCash();
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Going to deposit into restaurant account: " + cash);
		}
	}
	
	private void produceCheck(Check c) {
		double previousAmount = 0;
		synchronized(checks) {
			for (Check ch : checks) {
				if(checkState.debt == ch.cs && c.c == ch.c) {
					previousAmount = ch.amount;
					ch.cs = checkState.paid;
					AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Customer " + ((GLCustomerRole)c.c).myPerson.getName() + " previously owes us " + previousAmount + ". Added to current bill.");
				}
			}
		}
		double calculatedAmount = menu.get(c.choice) + previousAmount;
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Customer " + ((GLCustomerRole)c.c).myPerson.getName() + " check comes out to " + calculatedAmount);
		c.amount = calculatedAmount;
		GLWaiterRole waiter = (GLWaiterRole)c.w;
		waiter.msgHereIsCheck(c.c, c.amount);
		c.cs = checkState.unpaid;
	}

	private void fulfillCheck(Check c) {
		if(c.paid < c.amount) {
			c.amount -= c.paid;
			bank += c.paid;
			c.paid = 0;
			c.cs = checkState.debt;
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Customer " + ((GLCustomerRole)c.c).myPerson.getName() + " owes us " + c.amount);
		} else {
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Customer " + ((GLCustomerRole)c.c).myPerson.getName() + " paid us " + c.paid);
			c.cs = checkState.paid;
			bank += c.paid;
		}
		GLCustomerRole customer = (GLCustomerRole) c.c;
		customer.msgHereIsReceipt(c.paid);
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

