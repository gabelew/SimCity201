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

public class RepairManGui implements Gui
{
	private RepairManRole role = null;
	private boolean isPresent = true;
	
	private static BufferedImage repairmanImg = null;
	
	private int xPos = 0;
	private int yPos = 0;
	private int xDestination = 0;
	private int yDestination = 0;
	private enum state{waiting, backToHome, atCustomerHome, goingToCustomer};
	state workingState;
	
	static final int xSTART_POS = -20;
	static final int ySTART_POS = 200;
	enum Command {none, goToCustomer, leaving};
	Command command = Command.none;
	public RepairManGui(RepairManRole Role) 
	{
		try 
		{
			StringBuilder path = new StringBuilder("imgs/");
			repairmanImg = ImageIO.read(new File(path.toString() + "repairman.png"));
		} 
		catch(IOException e) {}
		this.role = Role;
		
		this.xPos = xSTART_POS;
		this.yPos = ySTART_POS;
		this.xDestination = xSTART_POS;
		this.yDestination = ySTART_POS;
	}
	
	public void updatePosition() 
	{
		
		if(command == Command.leaving)
		{	
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
			if(yPos == yDestination)
			{
				if (xPos < xDestination)
					xPos++;
				else if (xPos > xDestination)
					xPos--;
			}
		}
		else
		{
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
			if(xPos == xDestination)
			{
				if (yPos < yDestination)
					yPos++;
				else if (yPos > yDestination)
					yPos--;
			}
		}
			if(xPos == xDestination && yPos == yDestination && command == Command.goToCustomer)
			{
				command = Command.none;
				role.msgActionDone();
			}
			
			if(xPos == xDestination && yPos == yDestination && command == Command.leaving)
			{
				command = Command.none;
				role.msgActionDone();
				xPos = xSTART_POS;
				yPos = ySTART_POS;
				isPresent = false;
			}
		
	}

	
	public void draw(Graphics2D g)
	{
		g.drawImage(repairmanImg, xPos, yPos, null);
	}

	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void leaveHome() 
	{
		xDestination = xSTART_POS;
		yDestination = ySTART_POS;
		command = Command.leaving;
	}

	public void goToCustomer(int xdest, int ydest) 
	{
		xDestination = xdest;
		yDestination = ydest;
		command = Command.goToCustomer;
		isPresent = true;
		
	}
}
