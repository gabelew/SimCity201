package GHRestaurant.roles;

import CMRestaurant.roles.CMWaiterRole.AgentEvent;
import GHRestaurant.gui.GHWaiterGui;
import agent.Agent;
import restaurant.Restaurant;
import restaurant.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.interfaces.Person;
import city.roles.Role;

/**
 * Restaurant Waiter Agent
 */

public class GHWaiterRole extends Role implements Waiter{
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> waitingCustomers
	= new ArrayList<MyCustomer>();
	public List<CustomerCheck> customerChecks
	= new ArrayList<CustomerCheck>();
	public GHWaiterGui waitergui = null;
	private boolean WantToGoOnBreak = false;
	private boolean OnBreak = false;
	private boolean BackTW = false;
	//private String name;
	private Semaphore atDestination = new Semaphore(0,true);
	private Host host;
	private Cashier cashier;
	private Cook cook;
	enum CustomerState {Waiting, AskedToOrder, Ordered, Reorder, Ready, Done, Idle}
	enum WaiterState{None,GoingToWork, relieveFromDuty}
	WaiterState wState = WaiterState.None;
	private Restaurant restaurant;
	
	
	public GHWaiterRole(PersonAgent p, Restaurant r) {
		super(p);
		restaurant = r;

		//this.name = name;
	}

	/*public String getName() {
		return name;
	}*/

	public List getWaitingCustomers() {
		return waitingCustomers;
	}
	
	public void setHost(Host host){
		this.host = host;
	}
	
	public void setCook(Cook cook){
		this.cook = cook;
	}
	
	public void setCashier(Cashier ca){
		this.cashier = ca;
	}
	// Messages
	
	public void goesToWork() {//from animation
		print("Going to work");
		wState = WaiterState.GoingToWork;
		stateChanged();
	}
	
	public void msgTryToGoOnBreak(){//from animation
		WantToGoOnBreak = true;
		stateChanged();
	}
	
	public void msgGoBackToWork(){//from animation
		OnBreak = false;
		BackTW = true;
		stateChanged();
		
	}
	
	public void msgSitAtTable(Customer customer, int table){
	 	waitingCustomers.add(new MyCustomer(customer,table, CustomerState.Waiting));
	 	//print("msgSitAtTable");	
	 	stateChanged();
	 }
	 
	public void msgImReadyToOrder(Customer c){
		print("Taking Order");
		for(MyCustomer mycust : waitingCustomers){
			if(mycust.customer == c){
				mycust.cs = CustomerState.AskedToOrder;
			}
			
		}
		stateChanged();
	  }
	  
	 public void msgHereIsMyOrder(Customer c, String choice){
		 print("Customer ordered" + choice);
		 for(MyCustomer mycust : waitingCustomers){
				if(mycust.customer == c){
					mycust.cs = CustomerState.Ordered;
					mycust.choice = choice;
				}
				
			}
		 
		 stateChanged();
	  }
	  
	 public void msgOrderIsReady(String choice, int tablenumber){
		 print("Recieved msgOrderIsReady");
		 for(MyCustomer mycust : waitingCustomers){
				if(mycust.choice == choice && mycust.tablenumber ==tablenumber){
					mycust.cs = CustomerState.Ready;
				}
				
			}
		 stateChanged();
	  }
	 
	 public void msgOutOfOrder(int tablenumber, String choice){
		 for(MyCustomer mycust : waitingCustomers){
				if(mycust.choice == choice && mycust.tablenumber ==tablenumber){
					mycust.cs = CustomerState.Reorder;
				}
				
			}
		 stateChanged();
	 }
	  
	 public void msgDoneEatingandLeaving(Customer c){
		 for(MyCustomer mycust : waitingCustomers){
				if(mycust.customer == c){
					mycust.cs = CustomerState.Done;
				}
				
			}
		 stateChanged();
	 }
	 
	 public void msgHereIsCheck(Customer cust, String c, double co, int t){
		 print("Recieved msgHereIsCheck");
		 customerChecks.add(new CustomerCheck(cust,c,co,t));
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
		
	try{
		
		if(wState == WaiterState.relieveFromDuty){
			wState = WaiterState.None;
			myPerson.releavedFromDuty(this);
			//restaurant.insideAnimationPanel.removeGui(waitergui);
			return true;
		}
		
		if(BackTW){
			BackToWork();
			return true;
		}
		
		if(WantToGoOnBreak){
		    AskForBreak();
		    return true;
		 }
		
		if(wState == WaiterState.GoingToWork){
			wState = WaiterState.None;
			MsgHost();
			return true;
		}
		
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.Waiting){
			  SeatCustomer(customer);
			  	return true;
		  }
		}
		
		
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.AskedToOrder){
			  	TakeOrder(customer);
		  		return true;
		  }
		}
		
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.Reorder){
			  	ReAskToOrder(customer);
			  	return true;
		  }
		}
		 
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.Ordered){
			  	TakeOrderToCook(customer);
			  	return true;
		  }
		}
		
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.Ready){
			  	TakeOrderToCustomer(customer);
			  	return true;
		  }
		}
		
		for (MyCustomer customer : waitingCustomers) {
		  if(customer.getState() == CustomerState.Done){
			  	TellHost(customer);
			  	return true;
		  }
		}
		//waitergui.DoLeaveCustomer();

	}  
	catch(ConcurrentModificationException cme){
			return false;
		}
		 
		waitergui.DoLeaveCustomer();
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}


	// Actions
	
	private void MsgHost() {
		((GHHostRole) restaurant.host).msgSetWaiter(this);
	}
	
	
	private void SeatCustomer(MyCustomer customer) {
		//customer.cs = CustomerState.Seated;
		((GHCustomerRole) customer.customer).msgFollowMeToTable(customer.tablenumber,this);
		DoSeatCustomer(customer.customer, customer.tablenumber);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.cs = CustomerState.Idle;
	}

	private void DoSeatCustomer(Customer customer, int tablenumber){
		//print("Seating " + customer + " at table " + tablenumber);
		waitergui.DoBringToTable(customer, tablenumber);
	}
	
	private void TakeOrder(MyCustomer c){
		//c.cs = CustomerState.Asked;
		DoTakeOrder(c);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cs = CustomerState.Idle;
		((GHCustomerRole) c.customer).msgWhatWouldYouLike();
	  }
	
	private void DoTakeOrder(MyCustomer c){
		//print("Going to table "+ c.tablenumber + " to take "+ c.customer + "'s order.");
		waitergui.DoGoToTable(c.tablenumber);

	}
	
	private void ReAskToOrder(MyCustomer c){
		DoTakeOrder(c);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cs = CustomerState.Idle;
		((GHCustomerRole) c.customer).msgOutOfChoiceReorder(c.choice);
	}

	
	private void TakeOrderToCook(MyCustomer c){
		DoTakeOrderToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cs = CustomerState.Idle;
		((GHCookRole) restaurant.cook).msgHereIsAnOrder(this,c.choice,c.tablenumber);
	}
	  
	private void DoTakeOrderToCook(){
		print("Taking order to cook");
		waitergui.GoToCook();		
	}
	
	private void TakeOrderToCustomer(MyCustomer c){
		DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DoTakeOrderToCustomer(c);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cs = CustomerState.Idle;
		//When the waiter gives the customer his order he tells the cashier to produce the check
		//cashier.msgProduceCheck(this, c.customer, c.choice, c.tablenumber);
		//Then asks him for the check
		DoGiveCheck(c);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//cashier.msgGiveMeCheck(this);
		//c.customer.msgHereIsYourOrder();
		for(CustomerCheck cc : customerChecks){
			if((!(customerChecks.isEmpty())) && (cc.customer == c.customer) ){
				((GHCustomerRole) c.customer).msgHeresCheck(cc.choice, cc.cost);
			}
		}
		((GHCustomerRole) c.customer).msgHereIsYourOrder();

	}
	  
	private void DoGoToCook(){
		print("Going to cook to pick up order");
		waitergui.GoToCook();
	}
	
	private void DoTakeOrderToCustomer(MyCustomer c){
		print("Taking order to table " + c.tablenumber);
		((GHCashierRole) restaurant.cashier).msgProduceCheck(this, c.customer, c.choice, c.tablenumber);
		waitergui.DoGoToTable(c.tablenumber);
	}
	
	private void DoGiveCheck(MyCustomer c){
		print("Giving check to customer");
		((GHCashierRole) restaurant.cashier).msgGiveMeCheck(this);
		waitergui.DoGoToTable(c.tablenumber);
	}
	
	private void TellHost(MyCustomer c){
		print("Telling host a table is free");
		((GHHostRole) restaurant.host).msgLeavingTable(c.customer);
		c.cs = CustomerState.Idle;
	  
	}
	  
	private void AskForBreak(){
		print("Asking for break");
		((GHHostRole) restaurant.host).msgCanIGoOnBreak(this);
	}
	  
	private void BackToWork(){
		print("Going Back To Work");
		((GHHostRole) restaurant.host).msgSetWaiter(this);
		BackTW = false;
	}
	 
	/*private void HereIsCheck(CustomerCheck c){

		DoTakeCheck(c);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.customer.msgHeresCheck(c.choice, c.cost);
		customerChecks.remove(c);
	}
	
	private void DoTakeCheck(CustomerCheck c){
		print("Giving "+ c.customer + " the check");
		waitergui.DoGoToTable(c.tablenumber);
	}*/

	//utilities
	  
	  public void setGui(GHWaiterGui wg){
		  waitergui = wg;
	  }
	  
	  public GHWaiterGui getGui(){
		  return waitergui;
	  }
	  
	  public boolean getOnBreak(){
		  return OnBreak;
	  }
	  
	  public void setWantToGoOnBreak(boolean b){
		  WantToGoOnBreak = b;
	  }
	  
	  public void setOnBreak(boolean b){
		  OnBreak = b;
	  }
	   
	private class MyCustomer {
		Customer customer;
		int tablenumber;
		String choice;
		CustomerState cs;
		
		MyCustomer(Customer cust, int table, CustomerState cstate){
			customer = cust;
			tablenumber = table;
			cs = cstate;
		}
		
		public CustomerState getState(){
			return cs;
		}
	}
	
	private class CustomerCheck {
		Customer customer;
		String choice;
		double cost;
		int tablenumber;
		
		CustomerCheck(Customer cu, String c, double co, int t){		
			customer = cu;
			choice = c;
			cost = co;
			tablenumber = t;
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
		atDestination.release();
		wState = WaiterState.relieveFromDuty;
		stateChanged();		
	}

	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}


	@Override
	public void msgAskForBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGui(Gui g) {
		waitergui = (GHWaiterGui) g;
		
	}
}

