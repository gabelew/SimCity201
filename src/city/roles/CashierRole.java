package city.roles;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.PersonAgent;
import city.roles.HostRole.State;
import restaurant.Restaurant;
import restaurant.gui.CashierGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

public class CashierRole extends Role implements Cashier {
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	Timer timer = new Timer();
	
	public EventLog log = new EventLog();

	public Restaurant restaurant;
	public CashierGui cashierGui = null;
	Map<String, Double> pricingMap = new HashMap<String, Double>(); 
	public double bank = 5000; 
	enum State {none, goToWork, working, leaving, releaveFromDuty};
	State state = State.none;

	private Semaphore waitingResponse = new Semaphore(0,true);
	PersonAgent replacementPerson = null;
	
	static final int MAKE_CHECK_TIME = 5000;
	public class Order{
		public Waiter waiter;
		public Customer customer;
		public String choice;
		public double check;
		public double cashIn;
		public OrderState state;
		
		public Order(Waiter w, Customer ca, String c, OrderState s){
			waiter = w;
			customer = ca;
			choice = c;
			state = s;	
		}
		
	}
	public enum OrderState {requested, printingCheck, deliverCheck, awaitingPayment, paymentRecieved, done, inDebt};
	
	public class Bill{
		public DeliveryManRole deliveryMan;
		public double bill;
		public BillState state;
		
		Bill(DeliveryManRole DMR, double b, BillState s){
			deliveryMan=DMR;
			bill = b;
			state = s;
		}
	}
	public enum BillState {requested, payed};
	
	public static final double SALAD_COST = 4.99;
	public static final double STEAK_COST = 15.99;
	public static final double CHICKEN_COST = 10.99;
	public static final double BURGER_COST = 8.99;
	public static final double COOKIE_COST = 3.99;	
	
	public CashierRole(){
		super();
		
		pricingMap.put("salad", SALAD_COST);
		pricingMap.put("steak", STEAK_COST);
		pricingMap.put("chicken", CHICKEN_COST);
		pricingMap.put("burger", BURGER_COST);
		pricingMap.put("cookie", COOKIE_COST);
	}

	public void setGui(CashierGui g) {
		cashierGui = g;
	}

	public CashierGui getGui() {
		return cashierGui;
	}

	public void goesToWork() {
		state = State.goToWork;
		stateChanged();
	}

	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		state = State.leaving;
		this.stateChanged();
	}
	public void msgAnimationHasLeftRestaurant() {
		state = State.releaveFromDuty;
		waitingResponse.release();
	}
	public void msgProduceCheck(Waiter w, Customer c, String choice)
	{
		orders.add(new Order(w, c, choice, OrderState.requested));
		stateChanged();
		print("order recieved!!");
		log.add(new LoggedEvent("Received msgProduceCheck from waiter. Choice = "+ choice));
	}
	
	public void msgCheckPrinted(Order o)
	{
		Order deleteIt = null;
		synchronized(orders){
			for(Order order: orders){
				if(order.state == OrderState.inDebt && o.customer == order.customer){
					o.check += order.check;
					order.state = OrderState.done;
					deleteIt = order;
				}
			}
		}
		orders.remove(deleteIt);
		o.state = OrderState.deliverCheck;
		log.add(new LoggedEvent("Received msgCheckPrinted from cashier. Total of check = "+ o.check));
		stateChanged();
	}
	
	public void msgPayment(Customer c, double cash) {
		Order order = findOrder(c);
		order.cashIn = cash;
		order.state = OrderState.paymentRecieved;
		log.add(new LoggedEvent("Received msgPayment from customer. Total cash in = "+ cash));
		stateChanged();
	}

	public void msgHereIsInvoice(double price,DeliveryManRole DMR) {
		bills.add(new Bill(DMR,price,BillState.requested));
		stateChanged();
	}
	
	/*public void msgHereIsBill(Market m, double bill) {
		bills.add( new Bill(m, bill, BillState.requested));
		log.add(new LoggedEvent("Received msgHereIsBill from market. Total of Bill = "+ bill));
		stateChanged();
	}	*/
	
	private Order findOrder(Customer c) {
		synchronized(orders){
			for(Order o: orders){
				if(o.customer == c)
				{
					return o;
				}
			}
		}
		return null;
	}

	public boolean pickAndExecuteAnAction() {
		if(state == State.releaveFromDuty){
			state = State.none;
			myPerson.releavedFromDuty(this);
			if(replacementPerson != null){
				replacementPerson.waitingResponse.release();
			}
		}
		if(state == State.goToWork){
			state = State.working;
			cashierGui.DoEnterRestaurant();
			return true;
		}
		if(state == State.leaving){
			state = State.none;
			cashierGui.DoLeaveRestaurant();
			try {
				waitingResponse.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		Order temp = null;
		synchronized(orders){
			for (Order o : orders)
			{
				if(o.state == OrderState.paymentRecieved  && temp == null)
				{
					temp = o;
				}
			}
		}
		
		if(temp != null){
			processPayment(temp);
			return true;
		}

		synchronized(orders){
			for (Order o : orders)
			{
				if(o.state == OrderState.requested && temp == null)
				{
					temp = o;
				}
			}
		}
		
		if(temp != null){
			produceCheck(temp);
			return true;
		}
		
		synchronized(orders){
			for (Order o : orders)
			{
				if(o.state == OrderState.deliverCheck && temp == null)
				{
					temp = o;
				}
			}
		}
		
		if(temp != null){
			giveWaiter(temp);
			return true;
		}
		
		Bill temp2 = null;
		synchronized(bills){
			for (Bill b : bills)
			{
				if(b.state == BillState.requested && temp2 == null)
				{
					temp2 = b;
				}
			}
		}
		
		if(temp2 != null){
			payBill(temp2);
			return true;
		}
		
		return false;
	}



	//Actions
	/*private void payBill(Bill b) {
		log.add(new LoggedEvent("Performed payBill. new bank balance, "+ (bank - b.bill)
				+ ", = bank " + bank + ", - b.bill, "+ b.bill));
		
		bank = bank - b.bill;
		b.market.msgPayment(this, b.bill);
		bills.remove(b);
		print("payed bill");
	}
	*/
	
	private void payBill(Bill b){
		bank=bank-b.bill;
		b.deliveryMan.msgHereIsPayment(b.bill, this);
		bills.remove(b);
	}
	private void processPayment(Order o) {
		double cashOut = o.cashIn - o.check;
		cashOut = (Math.round(100*cashOut) / ((double)100));
		
		print(cashOut + " = " + o.cashIn + " - " + o.check);

		log.add(new LoggedEvent("Performed processPayment. Cash out, "+ cashOut + ", = o.cashIn, " + o.cashIn + ", - o.check, "+ o.check));
		
		if(cashOut < 0){
			
			if(o.cashIn >= 0){ 
				bank += o.cashIn;
				o.check = o.check - o.cashIn;
			}else{
				o.check = o.check;
			}
			
			o.state = OrderState.inDebt;
			print("You can pay us on your next visit");
			o.customer.msgPayMeLater();
		}else{
			bank += o.check;
			o.customer.msgChange(cashOut);
			orders.remove(o);
		}
		
	}

	private void produceCheck(Order o)
	{
		o.state = OrderState.printingCheck;
		o.check = pricingMap.get(o.choice.toLowerCase());
		log.add(new LoggedEvent("Performed produceCheck. Check = "+ o.check));
		printCheck(o);
	}
	
	private void printCheck(final Order o){
		timer.schedule(new TimerTask() {
			public void run() {
				msgCheckPrinted(o);
			}
		}, 
		MAKE_CHECK_TIME);
	}

	private void giveWaiter(Order o) {
		o.state = OrderState.awaitingPayment;
		o.waiter.msgHereIsCheck(o.customer, o.check);
		log.add(new LoggedEvent("Performed giveWaiter. Total of check = "+ o.check));
	}

	@Override
	public void msgHereIsBill(Market m, double bill) {
		//bills.add( new Bill(m, bill, BillState.requested));
		//log.add(new LoggedEvent("Received msgHereIsBill from market. Total of Bill = "+ bill));
		//stateChanged();
		
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
		
	}

}
