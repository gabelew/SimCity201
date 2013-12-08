package CMRestaurant.roles;

import java.util.*;
import java.util.concurrent.Semaphore;

import CMRestaurant.gui.CMCashierGui;
import market.interfaces.DeliveryMan;
import city.PersonAgent;
import city.gui.Gui;
import city.roles.DeliveryManRole;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

public class CMCashierRole extends Role implements Cashier {
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	Timer timer = new Timer();
	
	public EventLog log = new EventLog();

	public Restaurant restaurant;
	public CMCashierGui cashierGui = null;
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
		public DeliveryMan deliveryMan;
		public double bill;
		public BillState state;
		
		Bill(DeliveryMan DMR, double b, BillState s){
			deliveryMan=DMR;
			bill = b;
			state = s;
		}
	}
	public enum BillState {requested, payed, informed};
	
	public static final double SALAD_COST = 4.99;
	public static final double STEAK_COST = 15.99;
	public static final double CHICKEN_COST = 10.99;
	public static final double COOKIE_COST = 3.99;	
	
	public CMCashierRole(){
		super();
		
		pricingMap.put("salad", SALAD_COST);
		pricingMap.put("steak", STEAK_COST);
		pricingMap.put("chicken", CHICKEN_COST);
		pricingMap.put("cookie", COOKIE_COST);
	}

	public void setGui(CMCashierGui g) {
		cashierGui = g;
	}

	public CMCashierGui getGui() {
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

	public void msgHereIsInvoice(double price,DeliveryMan DMR) {
		bills.add(new Bill(DMR,price,BillState.informed));
		stateChanged();
	}
	
	@Override
	public void msgHereIsBill(DeliveryMan DM, double bill) {
		bills.add( new Bill(DM, bill, BillState.requested));
		log.add(new LoggedEvent("Received msgHereIsBill from market. Total of Bill = "+ bill));
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
			if(!"Saturday".equals(myPerson.dayOfWeek) && !"Sunday".equals(myPerson.dayOfWeek) && myPerson.aBankIsOpen())
				DepositBusinessCash();
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
		Bill temp3 = null;
		synchronized(bills){
			for (Bill b : bills)
			{
				if(b.state == BillState.requested && temp2 == null)
				{
					for(Bill b2: bills){
						if(b2.state == BillState.informed && b2.deliveryMan == b.deliveryMan){
							temp2 = b;
							temp3 = b2;
						}
					}
				}
			}
		}
		
		if(temp2 != null){
			payBill(temp2, temp3);
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
	
	private void DepositBusinessCash() {
		double cash = bank - 1500;
		cash = (Math.round(100*cash) / ((double)100));
		int balance = Double.compare(cash, 0);
		if(1 == balance) {
			bank -= cash;
			myPerson.businessFunds += cash;
			myPerson.msgDepositBusinessCash();
		}
	}
	
	private void payBill(Bill billFromDman, Bill invoiceFromCook){
		if(billFromDman.bill == invoiceFromCook.bill){
			bank = bank - billFromDman.bill;
			billFromDman.deliveryMan.msgHereIsPayment(billFromDman.bill, this);
			bills.remove(billFromDman);
			bills.remove(invoiceFromCook);
		}else{
			print("We are never ordering from this Market again.");
			bank = bank - billFromDman.bill;
			billFromDman.deliveryMan.msgHereIsPayment(billFromDman.bill, this);
			//tell cook to put market on naughty list
			restaurant.cook.msgNeverOrderFromMarketAgain(((DeliveryManRole)billFromDman.deliveryMan).Market);
			bills.remove(billFromDman);
			bills.remove(invoiceFromCook);
		}
	}
	private void processPayment(Order o) {
		double cashOut = o.cashIn - o.check;
		cashOut = (Math.round(100*cashOut) / ((double)100));

		log.add(new LoggedEvent("Performed processPayment. Cash out, "+ cashOut + ", = o.cashIn, " + o.cashIn + ", - o.check, "+ o.check));
		
		if(cashOut < 0){
			
			if(o.cashIn >= 0){ 
				bank += o.cashIn;
				o.check = o.check - o.cashIn;
			}else{
				o.check = o.check;
			}
			
			o.state = OrderState.inDebt;
			//print("You can pay us on your next visit");
			((CMCustomerRole) o.customer).msgPayMeLater();
		}else{
			bank += o.check;
			((CMCustomerRole) o.customer).msgChange(cashOut);
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
		((CMWaiterRole) o.waiter).msgHereIsCheck(o.customer, o.check);
		log.add(new LoggedEvent("Performed giveWaiter. Total of check = "+ o.check));
	}

	public void setRestaurant(Restaurant r) {
		restaurant = r;
		
	}

	public int getStateChangePermits() {
		return getStateChangePermits();
	}

	@Override
	public void setGui(Gui g) {
		cashierGui = (CMCashierGui) g;
	}

}
