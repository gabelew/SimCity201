package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;
import city.roles.*;
import market.interfaces.Market;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;

public class MarketAgent extends Agent implements Market {
public EventLog log = new EventLog();
public Point location;
private String name;
public List<MyCustomer>MyCustomers=new ArrayList<MyCustomer>();
public List<MyCook>MyCooks= new ArrayList<MyCook>();
public class MyCustomer{
	public MyCustomer(MarketCustomer MCR, customerState s) {
		MC=MCR;
		state=s;
	}
	public MarketCustomer MC;
	public customerState state;
}
public class MyCook{
	public MyCook(Cook c, cookState s) {
		cook=c;
		cookstate=s;
	}
	public Cook cook;
	public cookState cookstate;
}

public enum customerState{waiting, clerkGettingFood,done};
public enum cookState{waiting,deliveryGettingFood,done};

Clerk clerk;
DeliveryMan deliveryMan;

public boolean clerkFree=true;
public boolean deliveryFree=true;
public Map<String, Integer> Inventory = new HashMap<String, Integer>();

public MarketAgent(Clerk Clerk,DeliveryMan DMR,Point Location,String Name){
	this.clerk=Clerk;
	this.deliveryMan=DMR;
	this.location=Location;
	this.name=Name;
}

//messages to market
public void msgPlaceOrder(MarketCustomer CR){
	MyCustomers.add(new MyCustomer(CR, customerState.waiting));
	stateChanged();
	log.add(new LoggedEvent("Received msgPlaceOrder from MarketCustomer."));
}

public void msgPlaceDeliveryOrder(Cook cook){
	MyCooks.add(new MyCook(cook, cookState.waiting));
	stateChanged();
	log.add(new LoggedEvent("Received msgPlaceDeliveryOrder from CookCustomer."));
}

public void msgClerkDone(){
	clerkFree=true;
	stateChanged();
	log.add(new LoggedEvent("Received msgClerkDone from clerk."));
}

public void msgDeliveryDone(){
	deliveryFree=true;
	stateChanged();
	log.add(new LoggedEvent("Received msgDeliveryDone from deliveryMan."));
}

//scheduler
public boolean pickAndExecuteAnAction() {
	
	try{
		for (MyCustomer MC:MyCustomers){
			if (MC.state==customerState.waiting){
				giveToClerk(MC);
				return true;
			}
		}
		for (MyCook MC:MyCooks){
			if (MC.cookstate==cookState.waiting){
				giveToDelivery(MC);
				return true;
			}
		}
		
	}
	catch(ConcurrentModificationException e){
		return false;
	}
	return false;
}

//actions
private void giveToClerk(MyCustomer MC){
	clerkFree=false;
	clerk.msgTakeCustomer(MC.MC,this);
	MyCustomers.remove(MC);
}

private void giveToDelivery(MyCook MC){
	deliveryFree=false;
	deliveryMan.msgTakeCustomer(MC.cook,this);
	MyCooks.remove(MC);
}


public String getName(){
	return name;
}

public void setInventory(int cars, int chicken,int steak,int salad, int cookie){
	Inventory.put("steak", steak);
	Inventory.put("car", cars);
	Inventory.put("chicken",chicken);
	Inventory.put("salad", salad);
	Inventory.put("cookie", cookie);
}

}