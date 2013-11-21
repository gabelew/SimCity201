package city;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import city.roles.BankCustomerRole;
import agent.Agent;
import city.interfaces.Bank;

public class BankAgent extends Agent implements Bank{
	
	class BankAccount {
		double currentBalance = 0.0;
		BankCustomerRole accountHolder;
		double owed = 0.0;
		String accountType;
		BankAccount(BankCustomerRole bcr, double initialDeposit, String accountType) {
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
				currentBalance = 0.0;
				return currentBalance;
			} else {
				currentBalance -= amount;
				return amount;
			}
		}
	}
	
	class Transaction {
		BankAccount customer;
		BankAccount recipient;
		double amount;
		TransactionState ts;
		String purpose;
		Transaction(TransactionState ts, double amount, BankAccount customer, BankAccount recipient, String purpose) {
			this.ts = ts;
			this.amount = amount;
			this.customer = customer;
			this.recipient = recipient;
			this.purpose = purpose;
		}
	}
	
	enum TransactionState {none, checkBalance, withdraw, deposit, transfer, loanRequested, loanPayment};
	List<BankAccount> accounts = new CopyOnWriteArrayList<BankAccount>();
	List<Transaction> transactions = new CopyOnWriteArrayList<Transaction>();
	String name;
	double fundsAvailable = 50000.0;
	final double customerLoanMax = 500;
	final double businessLoanMax = 1000;

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
	public void msgCheckBalance(BankCustomerRole bcr, String accountType) {
		BankAccount account = findBankAccount(bcr.getPersonAgent(),accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.checkBalance, 0, account, null, "balance"));
		}
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
		stateChanged();
	}
	
	public void msgOpenAccount(BankCustomerRole bcr, double initialDeposit, String accountType) {
		accounts.add(new BankAccount(bcr,initialDeposit,accountType));
		stateChanged();
	}
	
	public void msgDepositMoney(BankCustomerRole bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr.getPersonAgent(),accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.deposit, amount, account, null, "deposit"));
		}
		stateChanged();
	}
	
	public void msgWithdrawMoney(BankCustomerRole bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr.getPersonAgent(),accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.withdraw, amount, account, null, "withdraw"));
		}
		stateChanged();
	}
	
	public void msgRequestLoan(BankCustomerRole bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr.getPersonAgent(),accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.loanRequested, amount, account, null, "requestLoan"));
		}
		stateChanged();
	}
	
	public void msgPayLoan(BankCustomerRole bcr, double amount, String accountType) {
		BankAccount account = findBankAccount(bcr.getPersonAgent(),accountType);
		if(account != null) {
			transactions.add(new Transaction(TransactionState.loanPayment, amount, account, null, "payLoan"));
		}
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
		stateChanged();
	}


	// Scheduler
	
	public boolean pickAndExecuteAnAction() {
		for(Transaction t : transactions) {
			if(TransactionState.deposit == t.ts) {
				customerDeposit(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.withdraw == t.ts) {
				customerWithdrawal(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.loanPayment == t.ts) {
				customerLoanPayment(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.loanRequested == t.ts) {
				customerLoanRequest(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.transfer == t.ts) {
				customerTransfer(t);
				return true;
			}
		}
		for(Transaction t : transactions) {
			if(TransactionState.checkBalance == t.ts) {
				customerBalance(t);
				return true;
			}
		}
		return false;
	}
	
	// Actions
	
	private void customerDeposit(Transaction t) {
		t.ts = TransactionState.none;
		t.customer.deposit(t.amount);
		transactions.remove(findTransactionIndex(t));
	}
	
	private void customerWithdrawal(Transaction t) {
		t.ts = TransactionState.none;
		double withdrew = t.customer.withdraw(t.amount);
		t.customer.accountHolder.msgHereIsMoney(withdrew, t.customer.accountType, t.customer.currentBalance);
		transactions.remove(findTransactionIndex(t));
	}
	
	private void customerLoanPayment(Transaction t) {
		t.ts = TransactionState.none;
		fundsAvailable += t.amount;
		t.customer.owed -= t.amount;
		transactions.remove(findTransactionIndex(t));
	}
	
	private void customerLoanRequest(Transaction t) {
		t.ts = TransactionState.none;
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
		} else{
			t.customer.owed += t.amount;
			fundsAvailable -= t.amount;
			t.customer.accountHolder.msgLoanApproved(t.amount, t.customer.accountType);
		}
		transactions.remove(findTransactionIndex(t));
	}
	
	private void customerTransfer(Transaction t) {
		t.ts = TransactionState.none;
		int customerAccountLimit = Double.compare(t.customer.currentBalance, t.amount);
		if(-1 == customerAccountLimit) {
			t.customer.accountHolder.getPersonAgent().msgTransferFailure(t.recipient.accountHolder.getPersonAgent(), t.amount, t.purpose);
		} else{
			t.customer.withdraw(t.amount);
			t.recipient.deposit(t.amount);
			t.customer.accountHolder.getPersonAgent().msgTransferCompleted(t.recipient.accountHolder.getPersonAgent(), t.amount, t.purpose);
			t.recipient.accountHolder.getPersonAgent().msgTransferSuccessful(t.recipient.accountHolder.getPersonAgent(), t.amount, t.purpose);
		}
		transactions.remove(findTransactionIndex(t));
	}
	
	private void customerBalance(Transaction t) {
		t.ts = TransactionState.none;
		if("pbalance".equals(t.purpose)) {
			t.customer.accountHolder.getPersonAgent().msgHereIsBalance(t.customer.currentBalance, t.customer.accountType);
		} else {
			t.customer.accountHolder.msgHereIsBalance(t.customer.currentBalance, t.customer.accountType);
		}
		transactions.remove(findTransactionIndex(t));
	}
	
	// Utilities
	
	private BankAccount findBankAccount(PersonAgent p, String accountType) {
		BankAccount ba = null;
		for(BankAccount b : accounts) {
			if(p.equals(b.accountHolder) && accountType.equals(b.accountType)) {
				ba = b;
			}
		}
		return ba;
	}
	
	private int findTransactionIndex(Transaction t) {
		int transactionIndex = -1;
		for(int i = 0; i < transactions.size(); i++) {
			if(t.equals(transactions.get(i))) {
				transactionIndex = i;
			}
		}
		return transactionIndex;
	}
}
