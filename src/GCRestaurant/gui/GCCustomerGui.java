package GCRestaurant.gui;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import city.gui.Gui;
import restaurant.Restaurant;
import GCRestaurant.roles.GCCustomerRole;

public class GCCustomerGui implements Gui{

	private GCCustomerRole role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	Restaurant gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterRestaurant, GoToSeat, LeaveRestaurant, GoToCashier, GoToWaitingArea};
	private enum FoodState{none, ordered, served};
	private FoodState foodState = FoodState.none;
	private Command command=Command.noCommand;
	
	private final int DEFAULT_POS = -20;
	private final int WAITING_AREA = 20;
	private final int CUST_SIZE = 20;
	private final int TABLE_SPACING = 100;
	private int xTable = 100;
	private final int yTable = 175;
	private String choice;
	private int cashierPosX = 110, cashierPosY = 93;
	private BufferedImage customerImg;
	private BufferedImage customerSittingImg;

	public GCCustomerGui(GCCustomerRole r){
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    customerImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		    customerSittingImg = ImageIO.read(new File(path.toString() + "customer_sitting_v1.png"));
		} 
		catch (IOException e) {}
		this.role = r;
		xPos = DEFAULT_POS;
		yPos = DEFAULT_POS;
		xDestination = DEFAULT_POS;
		yDestination = DEFAULT_POS;
	}

	public void updatePosition() 
	{
		if(command == Command.LeaveRestaurant)
		{
			if(xPos != xDestination)
	        {
                if (xPos < xDestination)
                        xPos++;
                else if (xPos > xDestination)
                        xPos--;
	        }
	        else
	        {
                if (yPos < yDestination)
                        yPos++;
                else if (yPos > yDestination)
                        yPos--;
	        }
		}
		else
		{
			if(xPos != xDestination)
	        {
                if (xPos < xDestination)
                        xPos++;
                else if (xPos > xDestination)
                        xPos--;
                if (yPos < yDestination+DEFAULT_POS)
                        yPos++;
                else if (yPos > yDestination+DEFAULT_POS)
                        yPos--;
	        }
	        else
	        {
                if (yPos < yDestination)
                        yPos++;
                else if (yPos > yDestination)
                        yPos--;
	        }
		}
		if (xPos == xDestination && yPos == yDestination && ((command==Command.GoToSeat) ||(command==Command.LeaveRestaurant) )) 
		{
			if (command==Command.GoToSeat)
			{
				command=Command.noCommand;
				role.msgAnimationFinishedGoToSeat();
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgActionDone();
				command=Command.noCommand;
				role.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
			}
			
		}
		if (xPos == xDestination && yPos == yDestination && command == Command.GoToCashier) 
		{
			command = Command.noCommand;
	        role.msgAtCashier();
		}
		if (xPos == xDestination && yPos == yDestination && command == Command.GoToWaitingArea) 
		{
			command = Command.noCommand;
	        role.msgActionDone();
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(customerImg, xPos, yPos, null);
		try
		{
		switch(foodState)
		{
			case ordered:
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial", Font.BOLD, 18));
				g.drawString("?", xPos, yPos+CUST_SIZE);
				break;
			case served:
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial", Font.BOLD, 18));
				String abrFood = choice.charAt(0) + "" + choice.charAt(1);
				g.drawString(abrFood, xPos, yPos+CUST_SIZE);
				break;
		default:
				break;
		}
		}
		catch(Exception e){return;}
	}
	
	public void orderedFood()
	{
		this.foodState = FoodState.ordered;	
	}
	public void servedFood(String c)
	{
		choice = c;
		this.foodState = FoodState.served;
	}
	public void leftTable()
	{
		this.foodState = FoodState.none;
	}
	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		command = Command.GoToWaitingArea;
		isHungry = true;
		role.gotHungry();
		setPresent(true);
		xDestination = WAITING_AREA;
		yDestination = WAITING_AREA;
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void enterRestaurant()
	{
		command = Command.GoToWaitingArea;
		xDestination = WAITING_AREA;
		yDestination = WAITING_AREA;
	}
	public void goToCashier()
	{
		command = Command.GoToCashier;
		xDestination = cashierPosX;
		yDestination = cashierPosY;
	}
	//seatnumber determines table destination
	public void DoGoToSeat(int seatnumber) 
	{
		xTable = (seatnumber)*TABLE_SPACING;
		xDestination = xTable;
		yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = -60;//DEFAULT_POS;
		yDestination = -60;
		command = Command.LeaveRestaurant;
	}
}
