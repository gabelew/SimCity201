package GHRestaurant.roles;

import restaurant.Restaurant;
import restaurant.interfaces.*;
import GHRestaurant.gui.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Cook Agent
 */

public class GHCookRole extends Role implements Cook {
	
	public List<Order> orders
	= Collections.synchronizedList(new ArrayList<Order>());
	public List<MarketOrder> marketOrders
	= Collections.synchronizedList(new ArrayList<MarketOrder>());
	static final int ORDERAMOUNT = 30;
	int nextmarket;
	private Timer timer = new Timer();
	public enum OrderState {PENDING,COOKING,DONECOOKING}
	public enum marketOrderState {waiting, ordering,ordered,waitingForBill, paying};
	//public String name;
	private GHCookGui cookgui = null;
	Map<String,Food> Inventory = new HashMap<String,Food>();	
	public List<MarketAgent> markets
	= new ArrayList<MarketAgent>();
	private Semaphore atDestination = new Semaphore(0,true);
	private Restaurant restaurant;


	public GHCookRole(int amount) {
		super();

		//this.name = name;
		nextmarket = 0;
		
		Inventory.put("steak", new Food("steak",5000));
		Inventory.put("chicken", new Food("chicken",5000));
		Inventory.put("salad", new Food("salad",amount));
		Inventory.put("pizza", new Food("pizza",7000));	
		
		Inventory.get("salad").setAmount(amount);


	}

	/*public String getName() {
		return name;
	}*/

	public List<Order> getOrders() {
		return orders;
	}
	
	/*
	public void setMarket(MarketAgent ma){
		markets.add(ma);
	}*/
	
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
	
	@Override
	public void msgCanIHelpYou(DeliveryMan DM, MarketAgent M) {
		for (MarketOrder order: marketOrders){
			if(order.Market==M){
				order.deliveryMan=DM;
				order.marketState=marketOrderState.ordering;
			}
		}	
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
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
		
		for (MarketOrder mOrder : marketOrders) {
			if(mOrder.marketState == marketOrderState.paying) {
				SendInvoiceToCashier(mOrder);
				return true;
			}
		}
		
		for (MarketOrder mOrder : marketOrders) {
			if(mOrder.marketState == marketOrderState.ordering) {
				mOrder.marketState = marketOrderState.ordered;
				GiveOrderToDeliverMan(mOrder);
				return true;
			}
		}
		
		OrderFromMarket();
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
		DoCookIt(o);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
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
	
	private void GiveOrderToDeliverMan(MarketOrder mo) {
		print("giving order to deliverman");
		mo.deliveryMan.msgHereIsOrder(mo.order);
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
			e.printStackTrace();
		}
		((GHWaiterRole) o.waiter).msgOrderIsReady(o.choice, o.tablenumber);
		orders.remove(o);
		
	}

	private void DoPlating(Order o){
		print(o.choice + " is ready!");
		cookgui.DoPlateIt();

	}
	
	
	private void SendInvoiceToCashier(MarketOrder mo) {
		((GHCashierRole) restaurant.cashier).msgHereIsInvoice(mo.deliveryMan, mo.cost);
		marketOrders.remove(mo);
	}
	
	private void OrderFromMarket() {
		Map<String,Integer>foodToOrder=new HashMap<String,Integer>();
		for(String s : Inventory.keySet()){
			Food food = Inventory.get(s);
			if(food.getAmount() <= food.getThreshold()){
				foodToOrder.put(s, ORDERAMOUNT);
			}
		}
		marketOrders.add(new MarketOrder(foodToOrder, markets.get(nextmarket), marketOrderState.waiting));
		markets.get(nextmarket).msgPlaceDeliveryOrder(this);
		nextmarket = (nextmarket+1)%markets.size();
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

	private class MarketOrder{
		public MarketOrder(Map<String, Integer> food,MarketAgent m, marketOrderState mos) {
			order=food;
			Market=m;
			marketState=mos;
		}
		Map<String,Integer> order;
		marketOrderState marketState;
		DeliveryMan deliveryMan;
		MarketAgent Market;
		double cost;
	}
	
	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market) {
		if(markets.size()==0){
			for(MarketAgent ma: restaurant.insideAnimationPanel.simCityGui.getMarkets()){
				this.addMarket(ma);
			}
		}
		
		else {
			for(MarketAgent ma : markets){
			if(ma.equals(market)){
				markets.remove(ma);
			}
		}
		}
	}

	@Override
	public void msgHereIsOrderFromMarket(DeliveryMan Dm, Map<String, Integer> choices, double cost) {
		for (MarketOrder order:marketOrders){
			if(order.deliveryMan==Dm){
				order.cost=cost;
				order.marketState=marketOrderState.paying;
			}
		}

		for(String s : choices.keySet()) {
			Food food = findFood(s);
			food.addFoodAmount(choices.get(s));
		}
		
	}

	private Food findFood(String s) {
		Food food = null;
		
		for(String i : Inventory.keySet()){
			if(i.equals(s)){
				food = Inventory.get(i);
			}
		}
		
		return food;
	}

	@Override
	public void msgIncompleteOrder(DeliveryMan deliveryMan, List<String> outOf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRelieveFromDuty(PersonAgent p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goesToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMarket(MarketAgent m) {
		markets.add(m);
	}

	@Override
	public void setGui(Gui g) {
		cookgui = (GHCookGui) g;
	}

	@Override
	public Gui getGui() {
		return cookgui;
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

	@Override
	public void msgMarketClosed(MarketAgent market) {
		// TODO Auto-generated method stub
		
	}
}

