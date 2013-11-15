package city;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.sql.Time;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import restaurant.CookAgent;
import agent.Agent;
import city.roles.*;
import restaurant.interfaces.Market;

public class MarketAgent extends Agent {
private String name;
List<MyCustomer>MyCustomers=new ArrayList<MyCustomer>();
List<MyCook>MyCooks= new ArrayList<MyCook>();
class MyCustomer{
	public MyCustomer(MarketCustomerRole MCR, customerState s) {
		MC=MCR;
		state=s;
	}
	MarketCustomerRole MC;
	customerState state;
}
class MyCook{
	public MyCook(CookRole c, cookState s) {
		cook=c;
		cookstate=s;
	}
	CookRole cook;
	cookState cookstate;
}

enum customerState{waiting, clerkGettingFood,done};
enum cookState{waiting,deliveryGettingFood,done};

ClerkRole clerk;
DeliveryManRole deliveryMan;

boolean clerkFree;
boolean deliveryFree;
public Map<String, Integer> Inventory = new HashMap<String, Integer>();

//messages to market
public void msgPlaceOrder(MarketCustomerRole CR){
	MyCustomers.add(new MyCustomer(CR, customerState.waiting));
	stateChanged();
}

public void msgPlaceDeliveryOrder(CookRole cook){
	MyCooks.add(new MyCook(cook, cookState.waiting));
	stateChanged();
}

public void msgClerkDone(){
	clerkFree=true;
	stateChanged();
}

public void msgDeliveryDone(){
	deliveryFree=true;
	stateChanged();
}

//scheduler
protected boolean pickAndExecuteAnAction() {
	
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
	deliveryMan.msgTakeCustomer(MC.cook,this);
	MyCooks.remove(MC);
}


public String getName(){
	return name;
}
}