package city.test.mock;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import restaurant.test.mock.Mock;
import city.interfaces.BankCustomer;

public class MockBankCustomer extends Mock implements BankCustomer{

	public EventLog log = new EventLog();
	
	public MockBankCustomer(String name) {
		super(name);
	}

	@Override
	public void msgHereIsMoney(double amount, String accountType,
			double remainingBalance) {
		log.add(new LoggedEvent("Received msgHereIsMoney from Bank for amount: " + amount 
				+ " for account type: " + accountType + " Remaining balance: " + remainingBalance));	
	}

	@Override
	public void msgHereIsBalance(double balance, String accountType) {
		log.add(new LoggedEvent("Received msgHereIsBalance from Bank for account type: " + accountType 
				+ " Current balance: " + balance));	
	}

	@Override
	public void msgLoanDenied(double amount, String accountType) {
		log.add(new LoggedEvent("Received msgLoanDenied from Bank for amount: " + amount 
				+ " for account type: " + accountType));
	}

	@Override
	public void msgLoanApproved(double amount, String accountType) {
		log.add(new LoggedEvent("Received msgLoanApproved from Bank for amount: " + amount 
				+ " for account type: " + accountType));
	}

	@Override
	public void msgDepositSuccessful(double amount, String accountType,
			double remainingBalance) {
		log.add(new LoggedEvent("Received msgDepositSuccessful from Bank for amount: " + amount 
				+ " for account type: " + accountType + " Remaining balance: " + remainingBalance));	
	}

	@Override
	public void msgLoanPaid(double amount, String accountType) {
		log.add(new LoggedEvent("Received msgLoanPaid from Bank for amount: " + amount 
				+ " for account type: " + accountType));
	}

}
