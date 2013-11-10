package restaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.gui.CookGui;

public class CookAgent extends Agent {
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
	public List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	private Semaphore awaitingTask = new Semaphore(0,true);
	Timer timer = new Timer();
	boolean orderFromMarket = true;
	
	private String name;
	private CashierAgent cashier;
	public CookGui cookGui = null;

    static final int SALAD_COOKTIME = 7000;
    static final int STEAK_COOKTIME = 15000;
    static final int CHICKEN_COOKTIME = 20000;
    static final int BURGER_COOKTIME = 15000;
    static final int COOKIE_COOKTIME = 5000;
	static final int SALAD_INIT_AMOUNT = 1;
	static final int SALAD_LOW_AMOUNT = 3;
	static final int SALAD_CAPACITY = 50;
	static final int STEAK_INIT_AMOUNT = 4;
	static final int STEAK_LOW_AMOUNT = 3;
	static final int STEAK_CAPACITY = 50;
	static final int CHICKEN_INIT_AMOUNT = 1;
	static final int CHICKEN_LOW_AMOUNT = 3;
	static final int CHICKEN_CAPACITY = 50;
	static final int BURGER_INIT_AMOUNT = 1;
	static final int BURGER_LOW_AMOUNT = 3;
	static final int BURGER_CAPACITY = 50;
	static final int COOKIE_INIT_AMOUNT = 1;
	static final int COOKIE_LOW_AMOUNT = 3;
	static final int COOKIE_CAPACITY = 50;
	private int grillItems = 0;
	private int counterItems = 0;
	private int pickUpTableItems = 0;
	
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
    
	public class Order{
		public WaiterAgent waiter;
		public String choice;
		public int table;
		public OrderState state;
		
		Order(WaiterAgent w, String c, int t ,OrderState s){
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
			foodInventoryMap.put("burger", InventoryState.POSSIBLE);
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

	public CookAgent(String n){
		super();
		this.name = n;

		foods.add(new Food("salad",SALAD_COOKTIME, SALAD_INIT_AMOUNT, SALAD_LOW_AMOUNT, SALAD_CAPACITY));
		foods.add(new Food("steak",STEAK_COOKTIME, STEAK_LOW_AMOUNT, STEAK_LOW_AMOUNT, STEAK_CAPACITY));
		foods.add(new Food("chicken",CHICKEN_COOKTIME, CHICKEN_INIT_AMOUNT, CHICKEN_LOW_AMOUNT, CHICKEN_CAPACITY));
		foods.add(new Food("burger",BURGER_COOKTIME, BURGER_INIT_AMOUNT, BURGER_LOW_AMOUNT, BURGER_CAPACITY));
		foods.add(new Food("cookie",COOKIE_COOKTIME, COOKIE_INIT_AMOUNT, COOKIE_LOW_AMOUNT, COOKIE_CAPACITY));
	}


	public void setGui(CookGui g) {
		cookGui = g;
	}
	public void setCashier(CashierAgent g) {
		cashier = g;
	}

	public CookGui getGui() {
		return cookGui;
	}
	public void msgHereIsOrder(WaiterAgent w, String choice, int table)
	{
		orders.add(new Order(w, choice, table, OrderState.PENDING));
		stateChanged();
		print("order recieved!!");
	}
	public void msgFoodDone(Order o)
	{
		o.state = OrderState.DONE;
		stateChanged();
	}
	
	public void msgDelivering(MarketAgent m, List<MarketAgent.MyFood> orderList) {
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
	}
	
	//Messages from animation

	public void msgAnimationFinishedAtFidge(){
		//From animation
		awaitingTask.release();
	}
	public void msgAnimationFinishedPutFoodOnGrill() {
		//From animation
		awaitingTask.release();
	}
	public void msgAnimationFinishedWaiterPickedUpFood(){
		//From animation
		pickUpTableItems--;
		synchronized(orders){
			for(Order order:orders){
				if(order.state == OrderState.BURNING){
					order.state = OrderState.DONE;
				}
			}
		}
		stateChanged();
	}
	public void msgAnimationFinishedPutFoodOnPickUpTable(Order o) {
		//From animation

		if(o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken") || o.choice.equalsIgnoreCase("burger")){
			grillItems--;
			synchronized(orders){
				for(Order order:orders){
					if(order.state == OrderState.QUEUED && (o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken") || o.choice.equalsIgnoreCase("burger"))){
						order.state = OrderState.PENDING;
					}
				}
			}
		}else if(o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie")){
			counterItems--;
			
			synchronized(orders){
				for(Order order:orders){
					if(order.state == OrderState.QUEUED && (o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie"))){
						order.state = OrderState.PENDING;
					}
				}
			}
		}
		awaitingTask.release();
	}	
	
	protected boolean pickAndExecuteAnAction() {
		
		if(orderFromMarket){
			print("ordering Food");
			orderFromMarket = false;
			orderFoodFromMarket();
			return true;
		}
		
		Order temp = null;
		synchronized(orders){
			for (Order o : orders)
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
			for (Order o : orders)
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
	
	private void plateIt(Order o) {
		DoPlating(o);
	
		try {
			awaitingTask.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(o.state == OrderState.DONE){
			print("order ready");
			o.waiter.msgOrderIsReady(o.choice, o.table);
			orders.remove(o);
		}
	}

	private void cookIt(Order o)
	{
		DoGoToFidge();
		try {
			awaitingTask.acquire();
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

			if((grillItems < 6 && (o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken") || o.choice.equalsIgnoreCase("burger")))
				|| (counterItems < 3 && (o.choice.equalsIgnoreCase("salad") || o.choice.equalsIgnoreCase("cookie")) )){
				DoCooking(o);
				if(o.choice.equalsIgnoreCase("steak") || o.choice.equalsIgnoreCase("chicken") || o.choice.equalsIgnoreCase("burger")){
					grillItems++;
				}else{
					counterItems++;
				}
			
				try {
					awaitingTask.acquire();
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
	private void DoCooking(Order o) {
		cookGui.DoCookFood(o);
		
	}


	private void DoPlating(Order o) {
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


	private void scheduleCook(final Order o){
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
				List<Food> foodsToOrder = new ArrayList<Food>();

				synchronized(foods){
					for(Food f: foods){
						if(f.amount <= f.low && m.foodInventoryMap.get(f.getChoice().toLowerCase()) == InventoryState.POSSIBLE  
								&& f.os != OrderingState.ordered){
							f.os = OrderingState.ordered;
							print("\t\tChoice:"+ f.getChoice()+"\tAmount: " + f.amount+ "\tCapacity: " + f.capacity + "\tInventorystate: " + m.getMarket().getName() + " "+  m.foodInventoryMap.get(f.getChoice()));
							m.foodInventoryMap.put(f.getChoice().toLowerCase(), InventoryState.ATTEMPTING);
							foodsToOrder.add(new Food(f));
							placeOrder = true;
							print("\t\tChoice:"+ f.getChoice()+"\tAmount: " + f.amount+ "\tCapacity: " + f.capacity + "\tInventorystate: " + m.getMarket().getName() + " "+  m.foodInventoryMap.get(f.getChoice()));
						} 
					}
				}
	
				if(placeOrder){
					m.market.msgHereIsOrder(this, cashier, foodsToOrder);
				}
			}
		}
		
	}
	private void goToRestPost() {
		cookGui.doGoToRestPost();
	}
	public String getName() {
		return name;
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
		markets.add(new MyMarket(m));
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






}
