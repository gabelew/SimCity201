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
	public int xPos;
	public int yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, walkToDestination, enterHome, walkingToBus, walkingOnToBus, waitingForBus};
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
		this.isPresent = true;
		if(agent.car == false){
			if(yPos == yDestination && xPos == xDestination){
				if(command != Command.waitingForBus && command != Command.walkingToBus){
					this.isPresent = false;
				}
			}
			else if (xPos < xDestination && (yPos - 103)%80==0)
				xPos++;
			else if (xPos > xDestination && (yPos - 103)%80==0)
				xPos--;
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination && xPos == xDestination)
				yPos--;
			else if(xPos != xDestination){
				yPos++;
			}else if(xPos != xDestination && yPos != yDestination){
				System.out.println("person stuck");
			}
		}else {//if(agent.car==true){
			this.isPresent = true;
			if(yPos == yDestination && xPos == xDestination){
				this.isPresent = false;
				
			}
			else if(xPos > xDestination  && (yPos - 115)%80==0 && xPos < 950){
				drivingDirection = DrivingDirection.right;
				xPos++;
			}
			else if(yPos != yDestination && xPos != xDestination && (yPos - 115)%80!=0){
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
			}else{
				System.out.println("car stuck");
			}
		}

		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.walkToDestination){
				command = Command.noCommand;
				agent.msgAnimationFinshed();
			}else if(command == Command.walkingToBus){
				command = Command.waitingForBus;
				agent.msgAnimationFinshed();
			}else if(command == Command.walkingOnToBus){
				isPresent = false;
				agent.msgAnimationFinshed();
				command = Command.noCommand;
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


	public void doGoToBus() {
		if(xPos < 432){
			xDestination = 67;
			if(yPos >= 68 && yPos <= 68+60)
				yDestination = 85;
			else if(yPos >= 68+80 && yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos >= 68+80*2 && yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos >= 68+80*3 && yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}else{
			xDestination = 797;
			if(yPos >= 68 && yPos <= 68+60)
				yDestination = 85;
			else if(yPos >= 68+80 && yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos >= 68+80*2 && yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos >= 68+80*3 && yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}
		command = Command.walkingToBus;
	}


	public void doGetOnBus() {

		if(xPos < 432){
			xDestination = agent.busLeft.getBusGui().xPos;
			yDestination = agent.busLeft.getBusGui().yPos;
		}else{
			xDestination = agent.busRight.getBusGui().xPos;
			yDestination = agent.busRight.getBusGui().yPos;
		}
		
		command = Command.walkingOnToBus;
	}


	public void doGetOffBus() {
		this.isPresent = true;
		if(xPos < 432){
			xDestination = 67;
			if(yPos >= 68 && yPos <= 68+60)
				yDestination = 85;
			else if(yPos >= 68+80 && yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos >= 68+80*2 && yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos >= 68+80*3 && yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}else{
			xDestination = 797;
			if(yPos >= 68 && yPos <= 68+60)
				yDestination = 85;
			else if(yPos >= 68+80 && yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos >= 68+80*2 && yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos >= 68+80*3 && yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}
		
	}

}
