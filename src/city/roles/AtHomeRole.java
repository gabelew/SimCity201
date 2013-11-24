package city.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import atHome.city.AtHomeGui;
import city.PersonAgent;

public class AtHomeRole extends Role
{	
/*********************
 ***** DATA
 ********************/
	//States for Orders
	public enum FoodOrderState {none, ordered};
	enum AppState {working, broken, repairRequested, payRepairman};
	enum EventState {none, goingHome, goToFridge, goToCounter, goToGrill};
	enum OrderState {pending, cooking, done, eating}
	EventState state = EventState.none;
	private Semaphore busy = new Semaphore(0,true);
	static final int EATING_TIME = 5000;
	
	Map<String, Food> findFood = new HashMap<String, Food>();
	//Lists
	public List<Food> foodInFridge = new ArrayList<Food>();
	public List<Order> orders = new ArrayList<Order>();
	List<Appliance> appliances = new ArrayList<Appliance>();
	public List<String> choices = new ArrayList<String>();
	Timer timer = new Timer(); //Timer for Cooking Food
	public PersonAgent myPerson = null;//PersonAgent that has this role
	AtHomeGui gui;
	public AtHomeRole(PersonAgent p) 
	{
		super(p);
		this.myPerson = p;
		//Adds initial food
		choices.add("salad");
		choices.add("steak");
		choices.add("cookie");
		choices.add("chicken");
		//Adds it to list of choices and hashmap
		for(String s : choices)
		{
			Food f = new Food(s, 2000);
			foodInFridge.add(f);
			findFood.put(s, f);
		}
		this.gui = new AtHomeGui(myPerson, this);
	}
	
	public AtHomeGui getGui()
	{
		return gui;
	}
	public void setGui(AtHomeGui g)
	{
		this.gui = g;
	}
/*********************
 ***** MESSAGES
 ********************/
	public void ImHungry()
	{
		if(myPerson.name.equals("salad"))
		{
			orders.add(new Order("salad"));
		}
		else
		{
			int choice = (new Random()).nextInt(choices.size());
			Order o = new Order( choices.get(choice) );
			orders.add(o);
		}
		state = EventState.goingHome;
	}
	
	public void restockFridge(Map<String,Integer> orderList)
	{
		for(Food f : foodInFridge)
		{
			f.amount += orderList.get(f.choice).intValue();
			f.state = FoodOrderState.none;
		}
	}
	public void pickSomethingElse()
	{
		int choice = (new Random()).nextInt(choices.size());
		Order o = new Order( choices.get(choice) );
		orders.add(o);
	}
	public void BrokenApplianceMsg(String app)
	{
		for(Appliance a : appliances)
		{
			if(a.appliance.equals(app))
			{
				a.state = AppState.broken;
			}
		}
	}
	public void ApplianceFixed(String app, double price)
	{
		for(Appliance a : appliances)
		{
			if(a.appliance.equals(app))
			{
				a.state = AppState.payRepairman;
				a.priceToFix = price;
			}
		}
	}
	public void msgGoLeaveHome()
	{
		gui.DoLeaveHome();
		try { busy.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		myPerson.msgDoneEatingAtHome();
	}
/*********************
 ***** ACTIONS
 ********************/
	private void PayForRepairs(Appliance a)
	{
		a.state = AppState.working;
		if(myPerson.cashOnHand >= a.priceToFix)
		{
			myPerson.cashOnHand -= a.priceToFix;
		}
		else
		{
			//repairMan.butYouOweMeOne(myPerson);
		}
		
	}
	
	private void requestFix(Appliance a)
	{
		a.state = AppState.repairRequested;
		//repairMan.fixAppliance(myPerson, a.appliance);
	}
	
	private void EatIt(final Order o)
	{
		o.state = OrderState.eating;
		timer.schedule(new TimerTask() {
			public void run() 
			{
				gui.DoneEating();
				orders.remove(o);
			}
		},
		EATING_TIME);
		
	}
	
	private void CookIt(final Order o)
	{
		o.state = OrderState.cooking;
		//Gets food from fridge
		gui.DoGoToFridge();
		try { busy.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
		//Break the fridge randomly -> V2 Implementation
		/*
		int fridgeBroken = (new Random()).nextInt(100)+1;
		if(fridgeBroken == 66)
		{
			appliances.add( new Appliance("fridge") );
		}
		*/
		Food food = findFood.get(o.choice);
		//Cooks Food if has it on hand
		//adds to marketOrder if low on food
		if( food.amount > 0)
		{
			myPerson.print("Cooking Food");
			food.amount--;
			//puts food on grill
			gui.DoCookFood(o.choice);
			try { busy.acquire();} 
			catch (InterruptedException e) {e.printStackTrace();}
			
			if(food.amount <= food.low)
			{
				makeMarketList();
			}
			//Cooking Timer for Food
			timer.schedule(new TimerTask() {
				public void run() 
				{
					gui.PlateAndEatFood();
					try { busy.acquire();} 
					catch (InterruptedException e) {e.printStackTrace();}
					myPerson.print("Food is Done!!!");
					o.state = OrderState.done;
				}
			},
			food.cookingTime);
			
		}
		else //Out of Food, Reorder food, and make a market order
		{
			if(choices.size() > 0)
			{
				choices.remove(o.choice);
				ImHungry();
			}
			else
			{
				myPerson.msgNoMoreFood();
			}
			if(food.state != FoodOrderState.ordered)
			{
				makeMarketList();
			}
			
			if(choices.size() == 0)
			{
				myPerson.msgNoMoreFood();
			}
			orders.remove(o);
		}
		
	}
	private void makeMarketList()
	{
		Map<String, Integer> groceryList = new HashMap<String, Integer>();
		for(Food f : foodInFridge )
		{
			if(f.amount <= f.low)
			{
				f.state = FoodOrderState.ordered;
				Integer numToOrder = new Integer(f.capacity - f.amount);
				groceryList.put(f.choice, numToOrder);
			}
		}
		myPerson.msgGetFoodFromMarket(groceryList);
	}
/*********************
 ***** SCHEDULER
 ********************/
	public boolean pickAndExecuteAnAction() 
	{
		if(state == EventState.goingHome)
		{
			goToHomePos();
		}
		for(Appliance a : appliances)
		{
			if(a.state == AppState.broken)
			{
				requestFix(a);
				return true;
			}
		}
		for(Appliance a : appliances)
		{
			if(a.state == AppState.payRepairman)
			{
				PayForRepairs(a);
				return true;
			}
		}
		for(Order o: orders)
		{
			if(o.state == OrderState.done)
			{
				EatIt(o);
				return true;
			}
		}
		for(Order o: orders)
		{
			if(o.state == OrderState.pending)
			{
				CookIt(o);
				return true;
			}
		}
		//GoToRestPos();
		return false;
	}

/*******************
 * Animation Methods START
 *******************/
	public void msgAnimationFinshed()
	{
		busy.release();
	}
	public void goToHomePos()
	{
		state = state.none;
		gui.doEnterHome();
		try { busy.acquire();} 
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
/*******************
 * Animation Methods END
 *******************/
	//Order for food cooked at home
	public class Order 
	{
	    String choice;
	    OrderState state = OrderState.pending;
	    public Order(String c)
	    {
	    	this.choice = c;
	    }
	}
	
	//Ingrediants for home
	public class Food
	{
	    public String choice;
	    public int cookingTime;
	    public int amount = 3;
	    public int low = 2;
	    public int capacity = 5;
	    public FoodOrderState state = FoodOrderState.none;
	    public Food(String c, int ct)
	    {
	    	this.choice = c;
	    	this.cookingTime = ct;
	    }
	}
	
	//utilities at home
	class Appliance
	{
	    String appliance;
	    AppState state = AppState.working;
	    double priceToFix;
	    public Appliance(String a)
	    {
	    	this.appliance = a;
	    }
	}
	
}