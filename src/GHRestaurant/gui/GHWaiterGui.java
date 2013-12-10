package GHRestaurant.gui;
import GHRestaurant.roles.*;
import restaurant.interfaces.*;

//import restaurant.Waiter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import city.gui.SimCityGui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
public class GHWaiterGui implements Gui {

    private GHWaiterRole role = null;
    private boolean isPresent = false;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = 20, yDestination = 20;//default start position
    private int xCook = 330, yCook = 230;
    private int tableNumber;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	private static BufferedImage waiterImg = null;


    //SimCityGui gui;
    
    public static final int xTable = 200;
    public static final int yTable = 250;

    public GHWaiterGui(GHWaiterRole w) {
        this.role = w;
        //this.gui = gui;
		try {
		    waiterImg = ImageIO.read(new File("imgs/waiter_v1.png"));
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

        if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == xTable + 20) && (yDestination == yTable + (80-(tableNumber*100)))) {
           ((GHWaiterRole) role).msgAtTable();
        }
           else if(xPos == xDestination && yPos == yDestination
           		&& (xDestination == xCook) && (yDestination == yCook)) {
               ((GHWaiterRole) role).msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
		g.drawImage(waiterImg, xPos, yPos, null);
    	
    }

    public boolean isPresent() {
        return isPresent;
    }
    
    public void setPresent(boolean p){
    	isPresent = p;
    }
    
    /*public void setWork(){
    	((GHWaiterRole) role).gotWork();
    	setPresent(true);
    }*/
    
    public void GoOnBreak(){
    	((GHWaiterRole) role).msgTryToGoOnBreak();
    }
    
    public void GoBackToWork(){
    	((GHWaiterRole) role).msgGoBackToWork();
    }
    
    public boolean OnBreak(){
    	return ((GHWaiterRole) role).getOnBreak();
    }

    public void DoBringToTable(Customer customer, int tablenumber) {
    	xDestination = xTable + 20;
        yDestination = yTable + (80-(tablenumber*100));
        tableNumber = tablenumber;
    }
    
    public void DoGoToTable(int tablenumber){
    	xDestination = xTable + 20;
    	yDestination = yTable + (80-(tablenumber*100));
    	tableNumber = tablenumber;
    }
    
    public void GoToCook(){
    	xDestination = xCook;
    	yDestination = yCook;
    }

    public void DoLeaveCustomer() {
        xDestination = 20;
        yDestination = 20;

    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
