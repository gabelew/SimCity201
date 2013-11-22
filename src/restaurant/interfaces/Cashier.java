package restaurant.interfaces;

import market.interfaces.DeliveryMan;

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
	public static final double SALAD_COST = 4.99;
	public static final double STEAK_COST = 15.99;
	public static final double CHICKEN_COST = 10.99;
	public static final double BURGER_COST = 8.99;
	public static final double COOKIE_COST = 3.99;	
	public abstract void msgPayment(Customer c, double cash);

	/**
	 */
	public abstract void msgProduceCheck(Waiter w, Customer c, String choice);

	public abstract void msgHereIsBill(DeliveryMan DMR, double bill);

}
