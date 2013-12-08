package GLRestaurant.gui;

import GLRestaurant.roles.GLHostRole;
import city.gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class GLHostGui implements Gui {
	private final int START_POSITION = -20;
	private final int xWorkingPosition = 65;
	private final int yWorkingPosition = 89;
    private GLHostRole role = null;
    private boolean isPresent = true;
    private int xPos = START_POSITION, yPos = START_POSITION;//default host position
    private int xDestination = START_POSITION, yDestination = START_POSITION;//default start position
    static class Point {
    	int x;
    	int y;
    	Point (int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    Map<Integer, Point> tableMap = new HashMap<Integer, Point>();
    
    public static final Point TableOne = new Point(100,100);
    public static final Point TableTwo = new Point(200,100);
    public static final Point TableThree = new Point(300,100);
    enum Command {none, enterRestaurant, leaveRestaurant};
    Command command = Command.none;
    private BufferedImage hostImg = null;
    
    public GLHostGui(GLHostRole role) {
        this.role = role;
        try {
        	hostImg = ImageIO.read(new File("imgs/host_v1.png"));
        } catch (IOException e) {
        	
        }
        tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
    }
    
    public GLHostRole getRole() {
    	return role;
    }
    
    public void setRole(GLHostRole role) {
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
