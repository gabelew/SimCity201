package GCRestaurant.roles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import market.interfaces.DeliveryMan;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import GCRestaurant.gui.GCCashierGui;
import agent.Agent;

/**
 * Restaurant Cook Agent
 */

public class GCCashierRole extends Role implements Cashier
{	
	private String name;
	private Map<String, Double> menuItems = new HashMap<String, Double>();
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public List<myCustomer> customers = Collections.synchronizedList(new ArrayList<myCustomer>());
	public List<MarketBill> orders = Collections.synchronizedList(new ArrayList<MarketBill>());
	private enum MarketBillState{none, unpaid, paid};
	private enum CheckState{none, calculated, ReceivedPayment };
	public double cash = 10;
	private DecimalFormat df = new DecimalFormat("#.##"); //formats all numbers to 2 decimal place
	private Semaphore waitingResponse = new Semaphore(0,true);
	
	enum State {none, goToWork, working, leaving, releaveFromDuty};
	State state = State.none;
	Restaurant restaurant;
	PersonAgent replacementPerson = null;
	public GCCashierGui cashierGui = null;
	//constructor
	public GCCashierRole() 
	{
		super();	
		//this.name = name;
		menuItems.put("Steak", new Double(15.99) );
		menuItems.put("Chicken", new Double(10.99) );
		menuItems.put("Cookie", new Double(8.99) );
		menuItems.put("Salad", new Double(5.99) );		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setCash(double c)
	{
		this.cash = c;
	}
	
	/**************************************************
	* Messages
	**************************************************/
	public void msgProduceCheck(Waiter w, Customer c, String choice)
	{
		print("Producing " + c.toString() + "'s check");
		double checkTotal = menuItems.get(choice).doubleValue();
		for(myCustomer cust : customers)
		{
			if(c == cust.c)
			{
				print(c.toString() +" owed " + df.format(cust.amountDue) +" from last time");
				checkTotal += cust.amountDue;
			}
		}
		Check temp = new Check(c, w, checkTotal);
		checks.add(temp);
		print(c.toString() + "'s total is: " + df.format(checkTotal));
		stateChanged();
	}
	
	
	public void msgPayment(Customer cust, double cash) 
	{
		for(Check check : checks)
		{
			if(check.customer.equals(cust))
			{
				print(cust.toString() + " has paid " + df.format(cash) + " dollars");
				check.amountGiven = cash;
				this.cash += cash;
				check.state = CheckState.ReceivedPayment;
				break;
			}
		}
		stateChanged();
	}
	
	public void cannotPayCheckMsg(Customer c, double dues)
	{
		customers.add(new myCustomer(c,dues));
	}
	
	
	public void msgHereIsBill(DeliveryMan DMR, double bill) 
	{
		print("Current Cash: " + df.format(cash) +", Received Bill for Food from Market: " + df.format(bill));
		orders.add(new MarketBill(DMR,bill));
		stateChanged();
	}
	
	public void receivedMarketBill(Market m, double cost)
	{
		
	}

	
	public void msgReleaveFromDuty(PersonAgent p) 
	{
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	/****************************************************
	 * Actions
	 ***************************************************/
	private void giveCheckToWaiter(Check c)
	{
		//gives a check to a waiter
		((GCWaiterRole)c.waiter).gotCustomerCheck(c);
	}
	private void computeChangeAction(Check c)
	{
		double change = c.amountGiven - c.amountDue;
		
		cash -= change;
		print(c.customer.toString() + " your change is " + df.format(change) + " dollars");
		((GCCustomerRole)c.customer).receivedChangeMsg(change);
		checks.remove(c);
		
	}
	/**
	 * Market pays the bill if it has the money on hand
	 * If the market has no money on hand, then it will wait until
	 * it has money from sales, and then pays the bill
	 */
	private void PayMarketAction(MarketBill m)
	{
		m.state = MarketBillState.paid;
		this.cash -= m.amountDue;
		print("Paid Bill to Market, cash left: " + df.format(cash));
		orders.remove(m);
	}
	/****************************************************
	 * Scheduler.  Determine what action is called for, and do it.
	 ***************************************************/
	public boolean pickAndExecuteAnAction() 
	{
		try
		{
			if(state == State.releaveFromDuty){
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
				if(!"Saturday".equals(myPerson.dayOfWeek) && !"Sunday".equals(myPerson.dayOfWeek) && myPerson.aBankIsOpen())
					DepositBusinessCash();
				cashierGui.DoLeaveRestaurant();
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return true;
			}
			
			for(Check c : checks)
			{
				if(c.state == CheckState.calculated)
				{
					c.state = CheckState.none;
					giveCheckToWaiter(c);
					return true;
				}
			}
			
			for(Check c : checks)
			{
				if(c.state == CheckState.ReceivedPayment)
				{
					computeChangeAction(c);
					return true;
				}
			}
			
			for(MarketBill m: orders)
			{
				if(m.state == MarketBillState.unpaid && cash >= m.amountDue)
				{
					PayMarketAction(m);
					return true;
				}
			}
			return false;
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}
	
	private void DepositBusinessCash() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Check objects cannot be created outside of a CashierAgent
	 * to workaround for JUnit testing, this function is a function that generates
	 * a check and returns a check to be used in JUnit testing
	 */
	public Check testCheck(Customer c, Waiter w, double a)
	{
		Check temp = new Check(c,w,a);
		return temp;
	}
	
	public class MarketBill
	{
		public MarketBillState state = MarketBillState.unpaid;
		public DeliveryMan deliveryMan;
		public double amountDue;
		
		public MarketBill(DeliveryMan DMR, double c)
		{
			this.deliveryMan = DMR;
			this.amountDue = c;
		}
	}
	public class Check
	{
		public Waiter waiter;
		public Customer customer;
		public double amountGiven = 0;
		public double amountDue;
		CheckState state = CheckState.calculated;
		public Check(Customer c, Waiter w, double a)
		{
			this.waiter = w;
			this.customer = c;
			this.amountDue = a;
		}
	}
	
	private class myCustomer
	{
		Customer c;
		double amountDue;
		myCustomer(Customer customer, double balance)
		{
			this.c = customer;
			this.amountDue = balance;
		}
	
	}

	@Override
	public void goesToWork() 
	{
		state = State.goToWork;
		stateChanged();
	}


	public Gui getGui() {
		return (Gui) cashierGui;
	}

	public void setRestaurant(Restaurant r) {
		this.restaurant = r;
	}

	public void setGui(Gui GuiFactory) {
		cashierGui = (GCCashierGui) GuiFactory;
	}

	public void msgAnimationHasLeftRestaurant() {
		// TODO Auto-generated method stub
		
	}

}

