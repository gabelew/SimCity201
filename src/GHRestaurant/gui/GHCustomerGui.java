package GHRestaurant.gui;
import GHRestaurant.roles.*;
//import restaurant.Customer;
import restaurant.interfaces.*;
import GHRestaurant.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import city.gui.SimCityGui;

public class GHCustomerGui implements Gui{

	private Customer role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	//SimCityGui gui;

	private int xPos = -20, yPos = -20;
	private int Cashierx = 50, Cashiery = 50;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	private static BufferedImage customerImg = null;


	public GHCustomerGui(GHCustomerRole role){ //HostAgent m) {
		this.role = role;
		//this.gui = gui;
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    customerImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		} catch (IOException e) {
		}
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
			if (command==Command.GoToSeat) ((GHCustomerRole) role).msgAnimationFinishedGoToSeat();
			if (command==Command.GoToCashier) ((GHCustomerRole) role).msgAnimationFinishedGoToCashier();
			 if (command==Command.LeaveRestaurant) {
				((GHCustomerRole) role).msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//setPresent(false);
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(customerImg, xPos, yPos, null);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		role.gotHungry();
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
