package GCRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import GCRestaurant.roles.GCHostRole;

public class GCHostGui implements Gui{

	private GCHostRole role = null;
	private boolean isPresent = true;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private final int StartPos = -20;
	private final int xWorkPos = 50;
	private final int yWorkPos = 55;

	private BufferedImage hostImg = null;
	enum Command {none,enterRestaurant, leaveRestaurant };
    Command command = Command.none;
    
	public GCHostGui(GCHostRole r){
		this.role = r;
		try {hostImg = ImageIO.read(new File("imgs/host_v1.png"));} 
		catch (IOException e) {}
		
		xPos = StartPos;
		yPos = StartPos;
		xDestination = StartPos;
		yDestination = StartPos;
	}

	public void updatePosition() {
		if(xPos != xDestination)
		{
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
		}
		else
		{
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
		}
	
		if(xPos == xDestination && yPos == yDestination){
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationHasLeftRestaurant();
        		command = Command.none;
        	}else if(command == Command.enterRestaurant){
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

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoLeaveRestaurant() {
        xDestination = StartPos;
        yDestination = StartPos;
        command = Command.leaveRestaurant;
    }
    public void DoEnterRestaurant(){
        xDestination = xWorkPos;
        yDestination = yWorkPos; 
        command = Command.enterRestaurant;   	
    }
}
