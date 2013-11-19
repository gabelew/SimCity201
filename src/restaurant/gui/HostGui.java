package restaurant.gui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import city.gui.Gui;
import city.roles.HostRole;

public class HostGui implements Gui {

    private HostRole role = null;

    private int xPos = -20, yPos = -20;//default waiter position
    public int xDestination = -20;//default start position

	private int yDestination = -20;
	private boolean isPresent = true;
    
    static final int xWorkingPosition = 80;
    static final int yWorkingPosition = 70;
    static final int START_POSITION = -20;
    enum Command {none,enterRestaurant, leaveRestaurant };
    Command command = Command.none;
    
	private BufferedImage hostImg = null;

    public HostGui(HostRole role) {
        this.setRole(role);
        try {
		    hostImg = ImageIO.read(new File("imgs/host_v1.png"));
		} catch (IOException e) {
		}
    }

    public HostRole getRole() {
		return role;
	}

	public void setRole(HostRole role) {
		this.role = role;
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
		g.drawImage(hostImg, xPos, yPos, null);
    }

	public void setPresent(boolean p) {
		isPresent = p;
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
}
