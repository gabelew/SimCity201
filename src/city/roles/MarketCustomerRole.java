package city.roles;


import java.util.HashMap;
import java.util.List;
import java.util.Map;





import market.gui.MarketCustomerGui;
import market.interfaces.MarketCustomer;
import city.MarketAgent;
import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Role implements MarketCustomer {
	private MarketCustomerGui marketCGui=new MarketCustomerGui(this);
	MarketAgent market;
	PersonAgent myPerson; 
	ClerkRole Clerk;
	Order o;
	class Order{
		Map<String, Integer> Choices = new HashMap<String, Integer>();
		orderState s;
		double amountOwed;
		Order(Map<String, Integer> c, orderState s){
			this.Choices = c;
			this.s = s;
		}
	}
	private enum orderState{waiting, ordering,ordered,paymentReceived, payed,done};
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
	}

	public void msgCanIHelpYou(ClerkRole clerk){
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
		}
		return false;
	}

	//actions
	private void giveOrder(){
		marketCGui.DoGoToClerk();
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
		o=null;
	}
	
	public MarketCustomerGui getMarketCustomerGui(){
		return marketCGui;
	}

}

