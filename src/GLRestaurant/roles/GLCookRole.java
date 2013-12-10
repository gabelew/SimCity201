package GLRestaurant.roles;

import agent.Agent;
import GLRestaurant.gui.GLCookGui;
import GLRestaurant.roles.GLHostRole.State;
import GLRestaurant.roles.WaiterOrder;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.LoggedEvent;

/**
 * Restaurant Cook Agent
 */

public class GLCookRole extends Role implements Cook{

	private GLRevolvingStandMonitor revolvingStand;
	public boolean checkStand;
	private final int STEAKTIME = 5000;
	private final int CHICKENTIME = 6000;
	private final int SALADTIME = 4000;
	private final int COOKIETIME = 7000;
	private final int CHECKSTANDTIME = 5000;
	private final int STARTAMOUNT = 50;
	private boolean restaurantClosed = false;
	boolean firstRestock = false;
	int restockCount = 0;
	PersonAgent replacementPerson = null;
	private Semaphore waitingResponse = new Semaphore(0,true);
	boolean marketsAreStocked = true;
	
	private int orderNum = 1;
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
		final int threshhold = 5; // when it is low
		final int capacity = 50;
		restockState rs = restockState.none;
		int unfulfilled;
		Food(String t, int c, int a) {
			type = t;
			cookingTime = c;
			amount = a;
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
	
	List<MarketOrder> marketOrders = new CopyOnWriteArrayList<MarketOrder>();
	
	public enum marketOrderState {waiting, ordering,ordered,waitingForBill, paying};
	
	public enum orderState {outOfFood, pending, preparing, cooked, finished};
	public enum restockState {none, outOfStock, pending, finished};
	enum State {none, goToWork, working, leaving, relieveFromDuty, wantsOffWork};
	State state = State.none;
	
	List<WaiterOrder> orders = Collections.synchronizedList(new ArrayList<WaiterOrder>());
	
	// agent correspondents
	private List<MyMarket> markets = new CopyOnWriteArrayList<MyMarket>(); 
	private MyMarket currentMarket;
	
	Map<String, Food> foods = new ConcurrentHashMap<String, Food>();
	
	public GLCookGui cookGui = null;

	public GLCookRole(int amount) {
		super();
		checkStand = true;
		foods.put("steak", new Food("steak", STEAKTIME, STARTAMOUNT));
		foods.put("chicken", new Food("chicken", CHICKENTIME, STARTAMOUNT));
		foods.put("salad", new Food("salad", SALADTIME, STARTAMOUNT));
		foods.put("cookie", new Food("cookie", COOKIETIME, amount));
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
	public void msgRelieveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.wantsOffWork;
		this.stateChanged();
	}
	public void msgAnimationHasLeftRestaurant() {
		state = State.relieveFromDuty;
		waitingResponse.release();
	}
	
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

	public void msgHereIsOrder(GLWaiterRole w, String choice, GLCustomerRole c) {
		if(foods.get(choice).amount > 0) {
			orders.add(new WaiterOrder(w, choice, c, orderState.pending, orderNum++));
			foods.get(choice).amount--;
		} else {
			orders.add(new WaiterOrder(w, choice, c, orderState.outOfFood, orderNum++));
		}
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
		
		if(!firstRestock) {
			firstRestock = true;
			currentMarket = markets.get(0);
		}
		
		if(state == State.wantsOffWork){
			boolean canGetOffWork = true;
			for (WaiterOrder o : orders)
			{
				if(o.s != orderState.finished)
				{
					canGetOffWork = false;
					break;
				}
			}
			if(canGetOffWork){
				state = State.leaving;
			}
		}
		
		if(state == State.relieveFromDuty){
			state = State.none;
			myPerson.releavedFromDuty(this);
			if(replacementPerson != null){
				replacementPerson.waitingResponse.release();
			}
		}
		if(state == State.goToWork){
			state = State.working;
			cookGui.DoEnterRestaurant();
			return true;
		}
		if(state == State.leaving){
			state = State.none;
			cookGui.DoLeaveRestaurant();
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(restaurantClosed) {
				restaurantClosed = false;
			}
			return true;
		}
		
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
		
		
		for (MarketOrder mOrder : marketOrders) {
			if(mOrder.marketState == marketOrderState.paying) {
				giveCashierMarketBill(mOrder);
				return true;
			}
		}
		
		for (MarketOrder mOrder : marketOrders) {
			if(mOrder.marketState == marketOrderState.ordering) {
				mOrder.marketState = marketOrderState.ordered;
				placeOrder(mOrder);
				return true;
			}
		}

		// Order food from market if nothing else on scheduler to be done
		orderFoodFromMarket();
		
		if(checkStand) {
			checkRevolvingStand();
			return true;
		}
		
		if(restaurantClosed) {
			state = State.wantsOffWork;
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void giveCashierMarketBill(MarketOrder mOrder) {
		((GLCashierRole)restaurant.cashier).msgHereIsInvoice(mOrder.price, mOrder.deliveryMan);
		marketOrders.remove(mOrder);
	}
	
	private void tellWaiter(WaiterOrder o) {
		Do("Out of " + o.choice);
		o.s = orderState.finished;
		o.w.msgOutOfFood(o.c, o.choice);
		orderFoodFromMarket();
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
		o.w.msgOrderDone(o.c, o.choice, cookGui.getPlateX(o.orderNum), cookGui.getPlateY(o.orderNum));
	}
	
	private void orderFoodFromMarket() {
		if(marketsAreStocked) {
			boolean orderNeeded = false;
			Map<String,Integer>foodsToOrder=new HashMap<String,Integer>();
			Iterator<String> it1 = foods.keySet().iterator();
			while(it1.hasNext()) { //iterate through foods map, send a message for each item
				String key = it1.next();
				Food f = foods.get(key);
				if (restockState.none == f.rs && f.amount <= f.threshhold) {
					int amountNeeded = f.capacity - f.amount;
					foodsToOrder.put(f.type, amountNeeded);
					f.rs = restockState.pending;
					orderNeeded = true;
				}
			}
			if(orderNeeded) {
				marketOrders.add(new MarketOrder(foodsToOrder, currentMarket.m, marketOrderState.waiting));
				currentMarket.m.msgPlaceDeliveryOrder((Cook) this);
			}
		}
	}

	private void changeMarket() {
		if(currentMarket.equals(markets.get(0))) {
			currentMarket = markets.get(1);			
		} else if(currentMarket.equals(markets.get(1))) {
			currentMarket = markets.get(2);			
		} else if(currentMarket.equals(markets.get(2))) {
			currentMarket = markets.get(3);			
		} else if(currentMarket.equals(markets.get(3))) {
			currentMarket = markets.get(4);				
		} else if(currentMarket.equals(markets.get(4))) {
			currentMarket = markets.get(5);			
		} else {
			marketsAreStocked = false;
		}
	}
	
	public void checkRevolvingStand() {
//		if(!revolvingStand.isEmpty()){
//			log.add(new LoggedEvent("Checked Revolving Stand and it had orders in it."));
//		}else{
//			log.add(new LoggedEvent("Checked Revolving Stand and it was empty."));
//		}
		while(!revolvingStand.isEmpty()) {
			WaiterOrder order = revolvingStand.remove();
			order.orderNum = orderNum++;
			if(order != null) {
				orders.add(order);
			}
		}
			checkStand = false;
			timer.schedule(new TimerTask() {
				public void run() {
					checkStand = true;
					stateChanged();
				}
			}, CHECKSTANDTIME);
		
	}
	
	private void placeOrder(MarketOrder mOrder) {
		mOrder.deliveryMan.msgHereIsOrder(mOrder.Choices);
	}

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

	@Override
	public void msgCanIHelpYou(DeliveryMan DM, MarketAgent m) {
		for(MarketOrder order: marketOrders) {
			if(order.Market == m) {
				order.deliveryMan = DM;
				order.marketState = marketOrderState.ordering;
			}
		}
	}

	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market) {
		if(markets.size() > 0) {
			markets.remove(market);
		} else {
			for(MarketAgent ma: restaurant.insideAnimationPanel.simCityGui.getMarkets()) {
				this.addMarket(ma);
			}
		}
	}

	@Override
	public void msgHereIsOrderFromMarket(DeliveryMan Dm,
			Map<String, Integer> choices, double amount) {
		for(MarketOrder order: marketOrders) {
			if(order.deliveryMan == Dm) {
				order.price = amount;
				order.marketState = marketOrderState.paying;
			}
		}
		for(String key:choices.keySet()) {
			Food food = findFood(key);
			food.amount = food.amount + choices.get(key);
		}
		
	}

	@Override
	public void msgIncompleteOrder(DeliveryMan deliveryMan, List<String> outOf) {
		changeMarket();
	}

	@Override
	public void setGui(Gui waiterGuiFactory) {
		this.cookGui = (GLCookGui) waiterGuiFactory;
	}
	
	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}
	
	public void setRevolvingStand(GLRevolvingStandMonitor r) {
		this.revolvingStand = r;
	}
	
	public GLRevolvingStandMonitor getRevolvingStand() {
		return revolvingStand;
	}

}

