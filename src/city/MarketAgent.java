package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;
import city.animationPanels.InsideAnimationPanel;
import city.roles.*;
import market.interfaces.Clerk;
import market.interfaces.DeliveryMan;
import market.interfaces.Market;
import restaurant.interfaces.Cook;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;

public class MarketAgent extends Agent implements Market {
public EventLog log = new EventLog();
public InsideAnimationPanel insideAnimationPanel;
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

List<clerk>clerks=new ArrayList<clerk>();
class clerk{
	public clerk(Clerk c, state free) {
		clerk=c;
		clerkState=free;
	}
	Clerk clerk;	
	state clerkState;
}
List<delivery>deliverys=new ArrayList<delivery>();
class delivery{
	public delivery(DeliveryMan dM, state free) {
		deliveryMan=dM;
		deliveryState=free;
	}
	DeliveryMan deliveryMan;
	state deliveryState;
}
enum state{free,busy,wantOffWork,offWork};
public boolean clerkFree=true;
public boolean deliveryFree=true;
public Map<String, Integer> Inventory = new HashMap<String, Integer>();

public MarketAgent(Point Location,String Name, InsideAnimationPanel iap){
	this.location=Location;
	this.name=Name;
	this.insideAnimationPanel = iap;
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

public void msgClerkDone(Clerk c){
	for(clerk cl:clerks){
		if(cl.clerk==c){
			if(cl.clerkState==state.wantOffWork)
				cl.clerkState=state.offWork;
			else
				cl.clerkState=state.free;
		}
	}
	stateChanged();
	log.add(new LoggedEvent("Received msgClerkDone from clerk."));
}

public void msgDeliveryDone(DeliveryMan D){
	for(delivery de: deliverys){
		if(de.deliveryMan==D){
			if(de.deliveryState==state.wantOffWork)
				de.deliveryState=state.offWork;
			else
				de.deliveryState=state.free;
		}
	}
	stateChanged();
	log.add(new LoggedEvent("Received msgDeliveryDone from deliveryMan."));
}

//scheduler
public boolean pickAndExecuteAnAction() {
	
	try{
		for(clerk c:clerks){
			if(c.clerkState==state.free){
				for (MyCustomer MC:MyCustomers){
					if (MC.state==customerState.waiting){
						giveToClerk(c,MC);
						return true;
					}
				}
			}
			else if(c.clerkState==state.offWork){
				clerkDone(c);
			}
		}
		for(delivery d: deliverys){
			if(d.deliveryState==state.free){
				for (MyCook MC:MyCooks){
					if (MC.cookstate==cookState.waiting){
						giveToDelivery(d,MC);
						return true;
					}
				}
			}
			else if(d.deliveryState==state.offWork){
				deliveryDone(d);
			}
		}
		
	}
	catch(ConcurrentModificationException e){
		return false;
	}
	return false;
}

//actions
private void giveToClerk(clerk c,MyCustomer MC){
	c.clerkState=state.busy;
	c.clerk.msgTakeCustomer(MC.MC,this);
	MyCustomers.remove(MC);
}

private void giveToDelivery(delivery d,MyCook MC){
	d.deliveryState=state.busy;
	d.deliveryMan.msgTakeCustomer(MC.cook,this);
	MyCooks.remove(MC);
}

private void clerkDone(clerk c){
	clerks.remove(c);
}

private void deliveryDone(delivery d){
	deliverys.remove(d);
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

public void addClerk(Clerk c){
	clerks.add(new clerk(c,state.free));
	stateChanged();
}

public void addDeliveryMan(DeliveryMan DM){
	deliverys.add(new delivery(DM,state.free));
	stateChanged();
}

public void offWork(Clerk c){
	for (clerk cl:clerks){
		if(cl.clerk==c){
			cl.clerkState=state.wantOffWork;
		}
	}
}

public void offWork(DeliveryMan DM){
	for (delivery de:deliverys){
		if(de.deliveryMan==DM){
			de.deliveryState=state.wantOffWork;
		}
	}
}

}