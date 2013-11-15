package city.roles;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import city.PersonAgent;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Role {
	PersonAgent myPerson; 
	ClerkRole Clerk;
	Order o;
	class Order{
		Map<String, Integer> Choices = new HashMap<String, Integer>();
		orderState s;
		double amountOwed;
	}
	private enum orderState{waiting, ordering,ordered,paymentReceived, payed,done};
	public MarketCustomerRole(PersonAgent p){
		super(p);
		this.myPerson=p;
	}
	
	//messages
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
		Clerk.msgPlaceOrder(o.Choices);
		o.s=orderState.ordered;
	}
	
	private void payForOrder(){
		//subtract amount from money in person agent
		Clerk.msgHereIsPayment(o.amountOwed);
		o.s=orderState.payed;
	}
	
	private void receivedOrder(){
		o=null;
	}
}

