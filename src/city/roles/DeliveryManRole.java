package city.roles;

//import restaurant.WaiterAgent.Menu;
//import restaurant.WaiterAgent.MenuItem;


import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import market.gui.DeliveryManGui;
import restaurant.interfaces.Cook;
import market.interfaces.Clerk;
import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.ClerkRole.AgentEvent;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

/**
 * Restaurant customer agent.
 */
public class DeliveryManRole extends Role implements DeliveryMan{
	private Semaphore atShelf=new Semaphore(0,true);
	public EventLog log = new EventLog();
	public Restaurant restaurant;
	private DeliveryManGui deliveryGui=new DeliveryManGui(this);
	private String name;
	public Order o;
	Point location;
	public class Order{
		public Order( orderState state) {
			s=state;
		}
		public Map<String, Integer> Choices = new HashMap<String, Integer>();
		public List<String> outOf;
		public orderState s;
		public double amountOwed;
	}
	double Price=5;
	Cashier cashier;
	public Cook cook;
	public MarketAgent Market;
	public enum orderState{noOrder,askedForOrder,waitingForOrder,waiting,ordered,givingBill,waitingForPayment,payed,done};
	public enum AgentEvent{none,GoToWork};
	AgentEvent event = AgentEvent.none;
	public DeliveryManRole(PersonAgent p){
		super(p);
		o=new Order(orderState.noOrder);
	}
	public DeliveryManRole(){
		super();
		o=new Order(orderState.noOrder);
	}
	//messages

	public void goesToWork() {
		event = AgentEvent.GoToWork;
		stateChanged();	
	}
	public void msgTakeCustomer(Cook c,MarketAgent m){
		cook=c;
		Market=m;
		o.s=orderState.askedForOrder;
		stateChanged();
		log.add(new LoggedEvent("Received msgTakeCustomer from Market."));
	}
	
	public void msgHereIsOrder(Map<String,Integer>choice){
		o.Choices=choice;
		o.s=orderState.waiting;
		stateChanged();
	}
	
	public void msgHereIsPayment(double payment, Cashier ca){
		cashier=ca;
		o.s=orderState.payed;
		stateChanged();
	}
	
	//scheduler
	public boolean pickAndExecuteAnAction() {
		print("DELIVERY??????????????????????????????????????????????");
		if(event == AgentEvent.GoToWork){
			event = AgentEvent.none;
			((MarketAgent)Market).addDeliveryMan(((DeliveryMan)this));
			return true;
		}
		if(cook!=null&&o.s==orderState.askedForOrder){
			askForOrder();
			return true;
		}
		if(o.s==orderState.waiting){
			fillOrder();
			return true;
		}
		if(o.s==orderState.ordered){
			giveOrder();
			return true;
		}
		if(o.s==orderState.waitingForPayment){
			giveBill();
			return true;
		}
		if (o.s==orderState.payed){
			orderDone();
			return true;
		}
		print("DELIVERYMANNNNNN");
		return false;
	}

	//actions
	private void askForOrder(){
		cook.msgCanIHelpYou((DeliveryMan) this,Market);
		o.s=orderState.waitingForOrder;
	}
	
	private void fillOrder(){
		Iterator it = o.Choices.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if(Market.Inventory.get(pairs.getKey())==0){
	        	o.outOf.add(pairs.getKey().toString());
	        	o.Choices.remove(pairs);
	        }
	        else if(o.Choices.get(pairs.getKey())>Market.Inventory.get(pairs.getKey())){
	        	o.Choices.put(pairs.getKey().toString(), Market.Inventory.get(pairs.getKey()));
	        	o.amountOwed=o.amountOwed+Market.Inventory.get(pairs.getKey())*Price;
	        	Market.Inventory.put(pairs.getKey().toString(), 0);
	        }
	        else{
	        	o.amountOwed=o.amountOwed+o.Choices.get(pairs.getKey().toString())*Price;
	        	Integer temp=o.Choices.get(pairs.getKey());
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(),(((MarketAgent)Market).Inventory.get(pairs.getKey())-temp));
	        }
	        //it.remove(); // avoids a ConcurrentModificationException
	    }

	    deliveryGui.DoGoGetFood(o.Choices);
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
	    if(o.outOf!=null)
	    	cook.msgIncompleteOrder((DeliveryMan)this,o.outOf);
	    //cook.msgHereIsPrice(o.amountOwed,this);
	    o.s=orderState.ordered;
	}
	
	private void giveOrder(){
		deliveryGui.DoGoPutOnTruck();
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
		deliveryGui.DoGoDeliver(location);
		(cook).msgHereIsOrderFromMarket((DeliveryMan) this,o.Choices, o.outOf,o.amountOwed);
		o.s=orderState.waitingForPayment;
	}
	private void giveBill(){
		o.s=orderState.givingBill;
		for(Restaurant r:myPerson.simCityGui.getRestaurants()){
			if(r.cook.equals(cook)){
				r.cashier.msgHereIsBill((DeliveryMan) this, o.amountOwed);
			}
		}
	}
	
	private void orderDone(){
		o.s=orderState.noOrder;
		cook=null;
		deliveryGui.DoGoBack();
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
		Market.msgDeliveryDone(this);
	}
	public DeliveryManGui getDeliveryManGui() {
		return deliveryGui;
	}
	
	public void atDest(){
		atShelf.release();
	}
}

