package GHRestaurant.gui;
import GHRestaurant.roles.*;
import restaurant.interfaces.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;

public class GHHostGui implements Gui {

    private GHHostRole role = null;
    private boolean isPresent = false;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = 60, yDestination = 30;//default start position
    private int tableNumber;

    public static final int xTable = 200;
    public static final int yTable = 250;
	private BufferedImage hostImg = null;
	
	public enum Command {Enter, Leaving, none}
	public Command command;


    public GHHostGui(GHHostRole role) {
        this.role = role;
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

        if(xPos == xDestination && yPos == yDestination){
        	if(command == Command.Leaving){
        		role.msgAnimationHasLeftRestaurant();
        		command = Command.none;
        	}else if(command == Command.Enter){
        		command = Command.none;
        	}		
        }
    }

    public void draw(Graphics2D g) {
		g.drawImage(hostImg, xPos, yPos, null);
    }

    public boolean isPresent() {
        return isPresent;
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

	@Override
	public void setPresent(boolean b) {
		isPresent = b;
	}

	public void DoEnterRestaurant() {
        xDestination = 60;
        yDestination = 30;	
        command = Command.Enter;
	}

	public void DoLeaveRestaurant() {
        xDestination = -20;
        yDestination = -20;	
        command = Command.Leaving;		
	}
}
