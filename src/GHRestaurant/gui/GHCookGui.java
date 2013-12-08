package GHRestaurant.gui;


import restaurant.interfaces.*;
import GHRestaurant.roles.*;

import java.awt.*;

import city.gui.Gui;

public class GHCookGui implements Gui {

    private Cook agent = null;

    private int xPos = 382, yPos = 260;//default cook position
    private int xDestination = 382, yDestination = 260;//default start position
    private int tableNumber;
    private boolean isPresent = false;

    public static final int xTable = 200;
    public static final int yTable = 250;

    public GHCookGui(Cook agent) {
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
          ((GHCookRole) agent).msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return isPresent;
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

	@Override
	public void setPresent(boolean b) {
		isPresent = b;
	}
}
