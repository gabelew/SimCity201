package city;

import java.awt.Point;
import java.util.*;

import agent.Agent;
import city.animationPanels.InsideAnimationPanel;
import city.roles.DeliveryManRole.Order;
import market.gui.MarketPanel;
import market.interfaces.Clerk;
import market.interfaces.DeliveryMan;
import market.interfaces.Market;
import restaurant.Restaurant;
import restaurant.interfaces.Cook;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import market.interfaces.*;

public class MarketAgent extends Agent implements Market {
private MarketPanel panel;
private boolean update=false;
public boolean notTesting=true;
public EventLog log = new EventLog();
public InsideAnimationPanel insideAnimationPanel;
public Point location;
public List<chair>chairs=new ArrayList<chair>();
public List<Order>failedOrders=new ArrayList<Order>();
public boolean isOpen = true;
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
	if(notTesting)
	update=true;
	log.add(new LoggedEvent("Received msgClerkDone from clerk."));
}

public void msgDeliveryDone(DeliveryMan D){
	for(delivery de: deliverys){
		if(de.deliveryMan==D){
			if(de.deliveryState==state.wantOffWork)
				de.deliveryState=state.offWork;
			else{
				de.deliveryState=state.free;
				print("Back at the market");
			}
		}
	}
	if(getStateChangePermits()==0){
			stateChanged();	
	}
	if(notTesting)
		update=true;
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
		for(delivery d:deliverys){
			if(d.deliveryState==state.free){
				for (Order o:failedOrders){
					for(Restaurant r:insideAnimationPanel.simCityGui.getRestaurants()){
						if(r.cook==o.cook){
							print("ccc");
							if(r.isOpen){
								giveToDelivery(d,o);
								return true;
							}
						}
					}
				}
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
		if(update){
			update=false;
			updatePanel();
			return true;
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

private void giveToDelivery(delivery d,Order o){
	d.deliveryState=state.busy;
	print("redeliver");
	d.deliveryMan.msgTryAgain(o,this);
	failedOrders.remove(o);
}

private void clerkDone(clerk c){
	c.clerk.msgDoneWithShift();
	clerks.remove(c);
}

private void deliveryDone(delivery d){
	d.deliveryMan.msgDoneWithShift();
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
	for (delivery dl:deliverys){
		if(dl.deliveryState==state.busy){
			dl.deliveryState=state.wantOffWork;
		}
		if(dl.deliveryState==state.free){
			dl.deliveryState=state.offWork;
		}
	}
	deliverys.add(new delivery(DM,state.free));
	if(getStateChangePermits()==0){
			stateChanged();	
		}
}

private void updatePanel(){
	if(notTesting){
	panel.steakPanel.labels.setText("Steak: "+Inventory.get("steak"));
	panel.saladPanel.labels.setText("Salad: "+Inventory.get("salad"));
	panel.cookiePanel.labels.setText("Cookie: "+Inventory.get("cookie"));
	panel.chickenPanel.labels.setText("Chicken: "+Inventory.get("chicken"));
	panel.carPanel.labels.setText("Car: "+Inventory.get("car"));
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

public void setPanel(MarketPanel p){
	panel=p;
}


public boolean isOpen() {
	return isOpen;
}
public void closeRestaurant(){
	isOpen = false;
	//TODO:notify employee to leave once no more customers
	
}
public void openRestaurant(){
	isOpen = true;
}

}