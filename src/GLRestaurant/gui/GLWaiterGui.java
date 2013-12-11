package GLRestaurant.gui;

import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLWaiterRole;
import GLRestaurant.roles.GLCookRole;
import city.animationPanels.GLRestaurantAnimationPanel;
import city.gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.imageio.ImageIO;

import restaurant.interfaces.Waiter;

public class GLWaiterGui implements Gui {

	public int waitingSpotNumber = -1;
	static final int NWAITSEATS = 16;
    static final int NWAITSEATS_COLUMNS = 8;
    static final int NWAITSEATS_ROWS = 2;
    static final int WAITINGSEATS_X_START = 245;
    static final int WAITINGSEATS_Y_START = 225;
    static final int WAITINGSEATS_X_GAP = 15;
    static final int WAITINGSEATS_Y_GAP = 35;
    static final int xWAITING_OVERFLOW_POS = 245;
    static final int yWAITING_OVERFLOW_POS = 345;
	private final int OFFSCREEN_POSITION = -20;
	private static BufferedImage waiterImg = null;
	private int platex, platey;
	private boolean isPresent = false;
    private Waiter role = null;
    private Map<Integer, Point> seatMap = new HashMap<Integer, Point>();
    GLCustomerGui customerGui;
    private int customerx, customery;
    private enum Command {noCommand, onBreak, ReturnFromBreak, IfAtTable, IfAtOrigin, IfAtCustomer, IfAtPlate, LeaveRestaurant, IfAtStand};
    private Command command = Command.noCommand;
    String customerChoice;
    boolean customerOrdered = false;
    boolean bringingFood = false;
    boolean goOnBreak = false;
    String foodCarried = "";
    private ArrayBlockingQueue<String> orders = new ArrayBlockingQueue<String>(10);

    private int xPos, yPos;//waiter's position
    private int xDestination, yDestination; // waiter's destination
    Map<Integer, Point> tableMap = new HashMap<Integer, Point>();
    
    public static final Point TableOne = new Point(100,100);
    public static final Point TableTwo = new Point(200,100);
    public static final Point TableThree = new Point(300,100);

    public GLWaiterGui(Waiter agent) {
    	try {
    		waiterImg = ImageIO.read(new File("imgs/waiter_v1.png"));
    	} catch(IOException e) {
    		
    	}
    	
        this.role = agent;
        tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
        for(int i = 0; i<NWAITSEATS/NWAITSEATS_ROWS; i++){
        	for(int j = 0; j < NWAITSEATS/NWAITSEATS_COLUMNS; j++ )
        		seatMap.put(j+i*2, new Point(i*WAITINGSEATS_X_GAP+WAITINGSEATS_X_START, j*WAITINGSEATS_Y_GAP+WAITINGSEATS_Y_START));
        }
    }
    
    public void DoGoToRevolvingStand() {
    	xDestination = 460;
    	yDestination = 180;
    	command = Command.IfAtStand;
    	if(waitingSpotNumber >= 0){
			((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.get(waitingSpotNumber).release();
			waitingSpotNumber = -1;
		}
    }
    
    private void findASpotToRest() {	
		for(int i = 0; i < ((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.size(); i++){
			if(waitingSpotNumber < 0){
				if(((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.get(i).tryAcquire()){
					waitingSpotNumber = i;
					xDestination = seatMap.get(i).x;
					yDestination = seatMap.get(i).y;
				}
			}
		}
		
		if(waitingSpotNumber < 0){
			xDestination = xWAITING_OVERFLOW_POS;
			yDestination = yWAITING_OVERFLOW_POS;
		}
	}
    public void setCustomerGui(GLCustomerGui g) {
    	this.customerGui = g;
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
        if (xDestination == xPos && yDestination == yPos) {
         
        	if(Command.IfAtTable == command){
        		((GLWaiterRole)role).msgAtTable();
        	} else if (Command.onBreak == command) {
        		//gui.setWaiterOnBreak(agent);
        	} else if (Command.ReturnFromBreak == command) {
        		//gui.setWaiterEnabled(agent);
        	} else if (Command.LeaveRestaurant == command) {
        		((GLWaiterRole)role).msgLeftTheRestaurant();
        	} else if (Command.IfAtCustomer == command && customerx == xDestination && customery == yDestination) {
        		((GLWaiterRole)role).msgAtCustomer();
        	} else if (Command.IfAtPlate == command && platex == xDestination && platey == yDestination) {
        		((GLWaiterRole)role).msgAtPlate();
        	} else if (Command.IfAtStand == command) {
        		((GLWaiterRole)role).msgAtStand();
        	}
        	command = Command.noCommand;
        	findASpotToRest();
        }
    }

    public void takenOrder(String choice) {
    	customerOrdered= true;
    	foodCarried = "";
    	for (String order : orders) {
			foodCarried += order + "? ";
		}
		if("steak".equals(choice)) {
			this.customerChoice = "ST";
		}
		else if("chicken".equals(choice)) {
			this.customerChoice = "CH";
		}
		else if("salad".equals(choice)) {
			this.customerChoice = "SD";
		}
		else if("cookie".equals(choice)){
			this.customerChoice = "CK";
		}
		orders.add(customerChoice);
		foodCarried += customerChoice + "? ";
	}
    
    public void servingFood(String choice) {
		customerOrdered = false;
		if("steak".equals(choice)) {
			this.customerChoice = "ST";
		}
		else if("chicken".equals(choice)) {
			this.customerChoice = "CH";
		}
		else if("salad".equals(choice)) {
			this.customerChoice = "SD";
		}
		else if("cookie".equals(choice)){
			this.customerChoice = "CK";
		}
		foodCarried = customerChoice;
		bringingFood = true;
	}
    
    public void ifAtCustomer(int x, int y) {
    	command = Command.IfAtCustomer;
    	customerx = x + 20;
    	customery = y - 20;
    }
    
    public void ifAtPlate(int x, int y) {
    	command = Command.IfAtPlate;
    	platex = x - 20;
    	platey = y;
    }
    
    public void finishedWithOrder(String choice) {
    	bringingFood = false;
    	foodCarried = "";
    	orders.remove();
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(waiterImg, xPos, yPos, null);
        if(customerOrdered) {
			g.setColor(Color.BLACK);		
			g.drawString(foodCarried, xPos, yPos+5);
		}
		else if(bringingFood) {
			g.setColor(Color.BLACK);
			g.drawString(foodCarried, xPos, yPos+5);
		}
    }

    public void DoLeaveRestaurant() {
    	xDestination = OFFSCREEN_POSITION;
    	yDestination = OFFSCREEN_POSITION;
    	if(waitingSpotNumber >= 0){
			((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.get(waitingSpotNumber).release();
			waitingSpotNumber = -1;
		}
    	command = Command.LeaveRestaurant;
    }
    
    public boolean isPresent() {
        return isPresent;
    }
    
    public boolean onBreak() {
    	return goOnBreak;
    }
    
    public void wantGoOnBreak() {
    	goOnBreak = true;
    	((GLWaiterRole)role).msgWantToGoOnBreak();
    }
    
    public void goOnBreak() {
    	xDestination = -40;
    	yDestination = -40;
    	if(waitingSpotNumber >= 0){
			((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.get(waitingSpotNumber).release();
			waitingSpotNumber = -1;
		}
    	command = Command.onBreak;
    }
    
    public void breakDone() {
    	goOnBreak = false;
    	command = Command.ReturnFromBreak;
    	((GLWaiterRole)role).msgReturnedFromBreak();
    }
    
    public void noBreak() {
    	goOnBreak = false;
    	command = Command.ReturnFromBreak;
    }

    public void GoToTable(GLCustomerRole customer, int tablenumber) {
    	command = Command.IfAtTable;
    	Point destination = tableMap.get(tablenumber);
    	if(null != destination) {
    		xDestination = destination.x + 20;
    		yDestination = destination.y - 20;
    	} 	
    	if(waitingSpotNumber >= 0){
			((GLRestaurantAnimationPanel)role.getRestaurant().insideAnimationPanel).waitingSeatsWaiter.get(waitingSpotNumber).release();
			waitingSpotNumber = -1;
		}
    }

    public void DoLeaveCustomer() {
        findASpotToRest();
    }
    
    public void GoToPlate(int x, int y) {
    	xDestination = x - 20;
    	yDestination = y;
    }
    
    public void GoToCustomer(int x, int y) {
    	xDestination = x + 20;
    	yDestination = y - 20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	@Override
	public void setPresent(boolean b) {
		isPresent = b;
	}
}
