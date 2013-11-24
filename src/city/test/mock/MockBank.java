package city.test.mock;

import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import restaurant.test.mock.Mock;
import city.PersonAgent;
import city.interfaces.Bank;
import city.interfaces.BankCustomer;
import city.roles.BankCustomerRole;

public class MockBank extends Mock implements Bank{
	
	public EventLog log = new EventLog();
	
	public MockBank(String name) {
		super(name);
	}

	@Override
	public void msgCheckBalance(BankCustomer bcr, String accountType) {
		log.add(new LoggedEvent("Received msgCheckBalance from BankCustomerRole for account type: " + accountType));
	}
	
	@Override
	public void msgCheckBalance(PersonAgent person, String accountType) {
		log.add(new LoggedEvent("Received msgCheckBalance from PersonAgent for account type: " + accountType));	
	}

	@Override
	public void msgOpenAccount(BankCustomer bcr, double initialDeposit,
			String accountType) {
		log.add(new LoggedEvent("Received msgOpenAccount from BankCustomerRole for initial deposit: " + initialDeposit + " and account type: " + accountType));
	}

	@Override
	public void msgDepositMoney(BankCustomer bcr, double amount,
			String accountType) {
		log.add(new LoggedEvent("Received msgDepositMoney from BankCustomerRole for amount: " + amount + " and account type: " + accountType));
	}

	@Override
	public void msgWithdrawMoney(BankCustomer bcr, double amount,
			String accountType) {
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankCustomerRole for amount: " + amount + " and account type: " + accountType));
	}

	@Override
	public void msgRequestLoan(BankCustomer bcr, double amount,
			String accountType) {
		log.add(new LoggedEvent("Received msgRequestLoan from BankCustomerRole for amount: " + amount + " and account type: " + accountType));
	}

	@Override
	public void msgPayLoan(BankCustomer bcr, double amount,
			String accountType) {
		log.add(new LoggedEvent("Received msgPayLoan from BankCustomerRole for amount: " + amount + " and account type: " + accountType));
	}

	@Override
	public void msgTransferFunds(PersonAgent sender, PersonAgent recipient,
			double amount, String senderAccountType,
			String recipientAccountType, String purpose) {
		log.add(new LoggedEvent("Received msgTransferFunds from PersonAgent for amount: " + amount + " and account type: " + senderAccountType
				+ "to " + recipient.getName() + " with account type: " + recipientAccountType + " for " + purpose));
	}

	@Override
	public void msgAutoPayLoan(BankCustomer bc, double amount,
			String accountType) {
		log.add(new LoggedEvent("Received msgAutoPayLoan from BankCustomer for amount: " 
			+ amount + " and account type: " + accountType));
		
	}
	
}
