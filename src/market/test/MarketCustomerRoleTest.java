package market.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import CMRestaurant.roles.CMCashierRole;
import CMRestaurant.roles.CMCookRole;
import city.MarketAgent;
import city.MarketAgent.cookState;
import city.MarketAgent.customerState;
import city.roles.ClerkRole.orderState;
import city.roles.MarketCustomerRole;
import city.roles.ClerkRole;
import city.roles.DeliveryManRole;
import restaurant.interfaces.*;
import market.test.mock.*;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the MarketAgent's basic interaction
 * with Cashiers,Cooks, customers, and the Delivery man.
 *
 * @author Emily Bernstein
 */
public class MarketCustomerRoleTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	MarketAgent market;
	MarketCustomerRole customer;
	MockClerk clerk;
	MockDeliveryMan deliveryMan;
	Map<String,Integer> choice;
	double normAmount=30;
	Integer amountLeft=8;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();	
		Point location= new Point(5,5);
		clerk = new MockClerk("clerk");
		deliveryMan=new MockDeliveryMan("deliveryMan");
		customer = new MarketCustomerRole(null);
		market= new MarketAgent(location,"Market",null);
		choice= new HashMap<String,Integer>();


		
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer will pay the exact check amount.
	 */
	public void testOneNormalCustomerScenario()
	{
		//add choices to order
		choice.put("steak", 2);
		choice.put("cookie", 2);
		choice.put("salad",2);
		market.setInventory(10, 10, 10, 10, 10);
		/*
		 * Normal one customer coming into store.
		 */
		//pre-initializing checks
		
	}
}