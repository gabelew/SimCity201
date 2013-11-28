package restaurant.interfaces;

import CMRestaurant.gui.CMCustomerGui;
import restaurant.interfaces.Waiter.Menu;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	/**
	 * @param check The cost according to the cashier
	 *
	 * Sent by the waiter prompting the customer's money after the customer has approached the cashier.
	 */
	public abstract void msgHereIsCheck(double check);

	/**
	 * @param cashBack change (if any) due to the customer
	 *
	 * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
	 */
	public abstract void msgChange(double cashBack);


	/**
	 * @param remaining_cost how much money is owed
	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
	 */
	public abstract void msgPayMeLater();
	
	public abstract void msgFollowMeToTable(Waiter w, Menu m);
	public abstract void msgWhatWouldYouLike();
	public abstract void msgOutOfOrder(String c);
	public abstract void msgHereIsYourFood();
	public abstract void msgWaitForOpenTable();
	public abstract void msgTableIsReady();
	public abstract void gotHungry();
	

}