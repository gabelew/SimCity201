package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.interfaces.*;
//import restaurant.Host;

import java.awt.*;

public class HostGui implements Gui {

    private Host agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private int tableNumber;

    public static final int xTable = 200;
    public static final int yTable = 250;

    public HostGui(Host agent) {
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

        if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == xTable + 20) & (yDestination == yTable + (80-(tableNumber*100)))) {
           agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int tablenumber) {
    	xDestination = xTable + 20;
        yDestination = yTable + (80-(tablenumber*100));
        tableNumber = tablenumber;
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;

    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
