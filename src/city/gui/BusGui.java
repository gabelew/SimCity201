package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.BusAgent;

public class BusGui implements Gui{
	
	private BusAgent agent = null;
	private boolean isPresent = false;
	private char type;
	
	private static BufferedImage bus_back = null;
	private static BufferedImage bus_front = null;
	public SimCityGui gui;
	public int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, atBusStop};
	private Command command=Command.noCommand;
	
	public BusGui(BusAgent b, SimCityGui gui, char type){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    bus_back = ImageIO.read(new File(path.toString() + "bus_back.png"));
		    bus_front = ImageIO.read(new File(path.toString() + "bus_front.png"));
		} catch (IOException e) {
		}
		
		agent = b;
		b.getBusStops();
		this.type = type;
		
		if(type == 'B'){
			xPos = 30;
			yPos = 410;
			xDestination = 30;
			yDestination = 410;
		}
		else if(type == 'F'){
			xPos = 825;
			yPos = -40;
			xDestination = 825;
			yDestination = -40;
		}
        
		this.gui = gui;
		
	}

	
	@Override
	public void updatePosition() {
		
			if (yPos != yDestination && type == 'F' && yPos < 420){
				yPos++;
			}else if (yPos != yDestination && type == 'B' && yPos > -50){
				yPos--;
			}else if(type == 'B' && yPos <= -50){
				yPos = 400;
			}else if(type == 'F' && yPos >= 420){
				yPos = -40;
			}

				if (xPos == xDestination && yPos == yDestination) {
					if(command == Command.atBusStop){
						command = Command.noCommand;
						agent.msgAtStop(new Point(xDestination, yDestination));
					}
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
	
	public void GoToBusStop(Point p){
		xDestination = p.x;
		yDestination = p.y;
		command = Command.atBusStop;
	}
	
	public void doGoToRest() {
		if(type == 'B'){
			xDestination = 30;
			yDestination = -40;
		}
		
		else if(type == 'F'){
			xDestination = 825;
			yDestination = 410;
		}
	}

}
