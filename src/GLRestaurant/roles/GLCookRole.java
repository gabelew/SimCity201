package GLRestaurant.roles;

import agent.Agent;
import CMRestaurant.roles.CMCookRole.marketOrderState;
import GLRestaurant.gui.GLCookGui;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.RoleOrder;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;

/**
 * Restaurant Cook Agent
 */

public class GLCookRole extends Role implements Cook{

	private final int STEAKTIME = 5000;
	private final int CHICKENTIME = 6000;
	private final int SALADTIME = 4000;
	private final int COOKIETIME = 7000;
	private int orderNumber = 1;
	boolean firstRestock = false;
	int restockCount = 0;
	
	public Restaurant restaurant;
	Timer timer = new Timer();
	private class MyMarket {
		MarketAgent m;
		MyMarket(MarketAgent market) {
			this.m = market;
		}
	}
	class Food {
		String type; 
		int cookingTime;
		int amount;
		final int threshhold = 3; // when it is low
		final int capacity = 8;
		MarketAgent currentMarket;
		restockState rs = restockState.none;
		int unfulfilled;
		Food(String t, int c, int a) {
			type = t;
			cookingTime = c;
			amount = a;
		}
	}
	class WaiterOrder {
		int orderNum;
		GLWaiterRole w;
		String choice;
		GLCustomerRole c;
		orderState s;
		WaiterOrder(GLWaiterRole w, String choice, GLCustomerRole c, orderState os) {
			this.w = w;
			this.choice = choice;
			this.c = c;
			this.s = os;
			this.orderNum = orderNumber++;
		}
	}
	
	public class MarketOrder{
		public MarketOrder(Map<String, Integer> foodsToOrder,MarketAgent market,marketOrderState s) {
			Choices=foodsToOrder;
			Market=market;
			marketState=s;
		}
		Map<String,Integer>Choices;
		marketOrderState marketState;
		DeliveryMan deliveryMan;
		MarketAgent Market;
		double price;
	}
	public enum marketOrderState {waiting, ordering,ordered,waitingForBill, paying};
	
	public enum orderState {outOfFood, pending, preparing, cooked, finished};
	public enum restockState {none, outOfStock, pending, finished};
	List<WaiterOrder> orders = Collections.synchronizedList(new ArrayList<WaiterOrder>());
	
	// agent correspondents
	private List<MyMarket> markets = new CopyOnWriteArrayList<MyMarket>(); 
	private GLHostRole host;
	
	Map<String, Food> foods = new ConcurrentHashMap<String, Food>();
	
	public GLCookGui cookGui = null;

	public GLCookRole(int amount) {
		super();
		foods.put("steak", new Food("steak", STEAKTIME, 3));
		foods.put("chicken", new Food("chicken", CHICKENTIME, 3));
		foods.put("salad", new Food("salad", SALADTIME, 3));
		foods.put("cookie", new Food("cookie", COOKIETIME, amount));
	}


	
	// Messages
	
	/**
	 * Hack to empty cook inventory of all foods to zero.
	 */
	public void msgRemoveAllFood() {
		Iterator<String> it1 = foods.keySet().iterator();
		while(it1.hasNext()) { 
			String key = it1.next();
			Food f = foods.get(key);
			f.amount = 0;
		}
		stateChanged();
	}
	
	public void msgOpeningRestock() {
		// initializes currentMarket for every food type
		Iterator<String> it1 = foods.keySet().iterator();
		while(it1.hasNext()) { 
			String key = it1.next();
			Food f = foods.get(key);
			f.currentMarket = markets.get(0).m; // default market is marketOne from RestaurantPanel
		}
		stateChanged();
	}

	public void msgHereIsOrder(GLWaiterRole w, String choice, GLCustomerRole c) {
		if(foods.get(choice).amount > 0) {
			orders.add(new WaiterOrder(w, choice, c, orderState.pending));
			foods.get(choice).amount--;
		} else {
			orders.add(new WaiterOrder(w, choice, c, orderState.outOfFood));
		}
		stateChanged();
	}
	
	public void msgNoStock(MarketAgent m, String choice, int unfulfilled) {
		Food f = findFood(choice);
		f.rs = restockState.outOfStock;
		f.unfulfilled = unfulfilled;
		stateChanged();
	}
	
	public void msgRestockDone(MarketAgent m, String choice, int amount) {
		if(!firstRestock) {
			restockCount++;
			if(4 == restockCount) {
				host.msgCookHasRestocked();
				firstRestock = true;
			}
		}
		Food f = findFood(choice);
		if(0 == f.unfulfilled) {
			f.rs = restockState.none;
		}
		f.amount += amount;
		stateChanged();
	}
	
	public void msgGotPlate(GLWaiterRole w, GLCustomerRole c) {
		synchronized(orders) {
			for (WaiterOrder o : orders) {
				if (o.s == orderState.finished && o.c == c) {
					cookGui.finPlate(o.orderNum);
				}
			}
		}
	}

	public void foodDone(WaiterOrder o) {
		o.s = orderState.cooked;
		stateChanged();
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(orders) {
			for (WaiterOrder o : orders) {
				if (o.s == orderState.outOfFood) {
					tellWaiter(o);
					return true;
				}
			}
		}
		
		// Plate any orders
		synchronized(orders) {
			for (WaiterOrder o : orders) {
				if (o.s == orderState.cooked) {
					plateIt(o);
					return true;
				}
			}
		}
		
		// Cook any pending orders
		synchronized(orders) {
			for (WaiterOrder o : orders) {
				if (o.s == orderState.pending) {
					o.s = orderState.preparing;
					cookIt(o);
					return true;
				}
			}
		}

		// Order food from market if nothing else on scheduler to be done
		//orderFoodFromMarket();

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void tellWaiter(WaiterOrder o) {
		Do("Out of " + o.choice);
		o.s = orderState.finished;
		o.w.msgOutOfFood(o.c, o.choice);
		//orderFoodFromMarket();
	}
	
	private void cookIt(final WaiterOrder o) {
		Do ("Cooking " + o.choice);
		cookGui.cook(o.choice, o.orderNum);
		timer.schedule(new TimerTask() {
			public void run() {
				foodDone(o);
			}
		},
		foods.get(o.choice).cookingTime);
	}
	
	private void plateIt(WaiterOrder o) {
		cookGui.finCook(o.orderNum);
		Do ("Plating " + o.choice);
		o.s = orderState.finished;
		cookGui.plate(o.choice, o.orderNum);
		o.w.msgOrderDone(o.c, o.choice, cookGui.getPlateX(), cookGui.getPlateY(o.orderNum));
	}
	
//	private void orderFoodFromMarket() {
//		Iterator<String> it1 = foods.keySet().iterator();
//		while(it1.hasNext()) { //iterate through foods map, send a message for each item
//			String key = it1.next();
//			Food f = foods.get(key);
//			if(restockState.outOfStock == f.rs) {
//				changeMarket(f.type);
//				if(restockState.finished != f.rs) {
//					Do("Reordering " + f.unfulfilled + " of " + f.type + " from " + f.currentMarket.getName());
//					f.currentMarket.msgHereIsRestockOrder(f.type, this, f.unfulfilled);
//					f.unfulfilled = 0;	
//					f.rs = restockState.pending;
//				}
//			} else if (restockState.none == f.rs && f.amount <= f.threshhold) {
//				int amountNeeded = f.capacity - f.amount;
//				Do("Ordering " + amountNeeded + " of " + f.type + " from " + f.currentMarket.getName());
//				f.currentMarket.msgHereIsRestockOrder(f.type, this, amountNeeded);
//				f.rs = restockState.pending;
//			}
//		}
//	}

//	private void changeMarket(String food) {
//		Iterator<String> it1 = foods.keySet().iterator();
//		while(it1.hasNext()) { 
//			String key = it1.next();
//			Food f = foods.get(key);
//			if (food == f.type) {
//				if(markets.get(0).m == f.currentMarket)	
//					f.currentMarket = markets.get(1).m; //change market to ButcherK
//				else if(markets.get(1).m == f.currentMarket)
//					f.currentMarket = markets.get(2).m; // change market to Pagodas
//				else {
//					f.currentMarket = null; // no more markets available
//					f.rs = restockState.finished;
//					print("All markets are out of stock of " + food);
//				}
//			}
//		}
//	}

	//utilities
	
	private Food findFood(String foodType) {
		Food food = null;
		Iterator<String> it1 = foods.keySet().iterator();
		while(it1.hasNext()) { 
			String key = it1.next();
			Food f = foods.get(key);
			if (foodType.equals(f.type))
				food = f;
		}
		return food;
	}
	
	public void addMarket(MarketAgent m) {
		markets.add(new MyMarket(m));
	}

	public void setGui(GLCookGui gui) {
		cookGui = gui;
	}
	
	public GLCookGui getGui() {
		return cookGui;
	}
	
	public void setHost(GLHostRole h) {
		this.host = h;
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
	public void setGui(Gui waiterGuiFactory) {
		this.cookGui = (GLCookGui) waiterGuiFactory;
	}
	
	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

}

