package GCRestaurant.roles;

import restaurant.Restaurant;
import restaurant.RoleOrder;
import GCRestaurant.gui.GCCookGui;
import GCRestaurant.roles.GCHostRole.Table;
import restaurant.interfaces.Cook;

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
	private Map<String, Food> foods= new HashMap<String, Food>();
	
	private String name;
	private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	private List<Food> foodList = new ArrayList<Food>();
	private List<MarketAgent> markets = new ArrayList<MarketAgent>();
	
	private CookState state = CookState.makingMarketOrder;
	private enum OrderState{pending, cooking, done, served}
	private enum CookState {free, cooking, makingMarketOrder, wantsOffWork, leaving, relieveFromDuty, none, goToWork}
	private final int THRESHOLD = 2;
	private final int MAXSUPPLY = 6;
	private int marketCounter = 0;
	public PersonAgent replacementPerson = null;
	Restaurant restaurant;

	public GCCookRole() {
		super();
		
		//foods list
		foodList.add( new Food("steak",5, 3) );
		foodList.add( new Food("chicken",4, 3) );
		foodList.add( new Food("pizza",3, 5) );
		foodList.add( new Food("salad",2, 0) );
		//hashmap values
		foods.put("steak", foodList.get(0));
		foods.put("chicken",foodList.get(1));
		foods.put("pizza",foodList.get(2));
		foods.put("salad",foodList.get(3));
				
		//this.name = name;
	}
	
	public void setMarket(MarketAgent m)
	{
		markets.add(m);
	}
	
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	
	/**************************************************
	* Messages
	**************************************************/
	//Order Received from Waiter
	public void HereIsOrderMsg(GCWaiterRole w, GCCustomerRole c, Table t, String choice)
	{
		print("Received order from " + w.getName());
		//Food food = new Food(choice,getCookingTime(choice));
		orders.add(new Order(w,c,t,choice));
		stateChanged();
	}
	public void cannotFufillOrderMsg()
	{
		print("Market: " + markets.get(marketCounter).getName() + " Cannot fufill order");
		//marketCounter++;
		state = CookState.makingMarketOrder;
		stateChanged();
	}
	
	/*
	public void marketDeliveryMsg(MarketOrder m)
	{
		//adds the purchased market inventory to inventory
		for(Entry<String, Integer> item : m.delivery.entrySet())
		{
			for(Food f: foodList)
			{
				if(item.getKey().equals( f.choice) )
				{
					f.amount += item.getValue().intValue();
					print("Replenished " + item.getKey() + ", Stock now: " + f.amount);
				}
			}
		}
		if(m.state == m.state.deliverIncomplete)
		{
			print("delivery order not fully satisfied");
			state = CookState.makingMarketOrder;
		}
		stateChanged();
	}*/

	public void gotFoodMsg() 
	{
		print("Waiter received order");
		cookGui.pickedUpFood();
	}
	/****************************************************
	 * Actions
	 ***************************************************/
	
	// (1) Cooks order
	private void CookItAction(Order o)
	{
		//print(o.choice  + " " + o.food.amount);
		if(state == CookState.free)
		{
			if(o.food.amount > 0)
			{
				state = CookState.cooking;
				o.state = OrderState.cooking;
				print("cooking order");
				DoCooking(o);
			}
			else
			{
				print(o.food.choice + " is out of stock");
				o.waiter.OutOfStockMsg(o.customer);
				orders.remove(o);
				marketCounter = 0;
				state = CookState.makingMarketOrder;
			}
		}
		stateChanged();
	}
	
	// (2) Helper Class for CookIt()
	private void DoCooking(final Order o)
	{
		//gets food from fridge
		print("Getting food from fridge");
		cookGui.goToFridge(o.choice, orders.indexOf(o));
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		//cookGui.doGoHome();
		//try {busy.acquire();} 
		//catch (InterruptedException e) { e.printStackTrace();}
		//puts it on grill
		print("putting order on grill");
		cookGui.cookOrder(orders.indexOf(o));
		cookGui.goToGrill(orders.indexOf(o));
		try {busy.acquire();} 
		catch (InterruptedException e) { e.printStackTrace();}
		o.food.amount--;//Decrease inventory
		int cookingTime = o.food.cookingTime;
		//Runs Timer for Food's Cooking time
		timer.schedule(new TimerTask() {
			public void run() 
			{
				print("Order: " + o.choice + " is done");
				cookGui.goToGrill(orders.indexOf(o));
				try { busy.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
				cookGui.plateFood(orders.indexOf(o));
				try { busy.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
				o.state = OrderState.done;
				state = CookState.free;
				stateChanged();
			}
		},
		cookingTime*TIMERCONST);
	}
	
	// (3) Food is done, Alert Waiter
	private void OrderDone(Order o)
		{
			print(o.waiter.getName() + ", " + o.customer.getName() + "'s order is done");
			o.waiter.getFoodFromCookMsg(o);
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
				order.put(f.choice, new Integer(MAXSUPPLY-f.amount));
			}
		}
		if(order.size() != 0)
		{
			print("ORDERING FROM " + markets.get(marketCounter).getName());
			/*for(Entry<String, Integer> item : order.entrySet())
			{
				print("WANT TO ORDER ~~~~~~~~~" + item.getKey() + " " + item.getValue());
			}*/
			//markets.get(marketCounter).receivedOrderFromCook(order, this);
			marketCounter++;
		}
		stateChanged();
	}
	
	/****************************************************
	 * Scheduler.  Determine what action is called for, and do it.
	 ***************************************************/
	public boolean pickAndExecuteAnAction() 
	{
		try
		{
			
			if(state == CookState.wantsOffWork){
				boolean canGetOffWork = true;
				for (Order o : orders)
				{
					if(o.state != OrderState.pending)
					{
						canGetOffWork = false;
						break;
					}
				}
				if(canGetOffWork){
					state = CookState.leaving;
				}
			}
			
			if(state == state.relieveFromDuty){
				state = state.none;
				myPerson.releavedFromDuty(this);
				if(replacementPerson != null){
					replacementPerson.waitingResponse.release();
				}
				return true;
			}
			
			if(state == state.goToWork){
				state = state.free;
				cookGui.DoEnterRestaurant();
				return true;
			}
			
			if(state == CookState.makingMarketOrder && marketCounter < markets.size())
			{
				state = CookState.free;
				checkInventory();
				return true;
			}
			
			for(Order o: orders)
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
			return false;
		}
		catch(ConcurrentModificationException e)
		{
			return false;
		}
	}
	
	//release semaphore when action is done
	public void msgActionDone() {//from animation
		//print("msgAtTable() called");
		busy.release();// = true;
		stateChanged();
	}
	//Utility classes
	public class Food
	{
		public String choice;
		public int amount;
		public int cookingTime;
		//public int orderAmount;
		public Food(String _food, int cooktime, int inventory)
		{
			this.choice = _food;
			this.amount = inventory;
			this.cookingTime = cooktime;
		}	
	}
	
	public class Order 
	{
		Food food;
		Table table;
		GCWaiterRole waiter;
		GCCustomerRole customer;
		OrderState state;
		String choice;
		
		Order(GCWaiterRole w, GCCustomerRole c, Table t, String choice)
		{
			this.choice = choice;
			food = foods.get(choice);
			this.table = t;
			this.waiter = w;
			this.customer = c;
			state = OrderState.pending;
		}
	}

	@Override
	public void msgCanIHelpYou(DeliveryMan DM, MarketAgent M) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsOrderFromMarket(DeliveryMan Dm,
			Map<String, Integer> choices, double amount) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
	public void setGui(Gui GuiFactory) {
		cookGui = (GCCookGui) GuiFactory;
		
	}

	@Override
	public Gui getGui() {
		return (Gui) cookGui;
	}

	public void setRestaurant(Restaurant r) {
		this.restaurant = r;
	}

}

