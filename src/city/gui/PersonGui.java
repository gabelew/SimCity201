package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.PersonAgent;
import restaurant.CustomerAgent;
import restaurant.gui.Gui;

public class PersonGui implements Gui{
	
	private PersonAgent agent = null;
	private boolean isPresent = false;
	
	private static BufferedImage personImg = null;
	private static BufferedImage carImg = null;
	SimCityGui gui;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, walkToDestination};
	private Command command=Command.noCommand;
	
	private enum State {walking, driving};
	private State state = State.walking;
	
	public PersonGui(PersonAgent c, SimCityGui gui){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    personImg = ImageIO.read(new File(path.toString() + "person.png"));
		    carImg = ImageIO.read(new File(path.toString() + "person.png"));
		} catch (IOException e) {
		}
		
		agent = c;
		xPos = 20;
		yPos = 20;
		xDestination = 20;
		yDestination = 20;
        
		this.gui = gui;
		
	}

	
	@Override
	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.walkToDestination){
				agent.msgAnimationFinshed();
			}
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		if(state == State.driving)
		{
			g.drawImage(carImg, xPos, yPos, null);
		}
		else
		{
			g.drawImage(personImg, xPos, yPos, null);
		}
		
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoWalkTo(Point destination) {
		xPos = destination.x;
		yPos = destination.y;
		command = Command.walkToDestination;
		
	}

}
