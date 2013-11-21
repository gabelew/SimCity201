package city.interfaces;

import city.PersonAgent;
import city.roles.BankCustomerRole;

public interface Bank {
	public abstract void msgCheckBalance(BankCustomerRole bcr, String accountType);
	public abstract void msgCheckBalance(PersonAgent person, String accountType);
	public abstract void msgOpenAccount(BankCustomerRole bcr, double initialDeposit, String accountType);
	public abstract void msgDepositMoney(BankCustomerRole bcr, double amount, String accountType);
	public abstract void msgWithdrawMoney(BankCustomerRole bcr, double amount, String accountType);
	public abstract void msgRequestLoan(BankCustomerRole bcr, double amount, String accountType);
	public abstract void msgPayLoan(BankCustomerRole bcr, double amount, String accountType);
	public abstract void msgTransferFunds(PersonAgent sender, PersonAgent recipient, double amount, 
			String senderAccountType, String recipientAccountType, String purpose);
}
