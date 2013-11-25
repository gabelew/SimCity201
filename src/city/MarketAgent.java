package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;
import city.animationPanels.InsideAnimationPanel;
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
public List<chair>chairs=new ArrayList<chair>();
public class chair{
	public chair(int i, boolean b) {
		number=i;
		free=b;
	}
	public int number;
	public boolean free;
}
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

public enum customerState{waiting,assigned, clerkGettingFood,done};
public enum cookState{waiting,deliveryGettingFood,done};
public Map<String,Double> Prices=new HashMap<String,Double>();
public List<clerk>clerks=new ArrayList<clerk>();
public class clerk{
	public clerk(Clerk c, state free) {
		clerk=c;
		clerkState=free;
	}
	Clerk clerk;	
	public state clerkState;
}
public List<delivery>deliverys=new ArrayList<delivery>();
public class delivery{
	public delivery(DeliveryMan dM, state free) {
		deliveryMan=dM;
		deliveryState=free;
	}
	DeliveryMan deliveryMan;
	public state deliveryState;
}
public enum state{free,busy,wantOffWork,offWork};
public boolean clerkFree=true;
public boolean deliveryFree=true;
public Map<String, Integer> Inventory = new HashMap<String, Integer>();

public MarketAgent(Point Location,String Name, InsideAnimationPanel iap){
	this.location=Location;
	this.name=Name;
	this.insideAnimationPanel = iap;
	for (int i=0;i<10;i++){
		chairs.add(new chair(i,true));
	}
	Prices.put("steak",2.00);
	Prices.put("car", 200.00);
	Prices.put("cookie",.25);
	Prices.put("chicken", 1.50);
	Prices.put("salad",.75);
}

//messages to market
public void msgPlaceOrder(MarketCustomer CR){
	MyCustomers.add(new MyCustomer(CR, customerState.waiting));
	if(getStateChangePermits()==0){
			stateChanged();	
		}
	log.add(new LoggedEvent("Received msgPlaceOrder from MarketCustomer."));
}

public void msgPlaceDeliveryOrder(Cook cook){
	print("DELIVERY!!!");
	MyCooks.add(new MyCook(cook, cookState.waiting));
	if(getStateChangePermits()==0){
			stateChanged();	
		}
	log.add(new LoggedEvent("Received msgPlaceDeliveryOrder from CookCustomer."));
}

public void msgClerkDone(Clerk c){
	for(clerk cl:clerks){
		if(cl.clerk==c){
			if(cl.clerkState==state.wantOffWork)
				cl.clerkState=state.offWork;
			else{
				cl.clerkState=state.free;
			}
		}
	}
	if(getStateChangePermits()==0){
			stateChanged();	
		}
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
	if(getStateChangePermits()==0){
			stateChanged();	
		}
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
				return true;
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
	c.clerk.msgDoneWithShift();
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
	for (clerk cl:clerks){
		if(cl.clerkState==state.busy){
			cl.clerkState=state.wantOffWork;
		}
		if(cl.clerkState==state.free){
			cl.clerkState=state.offWork;
		}
	}
	clerks.add(new clerk(c,state.free));
	if(getStateChangePermits()==0){
			stateChanged();	
		}
}

public void addDeliveryMan(DeliveryMan DM){
	deliverys.add(new delivery(DM,state.free));
	if(getStateChangePermits()==0){
			stateChanged();	
		}
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