package atHome.city;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.PersonAgent;
import city.gui.Gui;
import city.gui.SimCityGui;

public class AtHomeGui implements Gui{
	
	private PersonAgent agent = null;
	private boolean isPresent = false;
	
	private static BufferedImage personImg = null;
	public SimCityGui gui;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, walkToDestination, enterHome};
	private Command command=Command.noCommand;
	private int xHomePosition = 20;
	private int yHomePosition = 20;
	
	public AtHomeGui(PersonAgent c, SimCityGui gui){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    personImg = ImageIO.read(new File(path.toString() + "person.png"));
		} catch (IOException e) {
		}
		
		agent = c;
		xPos = 75;
		yPos = 103; //103+80*i
		xDestination = 75;
		yDestination = 103;
        
		this.gui = gui;
		
	}

	
	@Override
	public void updatePosition() {
			if (xPos < xDestination && (yPos - 115)%80==0)
				xPos++;
			else if (xPos > xDestination && (yPos - 115)%80==0)
				xPos--;
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
			yPos--;
		

		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.walkToDestination){
				command = Command.noCommand;
				agent.msgAnimationFinshed();
			}
		}
		
	}

	@Override
	public void draw(Graphics2D g) 
	{
		g.drawImage(personImg, xPos, yPos, null);
	}

	public void doEnterHome()
	{
	        xDestination = xHomePosition;
	        yDestination = yHomePosition; 
	        command = Command.enterHome;   	
	}
	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoWalkTo(Point destination) {
		xDestination = destination.x;
		yDestination = destination.y;
		command = Command.walkToDestination;
		
	}

}
