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

public class RepairManDrivingGui implements Gui
{
	private RepairManRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage vanRightImg = null;
	private static BufferedImage vanBackImg = null;
	private static BufferedImage vanFrontImg = null;
	
	private int xPos, yPos;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	public SimCityGui gui;
	private enum state{waiting, backToHome, atCustomerHome, goingToCustomer};
	state workingState;
	
	static final int xHomeLoc = 0;
	static final int yHomeLoc = 0;
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 90;
	static final int yWAITING_START = 190;
	private enum DrivingDirection {up,down,right};
	DrivingDirection drivingDirection = DrivingDirection.right;
	
	public RepairManDrivingGui(RepairManRole Role,SimCityGui gui) 
	{
		try 
		{
			StringBuilder path = new StringBuilder("imgs/");
			vanRightImg = ImageIO.read(new File(path.toString() + "RepairVanRight.png"));
			vanBackImg = ImageIO.read(new File(path.toString() + "RepairVanBack.png"));
			vanFrontImg = ImageIO.read(new File(path.toString() + "RepairVanFront.png"));
		} catch(IOException e) {
			
		}
		role = Role;
		this.gui=gui;
	}
	
	public void updatePosition() 
	{
		if(yPos == yDestination && xPos == xDestination && workingState==state.goingToCustomer)
		{
			workingState=state.atCustomerHome;
			role.msgAnimationAtCustomer();
			this.isPresent = false;
		}
		else if(yPos == yDestination && xPos == xDestination){
			workingState=state.atCustomerHome;
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
	
	public void DoGoPutOnTruck(){
		xDestination=CUST_START_POS;
		yDestination=CUST_START_POS;
	}
	
	public void DoGoBack(){
		xDestination = xHomeLoc;
		yDestination = yHomeLoc;
		workingState = state.backToHome;
	}
	
	public void DoGoFix(Point Locations)
	{
		xDestination=(int)Math.round(Locations.getX());
		yDestination=(int)Math.round(Locations.getY());
		workingState= state.goingToCustomer;
	}
}
