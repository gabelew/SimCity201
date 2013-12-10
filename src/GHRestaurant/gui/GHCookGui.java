package GHRestaurant.gui;


import restaurant.interfaces.*;
import GHRestaurant.roles.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;

public class GHCookGui implements Gui {

    private GHCookRole role = null;

    private int xPos = -20, yPos = -20;//default cook position
    private int xDestination = 382, yDestination = 250;//default start position
    private int tableNumber;
    private boolean isPresent = false;
    enum Command {none, leaveRestaurant};
	Command command = Command.none;

    public static final int xTable = 200;
    public static final int yTable = 250;
	private BufferedImage cookImg = null;


    public GHCookGui(GHCookRole role) {
        this.role = role;
		try {
		    cookImg = ImageIO.read(new File("imgs/chef_v1.png"));
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

        if (xPos == xDestination && yPos == yDestination) {
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationLeaving();
        	}
        	else{
        		((GHCookRole) role).msgAtTable();
        	}
        }
    }

    public void draw(Graphics2D g) {
		g.drawImage(cookImg, xPos, yPos, null);
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void DoCookIt() {
        xDestination = 450;
        yDestination = 160;

    }
    
    public void DoGoHome(){
    	xDestination = 382;
    	yDestination = 250;
    	
    }
    
    public void DoLeaveRestaurant(){
    	xDestination = -20;
    	yDestination = -20;
    	command = Command.leaveRestaurant;
    }
    
    public void DoPlateIt(){
    	xDestination = 382;
    	yDestination = 250;
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
