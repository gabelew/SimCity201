package EBRestaurant.roles;

import java.util.*;

import EBRestaurant.gui.EBCashierGui;
import market.interfaces.DeliveryMan;
import city.PersonAgent;
import city.gui.Gui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.DeliveryManRole;
import city.roles.Role;
import restaurant.Restaurant;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Customer;
import restaurant.test.mock.EventLog;
/**
 * EB Restaurant Cashier Role
 */

public class EBCashierRole extends Role implements Cashier {
	public double bank = 5000;
	public Restaurant restaurant;
	PersonAgent replacementPerson = null;
	private boolean exists;
	private EBMenu menu=new EBMenu();
	private int payNumber;
	enum CashState {none, goToWork, working, leaving, releaveFromDuty};
	CashState cashierState = CashState.none;
	public List<payment>Payments=new ArrayList<payment>();
	public class payment{
		DeliveryMan delivery;
		public double amount;
		public payState pState;
		int payNum;
		public payment(double a,DeliveryMan d,payState received,int num){
			delivery=d;
			amount=a;
			pState=received;
			payNum=num;
		}
	}
	public List<customer>Customers=new ArrayList<customer>();
	public class customer{
		Customer cust;
		public float owed;
		public customer(Customer c,float amount){
			cust=c;
			owed=amount;
		}
	}
	public List<Check>Checks= new ArrayList<Check>();
	public class Check{
		Waiter w;
		String choice;
		int tableNumber;
		public state S;
		public Check(Waiter waiter, String choice2, int tableNumber2,
				state created) {
			w=waiter;
			choice=choice2;
			tableNumber=tableNumber2;
			S=created;
		}
	}
	public enum payState{receivedBill,paying,lastTime,owes,paid};
	boolean receivedInvoice=false;
	
	HashMap<String,Integer>Inventory=new HashMap<String,Integer>();
	HashMap<String,Integer> hm=new HashMap<String,Integer>();
	public EBCashierGui cashierGui=null;
	public enum state{created,waiting,paid};
	Timer timer= new Timer();
	public EventLog log=new EventLog();
	public EBCashierRole() {
		super();
		payNumber=0;
	}
	
	// Messages
	
	public void msgHereIsCheck(Waiter w,String choice, int tableNumber)
	{
		Checks.add(new Check(w,choice, tableNumber, state.created));
		stateChanged();
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Creating check for table "+tableNumber);
	}
	
	public void msgPaying(double amount, int tableNumber,boolean payInFull){
		for (Check c: Checks){
			if (c.tableNumber==tableNumber){
				if(payInFull){
					c.S=state.paid;
				}
				else{
					c.S=state.waiting;
				}
				bank=bank+amount;
			}
		}
		amount= (Math.round(100*amount) / ((double)100));
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Received payment of "+amount+" for table "+tableNumber);
	}
	
	public void msgAddToTab(double amount,Customer cust){
			exists=false;
			for(customer c:Customers){
				if(c.cust==cust){
					c.owed=c.owed+(float)amount;
					exists=true;
				}
			}
			if (!exists)
			{
				Customers.add(new customer(cust,(float)amount));
			}

	}
	
	public void msgHereIsInvoice(double amount,DeliveryMan DM){
		receivedInvoice=true;
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Received invoice from cook");
		stateChanged();
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		try{
			if(cashierState == CashState.releaveFromDuty){
				cashierState = CashState.none;
				myPerson.releavedFromDuty(this);
				if(replacementPerson != null){
					replacementPerson.waitingResponse.release();
				}
			}
			if(cashierState == CashState.goToWork){
				cashierState = CashState.working;
				cashierGui.DoEnterRestaurant();
				return true;
			}
			if(cashierState == CashState.leaving){
				boolean needsToPay=false;
				for (payment p:Payments){
					if(p.pState!=payState.owes){
						needsToPay=true;
					}
				}
				if(!needsToPay){
					cashierState = CashState.none;
					if(!"Saturday".equals(myPerson.dayOfWeek) && !"Sunday".equals(myPerson.dayOfWeek) && myPerson.aBankIsOpen())
						DepositBusinessCash();
					cashierGui.DoLeaveRestaurant();
					return true;
				}
			}
			for (payment p:Payments){
				if(p.pState==payState.receivedBill&&receivedInvoice&&bank>0){
					payMarket(p);
					return true;
				}
				if(p.pState==payState.lastTime&&bank>0){
					neverOrderFromMarketAgain(p);
					payMarket(p);
					return true;
				}
				if(p.pState==payState.owes&&bank>0){
					payMarket(p);
					return true;
				}
			}
		for (Check c: Checks){
			if (c.S==state.created){
				c.S=state.waiting;
				createCheck(c.choice,c.tableNumber,c.w);
				return true;
			}
		}
		for (Check c: Checks){
			if (c.S==state.paid){
				removeCheck(c.tableNumber);
				return true;
			}
		}
		return false;
		}
		catch(ConcurrentModificationException e){
			return false;
		}
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void neverOrderFromMarketAgain(payment p){
		restaurant.cook.msgNeverOrderFromMarketAgain(((DeliveryManRole)p.delivery).Market);
	}
	private void payMarket(payment p){
		if (bank<p.amount){
			p.pState=payState.owes;
		}
		else
		{
			receivedInvoice=false;
			p.pState=payState.paid;
			p.delivery.msgHereIsPayment(p.amount, this);
			bank=bank-p.amount;
			Payments.remove(p);
			AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Payed market: "+p.amount);
		}
	}
	private void createCheck(String choice,int tableNumber,Waiter waiter){
		((EBWaiterRole) waiter).msgCheckCreated(menu.hm.get(choice),tableNumber);
	}
	
	public void removeCheck(int tableNumber){
		for (Check c:Checks){
			if (c.tableNumber==tableNumber){
				Checks.remove(c);
			}
		}
	}

	private void DepositBusinessCash() {
		double cash = bank - 1500;
		cash = (Math.round(100*cash) / ((double)100));
		int balance = Double.compare(cash, 0);
		if(1 == balance) {
			bank -= cash;
			myPerson.businessFunds += cash;
			myPerson.msgDepositBusinessCash();
		}
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Depositing business cash");

	}
	
	public void msgHereIsBill(DeliveryMan DMR, double bill) {
		boolean exists=false;
		for(payment p:Payments){
			if(p.delivery==DMR){
				if(receivedInvoice){
					exists=true;
					p.pState=payState.paying;
					if(p.amount!=bill){
						p.pState=payState.lastTime;
					}
				}
			}
		}
		if(!exists){
			Payments.add(new payment(bill,DMR,payState.receivedBill,payNumber));	
		}
		AlertLog.getInstance().logMessage(AlertTag.REST_CASHIER, this.getName(), "Received Bill from Delivery Man");
		stateChanged();
	}

	public void msgReleaveFromDuty(PersonAgent p) {
		replacementPerson = p;
		cashierState = CashState.leaving;
		this.stateChanged();
	}

	public void goesToWork() {
		cashierState = CashState.goToWork;
		stateChanged();
	}


	public void setRestaurant(Restaurant r) {
		restaurant = r;
	}

	public void setGui(EBCashierGui g) {
		cashierGui = g;
	}

	public EBCashierGui getGui() {
		return cashierGui;
	}

	public void setGui(Gui g) {
		cashierGui = (EBCashierGui) g;
	}

	public void msgLeft() {
		cashierState = CashState.releaveFromDuty;
		stateChanged();
	}

	public void msgClosed() {
		cashierState = CashState.leaving;
		stateChanged();
	}
}

