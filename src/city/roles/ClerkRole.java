package city.roles;


import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import market.gui.ClerkGui;
import market.interfaces.*;
import city.MarketAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

/**
 * Restaurant customer agent.
 */
public class ClerkRole extends Role implements Clerk {
	public EventLog log = new EventLog();
	private Semaphore atShelf=new Semaphore(0,true);
	private ClerkGui clerkGui=new ClerkGui(this);
	public Order o;
	public boolean notTesting=true;
	public class Order{
		public Order(orderState state) {
			s=state;
		}
		public Map<String, Integer> Choices = new HashMap<String, Integer>();
		public List<String> outOf=new ArrayList<String>();
		public orderState s;
		public double amountOwed;
	}
	public Market Market;
	public MarketCustomer MCR;
	public enum orderState{noOrder,askedForOrder,waitingForOrder,waiting, waitingForPayment, payed,done};
	public enum AgentEvent{none,GoToWork,offWork};
	AgentEvent event = AgentEvent.none;
	public ClerkRole(){
		super();
		o=new Order(orderState.noOrder);
	}

	//messages
	public void goesToWork(){
		event = AgentEvent.GoToWork;
		stateChanged();
	}
	public void msgTakeCustomer(MarketCustomer CR,Market m){
		MCR=CR;
		Market=m;
		o.s=(orderState.askedForOrder);
		log.add(new LoggedEvent("Received msgTakeCustomer from Market."));
		AlertLog.getInstance().logMessage(AlertTag.MARKET_CLERK, this.getName(), "Got customer");

		stateChanged();
	}
	
	public void msgPlaceOrder(Map<String,Integer> choice){
		o.Choices=choice;
		o.s=orderState.waiting;
		stateChanged();
		AlertLog.getInstance().logMessage(AlertTag.MARKET_CLERK, this.getName(), "Customer placed order");

	}
	
	public void msgHereIsPayment(double money){
		if(money==o.amountOwed)
			o.s=orderState.payed;
		stateChanged();
		AlertLog.getInstance().logMessage(AlertTag.MARKET_CLERK, this.getName(), "Received payment from customer");

	}
	
	public void msgDoneWithShift(){
		event=AgentEvent.offWork;
		stateChanged();
	}
	public void msgMarketClosed() {
		event=AgentEvent.offWork;
		stateChanged();
	}
	//scheduler
	public boolean pickAndExecuteAnAction() {
		if(event == AgentEvent.GoToWork){
			event = AgentEvent.none;
			((MarketAgent)Market).addClerk(((Clerk)this));
			return true;
		}
		if(event==AgentEvent.offWork){
			event=AgentEvent.none;
			leaveWork();
			return true;
		}
		if(MCR!=null &&o.s==orderState.askedForOrder){
			askForOrder();
			return true;
		}
		if (o.s==orderState.waiting){
			fillOrder();
			return true;
		}
		if (o.s==orderState.payed){
			giveOrder();
			return true;
		}
		if(o.s==orderState.done){
			orderDone();
			return true;
		}
		
		return false;
	}
	//actions
	private void askForOrder(){
		o.s=orderState.waitingForOrder;
		if(notTesting){
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		MCR.msgCanIHelpYou(this);
	}
	
	private void fillOrder(){
		Iterator it = o.Choices.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if(((MarketAgent)Market).Inventory.get(pairs.getKey())==0){
	        	o.outOf.add(pairs.getKey().toString());
	        	o.Choices.remove(pairs);
	        }
	        else if(o.Choices.get(pairs.getKey())>((MarketAgent)Market).Inventory.get(pairs.getKey())){
	        	o.Choices.put(pairs.getKey().toString(), ((MarketAgent)Market).Inventory.get(pairs.getKey()));
	        	o.amountOwed=o.amountOwed+((MarketAgent)Market).Inventory.get(pairs.getKey())*((MarketAgent)Market).Prices.get(pairs.getKey());
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(), 0);
	        }
	        else{
	        	o.amountOwed=o.amountOwed+o.Choices.get(pairs.getKey().toString())*((MarketAgent)Market).Prices.get(pairs.getKey());
	        	Integer temp=o.Choices.get(pairs.getKey());
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(),(((MarketAgent)Market).Inventory.get(pairs.getKey())-temp));
	        }
	    }
	    clerkGui.DoGoGetFood(o.Choices);
	    if(notTesting){
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    }
	    MCR.msgHereIsPrice(o.amountOwed);
	    o.s=orderState.waitingForPayment;
	}
	
	private void giveOrder(){
		clerkGui.DoGoGiveOrder();
		if(notTesting){
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		MCR.msgHereIsOrder(o.Choices,o.outOf);
		o.s=orderState.done;
		AlertLog.getInstance().logMessage(AlertTag.MARKET_CLERK, this.getName(), "Gave order to customer");

	}
	
	private void orderDone(){
		clerkGui.DoDoneWithOrder();
		o.s=orderState.noOrder;
		o.amountOwed=0;
		o.outOf.clear();
		MCR=null;
		Market.msgClerkDone(this);
	}
	
	private void leaveWork(){
		clerkGui.DoLeaveWork();
		if(notTesting){
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		myPerson.releavedFromDuty(this);
	}
	
	public void atShelf(){
		if(notTesting){
		atShelf.release();
		}
	}
	public Gui getGui(){
		return clerkGui;
	}

	@Override
	public void setGui(Gui gui) {
		clerkGui = (ClerkGui) gui;
		
	}

}

