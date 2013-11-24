package city.test;

import junit.framework.TestCase;
import city.PersonAgent;
import city.roles.BankCustomerRole;
import city.roles.BankCustomerRole.CustomerState;
import city.test.mock.MockBank;


public class BankCustomerRoleTest extends TestCase{
	BankCustomerRole customer;
	MockBank bank;
	PersonAgent person;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("Stanley", 550.0, 1000.0);
		customer = new BankCustomerRole(person);	
		bank = new MockBank("BankOfSimCity");
	}	
	
	public void testOpenAccountAndDeposit() {
		//setUp() runs first before this test!
		customer.bankTeller = bank;
		
		// check preconditions
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer has a task to open an account with the bank
		 */
		customer.msgIWantToOpenAccount(200, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		
		assertTrue("Customer's scheduler should have returned true. It needs to open an account with the bank. It doesn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("MockBank should have logged \"Received msgOpenAccount\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgOpenAccount from "
						+ "BankCustomerRole for initial deposit: 200.0 and account type: personal"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		
		/**
		 * Step 2: Customer has a task to deposit money in her account
		 */
		customer.msgIWantToDeposit(350.0, "personal");
		
		// check postconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		
		assertTrue("Customer's scheduler should have returned true. It needs to deposit money into her account. It doesn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("MockBank should have logged \"Received msgDepositMoney\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgDepositMoney from "
						+ "BankCustomerRole for amount: 350.0 and account type: personal"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		
	}
	
	public void testCheckBalanceAndWithdraw() {
		//setUp() runs first before this test!
		customer.bankTeller = bank;
				
		// check preconditions
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
				
		/**
		 * Step 1: Customer has a task to check his balance with the bank
		 */
		customer.msgIWantToCheckBalance("business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		
		assertTrue("Customer's scheduler should have returned true. It needs to check balance with the bank. It doesn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("MockBank should have logged \"Received msgCheckBalance\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgCheckBalance from BankCustomerRole for account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		
		/**
		 * Step 2: Customer has a task to withdraw money from her account
		 */
		customer.msgIWantToWithdraw(225.0, "business");
		
		// check postconditions for step 2 and preconditions for step 3
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		
		assertTrue("Customer's scheduler should have returned true. It needs to withdraw money into her account. It doesn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("MockBank should have logged \"Received msgWithdrawMoney\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgWithdrawMoney from "
						+ "BankCustomerRole for amount: 225.0 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
		
		/**
		 * Step 3: Customer gets money from withdrawal and person's businessFunds increases
		 */
		customer.msgHereIsMoney(225.0, "business", 50.0);
		
		// check postconditions for step 3
		assertTrue("Person should have total of 1000+225 = 1225 on hand. It doesn't.", 1225 == person.businessFunds);
		
	}
	
	public void testCustomerRequestLoanAndLoanApproved() {
		//setUp() runs first before this test!
		customer.bankTeller = bank;
						
		// check preconditions
		assertEquals("BankCustomerRole should have no loans in it. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer has a task to request a loan with the bank
		 */
		customer.msgIWantToGetALoan(80.0, "personal");
		
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
				
		assertTrue("Customer's scheduler should have returned true. It needs to get a loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
				
		assertTrue("MockBank should have logged \"Received msgRequestLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgRequestLoan from "
						+ "BankCustomerRole for amount: 80.0 and account type: personal"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
				
		/**
		 * Step 2: Customer gets their loan approved and cash on hand increases
		 */
		customer.msgLoanApproved(80.0, "personal");
		
		// check postconditions for step 2
		assertTrue("Person should have total of 550+80 = 630 on hand. It doesn't.", 630 == person.cashOnHand);
		assertEquals("Person should have 1 loan in loans. It doesn't.", customer.loans.size(), 1);
		assertTrue("The loan should be of amount 80.0. It isn't.", 80 == customer.loans.get(0).amount);
		assertTrue("The loan should be of type personal. It isn't", "personal".equals(customer.loans.get(0).accountType));
	}
	
	public void testCustomerPayBackLoan() {
		//setUp() runs first before this test!
		customer.bankTeller = bank;
						
		// check preconditions
		assertEquals("BankCustomerRole should have no loans in it. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer has a task to request a loan with the bank
		 */
		customer.msgIWantToGetALoan(89.5, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
				
		assertTrue("Customer's scheduler should have returned true. It needs to get a loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
				
		assertTrue("MockBank should have logged \"Received msgRequestLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgRequestLoan from "
						+ "BankCustomerRole for amount: 89.5 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
				
		/**
		 * Step 2: Customer gets their loan approved and cash on hand increases
		 */
		customer.msgLoanApproved(89.5, "business");
		
		// check postconditions for step 2
		assertTrue("Person should have total of 1000+89.5 = 1089.5 on hand. It doesn't.", 0 == Double.compare(1089.5,person.businessFunds));
		assertEquals("Person should have 1 loan in loans. It doesn't.", customer.loans.size(), 1);
		assertTrue("The loan should be of amount 89.5. It isn't.", 0 == Double.compare(89.5,customer.loans.get(0).amount));
		assertTrue("The loan should be of type business. It isn't", "business".equals(customer.loans.get(0).accountType));

		/**
		 * Step 3: Customer has a task to pay back a loan with the bank
		 */
		customer.msgIWantToPayBackLoan(89.5, "business");
		
		// check postconditions for step 1
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
				
		assertTrue("Customer's scheduler should have returned true. It needs to pay back her loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
				
		assertTrue("MockBank should have logged \"Received msgPayLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgPayLoan from "
						+ "BankCustomerRole for amount: 89.5 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		
		/**
		 * Step 4: Customer pay loan is successful.
		 */
		customer.msgLoanPaid(89.5, "business");
		
		assertEquals("Person should have no loan in loans. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
	}
	
	public void testCustomerPayBackLoanIfHaveEnoughMoney() {
		//setUp() runs first before this test!
		customer.bankTeller = bank;
						
		// check preconditions
		assertEquals("BankCustomerRole should have no loans in it. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer has a task to request a loan with the bank
		 */
		customer.msgIWantToGetALoan(89.5, "business");
		
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
				
		assertTrue("Customer's scheduler should have returned true. It needs to get a loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
				
		assertTrue("MockBank should have logged \"Received msgRequestLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgRequestLoan from "
						+ "BankCustomerRole for amount: 89.5 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It doesn't.", customer.tasks.size(), 0);
				
		/**
		 * Step 2: Customer gets their loan approved and cash on hand increases
		 */
		customer.msgLoanApproved(89.5, "business");
		
		// check postconditions for step 2
		assertTrue("Person should have total of 1000+89.5 = 1089.5 on hand. It doesn't.", 0 == Double.compare(1089.5,person.businessFunds));
		assertEquals("Person should have 1 loan in loans. It doesn't.", customer.loans.size(), 1);
		assertTrue("The loan should be of amount 89.5. It isn't.", 0 == Double.compare(89.5,customer.loans.get(0).amount));
		assertTrue("The loan should be of type business. It isn't", "business".equals(customer.loans.get(0).accountType));

		/**
		 * Step 3: Customer should create a task to pay back a loan with the bank
		 */
		assertTrue("Customer's scheduler should have returned true. It needs to pay back her loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
		
		// check postconditions for step 1
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		
		assertTrue("Customer's scheduler should have returned true. It needs to pay back her loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
							
		assertTrue("MockBank should have logged \"Received msgPayLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgPayLoan from "
						+ "BankCustomerRole for amount: 89.5 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		
		/**
		 * Step 4: Customer pay loan is successful.
		 */
		customer.msgLoanPaid(89.5, "business");
		
		assertEquals("Person should have no loan in loans. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
	}
	
	public void testAutoRequestLoan() {
		person.cashOnHand = 50;
		customer.bankTeller = bank;
		
		// check preconditions
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer checks balance
		 */
		customer.msgHereIsBalance(80, "personal");
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		assertTrue("Customer's scheduler should have returned true. It needs to request loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
		assertTrue("MockBank should have logged \"Received msgRequestLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgRequestLoan from "
						+ "BankCustomerRole for amount: 200.0 and account type: personal"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
	}
	
	public void testNoAutoRequestLoan() {
		customer.bankTeller = bank;
		
		// check preconditions
		assertEquals("BankCustomerRole should have no loans in it. It does.", customer.loans.size(), 0);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " + bank.log.toString(), 0, bank.log.size());
		customer.state = CustomerState.AtAtm; // assume customer is at the bank atm already.
		
		/**
		 * Step 1: Customer has a loan.
		 */
		customer.msgLoanApproved(200, "business");
		
		// check postconditions of step 1 and preconditions of step 2
		assertEquals("BankCustomerRole should have 1 loan in it. It doesn't.", customer.loans.size(), 1);
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
		assertEquals("MockBank's log should be empty before customer's scheduler is called. Instead, it reads: " 
		+ bank.log.toString(), 0, bank.log.size());
		
		
		/**
		 * Step 2: Customer checks balance
		 */
		customer.msgHereIsBalance(2889.92, "business");
		assertEquals("BankCustomerRole should have 1 task in it. It doesn't.", customer.tasks.size(), 1);
		assertTrue("Customer's scheduler should have returned true. It needs to auto pay loan with the bank. It doesn't.", customer.pickAndExecuteAnAction());
		assertTrue("MockBank should have logged \"Received msgAutoPayLoan\" but didn't. His log reads instead: " 
				+ bank.log.getLastLoggedEvent().toString(), bank.log.containsString("Received msgAutoPayLoan from "
						+ "BankCustomer for amount: 200.0 and account type: business"));
		
		assertEquals("BankCustomerRole should have no tasks in it. It does.", customer.tasks.size(), 0);
	}
}
