package restaurant.interfaces;

import java.util.List;

public interface Market {

	//public abstract void msgHereIsOrder(CookAgent c, Cashier cashier, List<CookAgent.Food> orderList);
	public void msgPayment(Cashier c, double payment);
	public abstract String getName();
	
}
