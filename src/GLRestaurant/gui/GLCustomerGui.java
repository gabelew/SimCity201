package GLRestaurant.gui;

import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLHostRole;
import GLRestaurant.gui.GLWaiterGui.Point;
import city.gui.Gui;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GLCustomerGui implements Gui{
	
	private static final int PERSONWIDTH = 20;
	private static final int PERSONHEIGHT = 20;
	private int ORIGINALX = -40;
	private int ORIGINALY = -40;
	private GLCustomerRole role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean orderedFood = false;
	private boolean eatingFood = false;
	private String choice;
	
	GLWaiterGui waiterGui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWait, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	static class Point {
    	int x;
    	int y;
    	Point (int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    Map<Integer, Point> tableMap = new HashMap<Integer, Point>();
    
    public static final Point TableOne = new Point(100,100);
    public static final Point TableTwo = new Point(200,100);
    public static final Point TableThree = new Point(300,100);

	public GLCustomerGui(GLCustomerRole c, int x, int y){
		ORIGINALX = x;
		ORIGINALY = y;
		role = c;
		xPos = -40;
		yPos = -40;
		xDestination = ORIGINALX;
		yDestination = ORIGINALY;
		
		tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
		
	}
	
	public GLCustomerGui(GLCustomerRole c){
		ORIGINALX = 30;
		ORIGINALY = 30;
		role = c;
		xPos = -40;
		yPos = -40;
		xDestination = ORIGINALX;
		yDestination = ORIGINALY;
		
		tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);

	}
	
	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}

	public void setWaiterGui(GLWaiterGui gui) {
		this.waiterGui = gui;
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
			if (command==Command.GoToSeat) role.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			} else if (command == Command.GoToWait) role.msgAnimationFinishedGoingToWaitingArea();
			command=Command.noCommand;
		}
	}

	public void ordered(String choice) {
		orderedFood = true;
		if(choice == "steak") {
			this.choice = "ST";
		}
		else if(choice == "chicken") {
			this.choice = "CH";
		}
		else if(choice == "salad") {
			this.choice = "SD";
		}
		else if(choice == "cookie"){
			this.choice = "CK";
		}
	}
	
	public void eating() {
		orderedFood = false;
		eatingFood = true;
	}
	
	public void reorder() {
		orderedFood = false;
		eatingFood = false;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
        g.fillRect(xPos, yPos, PERSONWIDTH, PERSONHEIGHT);
		if(orderedFood) {
			g.setColor(Color.BLACK);
			g.drawString(choice + "?", xPos, yPos + 10);
		}
		else if(eatingFood) {
			g.setColor(Color.BLACK);
			g.drawString(choice, xPos, yPos + 10);
		}
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

	public void DoGoToSeat(int seatnumber) {
		Point destination = tableMap.get(seatnumber);
    	xDestination = destination.x;
    	yDestination = destination.y;
		command = Command.GoToSeat;
	}
	
	public void DoGoToWait() {
		xDestination = ORIGINALX;
		yDestination = ORIGINALY;
		command = Command.GoToWait;
	}

	public void DoExitRestaurant() {
		eatingFood = false;
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
