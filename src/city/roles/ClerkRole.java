package city.roles;

import restaurant.CashierAgent;
import restaurant.gui.CustomerGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
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

import city.MarketAgent;
import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class ClerkRole extends Role {
	Order o;
	class Order{
		public Order(Map<String, Integer> choice, orderState state) {
			Choices=choice;
			s=state;
		}
		Map<String, Integer> Choices = new HashMap<String, Integer>();
		List<String> outOf;
		orderState s;
		double amountOwed;
	}
	MarketAgent Market;
	MarketCustomerRole MCR;
	private enum orderState{waiting, waitingForPayment, payed,done};
	PersonAgent myPerson; 
	double Price=5;
	public ClerkRole(PersonAgent p){
		super(p);
		this.myPerson=p;
	}

	//messages
	public void msgTakeCustomer(MarketCustomerRole CR,MarketAgent m){
		MCR=CR;
		Market=m;
	}
	
	public void msgPlaceOrder(Map<String,Integer> choice){
		o=new Order(choice,orderState.waiting);
	}
	
	public void msgHereIsPayment(double money){
		o.s=orderState.payed;
	}
	//scheduler
	public boolean pickAndExecuteAnAction() {
		if(MCR!=null &&o==null){
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
		MCR.msgCanIHelpYou(this);
	}
	
	private void fillOrder(){
		//DoGoGetFood();
		Iterator it = o.Choices.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if(Market.Inventory.get(pairs)==0){
	        	o.outOf.add(pairs.getKey().toString());
	        	o.Choices.remove(pairs);
	        }
	        else if(o.Choices.get(pairs)>Market.Inventory.get(pairs)){
	        	o.Choices.put(pairs.getKey().toString(), Market.Inventory.get(pairs.getKey()));
	        	o.amountOwed=o.amountOwed+Market.Inventory.get(pairs.getKey())*Price;
	        	Market.Inventory.put(pairs.getKey().toString(), 0);
	        }
	        else{
	        	o.amountOwed=o.amountOwed+o.Choices.get(pairs.getKey().toString())*Price;
	        	Market.Inventory.put(pairs.getKey().toString(), Market.Inventory.put(pairs.getKey().toString(), Market.Inventory.get(pairs.getKey().toString())-o.Choices.get(pairs.getKey().toString())));
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    MCR.msgHereIsPrice(o.amountOwed);
	    o.s=orderState.waitingForPayment;
	}
	
	private void giveOrder(){
		//DoGoGiveOrder();
		MCR.msgHereIsOrder(o.Choices,o.outOf);
		o.s=orderState.done;
	}
	
	private void orderDone(){
		o=null;
		MCR=null;
		Market.msgClerkDone();
	}

}

