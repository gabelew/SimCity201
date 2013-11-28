package restaurant;

import CMRestaurant.roles.CMCookRole.OrderState;
import restaurant.interfaces.Waiter;

public class RoleOrder{
	public Waiter waiter;
	public String choice;
	public int table;
	public OrderState state;
	
	public RoleOrder(Waiter w, String c, int t){
		waiter = w;
		choice = c;
		table = t;
		state = OrderState.PENDING;
	}
	
}
