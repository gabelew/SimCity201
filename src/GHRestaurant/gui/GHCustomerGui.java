package GHRestaurant.gui;
import GHRestaurant.roles.*;
//import restaurant.Customer;
import restaurant.interfaces.*;
import GHRestaurant.*;

import java.awt.*;

import city.gui.Gui;
import city.gui.SimCityGui;

public class GHCustomerGui implements Gui{

	private Customer agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	SimCityGui gui;

	private int xPos, yPos;
	private int Cashierx = -50, Cashiery = -50;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;

	public GHCustomerGui(Customer c, SimCityGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) ((GHCustomerRole) agent).msgAnimationFinishedGoToSeat();
			if (command==Command.GoToCashier) ((GHCustomerRole) agent).msgAnimationFinishedGoToCashier();
			 if (command==Command.LeaveRestaurant) {
				((GHCustomerRole) agent).msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//setPresent(false);
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		xDestination = xTable;
		yDestination = yTable + (100-(seatnumber*100));
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier(){
		xDestination = Cashierx;
		yDestination = Cashiery;
		command = Command.GoToCashier;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
