package EBRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import EBRestaurant.roles.EBCustomerRole;

public class EBCustomerGui implements Gui{
	private static BufferedImage customerImg = null;
	private EBCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	private int xArea=10;
	private int yArea;
	private int xPos, yPos;
	private final int startX=-40;
	private final int startY=-40;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 100;
    private final int yTable1 = 100;
    private final int yTable2 = 200;
    private final int yTable3 = 300;
	private int Width=20;
	private int Height=20;
	
	public EBCustomerGui(EBCustomerRole c){ 
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		yArea=60;
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
			if (command==Command.GoToSeat) 
				agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
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
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToWaitingArea(int y){
		xDestination = xArea;
		yDestination = y;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if (seatnumber==1)
		{
			xDestination = xTable;
			yDestination = yTable1;
			command = Command.GoToSeat;
		}
		else if (seatnumber==2)
		{
			xDestination = xTable;
			yDestination = yTable2;
			command = Command.GoToSeat;
		}
		else if (seatnumber==3)
		{
			xDestination = xTable;
			yDestination = yTable3;
			command = Command.GoToSeat;
		}
	}

	public void DoExitRestaurant() {
		xDestination = startX;
		yDestination = startY;
		command = Command.LeaveRestaurant;
	}
}
