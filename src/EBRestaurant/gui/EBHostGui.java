package EBRestaurant.gui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBHostRole;

public class EBHostGui implements Gui {

    private EBHostRole agent = null;
    private BufferedImage hostImg = null;
    private int xPos = -20, yPos = -20;//default host position
    private int xDestination = -20, yDestination = -20;//default host position
    private final int xChange = 20;
    private final int yChange = -20;

    public int xTable = 100;
    public int yTable = 100;

    public EBHostGui(EBHostRole agent) {
        this.agent = agent;
        try {
		    hostImg = ImageIO.read(new File("imgs/host_v1.png"));
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
        		& (xDestination == xTable + xChange) & (yDestination == yTable + yChange)) {
           //agent.msgAtTable();
        }
        if (xPos==yChange && yPos==yChange)
        {
        	agent.msgAtStart();
        }
    }

    public void draw(Graphics2D g) {
    	g.drawImage(hostImg, xPos, yPos, null);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(EBCustomerRole customer, int tableNumber) {
        if (tableNumber==1)
        {
        	yTable=100;
        	xDestination = xTable + xChange;
        	yDestination = yTable + yChange;
        }
        else if (tableNumber==2)
        {
        	yTable=200;
        	xDestination = xTable + xChange;
        	yDestination = yTable + yChange;
        }
        else if (tableNumber==3)
        {
        	yTable=300;
        	xDestination = xTable + xChange;
        	yDestination = yTable + yChange;
        }
    }

    public void DoLeaveCustomer() {
        xDestination = yChange;
        yDestination = yChange;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoEnterRestaurant() {
		xDestination=80;
		yDestination=80;
	}
}
