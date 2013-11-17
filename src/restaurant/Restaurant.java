package restaurant;

import java.awt.Point;

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
	
	public Restaurant(Host h, Cashier c, Cook co, Menu m, String cr, String t, InsideAnimationPanel iap, Point p){
		this.host = h;
		this.cashier = c;
		this.cook = co;
		this.customerRole = cr;
		this.type = t;
		this.insideAnimationPanel = iap;
		this.location = p;
	}
}
