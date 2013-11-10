package restaurant.interfaces;

import restaurant.MarketAgent;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Chad Martin
 *
 */
public interface Cashier {
	/**
	 * @param c The customer who is paying the bill
	 * @param cash The cash given to the cashier for payment
	 *
	 * Sent by the customer to pay for food eaten.
	 */
	public abstract void msgPayment(Customer c, double cash);

	/**
	 */
	public abstract void msgProduceCheck(Waiter w, Customer c, String choice);

	public abstract void msgHereIsBill(Market marketAgent, double bill);

}
