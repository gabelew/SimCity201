package restaurant.interfaces;

import restaurant.CustomerAgent;

/**
 * A sample Waiter interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Waiter {
	
	/**
	 * 
	 * 
	 */
	public abstract void msgHereIsCheck(Customer c, double check);
	public abstract void msgSitAtTable(Customer c, int table);
	public abstract void msgImReadyToOrder(Customer c);
	public abstract void msgHereIsMyOrder(Customer c, String choice);
	public abstract void msgDoneEatingAndLeaving(Customer c);

}
