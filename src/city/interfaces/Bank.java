package city.interfaces;

import city.PersonAgent;
import city.roles.BankCustomerRole;

public interface Bank {
	public abstract void msgCheckBalance(BankCustomer bcr, String accountType);
	public abstract void msgCheckBalance(PersonAgent person, String accountType);
	public abstract void msgOpenAccount(BankCustomer bcr, double initialDeposit, String accountType);
	public abstract void msgDepositMoney(BankCustomer bcr, double amount, String accountType);
	public abstract void msgWithdrawMoney(BankCustomer bcr, double amount, String accountType);
	public abstract void msgRequestLoan(BankCustomer bcr, double amount, String accountType);
	public abstract void msgPayLoan(BankCustomer bcr, double amount, String accountType);
	public abstract void msgTransferFunds(PersonAgent sender, PersonAgent recipient, double amount, 
			String senderAccountType, String recipientAccountType, String purpose);
	public abstract void msgAutoPayLoan(BankCustomer bc,
			double amount, String accountType);
}
