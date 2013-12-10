package city.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.PersonAgent;
import city.gui.AnimationPanel.GridSpot;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

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
	private enum Command {noCommand, walkToDestination, enterHome, walkingToBus, walkingOnToBus, waitingForBus, walkingHome};
	private Command command=Command.noCommand;
	private enum DrivingDirection {up,down,right};
	DrivingDirection drivingDirection = DrivingDirection.right;
	private int xHomePosition = 20;
	private int yHomePosition = 20;
	
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
		xPos = agent.myHome.location.x;
		yPos = agent.myHome.location.y; //103+80*i
		xDestination = agent.myHome.location.x;
		yDestination = agent.myHome.location.y;
        
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
			boolean canKeepMoving = true;
			///grab left semaphore
			//if xPos == semaphore - 20 && yPos == semaphore
			//release left semaphore
			//if xPos == semaphore + 20 || yPos == semaphore +20
			//if leaving building
				//grab down semaphore and shift left 20
					//which means if xPos + 20 == semaphore and if yPos+20 == semaphore
				//release semaphore if leaveBuilding == true && yPos == semaphore - 20
			//if entering building
				//grab up semaphore
					//which means if xPos == semaphore and if yPos-20 == semaphore && destination == buildingAboveSemaphore
				//release semaphore 
					//if at destination xPos== semaphore -7 && yPos == semaphore
			
			for(int i = 0;i< 44;i++){
	    		for(int j =0; j<4;j++){
	    			if((xPos == i*20 - 23 - 35 && yPos == 115+j*80) || (xPos ==i*20 - 23 && yPos == 115+j*80-20 && drivingDirection == DrivingDirection.down)){
	    				GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(i*20 - 23,115+j*80));
	    				canKeepMoving = gridSpot.spot.tryAcquire();
	    				
	    			}
	    			
	    			if((xPos == i*20 - 23 + 35 && yPos == 115+j*80) || (xPos == i*20 - 23 && yPos == 115+j*80-20 && drivingDirection == DrivingDirection.up)){
	    				GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(i*20 - 23,115+j*80));
	    				
	    				if(gridSpot.spot.availablePermits()==0){
	    					gridSpot.spot.release();
	    				}
	    			}
	    			//left semaphores i*20 - 23, 115+j*80
	    			if(i%2==0 && i<33){
		    			//up semaphores i*20 +97, 95+j*80
	    			}
	    			if(i%2+1==2 && i<34){
	    				// down semaphore i*20 +97, 95+j*80
	    			}
	    		}
	    		
	    	}
			
			if(canKeepMoving){
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
					xPos = -60;
					yPos = yDestination + 46;
				}
				else if (yPos != yDestination &&(xPos < xDestination || (xPos < 950 && (Math.abs(yPos - yDestination) > 47 || yPos - yDestination == -33)))){
					drivingDirection = DrivingDirection.right;
					xPos++;
				}
				else if (yPos > yDestination){
					drivingDirection = DrivingDirection.up;
					GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(xPos+20,yPos));
    				if(gridSpot!=null){
    					if(gridSpot.spot.availablePermits()==0){
	    					gridSpot.spot.release();
	    				}
    				}
    				gridSpot = gui.animationPanel.gridMap.get(new Point(xPos-20,yPos));
    				if(gridSpot!=null){
    					if(gridSpot.spot.availablePermits()==0){
	    					gridSpot.spot.release();
	    				}
    				}
    				
					yPos--;
				}
				else if(xPos != xDestination){
					drivingDirection = DrivingDirection.down;
					yPos++;
				}else{
					System.out.println("car stuck");
				}
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
			}else if(command == Command.walkingHome){
				agent.msgWalkingHomeAnimationFinshed();
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
			if(yPos <= 68+60)
				yDestination = 85;
			else if(yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}else{
			xDestination = 797;
			if(yPos <= 68+60)
				yDestination = 85;
			else if(yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}
		command = Command.walkingToBus;
	}


	public void doGetOnBus() {

		if(xPos < 432){
			xDestination = agent.busLeft.getBusGui().xPos+5;
			yDestination = agent.busLeft.getBusGui().yPos+20;
		}else{
			xDestination = agent.busRight.getBusGui().xPos+5;
			yDestination = agent.busRight.getBusGui().yPos+20;
		}
		
		command = Command.walkingOnToBus;
	}


	public void doGetOffBus() {
		this.isPresent = true;
		if(xPos < 432){
			xDestination = 67;
			if( yPos <= 68+60)
				yDestination = 85;
			else if(yPos <= 68+80+60)
				yDestination = 85+80;
			else if(yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}else{
			xDestination = 797;
			if(yPos <= 68+60)
				yDestination = 85;
			else if(yPos <= 68+80+60)
				yDestination = 85+80;
			else if( yPos <= 68+80*2+60)
				yDestination = 85+80*2;
			else if(yPos <= 68+80*3+60)
				yDestination = 85+80*3;
		}
		
	}
	public void doWalkToHome(){
		xDestination = agent.myHome.location.x;
		yDestination = agent.myHome.location.y;		
		command = Command.walkingHome;
	}
}
