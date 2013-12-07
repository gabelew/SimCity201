package EBRestaurant.roles;

import EBRestaurant.gui.EBCookGui;

import java.util.*;

import market.interfaces.DeliveryMan;
import city.MarketAgent;
import city.PersonAgent;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.*;

/**
 * Restaurant Cook Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class EBCookRole extends Role implements Cook {
	public Restaurant restaurant;
	PersonAgent replacementPerson = null;
	private String name;
	private EBCookGui cookgui;
	List<Order>Orders=Collections.synchronizedList(new ArrayList<Order>());
	List<market>markets=new ArrayList<market>();
	List<marketOrder>marketOrders=new ArrayList<marketOrder>();
	private boolean putOrder=false;
	private int numMarket=0;
	private List<String>stock=new ArrayList<String>();
	enum CState {none, goToWork, working, leaving, relieveFromDuty, wantsOffWork};
	CState cookState=CState.none;
	private class market{
		public market(MarketAgent m,List<String>s) {
			market=m;
			inStock=s;
		}
		MarketAgent market;
		List<String>inStock=new ArrayList<String>();
	}
	private class marketOrder{
		public marketOrder(String choice, int amount,states waiting,MarketAgent m) {
			order.put(choice,amount);
			marketState=waiting;
			market=m;
		}
		MarketAgent market;
		DeliveryMan delivery;
		HashMap<String,Integer> order=new HashMap<String,Integer>();
		states marketState;
		double amountOwed;
	}
	public enum states{waiting,ordering,ordered,payed,received};
	private class Order{
		Waiter w;
		String choice;
		int tableNumber;
		state S;
		public Order(Waiter waiter, String choice2, int tableNumber2,
				state pending) {
			w=waiter;
			choice=choice2;
			tableNumber=tableNumber2;
			S=pending;
		}
	}

	HashMap<String,Integer>Inventory=new HashMap<String,Integer>();
	HashMap<String,Integer> hm=new HashMap<String,Integer>();
	
	public enum state{pending,cooking,done};
	Timer timer= new Timer();
	class MyTimerTask extends TimerTask{
		
		private Order O;
		public MyTimerTask(Order O){
			this.O=O;
		}
		public void run(){
			foodDone(O);
		}
	}
	public EBCookRole(String name) {
		super();
		this.name=name;
		hm.put("Steak",20*1000);
		hm.put("Salad", 7*1000);
		hm.put("Pizza", 12*1000);
		hm.put("Chicken", 15*1000);
		Inventory.put("Steak",1);
		Inventory.put("Salad", 1);
		Inventory.put("Pizza", 1);
		Inventory.put("Chicken", 1);
		stock.add("Steak");
		stock.add("Chicken");
		stock.add("Pizza");
		stock.add("Salad");
	}
	
	public String getName() {
		return name;
	}
	// Messages
	
	public void msgHereIsOrder(String choice, int tableNumber, Waiter waiter)
	{
		Orders.add(new Order(waiter, choice, tableNumber, state.pending));
		stateChanged();
	}
	
	public void msgAnimationTakingFood(int tableNumber){
		cookgui.setReady("", tableNumber);
	}
	
	public void msgRefillOrder(int amount, String choice){
		Inventory.put(choice, (Inventory.get(choice)+amount));
		Do(choice+" refilled to "+Inventory.get(choice));
		putOrder=false;
		stateChanged();
	}
	
	public void msgOrderEmpty(String choice,Market m){
		synchronized(markets){
		for(market Market:markets){
			if(Market.market==m){
				Market.inStock.remove(choice);
			}
		}
		}
		putOrder=false;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(cookState == CState.wantsOffWork){
			boolean canGetOffWork = true;
			for (Order o :Orders)
			{
				if(o.S != state.pending)
				{
					canGetOffWork = false;
					break;
				}
			}
			if(canGetOffWork){
				cookState = CState.leaving;
			}
		}
		
		if(cookState == CState.relieveFromDuty){
			cookState = CState.none;
			myPerson.releavedFromDuty(this);
			if(replacementPerson != null){
				replacementPerson.waitingResponse.release();
			}
			return true;
		}
		
		if(cookState == CState.goToWork){
			cookState = CState.working;
			cookGui.DoEnterRestaurant();
			return true;
		}
		
		
		if(cookState == CState.leaving){
			cookState = CState.none;
			cookGui.DoLeaveRestaurant();
			/*try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			cookState = CState.relieveFromDuty;
			return true;
		}
		for (Order O: Orders){
			if (O.S==state.done){
				doneCooking(O);
				return true;
			}
		}
		for (Order O: Orders){
			if (O.S==state.pending){
				CookIt(O);
				return true;
			}
		}
		for(marketOrder m:marketOrders){
			if(m.marketState==states.received){
				giveInvoice(m);
			}
			if(m.marketState==states.ordering){
				m.marketState=states.ordered;
				placeOrder(m);
			}
		}
		for (String key: Inventory.keySet()){
			if(markets.size()>0)
			{
				if (Inventory.get(key)<2&&!putOrder){
					for(String in:markets.get(numMarket).inStock)
					{
						if(in==key){
							orderIt(key,numMarket);
							return true;
						}
					}
					numMarket++;
					if (numMarket>=markets.size()){
						numMarket=0;
					}
				}
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void CookIt(Order O){
		Do("Received order for table "+ O.tableNumber);
		if (Inventory.get(O.choice)==0)
		{
			((EBWaiterRole) O.w).msgOutOfOrder(O.choice, O.tableNumber);
			Do("Out of "+O.choice+" please re-order");
			if(markets.size()>0)
			{
				//markets.get(numMarket).market.msgHereIsOrder(O.choice,this);
				//putOrder=true;
			}
			Orders.remove(O);
		}
		else
		{
			Inventory.put(O.choice, (Inventory.get(O.choice)-1));//remove one from Inventory
			Do(O.choice+" amount="+ Inventory.get(O.choice));
			timer.schedule(new MyTimerTask(O), hm.get(O.choice));
			O.S=state.cooking;
			cookgui.setCooking(O.choice,O.tableNumber);
			stateChanged();
		}
	}
	protected void foodDone(Order O) {
		O.S=state.done;
		stateChanged();
		cookgui.setCooking("",O.tableNumber);
		cookgui.setReady(O.choice, O.tableNumber);
		Do("Food done cooking");
	}
	
	private void giveInvoice(marketOrder order){
		((EBCashierRole)restaurant.cashier).msgHereIsInvoice(order.amountOwed,order.delivery);
		marketOrders.remove(order);
	}
	
	private void placeOrder(marketOrder order){
		order.delivery.msgHereIsOrder(order.order);
	}
	
	private void orderIt(String choice, int numMarket){
		marketOrders.add(new marketOrder(choice,50,states.waiting,markets.get(numMarket).market));
		markets.get(numMarket).market.msgPlaceDeliveryOrder(this);
	}
	
	private void doneCooking(Order O){
		Do("Waiter, "+O.choice+" is ready for table "+O.tableNumber);
		((EBWaiterRole) O.w).msgOrderIsReady(O.choice, O.tableNumber);
		Orders.remove(O);
	}

	
	/*public void pauseIt(){
		pause();
	}
	
	public void resumeIt(){
		resume();
	}*/

	public void setGui(EBCookGui g) {
		cookgui = g;
	}

	@Override
	public void msgCanIHelpYou(DeliveryMan DM, MarketAgent M) {
		for(marketOrder m:marketOrders){
			if(m.market==M){
				m.delivery=DM;
				m.marketState=states.ordering;
			}
		}
	}

	public void msgNeverOrderFromMarketAgain(MarketAgent market) {
		if(markets.size()!=0){
			markets.remove(market);
		}
	}

	public void msgHereIsOrderFromMarket(DeliveryMan Dm,
			Map<String, Integer> choices, double amount) {
		for(marketOrder order:marketOrders){
			if(order.delivery==Dm){
				order.marketState=states.received;
				order.amountOwed=amount;
			}
		}
		for(String key:choices.keySet()){
			Inventory.put(key, choices.get(key));
		}
	}

	@Override
	public void msgIncompleteOrder(DeliveryMan deliveryMan, List<String> outOf) {
		for (market m:markets){
			if(m.market==((city.roles.DeliveryManRole)deliveryMan).Market){
				for(String in:m.inStock){
					for(String out:outOf){
						if(in==out){
							m.inStock.remove(in);
						}
					}
				}
			}
		}
	}

	public void msgRelieveFromDuty(PersonAgent p) {
		replacementPerson = p;
		cookState = CState.wantsOffWork;
		this.stateChanged();
	}
	
	public void addMarket(MarketAgent m) {
		 markets.add(new market(m,stock));
	}

	public void goesToWork() {
		cookState = CState.goToWork;
		stateChanged();
	}
	
	public void setRestaurant(Restaurant r) {
		restaurant = r;
		
	}

}

