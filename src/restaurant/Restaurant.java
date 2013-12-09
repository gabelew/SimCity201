package restaurant;

import java.awt.Point;

import city.BankAgent.BankAccount;
import city.animationPanels.InsideAnimationPanel;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter.Menu;

public class Restaurant {
	public Point location;
	public Host host;
	public Cashier cashier;
	public Cook cook;
	public String customerRole;
	public Menu m;
	public String type;
	public InsideAnimationPanel insideAnimationPanel;
	public String waiterRole;
	public BankAccount restaurantAccount;
	public boolean isOpen = true;
	
	public Restaurant(Host h, Cashier c, Cook co, Menu m, String cr, String t, InsideAnimationPanel iap, Point p, String wr){
		this.host = h;
		this.cashier = c;
		this.cook = co;
		this.customerRole = cr;
		this.type = t;
		this.insideAnimationPanel = iap;
		this.location = p;
		this.waiterRole = wr;
	}
	
	public void setRestaurantAccount(BankAccount b) {
		this.restaurantAccount = b;
	}
	
	public BankAccount getRestaurantAccount() {
		return this.restaurantAccount;
	}

	public boolean isOpen() {
		return isOpen;
	}
	public void closeRestaurant(){
		isOpen = false;
		//TODO:notify host
		//this.host.msgCloseRestaurant();
		
	}
	public void openRestaurant(){
		isOpen = true;
	}
}
