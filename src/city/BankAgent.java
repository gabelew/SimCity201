package city;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import city.roles.BankCustomerRole;
import city.roles.BankRobberRole;
import agent.Agent;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.interfaces.Bank;
import city.interfaces.BankCustomer;

public class BankAgent extends Agent implements Bank{
	
	public class BankAccount {
		public double currentBalance = 0.0;
		public BankCustomer accountHolder;
		public double owed = 0.0;
		public String accountType;
		public BankAccount(BankCustomer bcr, double initialDeposit, String accountType) {
			this.accountHolder = bcr;
			this.currentBalance = initialDeposit;
			this.accountType = accountType;
		}
		void deposit(double amount) {
			currentBalance += amount;
		}
		double withdraw(double amount) {
			int result = Double.compare(currentBalance, amount);
			if(-1 == result) {
				double withdrew = currentBalance;
				currentBalance = 0.0;
				return withdrew;
			} else {
				currentBalance -= amount;
				return amount;
			}
		}
	}
	static final int ALGORITHM_MIN = 1, ALGORITHM_MAX = 4;
	
	
	enum HackState {pending,attemptToDefend };
	public class HackAttack {
		public BankRobberRole hacker;
		int hackAlgorithm = -1;
		HackState hs = HackState.pending;
		public HackAttack(BankRobberRole h, int hack) {
			this.hacker = h;
			this.hackAlgorithm = hack;
		}
	}
	public List<HackAttack>hackAttacks = new CopyOnWriteArrayList<HackAttack>();
	
	public class Transaction {
		public BankCustomer bc;
		public BankAccount customer;
		public BankAccount recipient;
		public double amount;
		public TransactionState ts;
		public String purpose;
		Transaction(TransactionState ts, double amount, BankAccount customer, BankAccount recipient, String purpose,
				BankCustomer b) {
			this.bc = b;
			this.ts = ts;
			this.amount = amount;
			this.customer = customer;
			this.recipient = recipient;
			this.purpose = purpose;
		}
		Transaction(TransactionState ts, double amount, BankAccount customer, BankAccount recipient, String purpose) {
			this.ts = ts;
			this.amount = amount;
			this.customer = customer;
			this.recipient = recipient;
			this.purpose = purpose;
		}
	}
	
	public enum TransactionState {none, checkBalance, withdraw, deposit, transfer, loanRequested, autoLoanPayment, loanPayment};
	public List<BankAccount> accounts = new CopyOnWriteArrayList<BankAccount>();
	public List<Transaction> transactions = new CopyOnWriteArrayList<Transaction>();
	String name;
	public double fundsAvailable = 50000.0;
	final double customerLoanMax = 500;
	final double businessLoanMax = 1000;
	public boolean testing = false;

	/**
	 * Constructor
	 */
	public BankAgent(String name) {
		this.name = name;
	}
	
	// Messages
	/**
	 * For BankCustomerRole to check balance.
	 * @param bcr
	 * @param accountType type of account
	 */
	public void msgCheckBalance(BankCustomer bcr, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.checkBalance, 0, account, null, "balance"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgThisIsAHackAttack(BankRobberRole brr, int hackAlgorithm){
		AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "We are getting hacked!");
		hackAttacks.add(new HackAttack(brr, hackAlgorithm));
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	/**
	 * For PersonAgent to check balance.
	 * @param p
	 * @param accountType type of account
	 */
	public void msgCheckBalance(PersonAgent p, String accountType) {
		BankAccount account = findBankAccount(p,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.checkBalance, 0, account, null, "pbalance"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgOpenAccount(BankCustomer bcr, double initialDeposit, String accountType) {
		accounts.add(new BankAccount(bcr,initialDeposit,accountType));
		if(bcr instanceof BankCustomerRole) {
			BankCustomerRole bc = (BankCustomerRole) bcr;
			BankAccount b = findBankAccount(bcr, accountType);
			if("personal".equals(accountType))
				bc.setPersonalAccount(b);
			else
				bc.setBusinessAccount(b);
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgDepositMoney(BankCustomer bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.deposit, amount, account, null, "deposit", bcr));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgDepositToAccount(BankCustomer bc, BankAccount businessAccount, double amount) {
		BankAccount account = findAccount(businessAccount);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.deposit, amount, account, null, "deposit", bc));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgWithdrawMoney(BankCustomer bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.withdraw, amount, account, null, "withdraw"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgRequestLoan(BankCustomer bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.loanRequested, amount, account, null, "requestLoan"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgPayLoan(BankCustomer bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.loanPayment, amount, account, null, "payLoan"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	public void msgAutoPayLoan(BankCustomer bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr,accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.autoLoanPayment, amount, account, null, "payLoan"));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}
	
	
	public void msgTransferFunds(PersonAgent sender, PersonAgent recipient, 
			double amount, String senderAccountType, 
			String recipientAccountType, String purpose) {
		BankAccount senderAccount = findBankAccount(sender,senderAccountType);
		BankAccount recipientAccount = findBankAccount(recipient,recipientAccountType);
		if(senderAccount != null && recipientAccount != null) {
			transactions.add(new Transaction(TransactionState.transfer, amount, senderAccount, recipientAccount, purpose));
		}
		if(0 == getStateChangePermits())
			stateChanged();
	}


	// Scheduler
	
	public boolean pickAndExecuteAnAction() {
		
		for(HackAttack h : hackAttacks) {
			if(HackState.pending == h.hs) {
				h.hs = HackState.attemptToDefend;
				defendHack(h);
				return true;
			}
		}
		
		for(Transaction t : transactions) {
			if(TransactionState.deposit == t.ts) {
				t.ts = TransactionState.none;
				customerDeposit(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.withdraw == t.ts) {
				t.ts = TransactionState.none;
				customerWithdrawal(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.loanPayment == t.ts) {
				t.ts = TransactionState.none;
				customerLoanPayment(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.autoLoanPayment == t.ts) {
				t.ts = TransactionState.none;
				customerAutoLoanPayment(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.loanRequested == t.ts) {
				t.ts = TransactionState.none;
				customerLoanRequest(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.transfer == t.ts) {
				t.ts = TransactionState.none;
				customerTransfer(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.checkBalance == t.ts) {
				t.ts = TransactionState.none;
				customerBalance(t);
				return true;
			}
		}
		return false;
	}
	
	// Actions
	
	private void defendHack(HackAttack h) {
		int defenseWeakness = randInt(ALGORITHM_MIN, ALGORITHM_MAX);
		if(defenseWeakness == h.hackAlgorithm) {
			double stolenAmount = h.hackAlgorithm * 500;
			h.hacker.msgHackSuccessful(stolenAmount);
			fundsAvailable -= stolenAmount;
			AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Failed to defend against hack. Lost " + stolenAmount);
		} else {
			h.hacker.msgHackDefended();
			AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Successfully protected against the hack!");
		}
		hackAttacks.remove(h);
	}
	
	private void customerDeposit(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		t.customer.deposit(t.amount);
		t.customer.currentBalance = (Math.round(100*t.customer.currentBalance) / ((double)100));
		t.bc.msgDepositSuccessful(t.amount, t.customer.accountType, t.customer.currentBalance);
		transactions.remove(t);
	}
	
	private void customerWithdrawal(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		double withdrew = t.customer.withdraw(t.amount);
		t.customer.currentBalance = (Math.round(100*t.customer.currentBalance) / ((double)100));
		t.customer.accountHolder.msgHereIsMoney(withdrew, t.customer.accountType, t.customer.currentBalance);
		transactions.remove(t);
	}
	
	private void customerLoanPayment(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		fundsAvailable += t.amount;
		fundsAvailable = (Math.round(100*fundsAvailable) / ((double)100));
		t.customer.owed -= t.amount;
		t.customer.owed = (Math.round(100*t.customer.owed) / ((double)100));
		int owedBalance = Double.compare(t.customer.owed, 0);
		if(-1 == owedBalance) {
			t.customer.owed = 0;
		}
		if(!testing)
			AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Received loan payment from " + ((BankCustomerRole)t.customer.accountHolder).getName() + " for $" + t.amount);
		t.customer.accountHolder.msgLoanPaid(t.amount, t.customer.accountType);
		transactions.remove(t);
	}
	
	private void customerAutoLoanPayment(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		t.customer.currentBalance -= t.amount;
		fundsAvailable += t.amount;
		fundsAvailable = (Math.round(100*fundsAvailable) / ((double)100));
		t.customer.owed -= t.amount;
		t.customer.owed = (Math.round(100*t.customer.owed) / ((double)100));
		int owedBalance = Double.compare(t.customer.owed, 0);
		if(-1 == owedBalance) {
			t.customer.owed = 0;
		}
		if(!testing)
			AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Received loan payment from " + ((BankCustomerRole)t.customer.accountHolder).getName() + " for $" + t.amount);
		t.customer.accountHolder.msgLoanPaid(t.amount, t.customer.accountType);
		transactions.remove(t);
	}
	
	private void customerLoanRequest(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		double canLoan = 0.0;
		if("personal".equals(t.customer.accountType)) {
			canLoan = customerLoanMax - t.customer.owed;
		} else {
			canLoan = businessLoanMax - t.customer.owed;
		}
		int loanLimit = Double.compare(canLoan, t.amount);
		int bankLimit = Double.compare(fundsAvailable, t.amount);
		if(-1 == loanLimit || -1 == bankLimit) {
			t.customer.accountHolder.msgLoanDenied(t.amount, t.customer.accountType);
			if(!testing)
				AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Denied loan request from " + ((BankCustomerRole)t.customer.accountHolder).getName() + " for $" + t.amount);
		} else{
			
			t.customer.owed += t.amount;
			t.customer.owed = (Math.round(100*t.customer.owed) / ((double)100));
			fundsAvailable -= t.amount;
			fundsAvailable = (Math.round(100*fundsAvailable) / ((double)100));
			t.customer.accountHolder.msgLoanApproved(t.customer.owed, t.customer.accountType);
			if(!testing)
				AlertLog.getInstance().logMessage(AlertTag.BANK_SYSTEM, this.getName(), "Approved loan request from " + ((BankCustomerRole)t.customer.accountHolder).getName() + " for $" + t.amount);
		}
		transactions.remove(t);
	}
	
	private void customerTransfer(Transaction t) {
		t.amount = (Math.round(100*t.amount) / ((double)100));
		int customerAccountLimit = Double.compare(t.customer.currentBalance, t.amount);
		if(-1 == customerAccountLimit) {
			BankCustomerRole r = (BankCustomerRole)t.customer.accountHolder;
			r.getPersonAgent().msgTransferFailure(t.recipient.accountHolder.getPersonAgent(), t.amount, t.purpose);
		} else{
			BankCustomerRole r = (BankCustomerRole)t.customer.accountHolder;
			PersonAgent sender = r.getPersonAgent();
			PersonAgent receiver = t.recipient.accountHolder.getPersonAgent();
			t.customer.withdraw(t.amount);			
			t.customer.currentBalance = (Math.round(100*t.customer.currentBalance) / ((double)100));
			t.recipient.deposit(t.amount);		
			t.recipient.currentBalance = (Math.round(100*t.recipient.currentBalance) / ((double)100));
			receiver.msgTransferCompleted(sender, t.amount, t.purpose);
			sender.msgTransferSuccessful(receiver, t.amount, t.purpose);
		}
		transactions.remove(t);
	}
	
	private void customerBalance(Transaction t) {
		if("pbalance".equals(t.purpose)) {
			BankCustomerRole r = (BankCustomerRole)t.customer.accountHolder;
			r.getPersonAgent().msgHereIsBalance(t.customer.currentBalance, t.customer.accountType);
		} else {
			t.customer.accountHolder.msgHereIsBalance(t.customer.currentBalance, t.customer.accountType);
		}
		transactions.remove(t);
	}
	
	// Utilities
	public static int randInt(int min, int max) {
	    Random i = new Random();
	    return i.nextInt((max - min) + 1) + min;
	}
	
	private BankAccount findAccount(BankAccount b) {
		BankAccount ba = null;
		for (BankAccount a : accounts) {
			if(b.equals(a)) {
				ba = a;
			}
		}
		return ba;
	}
	
	private BankAccount findBankAccount(BankCustomer bc, String accountType) {
		BankAccount ba = null;
		for(BankAccount b : accounts) {
			if(bc.equals(b.accountHolder) && accountType.equals(b.accountType)) {
				ba = b;
			}
		}
		return ba;
	}
	
	private BankAccount findBankAccount(PersonAgent p, String accountType) {
		BankAccount ba = null;
		for(BankAccount b : accounts) {
			BankCustomerRole r = (BankCustomerRole)b.accountHolder;
			if(p.equals(r.getPersonAgent()) && accountType.equals(b.accountType)) {
				ba = b;
			}
		}
		return ba;
	}
}
