package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import city.gui.Gui;
import city.gui.AnimationPanel.GridSpot;
import city.roles.RepairManRole;

public class RepairManDrivingGui implements Gui
{
	
	public SimCityGui gui;
	private RepairManRole role = null;
	private boolean isPresent = true;
	private boolean leavingBuilding = false;
	private boolean atStopSign = false;
	Timer timer = new Timer();
	
	private static BufferedImage vanRightImg = null;
	private static BufferedImage vanBackImg = null;
	private static BufferedImage vanFrontImg = null;
	
	private int xPos = 40;
	private int yPos = 40;
	private int xDestination = 0;
	private int yDestination = 0;
	static int xHomeLoc = 0;
	static int yHomeLoc = 0;
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
		
		xHomeLoc = role.myPerson.myHome.location.x;
		yHomeLoc = role.myPerson.myHome.location.y;
		xPos = xHomeLoc;
		yPos = yHomeLoc;
		xDestination = xHomeLoc;
		yDestination = yHomeLoc;
	}
	
	public void updatePosition() 
	{
		
		this.isPresent = true;
		boolean canKeepMoving = true;

		for(int i = 0;i< 44;i++){
    		for(int j =0; j<4;j++){
    			if((xPos == i*20 - 23 - 35 && yPos == 115+j*80) || (xPos ==i*20 - 23 && yPos == 115+j*80-20 && drivingDirection == DrivingDirection.down)){
    				GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(i*20 - 23,115+j*80));
    				canKeepMoving = gridSpot.spot.tryAcquire();
    				if(canKeepMoving && gridSpot.stopSign==true){
    					gridSpot.owner = role.myPerson;
    					timer.schedule(new TimerTask() {
    						public void run() {
    							atStopSign=true;
    						}
    					}, 
    					200);

    					timer.schedule(new TimerTask() {
    						public void run() {
    							atStopSign = false;
    						}
    					}, 
    					1200);
    				}
    			}
    			
    			if((xPos == i*20 - 23 + 35 && yPos == 115+j*80) || (xPos == i*20 - 23 && yPos == 115+j*80-20 && drivingDirection == DrivingDirection.up)){
    				GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(i*20 - 23,115+j*80));
    				
    				if(gridSpot.spot.availablePermits()==0){
    					gridSpot.owner=null;
    					gridSpot.spot.release();
    				}
    			}
    			//left semaphores i*20 - 23, 115+j*80
    			if(i%2==0 && i<33){
	    			//up semaphores i*20 +97, 95+j*80
    			}
    			if(i%2+1==2 && i<34){

    				if(yPos != yDestination && xPos != xDestination && xPos +18 <=i*20 +97  && xPos +22 >=i*20 +97 && yPos+25 <=  95+j*80 && yPos+29 >=  95+j*80){
    					GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(i*20 +97, 95+j*80));
	    				canKeepMoving = gridSpot.spot.tryAcquire();
	    				if(canKeepMoving){
	    					gridSpot.owner = role.myPerson;
	    					leavingBuilding = true;	
	    					xPos+=20;
	    				}
    				}
    				// down semaphore i*20 +97, 95+j*80
    			}
    		}
    		
    	}
		
		if(leavingBuilding){
			GridSpot gridSpot = gui.animationPanel.gridMap.get(new Point(xPos,yPos-20));
			if(gridSpot!=null){
				if(gridSpot.spot.availablePermits()==0){
    				leavingBuilding = false;
					gridSpot.owner=null;
					gridSpot.spot.release();    					
				}
			}	
		}
		
		if(canKeepMoving && !atStopSign){
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
    					gridSpot.owner=null;
    					gridSpot.spot.release();
    				}
				}
				gridSpot = gui.animationPanel.gridMap.get(new Point(xPos-20,yPos));
				if(gridSpot!=null){
					if(gridSpot.spot.availablePermits()==0){
    					gridSpot.owner=null;
    					gridSpot.spot.release();
    				}
				}
				
				yPos--;
			}
			else if(xPos != xDestination){
				drivingDirection = DrivingDirection.down;
				yPos++;
			}
		}

		
		
		
		
		
		
		
		
		
			if(xPos == xDestination && yPos == yDestination && command == Command.goToCustomer)
			{
				command = Command.none;
				role.msgActionDone();
				isPresent = false;
			}
			
			if(xPos == xDestination && yPos == yDestination && command == Command.backToHome)
			{
				command = Command.none;
				role.msgActionDone();
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
		System.out.println("!@#$%^" + xDestination + " " + yDestination);
		command = Command.goToCustomer;
		isPresent = true;
	}
}
