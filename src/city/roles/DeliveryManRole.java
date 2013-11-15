package city.roles;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent.Menu;
import restaurant.WaiterAgent.MenuItem;
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
public class DeliveryManRole extends Role {
	private String name;
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
	double Price=5;
	CashierRole cashier;
	CookRole cook;
	MarketAgent Market;
	enum orderState{waiting,ordered,waitingForPayment,payed,done};
	PersonAgent myPerson; 
	public DeliveryManRole(PersonAgent p){
		super(p);
		this.myPerson=p;
	}
	//messages
	public void msgTakeCustomer(CookRole c,MarketAgent m){
		cook=c;
		Market=m;
		stateChanged();
	}
	
	public void msgHereIsOrder(Map<String,Integer>choice){
		o=new Order(choice,orderState.waiting);
		stateChanged();
	}
	
	public void msgHereIsPayment(double payment, CashierRole ca){
		cashier=ca;
		o.s=orderState.payed;
		stateChanged();
	}
	
	//scheduler
	public boolean pickAndExecuteAnAction() {
		if(cook!=null&&o==null){
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
		if (o.s==orderState.payed){
			orderDone();
			return true;
		}
		
		return false;
	}

	//actions
	private void askForOrder(){
		cook.msgCanIHelpYou(this,Market);
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
	    cook.msgHereIsPrice(o.amountOwed,this);
	    o.s=orderState.ordered;
	}
	
	private void giveOrder(){
		//DoGoPutOnTruck();
		//DoGoDeliver();
		cook.msgHereIsOrderFromMarket(this,o.Choices, o.outOf);
		o.s=orderState.waitingForPayment;
	}
	
	private void orderDone(){
		o=null;
		cook=null;
		Market.msgDeliveryDone();
	}
}

