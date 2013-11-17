package city.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import city.PersonAgent;

public class AtHomeRole extends Role
{	
/*********************
 ***** DATA
 ********************/
	//States for Orders
	enum FoodOrderState {none, order, ordered};
	enum AppState {working, broken, repairRequested, payRepairman};
	enum OrderState {pending, cooking, done, eating}
	Map<String, Food> findFood = new HashMap<String, Food>();
	//Lists
	List<Food> foodInFridge = new ArrayList<Food>();
	List<Order> orders = new ArrayList<Order>();
	List<Appliance> appliances = new ArrayList<Appliance>();
	List<String> choices = new ArrayList<String>();
	Timer timer = new Timer(); //Timer for Cooking Food
	PersonAgent myPerson;
	
	public AtHomeRole(PersonAgent p) 
	{
		super(p);
		//Adds initial food
		choices.add("ceaser_salad");
		choices.add("prime_rib");
		choices.add("macncheese");
		choices.add("pasta");
		//Adds it to list of choices and hashmap
		for(String s : choices)
		{
			Food f = new Food(s, 5);
			foodInFridge.add(f);
			findFood.put(s, f);
		}
	}
/*********************
 ***** MESSAGES
 ********************/
	public void ImHungry()
	{
		int choice = (new Random()).nextInt(choices.size());
		Order o = new Order( choices.get(choice) );
		orders.add(o);
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
	
	private void EatIt(Order o)
	{
		o.state = OrderState.eating;
		//DoPlating();
		//DoEatFood();
		orders.remove(o);
	}
	
	private void CookIt(final Order o)
	{
		o.state = OrderState.cooking;
		//DoGoToFridge();
		//Break the fridge randomly
		int fridgeBroken = (new Random()).nextInt(100)+1;
		if(fridgeBroken == 66)
		{
			appliances.add( new Appliance("fridge") );
		}
		Food food = findFood.get(o.choice);
		//Cooks Food if has it on hand
		//adds to marketOrder if low on food
		if( food.amount > 0)
		{
			food.amount--;
			if(food.amount <= food.low)
			{
				makeMarketList();
			}
			//Cooking Timer for Food
			timer.schedule(new TimerTask() {
				public void run() 
				{
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
				//myPerson.PickAnotherFoodMsg();
			}
			else
			{
				//myPerson.NoMoreFoodMsg();
			}
			if(food.state != FoodOrderState.ordered)
			{
				makeMarketList();
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
		//myPerson.getFoodFromMarket(groceryList);
	}
/*********************
 ***** SCHEDULER
 ********************/
	public boolean pickAndExecuteAnAction() 
	{
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

	//Order for food cooked at home
	class Order 
	{
	    String choice;
	    OrderState state = OrderState.pending;
	    public Order(String c)
	    {
	    	this.choice = c;
	    }
	}
	
	//Ingrediants for home
	class Food
	{
	    String choice;
	    int cookingTime;
	    int amount = 3;
	    int low = 2;
	    int capacity = 5;
	    FoodOrderState state = FoodOrderState.none;
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