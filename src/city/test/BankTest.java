package city.test;

import junit.framework.TestCase;
import city.PersonAgent;
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
}
