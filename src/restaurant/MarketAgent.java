package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.CookAgent.Food;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Market;
   
public class MarketAgent extends Agent implements Market {
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<MyFood> foods = Collections.synchronizedList(new ArrayList<MyFood>());
	Timer timer = new Timer();
	Map<String, Double> pricingMap = new HashMap<String, Double>(); 
	
	private String name;
	public double bank = 10000;
	
    public class MyFood{
    	private String choice;
    	private int amount;
    	
    	public MyFood(String c, int a)
    	{
    		setChoice(c);
    		setAmount(a);
    	}
    	MyFood(MyFood f)
    	{
    		setChoice(f.getChoice());
    		setAmount(f.getAmount());
    	}
		public MyFood(Food f) {
    		setChoice(f.getChoice());
    		setAmount(f.capacity);
		}
		public String getChoice() {
			return choice;
		}
		public void setChoice(String choice) {
			this.choice = choice;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
    }
    
	public class Order{
		private CookAgent cook;
		private Cashier cashier;
		private List<MyFood> orderList = new ArrayList<MyFood>();
		private OrderState state;
		private double bill;
		public Order(CookAgent c,Cashier cashier, List<CookAgent.Food> list, OrderState s) {
			cook = c;
			this.cashier = cashier;
			for(CookAgent.Food f: list){
				orderList.add(new MyFood(f.getChoice(), f.capacity));
			}
			bill = 0;
			state = s;	
		}	
	}
	
	public enum OrderState {PENDING, DELIVERING, ATRESTAURANT, AWAITINGPAYMENT, DONE};
	private final int deliveryTime = CustomerAgent.randInt(MIN_DELIVERY_TIME, MAX_DELIVERY_TIME);
	private static final int MAX_DELIVERY_TIME = 15000;
	private static final int MIN_DELIVERY_TIME = 5000;
	private static final int defaultQuantity = 200;
	static final double SALAD_COST = 0.75;
	static final double STEAK_COST = 2.00;
	static final double CHICKEN_COST = 1.50;
	static final double BURGER_COST = 1.00;
	static final double COOKIE_COST = 0.25;	
	
	public MarketAgent(String n){
		super();
		this.name = n;

		foods.add(new MyFood("salad", defaultQuantity));
		foods.add(new MyFood("steak", defaultQuantity));
		foods.add(new MyFood("chicken",defaultQuantity));
		foods.add(new MyFood("burger", defaultQuantity));
		foods.add(new MyFood("cookie", defaultQuantity));

		pricingMap.put("salad", SALAD_COST);
		pricingMap.put("steak", STEAK_COST);
		pricingMap.put("chicken", CHICKEN_COST);
		pricingMap.put("burger", BURGER_COST);
		pricingMap.put("cookie", COOKIE_COST);
	}
	
	public MarketAgent(String n, int saQuant, int stQuant, int chQuant, int buQuant, int coQuant){
		super();
		this.name = n;

		foods.add(new MyFood("salad", saQuant));
		foods.add(new MyFood("steak", stQuant));
		foods.add(new MyFood("chicken",chQuant));
		foods.add(new MyFood("burger", buQuant));
		foods.add(new MyFood("cookie", coQuant));
	
		pricingMap.put("salad", SALAD_COST);
		pricingMap.put("steak", STEAK_COST);
		pricingMap.put("chicken", CHICKEN_COST);
		pricingMap.put("burger", BURGER_COST);
		pricingMap.put("cookie", COOKIE_COST);
	}

	
	public void msgHereIsOrder(CookAgent c, Cashier cashier, List<CookAgent.Food> orderList)
	{
		orders.add(new Order(c, cashier, orderList, OrderState.PENDING));
		stateChanged();
		print("order recieved!!");
	}
	
	public void msgFoodDelivered(Order o)
	{
		o.state = OrderState.ATRESTAURANT;
		stateChanged();
	}
	
	public void msgPayment(Cashier c, double payment){
		Order o = findOrder(c);
		o.bill = o.bill - payment;
		bank += payment;
		orders.remove(o);
	}
	
	private Order findOrder(Cashier c) {
		synchronized(orders){
			for(Order o: orders){
				if(o.cashier == c){
					return o;
				}
			}
		}
		print("null pointer Ahhhh");
		return null;
	}

	protected boolean pickAndExecuteAnAction() {
		
		Order temp = null;
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
			temp.state = OrderState.DELIVERING;
			fillOrder(temp);
			return true;
		}
		
		synchronized(orders){
			for (Order o : orders)
			{
				if(o.state == OrderState.ATRESTAURANT && temp == null)
				{
					temp = o;
				}
			}
		}

		if(temp != null){
			deliverFood(temp);
			return true;
		}
		
		return false;
	}




	private void fillOrder(Order o) {
		List<MyFood> incompleteFoods = new ArrayList<MyFood>();
		boolean canDeliverSomething = false;
		
		for(MyFood f: o.orderList){
			print("\t\tChoice:"+ f.getChoice()+"\tAmount: " + f.getAmount());
			
			MyFood food = findFood(f.getChoice());
			
			if(food.getAmount() >= f.getAmount()){
				//print("Delivering " + f.choice);
				food.setAmount(food.getAmount() - f.getAmount());
				o.bill += (f.amount*pricingMap.get(f.choice));
				canDeliverSomething = true;
			}
			else{
				f.setAmount(food.getAmount());
				food.setAmount(0);
				o.bill += (f.amount*pricingMap.get(f.choice));
				incompleteFoods.add(f);
				print("\t\t\tCant deliver"+ "\tAmount on its way: " + f.getAmount());	
				if(f.getAmount() > 0){
					canDeliverSomething = true;
				}
			}
			
		}
		
		if(canDeliverSomething){
			this.scheduleOrder(o);
		}
		if(!incompleteFoods.isEmpty()){
			o.cook.msgOutOfOrder(this, incompleteFoods);
		}

	}

	private void scheduleOrder(final Order o) {
		timer.schedule(new TimerTask() {
			public void run() {
				msgFoodDelivered(o);
			}
		}, 
		deliveryTime);
		
	}

	private void deliverFood(Order o){
		//DoDeliverFood(o);
		print("Your order has been delivered");
		o.cook.msgDelivering(this, o.orderList);
		o.cashier.msgHereIsBill(this, o.bill);
		o.state = OrderState.AWAITINGPAYMENT;
	}

	private MyFood findFood(String choice) {

		synchronized(foods){
			for(MyFood f:foods){
				if(f.getChoice().equalsIgnoreCase(choice)){
					return f;
				}
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}
	public void msgTossEverythingButCookies() {
		synchronized(foods){
			for(MyFood f:foods){
				if(!f.getChoice().equalsIgnoreCase("cookies")){
					f.setAmount(0);
				}
			}
		}
		
	}
	
}
