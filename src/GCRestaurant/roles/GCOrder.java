package GCRestaurant.roles;

import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import GCRestaurant.roles.GCCookRole.OrderState;
import GCRestaurant.roles.GCHostRole.Table;


public class GCOrder 
{
	public Table table;
	public Waiter waiter;
	public Customer customer;
	public OrderState state;
	public String choice;
	
	GCOrder(Waiter w, Customer c, Table t, String choice)
	{
		this.choice = choice;
		this.table = t;
		this.waiter = w;
		this.customer = c;
		state = OrderState.pending;
	}
}
