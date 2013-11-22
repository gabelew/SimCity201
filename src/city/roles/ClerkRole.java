package city.roles;

import restaurant.gui.CustomerGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import agent.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.gui.ClerkGui;
import market.interfaces.*;
import city.MarketAgent;
import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class ClerkRole extends Role implements Clerk {
	public EventLog log = new EventLog();
	private ClerkGui clerkGui=new ClerkGui(this);
	public Order o;
	public class Order{
		public Order(orderState state) {
			s=state;
		}
		Map<String, Integer> Choices = new HashMap<String, Integer>();
		List<String> outOf;
		orderState s;
		double amountOwed;
	}
	Market Market;
	public MarketCustomer MCR;
	public enum orderState{askedForOrder,waitingForOrder,waiting, waitingForPayment, payed,done};
	PersonAgent myPerson; 
	double Price=5;
	public ClerkRole(){
		super();
	}

	//messages
	public void msgTakeCustomer(MarketCustomer CR,Market m){
		MCR=CR;
		Market=m;
		o=new Order(orderState.askedForOrder);
		log.add(new LoggedEvent("Received msgTakeCustomer from Market."));
	}
	
	public void msgPlaceOrder(Map<String,Integer> choice){
		o.Choices=choice;
		o.s=orderState.waiting;
	}
	
	public void msgHereIsPayment(double money){
		o.s=orderState.payed;
	}
	//scheduler
	public boolean pickAndExecuteAnAction() {
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
		MCR.msgCanIHelpYou(this);
	}
	
	private void fillOrder(){
		Iterator it = o.Choices.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if(((MarketAgent)Market).Inventory.get(pairs)==0){
	        	o.outOf.add(pairs.getKey().toString());
	        	o.Choices.remove(pairs);
	        }
	        else if(o.Choices.get(pairs)>((MarketAgent)Market).Inventory.get(pairs)){
	        	o.Choices.put(pairs.getKey().toString(), ((MarketAgent)Market).Inventory.get(pairs.getKey()));
	        	o.amountOwed=o.amountOwed+((MarketAgent)Market).Inventory.get(pairs.getKey())*Price;
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(), 0);
	        }
	        else{
	        	o.amountOwed=o.amountOwed+o.Choices.get(pairs.getKey().toString())*Price;
	        	((MarketAgent)Market).Inventory.put(pairs.getKey().toString(), ((MarketAgent)Market).Inventory.put(pairs.getKey().toString(), ((MarketAgent)Market).Inventory.get(pairs.getKey().toString())-o.Choices.get(pairs.getKey().toString())));
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    clerkGui.DoGoGetFood(o.Choices);
	    MCR.msgHereIsPrice(o.amountOwed);
	    o.s=orderState.waitingForPayment;
	}
	
	private void giveOrder(){
		clerkGui.DoGoGiveOrder();
		MCR.msgHereIsOrder(o.Choices,o.outOf);
		o.s=orderState.done;
	}
	
	private void orderDone(){
		o=null;
		MCR=null;
		Market.msgClerkDone();
	}

}

