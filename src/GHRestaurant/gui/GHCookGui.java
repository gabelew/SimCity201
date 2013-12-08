package GHRestaurant.gui;


import restaurant.CustomerAgent;
import restaurant.interfaces.*;
//import restaurant.Host;

import java.awt.*;

public class CookGui implements Gui {

    private Cook agent = null;

    private int xPos = 365, yPos = 230;//default waiter position
    private int xDestination = 365, yDestination = 230;//default start position
    private int tableNumber;

    public static final int xTable = 200;
    public static final int yTable = 250;

    public CookGui(Cook agent) {
        this.agent = agent;
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
          agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoCookIt() {
        xDestination = 400;
        yDestination = 180;

    }
    
    public void DoGoHome(){
    	xDestination = 365;
    	yDestination = 230;
    	
    }
    
    public void DoPlateIt(){
    	xDestination = 365;
    	yDestination = 230;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
