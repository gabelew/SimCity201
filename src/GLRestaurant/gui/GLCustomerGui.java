package GLRestaurant.gui;

import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLHostRole;
import city.animationPanels.GLRestaurantAnimationPanel;
import city.gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class GLCustomerGui implements Gui{
	
	static final int xWAITING_AREA = 50;
	static final int yWAITING_AREA = 70;
	private int waitingSeatNumber = -1;
	private GLCustomerRole role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean orderedFood = false;
	private boolean eatingFood = false;
	private String choice;
	private enum State {standing, sitting};
	State state = State.standing;
	private static BufferedImage customerImg = null;
	private static BufferedImage customerSittingImg = null;
	
	GLWaiterGui waiterGui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWait, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	private final int START_POSITION = -20;
	static final int xSEAT_OFFSET = 25;
	static final int NWAITSEATS = 6;
    static final int NWAITSEATS_COLUMNS = 3;
    static final int NWAITSEATS_ROWS = 2;
    static final int WAITINGSEATS_X_START = 90;
    static final int WAITINGSEATS_Y_START = 10;
    static final int WAITINGSEATS_X_GAP = 30;
    static final int WAITINGSEATS_Y_GAP = 30;
    
    Map<Integer, Point> tableMap = new HashMap<Integer, Point>();
    private Map<Integer, Point> seatMap = new HashMap<Integer, Point>();
    public static final Point TableOne = new Point(100,100);
    public static final Point TableTwo = new Point(200,100);
    public static final Point TableThree = new Point(300,100);

//	public GLCustomerGui(GLCustomerRole c, int x, int y){
//		ORIGINALX = x;
//		ORIGINALY = y;
//		role = c;
//		xPos = -40;
//		yPos = -40;
//		xDestination = ORIGINALX;
//		yDestination = ORIGINALY;
//		
//		tableMap.put(1, TableOne);
//        tableMap.put(2, TableTwo);
//        tableMap.put(3, TableThree);
//		
//	}
	
	public GLCustomerGui(GLCustomerRole c){
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    customerImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		    customerSittingImg = ImageIO.read(new File(path.toString() + "customer_sitting_v1.png"));
		} catch (IOException e) {
		}
		role = c;
		xPos = START_POSITION;
		yPos = START_POSITION;
		xDestination = xWAITING_AREA;
		yDestination = yWAITING_AREA;
		
		tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
        
        for(int i = 0; i<NWAITSEATS/NWAITSEATS_ROWS; i++){
        	for(int j = 0; j < NWAITSEATS/NWAITSEATS_COLUMNS; j++ )
        		seatMap.put(j+i*2, new Point(i*WAITINGSEATS_X_GAP+WAITINGSEATS_X_START, j*WAITINGSEATS_Y_GAP+WAITINGSEATS_Y_START));
        }

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
			if (command==Command.GoToSeat){ 
				state = State.sitting;
				role.msgAnimationFinishedGoToSeat();
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			} else if (command == Command.GoToWait){ 
				if(waitingSeatNumber >= 0)
					state = State.sitting;
				role.msgAnimationFinishedGoingToWaitingArea();
			}
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
		if(state == State.sitting){
			g.drawImage(customerSittingImg, xPos, yPos, null);
		} else {
			g.drawImage(customerImg, xPos, yPos, null);
		}
		if(orderedFood) {
			g.setColor(Color.BLACK);
			g.drawString(choice + "?", xPos+5, yPos + 10);
		}
		else if(eatingFood) {
			g.setColor(Color.BLACK);
			g.drawString(choice, xPos+5, yPos + 10);
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
		state = State.standing;
		Point destination = tableMap.get(seatnumber);
		if(waitingSeatNumber >= 0){
			((GLRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
    	xDestination = destination.x + xSEAT_OFFSET;
    	yDestination = destination.y;
		command = Command.GoToSeat;
	}
	
	public void DoGoToWait() {
		findPlaceToWait();
		command = Command.GoToWait;
	}
	
	private void findPlaceToWait() {		
		for(int i = 0; i < ((GLRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.size(); i++){
			if(waitingSeatNumber < 0){
				if(((GLRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(i).tryAcquire()){
					waitingSeatNumber = i;
					xDestination = seatMap.get(i).x;
					yDestination = seatMap.get(i).y;
				}
			}
		}
		
		if(waitingSeatNumber < 0){
			xDestination = 10;
			yDestination = -40;
		}
	}

	public void DoExitRestaurant() {
		state = State.standing;
		if(waitingSeatNumber >= 0){
			((GLRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		eatingFood = false;
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
