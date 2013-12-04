package city.roles;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import restaurant.test.mock.LoggedEvent;
import bank.BankBuilding;
import bank.gui.BankCustomerGui;
import city.BankAgent;
import city.BankAgent.BankAccount;
import city.PersonAgent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.interfaces.Bank;
import city.interfaces.BankCustomer;

public class BankCustomerRole extends Role implements BankCustomer{
	
	// Data
	class Task {
		BankingState bs;
		double amount;
		String accountType;
		Task(BankingState bs, double amount, String at) {
			this.bs = bs;
			this.amount = amount;
			this.accountType = at;
		}
	}
	
	public class Loan {
		public double amount;
		public String accountType;	
		public Loan(double amount, String at) {
			this.amount = amount;
			this.accountType = at;
		}
	}
	
	public BankBuilding bank;
	public List<Loan> loans = new CopyOnWriteArrayList<Loan>();
	public List<Task> tasks = new CopyOnWriteArrayList<Task>();
	
	enum BankingState{WantToCheckBalance, WantToOpenAccount, WantToDeposit, WantToWithdraw, 
		WantToGetALoan, WantToPayBackLoan, WantToAutoPayLoan, WantToDepositToBusiness, CheckingBalance, 
		OpeningAccount, Depositing, DepositingBusiness, Withdrawing, RequestingALoan, PayingLoan };
	public enum CustomerState {None, EnteringBank, InBank, FindingATM, AtAtm, LeavingBank};
	public CustomerState state = CustomerState.None;
	private BankCustomerGui customerGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	static final int PERSONAL_BROKE_AMOUNT = 100;
	static final int BUSINESS_BROKE_AMOUNT = 500;
	static final int PERSONAL_BROKE_BORROW_AMOUNT = 200;
	static final int BUSINESS_BROKE_BORROW_AMOUNT = 700;
	static final int AT_ATM_TIME = 2500;
	Timer timer = new Timer();

	public BankCustomerRole(PersonAgent p) {
		super(p);
	}
	
	
	// Messages
	public void goingToBank() {
		// default check balance tasks in bank
		if(myPerson.personalAccount != null) {
			tasks.add(new Task(BankingState.WantToCheckBalance, 0, "personal"));
			// deposit excess cash
			int excessCash = Double.compare(myPerson.cashOnHand, 600);
			if(1 == excessCash && 0 == loans.size()) {
				double cash = myPerson.cashOnHand - 600;
				cash = (Math.round(100*cash) / ((double)100));
				tasks.add(new Task(BankingState.WantToDeposit, cash, "personal"));
			}
			int lessCash = Double.compare(myPerson.cashOnHand, 100);
			if(-1 == lessCash) {
				tasks.add(new Task(BankingState.WantToWithdraw, 100, "personal"));
			}
		} else {
			tasks.add(new Task(BankingState.WantToOpenAccount, 0, "personal"));
		}
		
		if(myPerson.businessAccount != null) {
			int excessCash = Double.compare(myPerson.businessFunds, 0);
			if(1 == excessCash){
				tasks.add(new Task(BankingState.WantToDepositToBusiness, myPerson.businessFunds, "business"));
			}
		}
		if(myPerson.businessAccount != null && myPerson.isManager) {
			tasks.add(new Task(BankingState.WantToCheckBalance, 0, "business"));
		}
		
		// checks if they can pay back their loan
		for(Loan l: loans) {
			int payLoan;
			if ("personal".equals(l.accountType))
				payLoan = Double.compare(myPerson.cashOnHand - PERSONAL_BROKE_BORROW_AMOUNT, l.amount);
			else
				payLoan = Double.compare(myPerson.businessFunds - BUSINESS_BROKE_BORROW_AMOUNT, l.amount);
			if(1 == payLoan)
				tasks.add(new Task(BankingState.WantToPayBackLoan, l.amount, l.accountType));
		}
		state = CustomerState.EnteringBank;
		stateChanged();
		myPerson.log.add(new LoggedEvent("Recieved goingToBank from person Agent."));
	}
	
	public void msgIWantToCheckBalance(String accountType) {
		tasks.add(new Task(BankingState.WantToCheckBalance, 0, accountType));
	}
	
	public void msgIWantToOpenAccount(double amount, String accountType) {
		tasks.add(new Task(BankingState.WantToOpenAccount, amount, accountType));
	}
	
	public void msgIWantToDeposit(double amount, String accountType) {
		tasks.add(new Task(BankingState.WantToDeposit, amount, accountType));
	}
	
	public void msgIWantToWithdraw(double amount, String accountType) {
		tasks.add(new Task(BankingState.WantToWithdraw, amount, accountType));
	}
	
	public void msgIWantToGetALoan(double amount, String accountType) {
		tasks.add(new Task(BankingState.WantToGetALoan, amount, accountType));
	}
	
	public void msgIWantToPayBackLoan(double amount, String accountType) {
		tasks.add(new Task(BankingState.WantToPayBackLoan, amount, accountType));
	}
	
	public void msgIWantToDepositInBusinessAccount(double amount) {
		tasks.add(new Task(BankingState.WantToDepositToBusiness, amount, "business"));
	}
	
	public void msgHereIsMoney(double amount, String accountType, double remainingBalance) {
		if("personal".equals(accountType)) {
			myPerson.cashOnHand += amount;
		} else {
			myPerson.businessFunds += amount;
		}
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "Withdrew  $" + amount + " from " + accountType + ". Cash on hand: " + myPerson.cashOnHand + " Remaining balance is: " + remainingBalance);
		//print("Withdrew  $" + amount + " from " + accountType + ". Cash on hand: " + myPerson.cashOnHand + " Remaining balance is: " + remainingBalance);
		stateChanged();
	}
	
	public void msgHereIsBalance(double balance, String accountType) {
		int balanceBroke;
		int onHandBroke;
		if("personal".equals(accountType)) {
			balanceBroke = Double.compare(balance, PERSONAL_BROKE_AMOUNT);
			onHandBroke = Double.compare(myPerson.cashOnHand, PERSONAL_BROKE_AMOUNT);
			if(-1 == balanceBroke && -1 == onHandBroke) {
				tasks.add(new Task(BankingState.WantToGetALoan, PERSONAL_BROKE_BORROW_AMOUNT, accountType));
			}
		} else {
			balanceBroke = Double.compare(balance, BUSINESS_BROKE_AMOUNT);
			onHandBroke = Double.compare(myPerson.businessFunds, BUSINESS_BROKE_AMOUNT);
			if(-1 == balanceBroke && -1 == onHandBroke) {
				tasks.add(new Task(BankingState.WantToGetALoan, BUSINESS_BROKE_BORROW_AMOUNT, accountType));
			}
		}
		for(Loan l: loans) {
			int payLoan;
			if ("personal".equals(l.accountType))
				payLoan = Double.compare(balance - PERSONAL_BROKE_BORROW_AMOUNT, l.amount);
			else
				payLoan = Double.compare(balance - BUSINESS_BROKE_BORROW_AMOUNT, l.amount);
			if(1 == payLoan)
				tasks.add(new Task(BankingState.WantToAutoPayLoan, l.amount, l.accountType));
		}
		stateChanged();
	}
	
	public void msgLoanDenied(double amount, String accountType) {
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "$" + amount + " loan for " + accountType + " account was denied.");
		//print("$" + amount + " loan for " + accountType + " account was denied.");
		stateChanged();
	}
	
	public void msgLoanApproved(double amount, String accountType) {
		if("personal".equals(accountType)) {
			myPerson.cashOnHand += amount;
		} else {
			myPerson.businessFunds += amount;
		}
		loans.add(new Loan(amount, accountType));
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "$" + amount + " loan for " + accountType + " account was approved.");
		//print("$" + amount + " loan for " + accountType + " account was approved.");
		stateChanged();
	}
	
	public void msgLoanPaid(double amount, String accountType) {
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "$" + amount + " loan for " + accountType + " account was paid.");
		//print("$" + amount + " loan for " + accountType + " account was paid.");
		loans.remove(findLoanIndex(amount, accountType));
		stateChanged();
	}
	
	public void msgDepositSuccessful(double amount, String accountType, double remainingBalance) {
		AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "$" + amount + " was deposited successfully into account: " + accountType + ". Remaining balance: " + remainingBalance);
		//print("$" + amount + " was deposited successfully into account: " + accountType + ". Remaining balance: " + remainingBalance);
		stateChanged();
	}
	
	public void msgAtATM() {
		waitingResponse.release();
		state = CustomerState.AtAtm;
		stateChanged();
	}
	
	public void msgAnimationFinishedEnterBank() {
		waitingResponse.release();
		stateChanged();
	}
	
	public void msgLeftBank() {
		waitingResponse.release();
		stateChanged();
	}
	
	// Scheduler
	public boolean pickAndExecuteAnAction() {
		
		if(CustomerState.EnteringBank.equals(state)) {
			state = CustomerState.InBank;
			EnterBank();
			return true;
		} else if(CustomerState.InBank.equals(state)) {
			state = CustomerState.FindingATM;
			FindATM();
			return true;
		} else if(CustomerState.AtAtm.equals(state)) {
			for(Task t : tasks) {
				if(BankingState.WantToCheckBalance.equals(t.bs)){
					t.bs = BankingState.CheckingBalance;
					CheckBalance(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToOpenAccount.equals(t.bs)){
					t.bs = BankingState.OpeningAccount;
					OpenAccount(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToDeposit.equals(t.bs)){
					t.bs = BankingState.Depositing;
					DepositMoney(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToDepositToBusiness.equals(t.bs)){
					t.bs = BankingState.Depositing;
					DepositToBusiness(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToWithdraw.equals(t.bs)){
					t.bs = BankingState.Withdrawing;
					WithdrawMoney(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToPayBackLoan.equals(t.bs)){
					t.bs = BankingState.PayingLoan;
					PayLoan(t);
					return true;
				}
			}
			for(Task t : tasks) {
				if(BankingState.WantToGetALoan.equals(t.bs)){
					t.bs = BankingState.RequestingALoan;
					RequestLoan(t);
					return true;
				}
			}
		} else if(CustomerState.LeavingBank.equals(state)) {
			state = CustomerState.None;
			LeaveBank();
			return true;
		}
		
		for(Task t: tasks) {
			if(BankingState.WantToAutoPayLoan.equals(t.bs)) {
				t.bs = BankingState.PayingLoan;
				AutoPayLoan(t);
				return true;
			}
		}
		
		
		
		if(0 == tasks.size() && !CustomerState.None.equals(state)) {
			state = CustomerState.LeavingBank;
			return true;
		}
		
		return false;
	}
	
	//Actions
	private void EnterBank() {
		customerGui.DoEnterBank();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void FindATM() {
		customerGui.DoGoToATM();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void LeaveBank() {	
		timer.schedule(new TimerTask() {
			public void run() {
				Do("Leaving bank");
				customerGui.DoLeaveBank();
				try {
					waitingResponse.acquire();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				myPerson.msgDoneAtBank();
			}
		}, AT_ATM_TIME);
		
	}
	
	private void CheckBalance(Task t) {
		myPerson.bankTeller.msgCheckBalance(this, t.accountType);
		tasks.remove(t);
	}
	private void OpenAccount(Task t) {
		myPerson.bankTeller.msgOpenAccount(this, t.amount, t.accountType);
		tasks.remove(t);
	}
	private void DepositMoney(Task t) {
		int cashLimit = 0;
		if("personal".equals(t.accountType)) {
			cashLimit = Double.compare(myPerson.cashOnHand, t.amount);
		} else {
			cashLimit = Double.compare(myPerson.businessFunds, t.amount);
		}	
		if(-1 == cashLimit) {
			AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "Insufficient funds to deposit");
			//print("Insufficient funds to deposit");
		} else {
			myPerson.bankTeller.msgDepositMoney(this, t.amount, t.accountType);
			if("personal".equals(t.accountType)) {
				myPerson.cashOnHand -= t.amount;
			} else {
				myPerson.businessFunds -= t.amount;
			}
		}
		tasks.remove(t);
	}
	private void WithdrawMoney(Task t) {
		myPerson.bankTeller.msgWithdrawMoney(this, t.amount, t.accountType);
		tasks.remove(t);
	}
	private void RequestLoan(Task t) {
		myPerson.bankTeller.msgRequestLoan(this, t.amount, t.accountType);
		tasks.remove(t);
	}
	
	private void DepositToBusiness(Task t) {
		myPerson.bankTeller.msgDepositToAccount(this, myPerson.businessAccount, t.amount);
		myPerson.businessFunds -= t.amount;
		tasks.remove(t);
	}
	
	private void PayLoan(Task t) {
		int cashLimit = 0;
		if("personal".equals(t.accountType)) {
			cashLimit = Double.compare(myPerson.cashOnHand, t.amount);
		} else {
			cashLimit = Double.compare(myPerson.businessFunds, t.amount);
		}	
		if(-1 == cashLimit) {
			AlertLog.getInstance().logMessage(AlertTag.BANK_CUSTOMER, this.getName(), "Insufficient funds to pay off loan");
			//print("Insufficient funds to pay off loan");
		} else {
			myPerson.bankTeller.msgPayLoan(this, t.amount, t.accountType);
			if("personal".equals(t.accountType)) {
				myPerson.cashOnHand -= t.amount;
			} else {
				myPerson.businessFunds -= t.amount;
			}
		}
		tasks.remove(t);
	}
	
	private void AutoPayLoan(Task t) {
		myPerson.bankTeller.msgAutoPayLoan(this, t.amount, t.accountType);
		tasks.remove(t);
	}
	
	// Utilities
	
	private int findLoanIndex(double amount, String accountType) {
		int loanIndex = -1;
		for(int i = 0; i < loans.size(); i++) {
			if(amount == loans.get(i).amount && accountType.equals(loans.get(i).accountType)) {
				loanIndex = i;
			}
		}
		return loanIndex;
	}
	
	public BankCustomerGui getGui() {
		return customerGui;
	}
	public void setGui(BankCustomerGui g) {
		this.customerGui = g;
	}
	
	public void setBankBuilding(BankBuilding b) {
		this.bank = b;
	}
	
	public void setBusinessAccount(BankAccount b) {
		myPerson.businessAccount = b;
	}
	
	public void setPersonalAccount(BankAccount p) {
		myPerson.personalAccount = p;
	}
	
	public PersonAgent getPersonAgent() {
		return myPerson;
	}
	
}
