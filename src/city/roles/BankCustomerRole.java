package city.roles;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import bank.gui.BankCustomerGui;
import city.BankAgent;
import city.PersonAgent;
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
	
	public List<Task> tasks = new CopyOnWriteArrayList<Task>();
	public Bank bank;
	enum BankingState{WantToCheckBalance, WantToOpenAccount, WantToDeposit, WantToWithdraw, 
		WantToGetALoan, WantToPayBackLoan, CheckingBalance, OpeningAccount, Depositing, Withdrawing, 
		RequestingALoan, PayingLoan };
	public enum CustomerState {None, EnteringBank, InBank, FindingATM, AtAtm, LeavingBank};
	public CustomerState state = CustomerState.None;
	private BankCustomerGui customerGui;
	private Semaphore waitingResponse = new Semaphore(0,true);
	
	public BankCustomerRole(PersonAgent p) {
		super(p);
	}
	
	
	// Messages
	public void goingToBank() {
		state = CustomerState.EnteringBank;
		stateChanged();
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
	
	public void msgHereIsMoney(double amount, String accountType, double remainingBalance) {
		if("personal".equals(accountType)) {
			myPerson.cashOnHand += amount;
		} else {
			myPerson.businessFunds += amount;
		}
		print("Withdraw for $" + amount + " from " + accountType + " account was successful. Remaining balance is: " + remainingBalance);
		stateChanged();
	}
	
	public void msgHereIsBalance(double balance, String accountType) {
		print("Current balance for " + accountType + " account is: $" + balance);
		stateChanged();
	}
	
	public void msgLoanDenied(double amount, String accountType) {
		print("$" + amount + " loan for " + accountType + " account was denied.");
		stateChanged();
	}
	
	public void msgLoanApproved(double amount, String accountType) {
		if("personal".equals(accountType)) {
			myPerson.cashOnHand += amount;
		} else {
			myPerson.businessFunds += amount;
		}
		print("$" + amount + " loan for " + accountType + " account was approved.");
		stateChanged();
	}
	
	public void msgLoanPaid(double amount, String accountType) {
		print("$" + amount + " loan for " + accountType + " account was paid.");
		stateChanged();
	}
	
	public void msgDepositSuccessful(double amount, String accountType, double remainingBalance) {
		print("$" + amount + " was deposited successfully into account: " + accountType + ". Remaining balance: " + remainingBalance);
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
		
		if(0 == tasks.size()) {
			state = CustomerState.LeavingBank;
			return true;
		}
		
		return false;
	}
	
	//Actions
	private void EnterBank() {
		Do("Entering bank");
		customerGui.DoEnterBank();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void FindATM() {
		Do("Going to ATM");
		customerGui.DoGoToATM();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void LeaveBank() {
		Do("Leaving bank.");
		customerGui.DoLeaveBank();
		try {
			waitingResponse.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		myPerson.msgDoneAtBank();
	}
	
	private void CheckBalance(Task t) {
		bank.msgCheckBalance(this, t.accountType);
		tasks.remove(findTaskIndex(t));
	}
	private void OpenAccount(Task t) {
		bank.msgOpenAccount(this, t.amount, t.accountType);
		tasks.remove(findTaskIndex(t));
	}
	private void DepositMoney(Task t) {
		int cashLimit = 0;
		if("personal".equals(t.accountType)) {
			cashLimit = Double.compare(myPerson.cashOnHand, t.amount);
		} else {
			cashLimit = Double.compare(myPerson.businessFunds, t.amount);
		}	
		if(-1 == cashLimit) {
			print("Insufficient funds to deposit");
		} else {
			bank.msgDepositMoney(this, t.amount, t.accountType);
			if("personal".equals(t.accountType)) {
				myPerson.cashOnHand -= t.amount;
			} else {
				myPerson.businessFunds -= t.amount;
			}
		}
		tasks.remove(findTaskIndex(t));
	}
	private void WithdrawMoney(Task t) {
		bank.msgWithdrawMoney(this, t.amount, t.accountType);
		tasks.remove(findTaskIndex(t));
	}
	private void RequestLoan(Task t) {
		bank.msgRequestLoan(this, t.amount, t.accountType);
		tasks.remove(findTaskIndex(t));
	}
	private void PayLoan(Task t) {
		int cashLimit = 0;
		if("personal".equals(t.accountType)) {
			cashLimit = Double.compare(myPerson.cashOnHand, t.amount);
		} else {
			cashLimit = Double.compare(myPerson.businessFunds, t.amount);
		}	
		if(-1 == cashLimit) {
			print("Insufficient funds to deposit");
		} else {
			bank.msgPayLoan(this, t.amount, t.accountType);
			if("personal".equals(t.accountType)) {
				myPerson.cashOnHand -= t.amount;
			} else {
				myPerson.businessFunds -= t.amount;
			}
		}
		tasks.remove(findTaskIndex(t));
	}
	
	// Utilities
	private int findTaskIndex(Task t) {
		int taskIndex = -1;
		for(int i = 0; i < tasks.size(); i++) {
			if(t.equals(tasks.get(i))) {
				taskIndex = i;
			}
		}
		return taskIndex;
	}
	public BankCustomerGui getGui() {
		return customerGui;
	}
	public void setGui(BankCustomerGui g) {
		customerGui = g;
	}
	
}
