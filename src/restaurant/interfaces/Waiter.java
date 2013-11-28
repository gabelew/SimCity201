package restaurant.interfaces;

import java.util.ArrayList;
import java.util.List;

import restaurant.Restaurant;

/**
 * A sample Waiter interface built to unit test a CashierRole.
 *
 * @author Chad Martin
 *
 */
public interface Waiter {
	
	/**
	 * 
	 * 
	 */
	public class Menu{
		public List<MenuItem> menuItems = new ArrayList<MenuItem>();

		public Menu(){
			menuItems.add(new MenuItem("Salad", Cashier.SALAD_COST));
			menuItems.add(new MenuItem("Steak", Cashier.STEAK_COST));
			menuItems.add(new MenuItem("Chicken", Cashier.CHICKEN_COST));
			menuItems.add(new MenuItem("Cookie", Cashier.COOKIE_COST));
		}
	}
	public class MenuItem{
		public String item;
		public double cost;
		MenuItem(String i, double d){
			item = i;
			cost = d;
		}
	}
	public abstract void msgHereIsCheck(Customer c, double check);
	public abstract void msgSitAtTable(Customer c, int table);
	public abstract void msgImReadyToOrder(Customer c);
	public abstract void msgHereIsMyOrder(Customer c, String choice);
	public abstract void msgDoneEatingAndLeaving(Customer c);
	public abstract void msgOutOfOrder(String choice, int table);
	public abstract void msgOrderIsReady(String choice, int table);
	public abstract void msgGoOnBreak();
	public abstract void msgDontGoOnBreak();
	public abstract void msgAtEntrance();
	public abstract void msgLeftTheRestaurant();
	public abstract void msgAtTable();
	public abstract Restaurant getRestaurant();
	public abstract void goesToWork();
	public abstract void msgAskForBreak();

}
