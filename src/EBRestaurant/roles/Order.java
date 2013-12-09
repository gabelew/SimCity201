package EBRestaurant.roles;

import EBRestaurant.roles.EBCookRole.state;
import restaurant.interfaces.Waiter;

public class Order{
	Waiter w;
	String choice;
	int tableNumber;
	state S;
	public Order(Waiter waiter, String choice2, int tableNumber2,
			state pending) {
		w=waiter;
		choice=choice2;
		tableNumber=tableNumber2;
		S=pending;
	}
}
