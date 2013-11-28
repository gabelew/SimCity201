package CMRestaurant.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import CMRestaurant.gui.CMHostGui.Command;
import CMRestaurant.roles.CMCashierRole;
import city.gui.Gui;
import city.gui.SimCityGui;

public class CMCashierGui implements Gui  {

	private CMCashierRole role = null;
	private boolean isPresent = true;
	private BufferedImage cashierImg = null;
	SimCityGui gui;

	static final int xSTART_POSITION = -20;
	static final int ySTART_POSITION = -2;   

    static final int xWorkingPosition = 57;
    static final int yWorkingPosition = 258;
    
    private int xPos = xSTART_POSITION, yPos = ySTART_POSITION;//default waiter position
    private int xDestination = xSTART_POSITION, yDestination = ySTART_POSITION;//default start position

    enum Command {none,enterRestaurant, leaveRestaurant };
    Command command = Command.none;
    
	public CMCashierGui(CMCashierRole cashier) {
		try {
		    cashierImg = ImageIO.read(new File("imgs/cashier_v1.png"));
		} catch (IOException e) {
		}
			
		setRole(cashier);
		xDestination = xSTART_POSITION;
		yDestination = ySTART_POSITION;
	}
		
	public CMCashierRole getRole() {
		return role;
	}

	public void setRole(CMCashierRole agent) {
		this.role = agent;
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
		g.drawImage(cashierImg, xPos, yPos, null);
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
		
	public boolean isPresent() {
		return isPresent;
	}

	public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    public void DoLeaveRestaurant() {
        xDestination = xSTART_POSITION;
        yDestination = ySTART_POSITION;
        command = Command.leaveRestaurant;
    }
    public void DoEnterRestaurant(){
        xDestination = xWorkingPosition;
        yDestination = yWorkingPosition; 
        command = Command.enterRestaurant;   	
    }
}
