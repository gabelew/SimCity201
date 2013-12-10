package GCRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import city.gui.Gui;
import GCRestaurant.roles.GCCashierRole;

public class GCCashierGui implements Gui{

	private boolean isPresent = true;
	private GCCashierRole role = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private int xWorkingPos = 100;
	private int yWorkingPos = 82;
	
	final int DEFAULT_POS = -20;
	private BufferedImage cashierImg;
	enum Command {none,enterRestaurant, leaveRestaurant };
    Command command = Command.none;
    
	public GCCashierGui(GCCashierRole r){
		try {cashierImg = ImageIO.read(new File("imgs/cashier_v1.png"));}
		catch (IOException e) {}
		
		this.role = r;
		xPos = DEFAULT_POS;
		yPos = DEFAULT_POS;
		xDestination = DEFAULT_POS;
		yDestination = DEFAULT_POS;
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
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationHasLeftRestaurant();
        		command = Command.none;
        	}else if(command == Command.enterRestaurant){
        		command = Command.none;
        	}
        }
	}

	public void DoLeaveRestaurant() {
        xDestination = DEFAULT_POS;
        yDestination = DEFAULT_POS;
        command = Command.leaveRestaurant;
    }
	
    public void DoEnterRestaurant(){
        xDestination = xWorkingPos;
        yDestination = yWorkingPos; 
        command = Command.enterRestaurant;   	
    }
	
	public void draw(Graphics2D g) {
		g.drawImage(cashierImg, xPos, yPos, null);
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
}
