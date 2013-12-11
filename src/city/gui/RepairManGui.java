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
	
	private int xPos = 40;
	private int yPos = 200;
	private int xDestination = 0;
	private int yDestination = 0;
	private enum state{waiting, backToHome, atCustomerHome, goingToCustomer};
	state workingState;
	
	static final int xHomeLoc = 0;
	static final int yHomeLoc = 0;
	static final int xSTART_POS = 40;
	static final int ySTART_POS = 200;
	static final int xWAITING_START = -20;
	static final int yWAITING_START = -20;
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
		
	}
	
	public void updatePosition() 
	{
		//System.out.println(xPos+ " " + yPos + " TARGET >> "+ xDestination + " "+ yDestination);
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
				role.msgActionDone();
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
			
			if(xPos == xDestination && yPos == yDestination && command == Command.leaving)
			{
				command = Command.none;
				role.msgActionDone();
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				xDestination = 100;
				yDestination = 200;
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
		xDestination = ySTART_POS;
		yDestination = xSTART_POS;
		command = Command.leaving;
	}

	public void goToCustomer() 
	{
		xDestination = 200;
		yDestination = 200;
		command = Command.goToCustomer;
		isPresent = true;
		
	}
}
