package GHRestaurant.roles;

import agent.Agent;
import restaurant.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.roles.Role;

/**
 * Restaurant Market Agent
 */

public class GHMarketRole extends Role implements Market {
	public List<Order> orders
	= Collections.synchronizedList(new ArrayList<Order>());
	public enum OrderState {PENDING,PROCESSED,SENT}
	public String name;
	public double marketMoney;
	public Cashier cashier;
	Map<String,Food> Inventory = new HashMap<String,Food>();

	public GHMarketRole(String name, int s, int c, int sa, int p) {
		super();

		this.name = name;
		marketMoney = 1000;
		
		//The market is initially stocked with 1000 of each food
		Inventory.put("Steak", new Food("Steak",s));
		Inventory.put("Chicken", new Food("Chicken",c));
		Inventory.put("Salad", new Food("Salad",sa));
		Inventory.put("Pizza", new Food("Pizza",p));		
		
		}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return orders;
	}
	
	public void setCashier(Cashier ca){
		cashier = ca;
	}
	
	// Messages

	public void msgHereIsTheOrder(Cook co, String choice, int amount){
		print("Recieved msgHereIsTheOrder");
		orders.add(new Order(co, choice, amount, OrderState.PENDING));
		stateChanged();
	  }
	
	public void msgHereIsPayment(double m){
		print("Recieved msgHereIsPayment");
		marketMoney += m;
	}
	  
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		synchronized(orders){
		for (Order o : orders) {
		if (o.getState() == OrderState.PENDING) {
				ProcessOrder(o);//the action
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

	private void ProcessOrder(Order o){
		if(Inventory.get(o.choice).getAmount() <= 0){
			print("Out of order! Sorry for the inconvenience");
			Order temp = o;
			orders.remove(o);
			temp.cook.msgOutOfOrder();
		}
		else{
		print("processing order of " + o.amount + " " + o.choice + "'s");
		o.cook.msgDelivery(o.choice, o.amount);
		Inventory.get(o.choice).decAmount(o.amount);
		cashier.msgPayMarket(this,o.getCost());
		orders.remove(o);
		}
	}
	

	//utilities
	public class Order {
		Cook cook;
		int amount;
		private double cost;
		String choice;
		OrderState os;

		public Order(Cook co, String c, int a, OrderState o){
			cook = co;
			choice = c;
			amount = a;
			os = o;
			
			//the market will choose the cost of the order based on the amount and choice
			switch(c){
			case "Steak": setCost(amount * 10.00);
			break;
			
			case "Chicken": setCost(amount * 8.00);
			break;
			
			case "Salad": setCost(amount * 4.00);
			break;
			
			case "Pizza": setCost(amount * 6.00);
			break;
					
			}
			
		}
		
		public OrderState getState(){
			return os;
		}

		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}
		
	}
	
	private class Food{
		String foodtype;
		int amount;
		
		Food(String choice, int a){
			foodtype = choice;
			amount = a;
		}
		
		public void decAmount(int dec){
			amount -= dec;
		}
		
		public int getAmount(){
			return amount;
		}
				
		public void setAmount(int a){
			amount = a;
		}
	}
}

