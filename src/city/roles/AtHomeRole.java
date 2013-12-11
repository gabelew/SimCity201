package city.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import restaurant.test.mock.LoggedEvent;
import atHome.city.Apartment;
import atHome.city.AtHomeGui;
import city.animationPanels.*;
import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.interfaces.AtHome;

public class AtHomeRole extends Role implements AtHome
{	
/*********************
 ***** DATA
 ********************/
	//States for Orders
	public enum FoodOrderState {none, ordered};
	enum AppState {working, broken, repairRequested, payRepairman};
	public enum EventState {none, leavingHome, goingHome, makingFood, goToFridge, OutOfFood};
	enum OrderState {pending, cooking, done, eating}
	public EventState state = EventState.none;
	private Semaphore busy = new Semaphore(0,true);
	static final int EATING_TIME = 3000;
	static final int COOKTIME = 1000;
	public boolean testing = false;
	boolean NoMoreFood = false;
	
	Map<String, Food> findFood = new HashMap<String, Food>();
	//Lists
	public List<Food> foodInFridge = new ArrayList<Food>();
	public List<Order> orders = new ArrayList<Order>();
	List<Appliance> appliances = new ArrayList<Appliance>();
	public List<String> choices = new ArrayList<String>();
	Timer timer = new Timer(); //Timer for Cooking Food
	public PersonAgent myPerson = null;//PersonAgent that has this role
	public AtHomeGui gui;
	public AtHomeRole(PersonAgent p, int foodAmount) 
	{
		super(p);
		this.myPerson = p;
		
		//Adds initial food
		foodInFridge.add(new Food("salad", 2*COOKTIME));
		foodInFridge.add(new Food("steak", 4*COOKTIME));
		foodInFridge.add(new Food("cookie", COOKTIME));
		foodInFridge.add(new Food("chicken", 3*COOKTIME));
		//Adds to Hashmap for searching
		for(Food f : foodInFridge)
		{
			findFood.put(f.choice, f);
		}
		
		//hack for no food
		if(foodAmount == 0)
		{
			for(Food f: foodInFridge)
			{
				f.amount = 0;
			}
			choices.clear();
		}
		else
		{
			for(Food f : foodInFridge)
			{
				if(f.amount > 0)
					choices.add(f.choice);
			}
		}
		if(foodAmount == 1)
		{
			findFood.get("steak").amount = 1;
		}

		this.gui = new AtHomeGui(myPerson, this);
	}
	
	public AtHomeGui getGui()
	{
		return gui;
	}
	public void setGui(Gui g)
	{
		this.gui = (AtHomeGui) g;
	}
/*********************
 ***** MESSAGES
 ********************/
	public void ImHungry()
	{
		if(myPerson.getName().equals("salad"))
		{
			Order o = new Order("salad");
			orders.add(o);
		}
		else if(choices.size() != 0)
		{
			int choice = (new Random()).nextInt(choices.size());
			Order o = new Order(choices.get(choice) );
			orders.add(o);
			AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "I'm hungry and want to make: " + o.choice);
			//print("I'm hungry and want to make: " + o.choice);
		}
		else if(choices.size() == 0)
		{
			NoMoreFood = true;
		}
		state = EventState.goingHome;
	}
	
	public void restockFridge(Map<String,Integer> orderList)
	{
		AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "I have food now!!");
			//print("I have food now!!");
		for(Food f : foodInFridge)
		{
			if(orderList.get(f.choice) != null)
			{
				f.amount += orderList.get(f.choice).intValue();
				choices.add(f.choice);
				f.state = FoodOrderState.none;
			}
		}
		AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "I have a selection of: " + choices.size() +" foods");
			//print("I have a selection of: " + choices.size() +" foods");
	}
	
	public void BrokenApplianceMsg(String app)
	{
		print("I have a broken:" + app);
		Appliance a = new Appliance(app);
		appliances.add(a);
		stateChanged();
	}
	
	public void ApplianceFixed(String app, double price)
	{
		print("I am: " + myPerson.getName() + " and I Received repair bill for: " + app);
		for(Appliance a : appliances)
		{
			if(a.appliance.equals(app))
			{
				a.state = AppState.payRepairman;
				a.priceToFix = price;
			}
		}
		
		if(myPerson.myHome instanceof Apartment)
		{
			((ApartmentAnimationPanel)myPerson.myHome.insideAnimationPanel).removePersonFromList(myPerson.getName(), this);
		}
		else
		{
			((HouseAnimationPanel)myPerson.myHome.insideAnimationPanel).removePersonFromList(myPerson.getName(), this);
		}
		
		stateChanged();
	}
	public void msgGoLeaveHome()
	{
		this.state = EventState.leavingHome;
	}
/*********************
 ***** ACTIONS
 ********************/
	private void LeavingHomeAction()
	{
		this.state = EventState.none;
		gui.DoLeaveHome();
		if(!myPerson.testing){
			try { busy.acquire();} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		myPerson.msgHasLeftHome();
	}
	
	private void PayForRepairs(Appliance a)
	{
		a.state = AppState.working;
		if(myPerson.cashOnHand >= a.priceToFix)
		{
			print("paid repairman, $" + a.priceToFix);
			myPerson.bankTeller.msgTransferFunds(myPerson, myPerson.repairman.myPerson,
					a.priceToFix, "personal", "personal", "repairs");
			myPerson.repairman.HereIsPayment(this);
		}
		else
		{
			myPerson.repairman.butYouOweMeOne(this);
		}
		appliances.remove(a);
		//resets break button
		
	}
	
	private void requestFix(Appliance a)
	{
		a.state = AppState.repairRequested;
		myPerson.repairman.fixAppliance(this, a.appliance);
	}
	
	private void EatIt(final Order o)
	{
		o.state = OrderState.eating;
		if(!testing)
		{
			timer.schedule(new TimerTask() {
				public void run() 
				{
					gui.DoneEating();
					orders.remove(o);
				    if(!getName().toLowerCase().contains("visiter")){
						myPerson.hungerLevel = 0;
				    }
					myPerson.msgDoneEatingAtHome();
				}
			},
			EATING_TIME);
		}
		else
		{
			orders.remove(o);
		}
		
	}
	
	private void CookIt(final Order o)
	{
		o.state = OrderState.cooking;
		//Gets food from fridge
		if(!testing)
		{
			gui.DoGoToFridge();
			try { busy.acquire();} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		
		Food food = findFood.get(o.choice);
		//Cooks Food if has it on hand
		//adds to marketOrder if low on food
		if( food.amount > 0)
		{
			AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "Cooking Food");
			//print("Cooking Food");
			food.amount--;
			//puts food on grill
			if(!testing)
			{
				gui.DoCookFood(o.choice);
				try { busy.acquire();} 
				catch (InterruptedException e) {e.printStackTrace();}
			}
			for(Food f : foodInFridge)
			{
				AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "Food: " + f.choice + ", amount: " + f.amount);
				//print("Food: " + f.choice + ", amount: " + f.amount);
				if(f.amount <= food.low)
				{
					makeMarketList();
				}
			}
			//Cooking Timer for Food
			timer.schedule(new TimerTask() {
				public void run() 
				{
					if(!testing)
					{
						//gets food from grill
						gui.PlateFood();
						try { busy.acquire();} 
						catch (InterruptedException e) {e.printStackTrace();}
						//sits and eats food
						gui.SitDownAndEatFood();
						try { busy.acquire();} 
						catch (InterruptedException e) {e.printStackTrace();}
					}
					AlertLog.getInstance().logMessage(AlertTag.AT_HOME, myPerson.getName(), "Food is Done!!!");
					//print("Food is Done!!!");
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
				if(choices.size() == 0)
				{
					//myPerson.msgNoMoreFood();
					myPerson.msgDoneEatingAtHome();
				}
				else
				{
					ImHungry();
				}
			}
			else
			{
				myPerson.msgNoMoreFood();
				myPerson.msgDoneEatingAtHome();
			}
			if(food.state != FoodOrderState.ordered)
			{
				makeMarketList();
			}
			orders.clear();
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
	private void ImOutOfFood()
	{
		AlertLog.getInstance().logMessage(AlertTag.AT_HOME, this.getName(), "I'm out of food");
			//print("I'm out of food");
		NoMoreFood = false;
		this.state = EventState.OutOfFood;
		makeMarketList();
		myPerson.msgDoneEatingAtHome();
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
		
		if(state == EventState.leavingHome)
		{
			LeavingHomeAction();
			return true;
		}
		if(state == EventState.goingHome && !testing)
		{
			goToHomePos();
			return true;
		}
		if(NoMoreFood)
		{
			ImOutOfFood();
			return true;
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
		if(!myPerson.testing){
			try { busy.acquire();} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		myPerson.log.add(new LoggedEvent("Recieved goToHomePos from myPerson."));
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
	    public int amount = 4;
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
	    AppState state = AppState.broken;
	    double priceToFix;
	    public Appliance(String a)
	    {
	    	this.appliance = a;
	    }
	}
	
}