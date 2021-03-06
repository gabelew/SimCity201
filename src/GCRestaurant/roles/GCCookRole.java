package GCRestaurant.roles;

import restaurant.Restaurant;
import GCRestaurant.gui.GCCookGui;
import GCRestaurant.roles.GCHostRole.Table;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;

/**
 * Restaurant Cook Agent
 */

public class GCCookRole extends Role implements Cook
{
	private GCCookGui cookGui;
	private Semaphore busy = new Semaphore(0,true);
	private Timer timer = new Timer();
	final int TIMERCONST = 1000;
	private final int CHECK_STAND_TIME = 4000;
	private Map<String, Food> foods= new HashMap<String, Food>();
	
	public enum OrderState{pending, cooking, done, served}
	public List<GCOrder> orders = Collections.synchronizedList(new ArrayList<GCOrder>());
	private List<Food> foodList = new ArrayList<Food>();
	private List<MarketAgent> markets = new ArrayList<MarketAgent>();
	private List<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
	public enum MarketState {hasItems, ordering, noItems, paying, made, paid, ordered};
	
	private CookState state = CookState.makingMarketOrder;
	private enum CookState {free, cooking, makingMarketOrder, wantsOffWork, leaving, relieveFromDuty, none, goToWork}
	private final int THRESHOLD = 56;
	private final int MAXSUPPLY = 70;
	private int marketCounter = 0;
	public PersonAgent replacementPerson = null;
	boolean restaurantClosed = false;
	Restaurant restaurant;
	public GCRevolvingStandMonitor orderStand;
	public boolean checkOrderStand = true;
	public boolean unitTesting = false;

	public GCCookRole() {
		super();
		
		//foods list
		foodList.add( new Food("Steak",5, 50) );
		foodList.add( new Food("Chicken",4, 50) );
		foodList.add( new Food("Cookie",3, 50) );
		foodList.add( new Food("Salad",2, 50) );
		//hashmap values
		foods.put("Steak", foodList.get(0));
		foods.put("Chicken",foodList.get(1));
		foods.put("Cookie",foodList.get(2));
		foods.put("Salad",foodList.get(3));
	}
	
	public void addMarket(MarketAgent m) {
		markets.add(m);
	}
	
	/**************************************************
	* Messages
	**************************************************/
	//Order Received from Waiter
	public void HereIsOrderMsg(Waiter w, Customer c, Table t, String choice)
	{
		print("Received order from " + ((GCWaiterRole)w).myPerson.getName());
		orders.add(new GCOrder(w,c,t,choice));
		stateChanged();
	}
	
	//Market scams you, don't order from this market
	public void msgNeverOrderFromMarketAgain(MarketAgent market) 
	{
		for(MarketAgent m : markets)
		{
			if(m.equals(market))
			{
				markets.remove(m);
				break;
			}
		}
		if(markets.size() == 0)
		{
			markets.add(market);
		}
	}
	
	public void cannotFufillOrderMsg()
	{
		print("Market: " + markets.get(marketCounter).getName() + " Cannot fufill order");
		//marketCounter++;
		state = CookState.makingMarketOrder;
		stateChanged();
	}
	
	/**
	 * deliveryman arrives at restaurant
	 */
	public void msgCanIHelpYou(DeliveryMan deliveryMan_, MarketAgent markt) {
		for (MarketOrder order: marketOrders)
		{
			if(order.market == markt){
				print("BEING HELPED BY DELIVERYMAN @@@@@@");
				order.deliveryMan = deliveryMan_;
				order.marketState = MarketState.ordering;
			}
		}
	}

	/*
	 * receives order from delivery man, resttocks
	 */
	public void msgHereIsOrderFromMarket(DeliveryMan deliveryMan_, Map<String, Integer> stockOrdered, double amount) 
	{
		print("GOT FOOD FROM DELIVERYMAN &&&&&&&&&&&");
		for (MarketOrder order: marketOrders)
		{
			if(order.deliveryMan == deliveryMan_)
			{
				order.price = amount;
				order.marketState = MarketState.paying;
			}
		}
		for(Entry<String, Integer> item : stockOrdered.entrySet())
		{
			for(Food f: foodList)
			{
				if(item.getKey().equals( f.choice.toLowerCase()) )
				{
					f.amount += item.getValue().intValue();
					print("Replenished " + item.getKey() + ", Stock now: " + f.amount);
				}
			}
		}
		
	}

	@Override
	public void msgIncompleteOrder(DeliveryMan deliveryMan, List<String> outOf) 
	{
		print("delivery order not fully satisfied");
		state = CookState.makingMarketOrder;
	}

	public void gotFoodMsg(GCOrder o) 
	{
		print("Waiter received order");
		cookGui.pickedUpFood(o);
	}
	public void msgRelieveFromDuty(PersonAgent p) 
	{
		replacementPerson = p;
		state = CookState.leaving;
		this.stateChanged();
	}
	public void msgRestaurantClosing() {
		restaurantClosed = true;
		stateChanged();
	}

	public void msgMarketClosed(MarketAgent market) {
		//markets.remove(market);
		
	}

	/****************************************************
	 * Animation functions
	 ***************************************************/
		public void goesToWork() 
		{
			state = CookState.goToWork;
			stateChanged();
		}
		//release semaphore when action is done
		public void msgActionDone() {
			busy.release();
		}
		//release semaphore for shift change
		public void doneWithShift()
		{
			busy.release();
			state = CookState.relieveFromDuty;
		}
	/****************************************************
	 * Actions
	 ***************************************************/
	
	// (1) Cooks order
	private void CookItAction(GCOrder o)
	{
		if(unitTesting)
		{
			OrderDone(o);
		}
		//print(o.choice  + " " + o.food.amount);
		if(state == CookState.free)
		{
			if((foods.get(o.choice)).amount <= THRESHOLD && marketOrders.size() == 0)
			{
				checkInventory();
			}
			if((foods.get(o.choice)).amount > 0)
			{
				state = CookState.cooking;
				o.state = OrderState.cooking;
				print("cooking order");
				DoCooking(o);
			}
			else
			{
				print(o.choice + " is out of stock");
				((GCWaiterRole)o.waiter).OutOfStockMsg(o.customer);
				orders.remove(o);
				marketCounter = 0;
				state = CookState.makingMarketOrder;
			}
		}
		stateChanged();
	}
	
	// (2) Helper Class for CookIt()
	private void DoCooking(final GCOrder o)
	{
		//gets food from fridge
		print("Getting food from fridge");
		cookGui.goToFridge(o);
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		
		//puts it on grill
		print("putting order on grill");
		cookGui.cookFoodOnGrill(o,orders.indexOf(o));
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		
		(foods.get(o.choice)).amount--;//Decrease inventory
		int cookingTime = (foods.get(o.choice)).cookingTime;
		//Runs Timer for Food's Cooking time
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print("Order: " + o.choice + " is done");
				cookGui.getFoodFromGrill(o);
				try { busy.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
				print("getting food from grill");
				cookGui.plateFood(o);
				try { busy.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
				print("FOOD IS DONE ~_~~~~~~~_!");
				o.state = OrderState.done;
				state = CookState.free;
				stateChanged();
			}
		},
		cookingTime*TIMERCONST);
	}
	
	// (3) Food is done, Alert Waiter
	private void OrderDone(GCOrder o)
		{
			print(((GCWaiterRole)o.waiter).getName() + ", " + ((GCCustomerRole)o.customer).getName() + "'s order is done");
			((GCWaiterRole)o.waiter).getFoodFromCookMsg(o);
			orders.remove(o);
			stateChanged();
		}
	
	// (4) Check Inventory
	private void checkInventory()
	{
		Map<String, Integer> order = new HashMap<String, Integer>();
		for(Food f: foodList)
		{
			if(f.amount <= THRESHOLD)
			{
				order.put(f.choice.toLowerCase(), new Integer(MAXSUPPLY-f.amount));
			}
		}
		if(order.size() != 0)
		{
			print("ORDERING FROM " + markets.get(marketCounter).getName());
			for(Entry<String, Integer> item : order.entrySet())
			{
				print("WANT TO ORDER ~~~~~~~~~" + item.getKey() + " " + item.getValue());
			}
			marketOrders.add(new MarketOrder(order, markets.get(marketCounter)));
			
			//finds a suitable market
			/*
			while(!markets.get(marketCounter).isOpen)
			{
				marketCounter++;
				if(marketCounter == markets.size()+1)
				{
					marketCounter = 0;
					break;
				}
			}
			if(marketCounter < markets.size())
			{
				
			}*/
			
			markets.get(marketCounter).msgPlaceDeliveryOrder((Cook)this);
		}
		stateChanged();
	}
	
	//(5) places the market order
	private void placeMarketOrder(MarketOrder mkOrder)
	{
		//print("DELIVERY MAN GIVE ME FOOD #######################");
		mkOrder.deliveryMan.msgHereIsOrder(mkOrder.choices);
	}
	
	//(6) pays for the market order
	private void payForMarketOrder(MarketOrder mkOrder)
	{
		((GCCashierRole)restaurant.cashier).msgHereIsBill(mkOrder.deliveryMan,mkOrder.price);
		marketOrders.remove(mkOrder);
	}
	
	//(7) checks order stand
	private void checkOrderStandAction()
	{
		if(!unitTesting)
		{
			cookGui.checkOrderStand();
			try { busy.acquire();} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		if(!orderStand.isEmpty())
		{
			print("Orderstand has orders in it!");
		}
		else
		{
			print("Orderstand has no orders in it!");
		}
		while(!orderStand.isEmpty()) 
		{
			GCOrder order = orderStand.remove();
			if(order != null) 
			{
				orders.add(order);
			}
		}
			
			timer.schedule(new TimerTask() {
				public void run() 
				{
					checkOrderStand = true;
					stateChanged();
				}
			}, CHECK_STAND_TIME);
		checkOrderStand = false;
	}
	/****************************************************
	 * Scheduler.  Determine what action is called for, and do it.
	 ***************************************************/
	public boolean pickAndExecuteAnAction() 
	{
		try
		{
			if(state == CookState.relieveFromDuty){
				state = CookState.none;
				myPerson.releavedFromDuty(this);
				if(replacementPerson != null){
					replacementPerson.waitingResponse.release();
				}
				return true;
			}
			
			if(state == CookState.goToWork){
				state = CookState.free;
				cookGui.DoEnterRestaurant();
				return true;
			}
			
			if(state == CookState.leaving){
				state = CookState.none;
				cookGui.DoLeaveRestaurant();
				try {busy.acquire();}
				catch (InterruptedException e) 
				{e.printStackTrace();}
				return true;
			}
			
			if(restaurantClosed)
			{
				state = CookState.leaving;
				restaurantClosed = false;
				return true;
			}
			if(state == CookState.makingMarketOrder && marketCounter < markets.size())
			{
				state = CookState.free;
				checkInventory();
				return true;
			}
			
			for(MarketOrder m : marketOrders)
			{
				if(m.marketState == MarketState.ordering)
				{
					placeMarketOrder(m);
					m.marketState = MarketState.ordered;
					return true;
				}
			}
			for(MarketOrder m : marketOrders)
			{
				if(m.marketState == MarketState.paying)
				{
					payForMarketOrder(m);
					m.marketState = MarketState.paid;
					return true;
				}
			}
			
			for(GCOrder o: orders)
			{
				//if order is done put it on a plate and serve it
				if(o.state == OrderState.done)
				{
					OrderDone(o);
					o.state = OrderState.served;
					return true;
				}
				//If order is pending, cook order
				if(o.state == OrderState.pending)
				{
					CookItAction(o);
					return true;
				}
			}
			//Shared Data
			if(checkOrderStand)
			{
				checkOrderStandAction();
				return true;
			}
			return false;
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}
	
	
	
	//Utility classes
	public class Food
	{
		public String choice;
		public int amount;
		public int cookingTime;
		public Food(String _food, int cooktime, int inventory)
		{
			this.choice = _food;
			this.amount = inventory;
			this.cookingTime = cooktime;
		}	
	}

	public class MarketOrder
	{
		public Map<String,Integer> choices;
		public MarketState marketState = MarketState.made;
		public DeliveryMan deliveryMan;
		public MarketAgent market;
		public double price;
		public MarketOrder(Map<String, Integer> foodsToOrder, MarketAgent mrket) 
		{
			this.choices = foodsToOrder;
			this.market = mrket;
		}
	}
	
	
/**
 * Utility functions
 */
	public void setGui(Gui GuiFactory) {
		cookGui = (GCCookGui) GuiFactory;
		
	}

	public Gui getGui() {
		return (Gui) cookGui;
	}

	public void setRestaurant(Restaurant r) {
		this.restaurant = r;
	}

	public void setRevolvingStand(GCRevolvingStandMonitor r) {
		this.orderStand = r;
	}

}

