package city.interfaces;

public interface BankCustomer {
	public abstract void msgHereIsMoney(double amount, String accountType, double remainingBalance);
	public abstract void msgHereIsBalance(double balance, String accountType);
	public abstract void msgLoanDenied(double amount, String accountType);
	public abstract void msgLoanApproved(double amount, String accountType);
	public abstract void msgDepositSuccessful(double amount, String accountType, double remainingBalance);
	public abstract void msgLoanPaid(double amount, String accountType);
}
