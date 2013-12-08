package GCRestaurant.gui;
import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import city.gui.Gui;
import restaurant.Restaurant;
import GCRestaurant.roles.GCCustomerRole;

public class GCCustomerGui implements Gui{

	private GCCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	Restaurant gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterRestaurant, GoToSeat, LeaveRestaurant};
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
	private int cashierPosX = -10, cashierPosY = -25;

	public GCCustomerGui(GCCustomerRole c, Restaurant gui){ //HostAgent m) {
		agent = c;
		xPos = DEFAULT_POS;
		yPos = DEFAULT_POS;
		xDestination = DEFAULT_POS;
		yDestination = DEFAULT_POS;
		//maitreD = m;
		this.gui = gui;
	}

	public void updatePosition() {
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
		
		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
		 if (xPos == xDestination && yPos == yDestination
	           		& (xDestination == cashierPosX) & (yDestination == cashierPosY)) 
		 {
	             xPos = DEFAULT_POS;
	             yPos = DEFAULT_POS;
	             xDestination = DEFAULT_POS;
	             yDestination = DEFAULT_POS;
	        	agent.msgAtCashier();
		 }
		 if (xPos == xDestination && yPos == yDestination
	           		& (xDestination == WAITING_AREA) & (yDestination == WAITING_AREA)) 
		 {
	        	agent.msgActionDone();
		 }
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, CUST_SIZE, CUST_SIZE);
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
	public void leftRest()
	{
		this.foodState = FoodState.none;
	}
	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
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
		xDestination = WAITING_AREA;
		yDestination = WAITING_AREA;
	}
	public void goToCashier()
	{
		xDestination = cashierPosX;
		yDestination = cashierPosY;
		isHungry = false;
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
		xDestination = DEFAULT_POS;
		yDestination = DEFAULT_POS;
		command = Command.LeaveRestaurant;
	}
}
