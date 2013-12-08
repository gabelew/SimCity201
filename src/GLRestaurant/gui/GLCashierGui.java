package GLRestaurant.gui;
import GLRestaurant.gui.GLHostGui.Command;
import GLRestaurant.roles.GLCashierRole;
import city.gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GLCashierGui implements Gui {
	private BufferedImage cashierImg = null;
	private final int START_POSITION = -20;
	private final int xWorkingPosition = 64;
	private final int yWorkingPosition = 200;
	enum Command {none, enterRestaurant, leaveRestaurant};
	Command command = Command.none;
    private GLCashierRole role = null;
    private boolean isPresent = false;
    private int xPos = START_POSITION, yPos = START_POSITION;//default position
    private int xDestination = START_POSITION, yDestination = START_POSITION;//default start position

    public GLCashierGui(GLCashierRole agent) {
        this.role = agent;
        try {
        	cashierImg = ImageIO.read(new File("imgs/cashier_v1.png"));
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
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationHasLeftRestaurant();
        		command = Command.none;
        	}else if(command == Command.enterRestaurant){
        		command = Command.none;
        	}
        		
        }

    }

    public void draw(Graphics2D g) {
        g.drawImage(cashierImg, xPos,yPos,null);
    }

    public boolean isPresent() {
        return isPresent;
    }
    
    public void DoLeaveRestaurant() {
        xDestination = START_POSITION;
        yDestination = START_POSITION;
        command = Command.leaveRestaurant;
    }
    public void DoEnterRestaurant(){
        xDestination = xWorkingPosition;
        yDestination = yWorkingPosition; 
        command = Command.enterRestaurant;   	
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
