package city.roles;

import java.util.*;
import java.util.concurrent.Semaphore;
import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import restaurant.Restaurant;
import restaurant.gui.CookGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;

public class CookRole extends Role implements Cook {
	
	public List<RoleOrder> orders = Collections.synchronizedList(new ArrayList<RoleOrder>());
	public List<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
	public List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	public List<MarketOrder>marketOrders=Collections.synchronizedList(new ArrayList<MarketOrder>());
	
	private Semaphore waitingResponse = new Semaphore(0,true);
	PersonAgent replacementPerson = null;
	Timer timer = new Timer();
	boolean orderFromMarket = true;
	
	public CookGui cookGui = null;

    static final int SALAD_COOKTIME = 7000;
    static final int STEAK_COOKTIME = 15000;
    static final int CHICKEN_COOKTIME = 20000;
    static final int COOKIE_COOKTIME = 5000;
	static final int SALAD_INIT_AMOUNT = 50;
	static final int SALAD_LOW_AMOUNT = 3;
	static final int SALAD_CAPACITY = 50;
	static final int STEAK_INIT_AMOUNT = 50;
	static final int STEAK_LOW_AMOUNT = 3;
	static final int STEAK_CAPACITY = 50;
	static final int CHICKEN_INIT_AMOUNT = 50;
	static final int CHICKEN_LOW_AMOUNT = 3;
	static final int CHICKEN_CAPACITY = 50;
	static final int COOKIE_INIT_AMOUNT = 50;
	static final int COOKIE_LOW_AMOUNT = 3;
	static final int COOKIE_CAPACITY = 50;
	private int grillItems = 0;
	private int counterItems = 0;
	private int pickUpTableItems = 0;
	public Restaurant restaurant;

	enum State {none, goToWork, working, leaving, relieveFromDuty};
	State state = State.none;
	
    public class Food{
    	private String choice;
    	int cookingTime;
    	int amount;
    	int low;
    	int capacity;
    	OrderingState os;
    	
    	Food(String c, int ct, int a, int l, int cap)
    	{
    		this.choice = c;
    		cookingTime = ct;
    		amount = a;
    		low = l;
    		capacity = cap;
    		os = OrderingState.none;
    	}

		public Food(Food f) {
			this.choice = f.choice;
    		cookingTime = f.cookingTime;
    		amount = f.amount;
    		low = f.low;
    		capacity = f.capacity;
    		os = OrderingState.none;
		}

		public String getChoice() {
			return choice;
		}

		public void setChoice(String choice) {
			this.choice = choice;
		}

		public int getAmount() {
			return this.amount;
		}
    }    
    private enum OrderingState {none, order, ordered};
    
	public class RoleOrder{
		public Waiter waiter;
		public String choice;
		public int table;
		public OrderState state;
		
		RoleOrder(Waiter w, String c, int t ,OrderState s){
			waiter = w;
			choice = c;
			table = t;
			state = s;	
		}
		
	}
	public enum OrderState {PENDING, COOKING, DONE, QUEUED, BURNING};
	
	public class MyMarket{
		private MarketAgent market;
		public Map <String, InventoryState> foodInventoryMap= new HashMap<String, InventoryState>();
		MyMarket(MarketAgent m){
			setMarket(m);
			foodInventoryMap.put("steak", InventoryState.POSSIBLE);
			foodInventoryMap.put("salad", InventoryState.POSSIBLE);
			foodInventoryMap.put("chicken", InventoryState.POSSIBLE);
			foodInventoryMap.put("cookie", InventoryState.POSSIBLE);
		}
		public MarketAgent getMarket() {
			return market;
		}
		public void setMarket(MarketAgent market) {
			this.market = market;
		}
	}
	public enum InventoryState {POSSIBLE, ATTEMPTING, OUTOFCHOICE};

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
	
	public CookRole(){
		super();
	
		foods.add(new Food("salad",SALAD_COOKTIME, SALAD_INIT_AMOUNT, SALAD_LOW_AMOUNT, SALAD_CAPACITY));
		foods.add(new Food("steak",STEAK_COOKTIME, STEAK_LOW_AMOUNT, STEAK_LOW_AMOUNT, STEAK_CAPACITY));
		foods.add(new Food("chicken",CHICKEN_COOKTIME, CHICKEN_INIT_AMOUNT, CHICKEN_LOW_AMOUNT, CHICKEN_CAPACITY));
		foods.add(new Food("cookie",COOKIE_COOKTIME, COOKIE_INIT_AMOUNT, COOKIE_LOW_AMOUNT, COOKIE_CAPACITY));
	}


	public void setGui(CookGui g) {
		cookGui = g;
	}

	public CookGui getGui() {
		return cookGui;
	}

	public void goesToWork() {
		state = State.goToWork;
		stateChanged();
	}
	public void msgRelieveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	public void msgAnimationHasLeftRestaurant() {
		state = State.relieveFromDuty;
		waitingResponse.release();
		this.stateChanged();
	}
	public void msgHereIsOrder(Waiter w, String choice, int table)
	{
		orders.add(new RoleOrder(w, choice, table, OrderState.PENDING));
		stateChanged();
		print("order received!!");
	}
	public void msgFoodDone(RoleOrder o)
	{
		o.state = OrderState.DONE;
		stateChanged();
	}
	
	public void msgCanIHelpYou(DeliveryMan DMR,MarketAgent m){
		for (MarketOrder order: marketOrders){
			if(order.Market==m){
				order.deliveryMan=DMR;
				order.marketState=marketOrderState.ordering;
			}
		}
	}
	public void msgHereIsOrderFromMarket(DeliveryMan DMR,Map<String,Integer>choices, List<String>outOf,double amountOwed){
		for (MarketOrder order:marketOrders){
			if(order.deliveryMan==DMR){
				order.price=amountOwed;
				order.marketState=marketOrderState.paying;
			}
		}
		for(String key:choices.keySet()){
			Food food=findFood(key);
			food.amount=food.amount+choices.get(key);
		}
	}
	
	@Override
	public void msgNeverOrderFromMarketAgain(MarketAgent market) {
		MyMarket m = findMarket(market);
		markets.remove(m);
	}
	/*public void msgHereIsPrice(double amountOwed,DeliveryManRole DMR){
		for (MarketOrder order:marketOrders){
			if(order.deliveryMan==DMR){
				order.price=amountOwed;
				order.marketState=marketOrderState.paying;
			}
		}
	}*/
	
	/*public void msgDelivering(MarketAgent m, List<MarketAgent.MyFood> orderList) {
		MyMarket mm = findMarket(m);
		for(MarketAgent.MyFood f: orderList){
			print("\t Recieved from "+ m.getName()+ "\tChoice:"+ f.getChoice()+"\tAmount: " + f.getAmount());
			if(mm.foodInventoryMap.get(f.getChoice().toLowerCase()) != InventoryState.OUTOFCHOICE){
				mm.foodInventoryMap.put(f.getChoice().toLowerCase(), InventoryState.POSSIBLE);
			}
			Food food = findFood(f.getChoice());
			food.amount = food.amount + f.getAmount();
			food.os = OrderingState.none;
		}
		
		stateChanged();
	}

	public void msgOutOfOrder(MarketAgent m, List<MarketAgent.MyFood> orderList) {
		MyMarket mm = findMarket(m);
		
		boolean reorder = false;
		
		for(MarketAgent.MyFood f: orderList){
			mm.foodInventoryMap.put(f.getChoice().toLowerCase(), InventoryState.OUTOFCHOICE);
			Food food = findFood(f.getChoice());
			
			if( (food.amount + f.getAmount()) <= food.low){
				reorder = true;
				food.os = OrderingState.order;
			}
		}
		
		if(reorder){
			reorder = false;
			orderFromMarket = true;
		}
		
		stateChanged();
	}*/
	
	//Messages from animation

	public void msgAnimationFinishedAtFidge(){
		//From animation
		waitingResponse.release();
	}
	public void msgAnimationFinishedPutFoodOnGrill() {
		//From animation
		waitingResponse.release();
	}
	public void msgAnimationFinishedWaiterPickedUpFood(){
		//From animation
		pickUpTableItems--;
		synchronized(orders){
			for(RoleOrder order:orders){
				if(order.state == OrderState.BURNING){
					order.state = OrderState.DONE;
				}
			}
		}
		stateChanged();
	}
	public void msgAnimationFinishedPutFoodOnPickUpTable(RoleOrder o) {
		//From animation

		if(o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken")){
			grillItems--;
			synchronized(orders){
				for(RoleOrder order:orders){
					if(order.state == OrderState.QUEUED && (o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken") )){
						order.state = OrderState.PENDING;
					}
				}
			}
		}else if(o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie")){
			counterItems--;
			
			synchronized(orders){
				for(RoleOrder order:orders){
					if(order.state == OrderState.QUEUED && (o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie"))){
						order.state = OrderState.PENDING;
					}
				}
			}
		}
		waitingResponse.release();
	}	
	
	public boolean pickAndExecuteAnAction() {
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
			state = State.relieveFromDuty;
			return true;
		}

		if(orderFromMarket){
			print("ordering Food");
			orderFromMarket = false;
			orderFoodFromMarket();
			return true;
		}
		
		synchronized(marketOrders){
			for (MarketOrder mOrder:marketOrders){
				if(mOrder.marketState==marketOrderState.ordering){
					mOrder.marketState = marketOrderState.ordered;
					placingMarketOrder(mOrder);
					return true;
				}
			}
		}
		
		synchronized(marketOrders){
			for (MarketOrder mOrder:marketOrders){
				if(mOrder.marketState==marketOrderState.paying){
					hereIsMarketBill(mOrder);
					return true;
				}
			}
		}
		
		RoleOrder temp = null;
		synchronized(orders){
			for (RoleOrder o : orders)
			{
				if(o.state == OrderState.DONE && temp == null)
				{
					temp = o;
				}
			}
		}
		if(temp != null){
			plateIt(temp);
			return true;
		}
		
		synchronized(orders){
			for (RoleOrder o : orders)
			{
				if(o.state == OrderState.PENDING && temp == null)
				{
					temp = o;
				}
			}
		}
		if(temp != null){
			temp.state = OrderState.COOKING;
			cookIt(temp);
			return true;
		}
		
		goToRestPost();
		return false;
	}




//Actions
	
	private void plateIt(RoleOrder o) {
		DoPlating(o);
	
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(o.state == OrderState.DONE){
			print("order ready");
			o.waiter.msgOrderIsReady(o.choice, o.table);
			orders.remove(o);
		}
	}

	private void cookIt(RoleOrder o)
	{
		DoGoToFidge();
		try {
			waitingResponse.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Food food = findFood(o.choice.toLowerCase());
		if(food.amount > 0){
			print("cooking");
			food.amount--;
			if(food.amount <= food.low){
				orderFoodFromMarket();
			}

			if((grillItems < 6 && (o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken")))
				|| (counterItems < 3 && (o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie")) )){
				DoCooking(o);
				if(o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken")){
					grillItems++;
				}else{
					counterItems++;
				}
			
				try {
					waitingResponse.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				scheduleCook(o);
				
			}else{
				o.state = OrderState.QUEUED;
			}
		}
		else{
			print("msgOutOfOrder");
			o.waiter.msgOutOfOrder(o.choice, o.table);
			
			Food f = findFood(o.choice);
			
			orders.remove(o);
			
			if(f.os != OrderingState.ordered){
				orderFoodFromMarket();
			}
			
		}
	}
	private void DoCooking(RoleOrder o) {
		cookGui.DoCookFood(o);
		
	}


	private void DoPlating(RoleOrder o) {
		if(pickUpTableItems < 8){
			cookGui.DoPlateFood(o);	
			pickUpTableItems++;
		}else{
			o.state = OrderState.BURNING;
		}
	}


	private void DoGoToFidge() {
		cookGui.DoGoToFidge();
		
	}


	private void scheduleCook(final RoleOrder o){
		timer.schedule(new TimerTask() {
			public void run() {
				msgFoodDone(o);
			}
		}, 
		findFood(o.choice.toLowerCase()).cookingTime);
	}

	private void orderFoodFromMarket(){

		synchronized(markets){
			for(MyMarket m: markets){
				boolean placeOrder = false;
				Map<String,Integer>foodsToOrder=new HashMap<String,Integer>();
				//List<Food> foodsToOrder = new ArrayList<Food>();

				synchronized(foods){
					for(Food f: foods){
						if(f.amount <= f.low && m.foodInventoryMap.get(f.getChoice().toLowerCase()) == InventoryState.POSSIBLE  
								&& f.os != OrderingState.ordered){
							f.os = OrderingState.ordered;
							print("\t\tChoice:"+ f.getChoice()+"\tAmount: " + f.amount+ "\tCapacity: " + f.capacity + "\tInventorystate: " + m.getMarket().getName() + " "+  m.foodInventoryMap.get(f.getChoice()));
							m.foodInventoryMap.put(f.getChoice().toLowerCase(), InventoryState.ATTEMPTING);
							foodsToOrder.put(f.choice,20);
							placeOrder = true;
							print("\t\tChoice:"+ f.getChoice()+"\tAmount: " + f.amount+ "\tCapacity: " + f.capacity + "\tInventorystate: " + m.getMarket().getName() + " "+  m.foodInventoryMap.get(f.getChoice()));
						} 
					}
				}
	
				if(placeOrder){
					//m.market.msgHereIsOrder(this, cashier, foodsToOrder);
					marketOrders.add(new MarketOrder(foodsToOrder,m.market,marketOrderState.waiting));
					m.market.msgPlaceDeliveryOrder((Cook) this);
					
				}
			}
		}
		
	}
	
	private void placingMarketOrder(MarketOrder mOrder){
		mOrder.deliveryMan.msgHereIsOrder(mOrder.Choices);
	}
	
	private void hereIsMarketBill(MarketOrder morder){
		((CashierRole)restaurant.cashier).msgHereIsInvoice(morder.price,morder.deliveryMan);
		marketOrders.remove(morder);
	}
	
	private void goToRestPost() {
		cookGui.doGoToRestPost();
	}

	private MyMarket findMarket(MarketAgent m) {
		synchronized(markets){
			for(MyMarket mm: markets){
				if(mm.getMarket() == m){
					return mm;
				}
			}
		}
		return null;
	}

	public void addMarket(MarketAgent m){
		//markets.add(new MyMarket(m));
	}


	public void badSteaks() {
		synchronized(foods){
			for(Food f: foods){
				if(f.getChoice().equalsIgnoreCase("steak")){
					f.amount = 0;
					print("Who left the steaks out!?!?!");
				}
			}
		}
	}
	private Food findFood(String choice) {
		synchronized(foods){
			for(Food f:foods){
				if(f.getChoice().equalsIgnoreCase(choice)){
					return f;
				}
			}
		}
		return null;
	}


	public void setSteaksAmount(int i) {

		synchronized(foods){
		for(Food f: foods){
			if(f.getChoice().equalsIgnoreCase("steak")){
				f.amount = i;
				print("Only "+ i +" steaks left!?!?!");
			}
		}
		}
		
	}


	public void cookieMonster() {
		synchronized(foods){
			for(Food f: foods){
				if(f.getChoice().equalsIgnoreCase("cookie")){
					f.amount = 0;
					print("Who ate all the cookies!?!?!");
				}
			}
		}
		
	}

/*
	@Override
	public void msgFoodDone(Order o) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgDelivering(Market m, List<MyFood> orderlist) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgOutOfOrder(Market m, List<MyFood> orderList) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgAnimationFinishedPutFoodOnPickUpTable(RoleOrder o) {
		// TODO Auto-generated method stub
		
	}*/


	public void setRestaurant(Restaurant r) {
		restaurant = r;
		
	}











}
