package restaurant.gui;


import restaurant.HostAgent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = 80, yDestination = 70;//default start position
    
    static final int START_POSITION = 20;
    
	private BufferedImage hostImg = null;

    public HostGui(HostAgent agent) {
        this.setAgent(agent);
        try {
		    hostImg = ImageIO.read(new File("imgs/host_v1.png"));
		} catch (IOException e) {
		}
    }

    public HostAgent getAgent() {
		return agent;
	}

	public void setAgent(HostAgent agent) {
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

    }

    public void draw(Graphics2D g) {
		g.drawImage(hostImg, xPos, yPos, null);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoLeaveCustomer() {
        xDestination = START_POSITION;
        yDestination = START_POSITION;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
