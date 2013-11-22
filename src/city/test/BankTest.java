package city.test;

import junit.framework.TestCase;
import city.BankAgent.TransactionState;
import city.PersonAgent;
import city.roles.BankCustomerRole.CustomerState;
import city.test.mock.MockBankCustomer;
import city.BankAgent;

public class BankTest extends TestCase{
	BankAgent bank;
	PersonAgent person;
	MockBankCustomer customer;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("Stanley", 550.0, 1000.0);
		customer = new MockBankCustomer("Whitney");	
		bank = new BankAgent("BankOfSimCity");
	}	
	
	public void testOpenAccountAndCheckBalance() {
		//setUp() runs first before this test!
		
		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler is called. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 480.55, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 480.55 in it. It doesn't.", 0 == Double.compare(480.55, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler is called. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to check their balance
		 */
		bank.msgCheckBalance(customer, "personal");
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state checkBalance. It doesn't.", TransactionState.checkBalance.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to return customer's balance. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("MockBankCustomer should have logged \"Received msgHereIsBalance\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsBalance from "
						+ "Bank for account type: personal Current balance: 480.55"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
	}
}
