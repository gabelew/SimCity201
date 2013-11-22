package market.interfaces;


/**
 * A sample MarketCustomer interface built to unit test a MarketAgent.
 *
 * @author Emily Bernstein
 *
 */
public interface Market {

	public abstract void msgPlaceOrder(MarketCustomer CR);
	
	public abstract void msgPlaceDeliveryOrder(Cook cook);
	
	public abstract void msgClerkDone();
	
	public abstract void msgDeliveryDone();
	
	

}