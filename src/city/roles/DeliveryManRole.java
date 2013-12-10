package city.roles;

//import restaurant.WaiterAgent.Menu;
//import restaurant.WaiterAgent.MenuItem;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import market.gui.DeliveryManGui;
import restaurant.interfaces.Cook;
import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.gui.DeliveryManDrivingGui;
import city.gui.Gui;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

/**
 * Restaurant customer agent.
 */
public class DeliveryManRole extends Role implements DeliveryMan{
	private Semaphore atShelf=new Semaphore(0,true);
	public boolean notTesting=true;
	public EventLog log = new EventLog();
	public Restaurant restaurant;
	private DeliveryManGui deliveryGui=new DeliveryManGui(this);
	private DeliveryManDrivingGui drivingGui;
	public Order o;
	Point location;
	public class Order{
		public Order( orderState state) {
			s=state;
		}
		public Map<String, Integer> Choices = new HashMap<String, Integer>();
		public List<String> outOf= new ArrayList<String>();
		public orderState s;
		public double amountOwed;
		public Cook cook;
	}
	Cashier cashier;
	public MarketAgent Market;
	public enum orderState{noOrder,closed,askedForOrder,waitingForOrder,waiting,ordered,onMyWay,atRestaurant,givingBill,waitingForPayment,payed,backAtMarket,done};
	public enum AgentEvent{none,GoToWork,offWork};
	AgentEvent event = AgentEvent.none;
	public DeliveryManRole(PersonAgent p){
		super(p);
		o=new Order(orderState.noOrder);
		drivingGui=new DeliveryManDrivingGui(this,myPerson.simCityGui);
		myPerson.simCityGui.animationPanel.addGui(drivingGui);
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
		o.cook=c;
		Market=m;
		o.s=orderState.askedForOrder;
		stateChanged();
		log.add(new LoggedEvent("Received msgTakeCustomer from Market."));
	}
	
	public void msgTryAgain(Order order, MarketAgent marketAgent) {
		for (Restaurant r: myPerson.simCityGui.getRestaurants()){
			if(r.cook==o.cook){
				if(r.isOpen){
					Market=marketAgent;
					o=order;
					o.s=orderState.ordered;
					stateChanged();
				}
				else{
					o.s=orderState.closed;
				}
			}
		}
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
	
	public void msgDoneWithShift(){
		event=AgentEvent.offWork;
		stateChanged();
	}
	
	public void msgAnimationAtRestaurant(){
		o.s=orderState.atRestaurant;
		stateChanged();
	}
	
	public void msgAnimationAtMarket(){
		o.s=orderState.backAtMarket;
		stateChanged();
	}
	
	//scheduler
	public boolean pickAndExecuteAnAction() {
		if(event == AgentEvent.GoToWork){
			event = AgentEvent.none;
			((MarketAgent)Market).addDeliveryMan(((DeliveryMan)this));
			return true;
		}
		if(event==AgentEvent.offWork){
			event=AgentEvent.none;
			leaveWork();
			return true;
		}
		if(o.cook!=null&&o.s==orderState.askedForOrder){
			askForOrder();
			return true;
		}
		if(o.s==orderState.waiting){
			fillOrder();
			return true;
		}
		if(o.s==orderState.closed){
			dontDeliver();
			return true;
		}
		if(o.s==orderState.ordered){
			giveOrder();
			return true;
		}
		if(o.s==orderState.atRestaurant){
			atRestaurant();
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
		if (o.s==orderState.backAtMarket){
			backAtMarket();
			return true;
		}
		return false;
	}

	//actions
	private void askForOrder(){
		o.cook.msgCanIHelpYou((DeliveryMan) this,Market);
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
	        	o.amountOwed=o.amountOwed+Market.Inventory.get(pairs.getKey())*((MarketAgent)Market).Prices.get(pairs.getKey());
	        	Market.Inventory.put(pairs.getKey().toString(), 0);
	        }
	        else{
	        	o.amountOwed=o.amountOwed+o.Choices.get(pairs.getKey().toString())*((MarketAgent)Market).Prices.get(pairs.getKey());
	        	Integer temp=o.Choices.get(pairs.getKey());
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(),(((MarketAgent)Market).Inventory.get(pairs.getKey())-temp));
	        }
	    }
	    deliveryGui.DoGoGetFood(o.Choices);
	    if(notTesting){
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
	    }
	    if(!o.outOf.isEmpty())
	    	o.cook.msgIncompleteOrder((DeliveryMan)this,o.outOf);
	    o.s=orderState.ordered;
	}
	
	private void giveOrder(){
		deliveryGui.DoGoPutOnTruck();
		if(notTesting){
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
		}
		if(notTesting){
		drivingGui.setStartPos();
		for (Restaurant r: myPerson.simCityGui.getRestaurants()){
			if(r.cook==o.cook){
				drivingGui.setPresent(true);
				drivingGui.DoGoDeliver(r.location);
			}
		}
		}
		o.s=orderState.onMyWay;
	}
	
	private void atRestaurant(){
		for(Restaurant R: myPerson.simCityGui.getRestaurants()){
			if(R.cook==o.cook){
				if(R.isOpen){
					(o.cook).msgHereIsOrderFromMarket((DeliveryMan) this,o.Choices,o.amountOwed);
					o.s=orderState.waitingForPayment;
				}
				else{
					Market.failedOrders.add(o);
					o.s=orderState.payed;
				}
			}
		}
	}
	private void dontDeliver(){
		Market.failedOrders.add(o);
		o.s=orderState.payed;
	}
	private void giveBill(){
		o.s=orderState.givingBill;
		for(Restaurant r:myPerson.simCityGui.getRestaurants()){
			if(r.cook.equals(o.cook)){
				r.cashier.msgHereIsBill((DeliveryMan) this, o.amountOwed);
			}
		}
	}
	
	private void orderDone(){
		o.s=orderState.noOrder;
		o.outOf.clear();
		o.cook=null;
		if(notTesting){
			drivingGui.setPresent(true);
			drivingGui.DoGoBack();
		}
	}
	
	private void backAtMarket(){
		Market.msgDeliveryDone(this);
		o.outOf.clear();
		o.s=orderState.done;
		deliveryGui.DoGoToStand();
	}
	
	private void leaveWork(){
		deliveryGui.DoLeaveWork();
		if(notTesting){
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		myPerson.releavedFromDuty(this);
	}
	public Gui getGui() {
		return deliveryGui;
	}
	
	public DeliveryManDrivingGui getDeliveryDrivingManGui() {
		return drivingGui;
	}
	
	public void atDest(){
		if(notTesting){
		atShelf.release();
		}
	}
	
	@Override
	public void setGui(Gui gui) {
		
	}

}

