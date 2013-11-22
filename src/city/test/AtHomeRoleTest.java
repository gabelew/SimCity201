package city.test;

import city.PersonAgent;
import city.roles.AtHomeRole;
import junit.framework.TestCase;

public class AtHomeRoleTest extends TestCase
{
	public PersonAgent person;
	public AtHomeRole role;
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception
	{
		super.setUp();		
		person = new PersonAgent("AtHomeGuy1", 550.0, 1000.0);
		role = new AtHomeRole(person);
	}
	
	public void testNormalAtHome()
	{
		//check preconditions
		assertNotSame("Role's pointer to person shouldn't be null", person, null);
		assertEquals("Role has no orders to cook", role.orders.size(), 0);
		assertNotSame("Fridge should contain food", role.foodInFridge.size(), 0);
		assertEquals("Choices should be equal to food avaliable in fridge ", role.foodInFridge.size(), role.choices.size());
		
		//got message to make food
		role.ImHungry();
		
		//post condition
		assertEquals("should have one order to cook", role.orders.size(), 1);
		
		//cook the food
		role.pickAndExecuteAnAction();
		//eat the food
		assertEquals("Should have one cooked item to eat", role.orders.size(), 1);
		role.pickAndExecuteAnAction();
		assertEquals("Finished eating food, no orders left", role.orders.size(), 0);
	}
}
