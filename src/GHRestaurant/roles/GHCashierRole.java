package GHRestaurant.roles;

import GHRestaurant.gui.GHCashierGui;
import restaurant.Restaurant;
import restaurant.interfaces.*;

import java.util.*;

import market.interfaces.DeliveryMan;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Cook Agent
 */

public class GHCashierRole extends Role implements Cashier{
	
	public List<Check> checks
	= Collections.synchronizedList(new ArrayList<Check>());
	public List<DeliveryBills> bills
	= Collections.synchronizedList(new ArrayList<DeliveryBills>());
	public enum CheckState {PENDING,PROCESSED,GIVECHECK,PAYING,NEXTTIME,MARKET}
	public enum billState {BILLED}
	//private String name;
	private double RestaurantMoney; 
	private Restaurant restaurant;
	public GHCashierGui cashiergui = null;


	public GHCashierRole() {
		super();

		//this.name = name;
		RestaurantMoney = 1000;
		}

	/*public String getName() {
		return name;
	}*/

	public List<Check> getChecks() {
		return checks;
	}
	
	public double getMoney(){
		return RestaurantMoney;
	}
	
	// Messages

	public void msgProduceCheck(Waiter wait, Customer cust, String choice, int tablenumber){
		print("Recieved msgHereIsAnOrder");
		checks.add(new Check(wait,cust,choice,tablenumber,CheckState.PENDING));
		stateChanged();
	 }
	
	public void msgGiveMeCheck(Waiter wait){
		print("Recieved msgGiveMeCheck");
		for (Check c : checks) {
			if(c.waiter == wait){
				c.os = CheckState.GIVECHECK;
			}
		}
		stateChanged();
	}
	
	public void msgCustomerPaying(Customer cust, String choice, double cost, int tablenumber){
		print("Recieved msgCustomerPaying");
		checks.add(new Check(cust,choice,tablenumber,cost,CheckState.PAYING));
		stateChanged();
	}
	  
	public void msgPayMarket(Market m, double cost){
		print("Recieved msgPayMarket");
		checks.add(new Check(m,cost,CheckState.MARKET));
	}
	
	@Override
	public void msgHereIsBill(DeliveryMan DMR, double bill) {
		bills.add(new DeliveryBills(DMR,bill,billState.BILLED));
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		
		synchronized(checks){
		for (Check c : checks) {
		if (c.getState() == CheckState.PENDING) {
				ProduceIt(c);//the action
				return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}
		}
		
		synchronized(checks){
		for (Check c : checks) {
			if (c.getState() == CheckState.GIVECHECK) {
					GiveCheck(c);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}
		}	
		
		synchronized(checks){
		for (Check c : checks) {
			if (c.getState() == CheckState.PAYING) {
					PayingBill(c);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}	
		}
		
		synchronized(checks){
		for (Check c : checks) {
			if (c.getState() == CheckState.MARKET) {
					PayMarket(c);//the action
					return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}	
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void ProduceIt(Check c){
		print("Producing check");

		switch(c.choice){
		
		case "Steak": c.cost = 15.99;
		break;
		
		case "Chicken": c.cost = 10.99;
		break;
		
		case "Salad": c.cost = 5.99;
		break;
		
		case "Pizza": c.cost = 8.99;
		break;
		}
		
		
		for(Check check : checks){
			if(check.os == CheckState.NEXTTIME && check.customer == c.customer){
				c.cost+=check.cost;
				checks.remove(check);
			}
		}		
		c.os = CheckState.PROCESSED;
	}
	
	private void GiveCheck(Check c){
		print("Giving check to " + c.waiter);
		((GHWaiterRole) c.waiter).msgHereIsCheck(c.customer, c.choice, c.cost, c.tablenumber);
		checks.remove(c);
	}
	

	private void PayingBill(Check c){
		print("Check being payed by " + c.customer);
		
		if(((GHCustomerRole) c.customer).getMoney() >= c.cost){
			double temp = ((GHCustomerRole) c.customer).getMoney() - c.cost;
			((GHCustomerRole) c.customer).setMoney(temp);
			RestaurantMoney+=c.cost;
			print("Checked payed by " + c.customer);
			checks.remove(c);
		}
		
		else if(((GHCustomerRole) c.customer).getMoney() < c.cost){
			c.os = CheckState.NEXTTIME;
			print("Check Can Be Payed Next Time");
		}
	}
	
	//the cashier will for now always have enough money to pay the market.
	private void PayMarket(Check c){
		print("Paying " + c.market.getName() + " for order");
		RestaurantMoney -= c.cost;
		//c.market.msgHereIsPayment(c.cost,this);
		checks.remove(c);
	}
	
	//utilities
	public class Check {
		Waiter waiter;
		Customer customer;
		Market market;
		String choice;
		int tablenumber;
		double cost;
		CheckState os;

		Check(Waiter w, Customer cu, String c, int t, CheckState o){		
			waiter = w;
			customer = cu;
			choice = c;
			tablenumber = t;
			os = o;
		}
		
		Check(Customer cu, String c, int t, double co, CheckState o){
			customer = cu;
			choice = c;
			tablenumber = t;
			cost = co;
			os = o;
		}
		
		Check(Market ma, double c, CheckState o){
			market = ma;
			cost = c;
			os = o;
		}
		
		public CheckState getState(){
			return os;
		}	
		
		public double getCost(){
			return cost;
		}
	}
	
	public class DeliveryBills{
		DeliveryMan dm;
		double cost;
		billState bs;

		public DeliveryBills(DeliveryMan dMR, double bill, billState billed) {
			dm = dMR;
			cost = bill;
			bs = billed;
		}
		
	}

	@Override
	public void msgReleaveFromDuty(PersonAgent p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goesToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGui(Gui g) {
		cashiergui = (GHCashierGui) g;
	}

	@Override
	public Gui getGui() {
		return cashiergui;
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

}

