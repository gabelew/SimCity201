package city.test;

import junit.framework.TestCase;
import city.BankAgent.TransactionState;
import city.test.mock.MockBankCustomer;
import city.BankAgent;

public class BankTest extends TestCase{
	BankAgent bank;
	MockBankCustomer customer, customer2;
	//MockPerson person
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		//person = new MockPerson("Stanley");
		customer = new MockBankCustomer("Whitney");	
		customer2 = new MockBankCustomer("Tony");
		bank = new BankAgent("BankOfSimCity");
	}	
	
	public void testOneCustomerOpenAccountAndCheckBalance() {
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
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
	}
	
	public void testOneCustomerOpenAccountAndDepositMoney() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 290.29, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type business. It isn't.", "business".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 290.29 in it. It doesn't.", 0 == Double.compare(290.29, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to deposit money
		 */
		bank.msgDepositMoney(customer, 81.50, "business");
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state deposit. It doesn't.", TransactionState.deposit.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to deposit into customer's account. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer's account should have 371.79 in it. It doesn't.", 0 == Double.compare(371.79, bank.accounts.get(0).currentBalance));
		
		assertTrue("MockBankCustomer should have logged \"Received msgDepositSuccessful\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgDepositSuccessful from Bank for amount: " 
				+ "81.5 for account type: business Remaining balance: 371.79"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
		
	}
	
	public void testOneCustomerOpenAccountAndWithdrawMoney() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 150.86, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 150.86 in it. It doesn't.", 0 == Double.compare(150.86, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to withdraw money
		 */
		bank.msgWithdrawMoney(customer, 5.22, "personal");
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state withdraw. It doesn't.", TransactionState.withdraw.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to withdraw from customer's account. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer's account should have 145.64 in it. It doesn't.", 0 == Double.compare(145.64, bank.accounts.get(0).currentBalance));
		
		assertTrue("MockBankCustomer should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsMoney from Bank for amount: " 
				+ "5.22 for account type: personal Remaining balance: 145.64"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
				
	}
	
	public void testOneCustomerOpenPersonalAccountAndRequestLoanAndPayBackLoan() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 852.35, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 852.35 in it. It doesn't.", 0 == Double.compare(852.35, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to request loan
		 */
		bank.msgRequestLoan(customer, 500, "personal");
		
		// check postconditions for step 2 and preconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanRequested. It doesn't.", TransactionState.loanRequested.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should owe 500 now. It doesn't.", 500 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should be 49500 now. It isn't.", 49500 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanApproved\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanApproved from Bank for amount: " 
				+ "500.0 for account type: personal"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
		
		/**
		 * Step 3: Bank gets a message from customer to pay back loan
		 */
		bank.msgPayLoan(customer, 500, "personal");
		
		// check postconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanPayment. It doesn't.", TransactionState.loanPayment.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should owe 0 now. It doesn't.", 0 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should be 50000 now. It isn't.", 50000 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanPaid\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanPaid from Bank for amount: " 
				+ "500.0 for account type: personal"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
	}
	
	public void testOneCustomerOpenAccountAndTriesToWithdrawMoreThanBalance() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 49.20, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type businessl. It isn't.", "business".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 49.20 in it. It doesn't.", 0 == Double.compare(49.20, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to withdraw money, but their balance is less than the money they requested.
		 */
		bank.msgWithdrawMoney(customer, 55.66, "business");
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state withdraw. It doesn't.", TransactionState.withdraw.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to withdraw from customer's account. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer's account should have 0.0 in it. It doesn't.", 0 == Double.compare(0, bank.accounts.get(0).currentBalance));
		
		assertTrue("MockBankCustomer should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsMoney from Bank for amount: " 
				+ "49.2 for account type: business Remaining balance: 0.0"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
	}
	
	public void testOneCustomerOpenBusinessAccountAndRequestLoanMoreThanAllowed() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 922.50, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type business. It isn't.", "business".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 922.50 in it. It doesn't.", 0 == Double.compare(922.50, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to request loan
		 */
		bank.msgRequestLoan(customer, 1000.1, "business");
		
		// check postconditions for step 2 and preconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanRequested. It doesn't.", TransactionState.loanRequested.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should owe 0.0. It doesn't.", 0 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should be 50000. It isn't.", 50000 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanDenied\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanDenied from Bank for amount: " 
				+ "1000.1 for account type: business"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
		
	}
	
	public void testCustomerOpensAccountAndTriesToLoanBankInsufficientFunds() {
		//setUp() runs first before this test!

		bank.fundsAvailable = 499.99;
		
		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 35.35, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 35.35 in it. It doesn't.", 0 == Double.compare(35.35, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		assertTrue("Bank's funds available should be 499.99 now. It isn't.", 499.99 == bank.fundsAvailable);
		
		/**
		 * Step 2: Bank gets a message from a customer to request loan
		 */
		bank.msgRequestLoan(customer, 500, "personal");
		
		// check postconditions for step 2 and preconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanRequested. It doesn't.", TransactionState.loanRequested.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should owe 0 now. It doesn't.", 0 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should be 499.99 now. It isn't.", 499.99 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanDenied\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanDenied from Bank for amount: " 
				+ "500.0 for account type: personal"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
				
	}
	
	public void testCustomerOpenAccountAndRequestsTwoLoansExceedingLimit() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 4913.92, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type business. It isn't.", "business".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 4913.92 in it. It doesn't.", 0 == Double.compare(4913.92, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to request loan
		 */
		bank.msgRequestLoan(customer, 520, "business");
		
		// check postconditions for step 2 and preconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanRequested. It doesn't.", TransactionState.loanRequested.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should owe 520 now. It doesn't.", 520 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should be 49480 now. It isn't.", 49480 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanApproved\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanApproved from Bank for amount: " 
				+ "520.0 for account type: business"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
		
		/**
		 * Step 3: Bank gets another message from a customer to request second loan
		 */
		bank.msgRequestLoan(customer, 480.1, "business");
		
		// check postconditions for step 3
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state loanRequested. It doesn't.", TransactionState.loanRequested.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer should still owe 520. It doesn't.", 520 == bank.accounts.get(0).owed);
		assertTrue("Bank's funds available should still be 49480. It isn't.", 49480 == bank.fundsAvailable);
		
		assertTrue("MockBankCustomer should have logged \"Received msgLoanDenied\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgLoanDenied from Bank for amount: " 
				+ "480.1 for account type: business"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
				
	}
	
	public void testTwoCustomersOpenDifferentAccountsOneWithdrawsOneDeposits() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer2.log.toString(), 0, customer2.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 150.86, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type personal. It isn't.", "personal".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 150.86 in it. It doesn't.", 0 == Double.compare(150.86, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from customer2 to open account.
		 */
		bank.msgOpenAccount(customer2, 96.25, "business");
		
		// check postconditions for step 2 and preconditions for step 3
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 2 accounts in it. It doesn't.", bank.accounts.size(), 2);	
		assertTrue("Account should belong to the customer. It doesnt.", customer2.equals(bank.accounts.get(1).accountHolder));
		assertTrue("Account should be of account type business. It isn't.", "business".equals(bank.accounts.get(1).accountType));
		assertTrue("Account balance should contain 96.25 in it. It doesn't.", 0 == Double.compare(96.25, bank.accounts.get(1).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer2.log.toString(), 0, customer2.log.size());
		
		
		/**
		 * Step 3: Bank gets 2 messages from customer and customer2 to withdraw money
		 */
		bank.msgWithdrawMoney(customer, 5.22, "personal");
		bank.msgDepositMoney(customer2, 52.55, "business");
		
		// check postconditions for step 2
		assertEquals("Bank should have 2 transactions in it. It doesn't.", bank.transactions.size(), 2);
		assertTrue("First transaction should have a state withdraw. It doesn't.", TransactionState.withdraw.equals(bank.transactions.get(0).ts));
		assertTrue("First transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Second transaction should have a state deposit. It doesn't.", TransactionState.deposit.equals(bank.transactions.get(1).ts));
		assertTrue("Second transaction should belong to customer2. It doesn't.", customer2.equals(bank.transactions.get(1).customer.accountHolder));
		
		assertTrue("Bank scheduler should return true for the deposit that is done first in the scheduler. It doesn't.", bank.pickAndExecuteAnAction());
		assertTrue("Customer's account should have 148.8 in it. It doesn't.", 0 == Double.compare(148.8, bank.accounts.get(1).currentBalance));
		
		assertTrue("MockBankCustomer should have logged \"Received msgDepositSuccessful\" but didn't. His log reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgDepositSuccessful from Bank for amount: " 
				+ "52.55 for account type: business Remaining balance: 148.8"));
		
		assertEquals("Bank should have 1 transactions in it. It doesn't.", bank.transactions.size(), 1);
		
		assertTrue("Bank scheduler should return true for the withdraw next. It doesn't.", bank.pickAndExecuteAnAction());		
		assertTrue("Customer's account should have 145.64 in it. It doesn't.", 0 == Double.compare(145.64, bank.accounts.get(0).currentBalance));
		assertTrue("MockBankCustomer should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsMoney from Bank for amount: " 
				+ "5.22 for account type: personal Remaining balance: 145.64"));
		
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
	}
	
	public void testOneCustomerDepositIntoSpecificAccount() {
		//setUp() runs first before this test!

		// check preconditions
		assertEquals("Bank should have no accounts in it. It does.", bank.accounts.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false. It doesn't.", bank.pickAndExecuteAnAction());
		
		/**
		 * Step 1: Bank gets a message from a customer to open an account.		
		 */
		bank.msgOpenAccount(customer, 150.86, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertFalse("Bank scheduler should return false since account is opened in the message. It doesn't.", bank.pickAndExecuteAnAction());	
		assertEquals("Bank should have 1 account in it. It doesn't.", bank.accounts.size(), 1);	
		assertTrue("Account should belong to the customer. It doesnt.", customer.equals(bank.accounts.get(0).accountHolder));
		assertTrue("Account should be of account type business. It isn't.", "business".equals(bank.accounts.get(0).accountType));
		assertTrue("Account balance should contain 150.86 in it. It doesn't.", 0 == Double.compare(150.86, bank.accounts.get(0).currentBalance));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertEquals("MockBankCustomer's log should be empty before bank's scheduler returns true. Instead, it reads: " + customer.log.toString(), 0, customer.log.size());
		
		/**
		 * Step 2: Bank gets a message from a customer to deposit money
		 */
		bank.msgDepositToAccount(customer, bank.accounts.get(0), 32.0);
		
		// check postconditions for step 2
		assertEquals("Bank should have 1 transaction in it. It doesn't.", bank.transactions.size(), 1);
		assertTrue("Transaction should have a state deposit. It doesn't.", TransactionState.deposit.equals(bank.transactions.get(0).ts));
		assertTrue("Transaction should belong to customer. It doesn't.", customer.equals(bank.transactions.get(0).customer.accountHolder));
		assertTrue("Bank scheduler should return true. It needs to withdraw from customer's account. It doesn't.", bank.pickAndExecuteAnAction());
		
		assertTrue("Customer's account should have 182.86 in it. It doesn't.", 0 == Double.compare(182.86, bank.accounts.get(0).currentBalance));
		
		assertTrue("MockBankCustomer should have logged \"Received msgDepositSuccessful\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgDepositSuccessful from Bank for amount: " 
				+ "32.0 for account type: business Remaining balance: 182.86"));
		assertEquals("Bank should have no transactions in it. It does.", bank.transactions.size(), 0);
		assertFalse("Bank scheduler should return false with nothing left to do. It doesn't.", bank.pickAndExecuteAnAction());	
				
	}
}
