package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.BusAgent;
import city.PersonAgent;
import restaurant.CustomerAgent;

public class BusGui implements Gui{
	
	private BusAgent agent = null;
	private boolean isPresent = false;
	
	private static BufferedImage bus_back = null;
	private static BufferedImage bus_front = null;
	public SimCityGui gui;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, walkToDestination};
	private Command command=Command.noCommand;
	
	private enum State {walking, driving};
	private State state = State.walking;
	
	public BusGui(BusAgent b, SimCityGui gui, char type){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    bus_back = ImageIO.read(new File(path.toString() + "bus_back.png"));
		    bus_front = ImageIO.read(new File(path.toString() + "bus_front.png"));
		} catch (IOException e) {
		}
		
		agent = b;
		
		if(type == 'B'){
			xPos = 30;
			yPos = 350;
			xDestination = 30;
			yDestination = 70;
		}
		else if(type == 'F'){
			xPos = 825;
			yPos = 70;
			xDestination = 825;
			yDestination = 300;
		}
        
		this.gui = gui;
		
	}

	
	@Override
	public void updatePosition() {
			if (xPos < xDestination) //&& (yPos - 130)%80==0)
				xPos++;
			else if (xPos > xDestination) //&& (yPos - 103)%80==0)
				xPos--;
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
		

		if (xPos == xDestination && yPos == yDestination) {
			//if(command == Command.walkToDestination){
				command = Command.noCommand;
				//agent.msgAtStop();
			//}
		}

	}

	@Override
	public void draw(Graphics2D g) {
		if(xPos == 30)
		{
			g.drawImage(bus_back, xPos, yPos, null);
		}
		if(xPos == 825){
			g.drawImage(bus_front, xPos, yPos, null);
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
		xDestination = destination.x;
		yDestination = destination.y;
		command = Command.walkToDestination;
		
	}

}
