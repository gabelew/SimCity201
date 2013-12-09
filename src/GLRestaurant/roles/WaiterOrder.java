package GLRestaurant.roles;

import GLRestaurant.roles.GLCookRole.orderState;

public class WaiterOrder {
	public int orderNum;
	public GLWaiterRole w;
	public String choice;
	public GLCustomerRole c;
	public orderState s = orderState.pending;
	public WaiterOrder(GLWaiterRole w, String choice, GLCustomerRole c, orderState os, int orderNum) {
		this.w = w;
		this.choice = choice;
		this.c = c;
		this.s = os;
		this.orderNum = orderNum;
	}
	public WaiterOrder(GLWaiterRole w, String choice, GLCustomerRole c) {
		this.w = w;
		this.choice = choice;
		this.c = c;
	}
}
