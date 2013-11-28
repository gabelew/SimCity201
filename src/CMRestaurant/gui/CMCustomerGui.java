package CMRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import restaurant.gui.FoodIcon;
import CMRestaurant.roles.CMCustomerRole;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

public class CMCustomerGui implements Gui{

	private CMCustomerRole role = null;
	private CMWaiterGui waiterGui = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean toldToSeat = false;
	private static BufferedImage customerImg = null;
	private static BufferedImage customerSittingImg = null;
	//private HostAgent host;
	//SimCityGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToHost, WaitForSeat, GoToWaiter, GoToSeat, LeaveRestaurant, GoToCashier};
	private Command command=Command.noCommand;
	private enum State {standing, sitting};
	private State state = State.standing;
	private FoodIcon food = null;
	private Semaphore executingAnimation = new Semaphore(1,true);
	private int waitingSeatNumber = -1;
	private Map<Integer, Point> seatMap = new HashMap<Integer, Point>();

    static final int CUST_START_POS = -40;
    static final int xWAITING_AREA = 50;
    static final int yWAITING_AREA = 70;
    static final int xSEAT_OFFSET = 25;
    static final int ySEAT_OFFSET = 0;
    static final int xFOOD_OFFSET = -20;
    static final int yFOOD_OFFSET = 10;
    static final int xFOOD_QUESTION_OFFSET = 15;
    static final int yFOOD_QUESTION_OFFSET = -20;
    static final int xCASHIER_POSITION = 80;
    static final int yCASHIER_POSITION = 245;
    static final int NWAITSEATS = 6;
    static final int NWAITSEATS_COLUMNS = 3;
    static final int NWAITSEATS_ROWS = 2;
    static final int WAITINGSEATS_X_START = 20;
    static final int WAITINGSEATS_Y_START = 10;
    static final int WAITINGSEATS_X_GAP = 30;
    static final int WAITINGSEATS_Y_GAP = 30;
    

	public CMCustomerGui(CMCustomerRole role){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    customerImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		    customerSittingImg = ImageIO.read(new File(path.toString() + "customer_sitting_v1.png"));
		} catch (IOException e) {
		}
		
		this.role = role;
		xPos = CUST_START_POS;
		yPos = CUST_START_POS;
		xDestination = xWAITING_AREA;
		yDestination = yWAITING_AREA;
		
        for(int i = 0; i<NWAITSEATS/NWAITSEATS_ROWS; i++){
        	for(int j = 0; j < NWAITSEATS/NWAITSEATS_COLUMNS; j++ )
        		seatMap.put(j+i*2, new Point(i*WAITINGSEATS_X_GAP+WAITINGSEATS_X_START, j*WAITINGSEATS_Y_GAP+WAITINGSEATS_Y_START));
        }
        
		//maitreD = m;
		//this.gui = gui;
		
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
			if(command==Command.GoToHost && xDestination == xWAITING_AREA && yDestination == yWAITING_AREA)
			{
				role.msgAnimationFinishedDoEnterRestaurant();
				command=Command.noCommand;
			}
			else if(command==Command.WaitForSeat){
				if(waitingSeatNumber >= 0)
					state = State.sitting;
				command=Command.noCommand;
			}
			else if(command==Command.GoToWaiter){
				command=Command.noCommand;
				waiterGui.msgImReadyToBeSeated(this);
			}
			else if (command==Command.GoToSeat && toldToSeat){ 
				toldToSeat = false;
				state = State.sitting;
				role.msgAnimationFinishedGoToSeat();
				command=Command.noCommand;
			}
			else if (command==Command.GoToCashier
					&& xDestination == xCASHIER_POSITION && yDestination == yCASHIER_POSITION){ 
				role.msgAnimationFinishedGoToCashier();
				command=Command.noCommand;
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
				command=Command.noCommand;
			}
		}
	}

	public void draw(Graphics2D g) {
		if(state == State.sitting)
		{
			g.drawImage(customerSittingImg, xPos, yPos, null);
		}
		else
		{
			g.drawImage(customerImg, xPos, yPos, null);
		}
	}
	
	public void drawFood(Graphics2D g) {
		if(food != null)
		{
			if(food.type == "question")
				g.drawImage(food.iconImg, xPos+xFOOD_QUESTION_OFFSET, yPos+yFOOD_QUESTION_OFFSET, null);
			else
				g.drawImage(food.iconImg, xPos+xFOOD_OFFSET, yPos+yFOOD_OFFSET, null);
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
	
	public void orderFood(String choice){
		food = new FoodIcon(choice + "q");
	}
	
	public void doneOrderingFood(){
		if(food !=null)
			food = null;
	}

	public void foodIsHere(String choice){
		food = new FoodIcon(choice);
	}
	
	public void doneEatingFood(){
		if(food !=null)
			food = null;
	}
	
	public void DoGoToSeat() {
		state = State.standing;
		toldToSeat = true;
	}

	public void msgYourTableIsReady(CMWaiterGui g) {
		xDestination = xWAITING_AREA;
		yDestination = yWAITING_AREA;
		waiterGui = g;
		command = Command.GoToWaiter;		
	}
	
	public void msgGoSitAtTable(Point p){
		xDestination = p.x + xSEAT_OFFSET;
		yDestination = p.y + ySEAT_OFFSET;
		if(waitingSeatNumber >= 0){
			((CMRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		command = Command.GoToSeat;
	}
	
	public void DoExitRestaurant() {
		state = State.standing;

		if(waitingSeatNumber >= 0){
			((CMRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
		xDestination = CUST_START_POS;
		yDestination = CUST_START_POS;
		command = Command.LeaveRestaurant;
	}
	
	public void DoEnterRestaurant() {
		System.out.println("chad im here");
		xDestination = xWAITING_AREA;
		yDestination = yWAITING_AREA;
		command = Command.GoToHost;
	}

	public void doGoToCashier() {
		state = State.standing;
		xDestination = xCASHIER_POSITION;
		yDestination = yCASHIER_POSITION;
		command = Command.GoToCashier;
		
	}

	public void DoWaitForTable() {
		findPlaceToWait();
		command = Command.WaitForSeat;
	}
	
	private void findPlaceToWait() {		
		for(int i = 0; i < ((CMRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.size(); i++){
			if(waitingSeatNumber < 0){
				if(((CMRestaurantAnimationPanel)role.restaurant.insideAnimationPanel).waitingSeats.get(i).tryAcquire()){
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





}
