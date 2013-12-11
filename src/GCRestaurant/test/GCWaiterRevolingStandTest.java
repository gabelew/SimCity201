package GCRestaurant.test;

import java.util.Timer;

import EBRestaurant.gui.EBCookGui;
import EBRestaurant.gui.EBWaiterGui;
import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBNormalWaiterRole;
import EBRestaurant.roles.EBRevolvingStandMonitor;
import EBRestaurant.roles.EBSharedWaiterRole;
import EBRestaurant.roles.EBWaiterRole;
import EBRestaurant.roles.EBWaiterRole.customerState;
import GCRestaurant.roles.GCCookRole;
import GCRestaurant.roles.GCRevolvingStandMonitor;
import GCRestaurant.roles.GCSharedWaiterRole;
import junit.framework.TestCase;
import restaurant.Restaurant;
import restaurant.interfaces.Waiter.Menu;
import city.PersonAgent;

public class GCWaiterRevolingStandTest  extends TestCase
{
	
	GCRevolvingStandMonitor orderStand;
	GCCookRole cook;
	GCSharedWaiterRole waiter;
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception
	{
		super.setUp();			
	}	
	
	/**
	 * First test is empty revolving stand, waiter puts 3 orders on the stand successfully, 6th fails. 
	 * Cook removes orders, waiter puts on the one.
	 */
	public void testSharedWaiterNormal() {
		
	}
}