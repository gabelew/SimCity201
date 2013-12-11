package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import city.gui.Gui;
import city.roles.RepairManRole;

public class RepairManDrivingGui implements Gui
{
	
	public SimCityGui gui;
	private RepairManRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage vanRightImg = null;
	private static BufferedImage vanBackImg = null;
	private static BufferedImage vanFrontImg = null;
	
	private int xPos, yPos;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	
	
	static final int xHomeLoc = 0;
	static final int yHomeLoc = 0;
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 90;
	static final int yWAITING_START = 190;
	
	enum DrivingDirection {up,down,right};
	enum Command {none, goToCustomer, backToHome};
	DrivingDirection drivingDirection = DrivingDirection.right;
	Command command = Command.none;
	public RepairManDrivingGui(RepairManRole Role,SimCityGui gui) 
	{
		try 
		{
			StringBuilder path = new StringBuilder("imgs/");
			vanRightImg = ImageIO.read(new File(path.toString() + "RepairVanRight.png"));
			vanBackImg = ImageIO.read(new File(path.toString() + "RepairVanBack.png"));
			vanFrontImg = ImageIO.read(new File(path.toString() + "RepairVanFront.png"));
		} 
		catch(IOException e) {}
		role = Role;
		this.gui=gui;
		//xHomeLoc = ;
	}
	
	public void updatePosition() 
	{
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
			
			if(xPos == xDestination && yPos == yDestination && command == Command.goToCustomer)
			{
				command = Command.none;
				isPresent = false;
			}
			
			if(xPos == xDestination && yPos == yDestination && command == Command.backToHome)
			{
				command = Command.none;
				isPresent = false;
			}
		
	}

	
	public void draw(Graphics2D g)
	{
		if(this.drivingDirection == DrivingDirection.right)
			g.drawImage(vanRightImg, xPos, yPos, null);
		else if(this.drivingDirection == DrivingDirection.down)
			g.drawImage(vanFrontImg, xPos, yPos, null);
		else if(this.drivingDirection == DrivingDirection.up)
			g.drawImage(vanBackImg, xPos, yPos, null);
	}

	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoBack()
	{
		xDestination = xHomeLoc;
		yDestination = yHomeLoc;
		command = Command.backToHome;
		isPresent = true;
	}
	
	public void DoGoFix(Point Locations)
	{
		xDestination=(int)Math.round(Locations.getX());
		yDestination=(int)Math.round(Locations.getY());
		command = Command.goToCustomer;
		isPresent = true;
	}
}
