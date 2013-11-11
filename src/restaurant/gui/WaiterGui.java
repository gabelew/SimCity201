package restaurant.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import city.gui.SimCityGui;
import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

public class WaiterGui implements Gui {

	private WaiterAgent agent = null;
	private boolean isPresent = false;
	private static BufferedImage waiterImg = null;
	SimCityGui gui;

	private enum Command {noCommand, GoToHost, GoToTable, GoToKitchen, GoToCashier, GoToRestArea};
	private Command command=Command.noCommand;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private FoodIcon food = null; 
	private CookGui cookGui;
	public static List<Semaphore> waitingSeats = new ArrayList<Semaphore>();
	private int waitingSeatNumber = -1;
	private Map<Integer, Point> seatMap = new HashMap<Integer, Point>();
	
    static final int START_POSITION = -20;  
    static final int xREST_POSITION = 335;//350;  
    static final int yREST_POSITION = 375;//400;  
    static final int xBREAK_POSITION = -20;  
    static final int yBREAK_POSITION = -20; 
    static final int xCOOK_DROPOFF_POSITION = 350;  
    static final int yCOOK_DROPOFF_POSITION = 100;
    static final int xCOOK_PICKUP_POSITION = 350;  
    static final int yCOOK_PICKUP_POSITION = 150;
    static final int WAITER_TABLE_OFFSET = 20;
    static final int xTABLE_AREA = 120;
    static final int yTABLE_AREA = -5;
    static final int xTABLE_AREA_WIDTH = 180;
    static final int yTABLE_AREA_WIDTH = 430;
    static final int xREST_AREA_START = 325;
    static final int xREST_AREA_END = 425;
    static final int yREST_AREA_START = 365;
    static final int yREST_AREA_END = 445;
    static final int xFOOD_OFFSET = 10;
    static final int yFOOD_OFFSET = 4;
    static final int xCUST_PICKUP_LOCATION = 80;
    static final int yCUST_PICKUP_LOCATION = 80;
    static final int xCASHIER_POSITION = 80;
    static final int yCASHIER_POSITION = 245;
    static final int NWAITSEATS = 16;
    static final int NWAITSEATS_COLUMNS = 8;
    static final int NWAITSEATS_ROWS = 2;
    static final int WAITINGSEATS_X_START = 345;
    static final int WAITINGSEATS_Y_START = 375;
    static final int WAITINGSEATS_X_GAP = 15;
    static final int WAITINGSEATS_Y_GAP = 35;
    static final int xWAITING_OVERFLOW_POS = 335;
    static final int yWAITING_OVERFLOW_POS = 465;
    
	public WaiterGui(WaiterAgent w, SimCityGui gui) {
		try {
		    waiterImg = ImageIO.read(new File("imgs/waiter_v1.png"));
		} catch (IOException e) {
		}
	    
		agent = w;
		xPos = START_POSITION;
		yPos = START_POSITION;
		xDestination = START_POSITION;
		yDestination = START_POSITION;
		
        for(int i = 0; i<NWAITSEATS/NWAITSEATS_ROWS; i++){
        	for(int j = 0; j < NWAITSEATS/NWAITSEATS_COLUMNS; j++ )
        		seatMap.put(j+i*2, new Point(i*WAITINGSEATS_X_GAP+WAITINGSEATS_X_START, j*WAITINGSEATS_Y_GAP+WAITINGSEATS_Y_START));
        }
		
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

        if (xPos == xDestination && yPos == yDestination && command==Command.GoToHost){
            command=Command.noCommand;
        	agent.msgAtEntrance();
        }
        else  if (xPos == xDestination && yPos == yDestination && xPos > xTABLE_AREA 
        		&& xPos < xTABLE_AREA + xTABLE_AREA_WIDTH && yPos > yTABLE_AREA && yPos < yTABLE_AREA + yTABLE_AREA_WIDTH
        		 && command==Command.GoToTable){
            command=Command.noCommand;
            agent.msgAtTable();
         }
        else  if (xPos == xDestination && yPos == yDestination && xPos == xCASHIER_POSITION 
        		&& yPos == yCASHIER_POSITION && command==Command.GoToCashier){
            command=Command.noCommand;
            agent.msgAtCashier();
         }
        else  if (xPos == xDestination && yPos == yDestination && command==Command.GoToKitchen){
            command=Command.noCommand;
        	agent.msgAtKitchen();
         }else if(xPos == xDestination && yPos == yDestination && xPos > xREST_AREA_START 
        		&& xPos < xREST_AREA_END && yPos > yREST_AREA_START && yPos < yREST_AREA_END
        		 && command==Command.GoToRestArea){
             command=Command.noCommand;
             findASpotToRest();
        	 
         }
	}

	private void findASpotToRest() {	
		for(int i = 0; i < waitingSeats.size(); i++){
			if(waitingSeatNumber < 0){
				if(waitingSeats.get(i).tryAcquire()){
					waitingSeatNumber = i;
					xDestination = seatMap.get(i).x;
					yDestination = seatMap.get(i).y;
				}
			}
		}
		
		if(waitingSeatNumber < 0){
			xDestination = xWAITING_OVERFLOW_POS;
			yDestination = yWAITING_OVERFLOW_POS;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(waiterImg, xPos, yPos, null);
		if(food != null)
			g.drawImage(food.iconImg, xPos+xFOOD_OFFSET, yPos+yFOOD_OFFSET, null);
	}

	public void setWorking() {
		agent.goesToWork();
		setPresent(true);
	}
	
	public void askBreak() {
		agent.msgAskForBreak();
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public void DoBringToTable(int t) {
        xDestination = gui.getTablesXCoord(t) + WAITER_TABLE_OFFSET;
        yDestination = gui.getTablesYCoord(t) - WAITER_TABLE_OFFSET;

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command=Command.GoToTable;
	}

	int table = -1;
	public void DoBringToTable(CustomerAgent c, int t) {
        c.getGui().msgYourTableIsReady(this);
        table = t;
	}

	public void msgImReadyToBeSeated(CustomerGui c){
        xDestination = gui.getTablesXCoord(table) + WAITER_TABLE_OFFSET;
        yDestination = gui.getTablesYCoord(table) - WAITER_TABLE_OFFSET;
        
		c.msgGoSitAtTable(new Point(gui.getTablesXCoord(table), gui.getTablesYCoord(table)));

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
		command=Command.GoToTable;
	}
	
    public void doGoToRestPos() {
        xDestination = xREST_POSITION;
        yDestination = yREST_POSITION;

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command = Command.GoToRestArea;
    }
	public void doGoToBreakPos() {
        xDestination = xBREAK_POSITION;
        yDestination = yBREAK_POSITION;
		
	}
	public void doGoToEntrance() {
        xDestination = xCUST_PICKUP_LOCATION;
        yDestination = yCUST_PICKUP_LOCATION;

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command=Command.GoToHost;
	}

	public void doGoDropOffOrderAtKitchen() {
        xDestination = xCOOK_DROPOFF_POSITION;
        yDestination = yCOOK_DROPOFF_POSITION;
        
		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command=Command.GoToKitchen;
	}
	public void doGoPickUpOrderAtKitchen() {
        xDestination = xCOOK_PICKUP_POSITION;
        yDestination = yCOOK_PICKUP_POSITION;

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command=Command.GoToKitchen;
	}
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
	public void grabbingFood(String choice){
		food = new FoodIcon(choice + "q");
	}
	
	public void placedOrder(){
		if(food !=null)
			food = null;
	}

	public void servingFood(WaiterAgent w, String choice, int t){
		cookGui.msgPickingUpMyOrder(w, choice, t);
		food = new FoodIcon(choice);
	}
	
	public void doneServingOrder(){
		if(food !=null)
			food = null;
	}

	public void doGoToCashier() {
        xDestination = xCASHIER_POSITION;
        yDestination = yCASHIER_POSITION;

		if(waitingSeatNumber >= 0){
			waitingSeats.get(waitingSeatNumber).release();
			waitingSeatNumber = -1;
		}
		
        command=Command.GoToCashier;
	}

	public void setCookGui(CookGui g){
		cookGui = g;
	}




}
