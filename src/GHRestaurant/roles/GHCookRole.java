package GHRestaurant.roles;

import agent.Agent;
import restaurant.interfaces.*;
import GHRestaurant.gui.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.roles.Role;

/**
 * Restaurant Cook Agent
 */

public class GHCookRole extends Role implements Cook {
	
	public List<Order> orders
	= Collections.synchronizedList(new ArrayList<Order>());
	static final int ORDERAMOUNT = 30;
	int nextmarket;
	private Timer timer = new Timer();
	public enum OrderState {PENDING,COOKING,DONECOOKING}
	public String name;
	private GHCookGui cookgui;
	Map<String,Food> Inventory = new HashMap<String,Food>();	
	public List<Market> markets
	= new ArrayList<Market>();
	private Semaphore atDestination = new Semaphore(0,true);


	public GHCookRole(String name) {
		super();

		this.name = name;
		nextmarket = 0;
		
		Inventory.put("Steak", new Food("Steak",5000));
		Inventory.put("Chicken", new Food("Chicken",5000));
		Inventory.put("Salad", new Food("Salad",3000));
		Inventory.put("Pizza", new Food("Pizza",7000));	
		
		Inventory.get("Steak").setAmount(10);
		Inventory.get("Chicken").setAmount(10);
		Inventory.get("Salad").setAmount(10);
		Inventory.get("Pizza").setAmount(10);

	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return orders;
	}
	
	public void setMarket(Market ma){
		markets.add(ma);
	}
	
	// Messages

	public void msgAtTable(){//from animation
		atDestination.release();
		stateChanged();
	}
	
	public void msgHereIsAnOrder(Waiter waiter, String choice, int tablenumber){
		print("Recieved msgHereIsAnOrder");
		orders.add(new Order(waiter,choice,tablenumber,OrderState.PENDING));
		stateChanged();
	  }
	  
	public void msgFoodDone(Order o){
		o.os = OrderState.DONECOOKING;
		stateChanged();
	 }
	
	public void msgDelivery(String choice, int amount){
		print("Recieved order form market");
		Inventory.get(choice).addFoodAmount(amount);
		stateChanged();
	}
	
	public void msgOutOfOrder(){
		print("Recieved msgOutOfOrder");
		stateChanged();
	}
	 

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		
		/*for(Map.Entry<String, Food> entry : Inventory.entrySet()){
		if(entry.getValue().getAmount() <= entry.getValue().getThreshold()){
				markets.get(nextmarket).msgHereIsTheOrder(this, entry.getKey(), ORDERAMOUNT);
				nextmarket = (nextmarket+1)%markets.size();
				return true;
		}
		}*/
		synchronized(orders){
		for (Order o : orders) {
		if (o.getState() == OrderState.PENDING) {
				CookIt(o);//the action
				return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}
		}
		
		synchronized(orders){
		for (Order o : orders) {
		if (o.getState() == OrderState.DONECOOKING) {
				PlateIt(o);//the action
				return true;//return true to the abstract agent to reinvoke the scheduler.
		}
		}
		}
			
		//cookgui.DoGoHome();
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void CookIt(final Order o){
		
		//The cook has ran out of the order amount and the customer must reorder
		if(Inventory.get(o.choice).getAmount() <= 0){
			print("Out of order! Please go back to customer and ask to reorder");
			Order temp = o;
			orders.remove(o);
			((GHWaiterRole) temp.waiter).msgOutOfOrder(temp.tablenumber, temp.choice);
		}
		else{
			
		//if food is "low" then the cook orders from different market.
		if(Inventory.get(o.choice).getAmount() <= Inventory.get(o.choice).getThreshold()){
			((GHMarketRole) markets.get(nextmarket)).msgHereIsTheOrder(this, Inventory.get(o.choice).getFoodType(), ORDERAMOUNT);
			nextmarket = (nextmarket+1)%markets.size();
		}
			
		DoCookIt(o);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		o.os = OrderState.COOKING;
		Inventory.get(o.choice).decAmount();
		timer.schedule(new TimerTask(){
			public void run(){
				msgFoodDone(o);
			}
		},Inventory.get(o.choice).cookingtime);
		

		}
	}
	
	private void DoCookIt(Order o){
		print("cooking " + o.choice);
		cookgui.DoCookIt();
	}

	private void PlateIt(Order o){
		DoPlating(o);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((GHWaiterRole) o.waiter).msgOrderIsReady(o.choice, o.tablenumber);
		orders.remove(o);
		
	}

	private void DoPlating(Order o){
		print(o.choice + " is ready!");
		cookgui.DoPlateIt();

	}
	
	//utilities
	
	public void setGui(GHCookGui cg){
		cookgui = cg;
	}

	
	public class Order {
		Waiter waiter;
		int tablenumber;
		String choice;
		OrderState os;

		Order(Waiter w, String c, int t, OrderState o){
			waiter = w;
			choice = c;
			tablenumber = t;
			os = o;
		}
		
		public OrderState getState(){
			return os;
		}
		
	}
	
	private class Food{
		String foodtype;
		int cookingtime;
		int amount;
		int threshold;
		//int capacity;
		
		Food(String choice, int ct){
			foodtype = choice;
			cookingtime = ct;
			amount = 20;
			threshold = 10;
			//capacity = 100;	
		}
		
		public void decAmount(){
			amount--;
		}
		
		public void addFoodAmount(int a){
			amount += a;
		}
		
		public int getAmount(){
			return amount;
		}
		
		public int getThreshold(){
			return threshold;
		}
		
		public void setAmount(int a){
			amount = a;
		}
		
		public String getFoodType(){
			return foodtype;
		}
	}
}

