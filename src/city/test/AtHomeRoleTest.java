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
		role = new AtHomeRole(person, 4);
		person.addRole(role);
	}
	
	public void testNormalAtHome()
	{
		role.testing = true;
		//check preconditions
		assertNotSame("Role's pointer to person shouldn't be null", role.myPerson, null);
		assertEquals("Role has no orders to cook", role.orders.size(), 0);
		assertNotSame("Fridge should contain food", role.foodInFridge.size(), 0);
		assertNotSame("Choices should not be 0 ", role.choices.size(), 0);
		
		//got message to make food
		role.ImHungry();
		//post condition
		assertEquals("should have one order to cook", role.orders.size(), 1);
		//cook the food
		role.pickAndExecuteAnAction();
		//eat the food
		assertEquals("Should have one cooked item to eat", role.orders.size(), 1);
		try { Thread.sleep(4000); } 
		catch(InterruptedException ex) { Thread.currentThread().interrupt();}
		role.pickAndExecuteAnAction();
		assertEquals("Finished eating food, no orders left", role.orders.size(), 0);
	}
	
	public void testFoodLow()
	{
		
		person = new PersonAgent("salad", 550.0, 1000.0);
		role.testing = true;
		//check preconditions
		assertNotSame("Role's pointer to person shouldn't be null", role.myPerson, null);
		assertEquals("Role has no orders to cook", role.orders.size(), 0);
		assertNotSame("Fridge should contain food", role.foodInFridge.size(), 0);
		assertEquals("Choices should be equal to food avaliable in fridge ", role.foodInFridge.size(), role.choices.size());
		
		//change food quantity for specific item
		assertEquals("First item in fridge should be salad ", role.foodInFridge.get(0).choice, "salad");
		role.foodInFridge.get(0).amount = 1;
		assertEquals("Salad's stock should be 1 ", role.foodInFridge.get(0).amount, 1);
		assertEquals("salad state should be none",  role.foodInFridge.get(0).state,role.foodInFridge.get(0).state.none );
		//add a salad order to list
		role.ImHungry();
		role.pickAndExecuteAnAction();
		assertEquals("should have one order to cook", role.orders.size(), 1);
		assertEquals("salad state should be ordered",  role.foodInFridge.get(0).state,role.foodInFridge.get(0).state.ordered );
	
	}
	
	/**
	 * If there is no food at home, the person leaves the house
	 */
	public void testNoFood()
	{
		person = new PersonAgent("nofood", 300,300);
		role = new AtHomeRole(person, 0);
		role.testing = true;
		assertEquals("Choices should be empty", role.choices.size(), 0);
		
		//hungry looks for food, but no food
		role.ImHungry();
		assertEquals("Choices should be empty", role.choices.size(), 0);
		
		assertEquals("Role's state is goingHome",  role.state, role.state.goingHome );
		role.pickAndExecuteAnAction();
		assertEquals("Role's state is outOfFood",  role.state, role.state.OutOfFood );
		
		
		
		
		
		
	}
}
