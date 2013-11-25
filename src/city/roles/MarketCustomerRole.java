package city.roles;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Semaphore;

import restaurant.test.mock.LoggedEvent;
import market.gui.MarketCustomerGui;
import market.interfaces.MarketCustomer;
import city.MarketAgent;
import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Role implements MarketCustomer {
	private MarketCustomerGui marketCGui=new MarketCustomerGui(this);
	private Semaphore atShelf=new Semaphore(0,true);
	private int numChair;
	MarketAgent market;
	PersonAgent myPerson; 
	ClerkRole Clerk;
	Order o;
	class Order{
		Map<String, Integer> Choices = new HashMap<String, Integer>();
		List<String> outOf=new ArrayList<String>();
		orderState s;
		double amountOwed;
		Order(Map<String, Integer> c, orderState s){
			this.Choices = c;
			this.s = s;
		}
	}
	private enum orderState{waiting,entered, ordering,ordered,paymentReceived, payed,done,left};
	public MarketCustomerRole(PersonAgent p){
		super(p);
		this.myPerson=p;
	}
	
	//messages
	public void startShopping(MarketAgent m,
			Map<String, Integer> toOrderFromMarket) {
		this.market = m;
		o = new Order(toOrderFromMarket, orderState.waiting);
		stateChanged();	
		market.msgPlaceOrder(this);
		myPerson.log.add(new LoggedEvent("received start shopping from person"));
	}

	public void msgCanIHelpYou(ClerkRole clerk){
		print("can I");
		Clerk=clerk;
		o.s=orderState.ordering;
		stateChanged();	
	}

	public void msgHereIsPrice(double amount){
		o.amountOwed=amount;
		o.s=orderState.paymentReceived;
		stateChanged();	
	}
	
	public void msgHereIsOrder(Map<String,Integer>choice,List<String>outOf){
		o.s=orderState.done;
		o.Choices=choice;
		if(outOf!=null)
			o.outOf=outOf;
		stateChanged();	
	}
	
	
	//scheduler
	public boolean pickAndExecuteAnAction() {
		if (o!=null){
			if(o.s==orderState.ordering){
				giveOrder();
				return true;
			}
			if(o.s==orderState.paymentReceived){
				payForOrder();
				return true;
			}
			if(o.s==orderState.done){
				receivedOrder();
				return true;
			}
			if(o.s==orderState.waiting){
				o.s=orderState.entered;
				findPlaceToWait();
				return true;
			}
		}
		return false;
	}

	//actions
	private void findPlaceToWait(){
		boolean trying=false;
		for(int i=0;i<market.chairs.size();i++)
		{
			if(market.chairs.get(i).free==true&&!trying){
				market.chairs.get(i).free=false;
				marketCGui.DoWait(i);
				trying=true;
				numChair=i;
			}
		}
		if(!trying){
			marketCGui.DoWait(1);
		}
	}
	private void giveOrder(){
		marketCGui.DoGoToClerk();
		market.chairs.get(numChair).free=true;
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
		Clerk.msgPlaceOrder(o.Choices);
		o.s=orderState.ordered;
	}
	
	private void payForOrder(){
		//subtract amount from money in person agent
		myPerson.cashOnHand=myPerson.cashOnHand-o.amountOwed;
		Clerk.msgHereIsPayment(o.amountOwed);
		o.s=orderState.payed;
	}
	
	private void receivedOrder(){
		marketCGui.DoLeaveMarket();
	    try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			
		}
	    if(o.outOf==null||o.outOf.size()==0)
	    	myPerson.marketNotStocked(market);
	    myPerson.doneShopping(o.Choices,market);
		o=null;
	}
	
	public MarketCustomerGui getMarketCustomerGui(){
		return marketCGui;
	}
	
	public void atSpot(){
		atShelf.release();
	}
	
}

