package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import city.PersonAgent;

public class PersonGui implements Gui{
	
	private PersonAgent agent = null;
	private boolean isPresent = false;
	
	private static BufferedImage personImg = null;
	private static BufferedImage carImgRight = null;
	private static BufferedImage carImgUp = null;
	private static BufferedImage carImgDown = null;
	public SimCityGui gui;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, walkToDestination, enterHome};
	private Command command=Command.noCommand;
	private enum DrivingDirection {up,down,right};
	DrivingDirection drivingDirection = DrivingDirection.right;
	private int xHomePosition = 20;
	private int yHomePosition = 20;
	
	private enum State {walking, driving};
	private State state = State.walking;
	
	public PersonGui(PersonAgent c, SimCityGui gui){ //HostAgent m) {
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    personImg = ImageIO.read(new File(path.toString() + "person.png"));
		    carImgRight = ImageIO.read(new File(path.toString() + "carRight.png"));
		    carImgUp = ImageIO.read(new File(path.toString() + "carBack.png"));
		    carImgDown = ImageIO.read(new File(path.toString() + "carFront.png"));
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
		if(agent.car ==false){
			if (xPos < xDestination && (yPos - 103)%80==0)
				xPos++;
			else if (xPos > xDestination && (yPos - 103)%80==0)
				xPos--;
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination && xPos == xDestination)
				yPos--;
			else if(xPos != xDestination){
				yPos++;
			}
		}else if(agent.car==true){
			if(yPos == yDestination && xPos == xDestination){}
			else if(xPos > xDestination  && (yPos - 115)%80==0 && xPos < 950){
				drivingDirection = DrivingDirection.right;
				xPos++;
			}
			else if(yPos != yDestination && xPos != xDestination && (yPos - 115)%80!=0){
				//System.out.println((yPos - 115)%80);
				//System.out.println(" xDestination "+ xDestination +" yDestination "+ yDestination);
				drivingDirection = DrivingDirection.down;
				yPos++;
			}else if (xPos == 950){
				drivingDirection = DrivingDirection.right;
				xPos = 0;
				yPos = yDestination + 46;
			}
			else if (yPos != yDestination &&(xPos < xDestination || (xPos < 950 && (Math.abs(yPos - yDestination) > 47 || yPos - yDestination == -33)))){
				drivingDirection = DrivingDirection.right;
				xPos++;
			}
			else if (yPos > yDestination){
				drivingDirection = DrivingDirection.up;
				yPos--;
			}
			else if(xPos != xDestination){
				drivingDirection = DrivingDirection.down;
				yPos++;
			}
		}

		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.walkToDestination){
				command = Command.noCommand;
				agent.msgAnimationFinshed();
			}
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		if(agent.car == true)
		{
			if(this.drivingDirection == DrivingDirection.right)
				g.drawImage(carImgRight, xPos, yPos, null);
			else if(this.drivingDirection == DrivingDirection.down)
				g.drawImage(carImgDown, xPos, yPos, null);
			else if(this.drivingDirection == DrivingDirection.up)
				g.drawImage(carImgUp, xPos, yPos, null);
		}
		else
		{
			g.drawImage(personImg, xPos, yPos, null);
		}
		
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
