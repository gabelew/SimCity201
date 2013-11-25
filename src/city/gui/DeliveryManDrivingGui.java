package city.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import city.gui.Gui;
import city.roles.DeliveryManRole;

public class DeliveryManDrivingGui implements Gui{

	private DeliveryManRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage deliveryDrivingImg = null;
	
	private int xPos =(int)Math.round(role.Market.location.getX()), yPos = (int)Math.round(role.Market.location.getY());
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	public SimCityGui gui;
	private enum state{waiting,gettingFood,givingFood,delivering,delivered};
	state deliveryState;
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 90;
	static final int yWAITING_START = 190;
	static final int xSHELF1=100;
	static final int ySHELF1=120;
	
	public DeliveryManDrivingGui(DeliveryManRole Role,SimCityGui gui) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			deliveryDrivingImg = ImageIO.read(new File(path.toString() + "carRight.png"));
		} catch(IOException e) {
			
		}
		this.role = Role;
		this.gui=gui;
	}
	
	@Override
	public void updatePosition() {
		if (xPos < xDestination) 
			xPos++;
		else if (xPos > xDestination)
			xPos --;
		
		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(deliveryDrivingImg, xPos, yPos, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoGetFood(Map<String,Integer> choices){

	}
	
	public void DoGoPutOnTruck(){
		xDestination=CUST_START_POS;
		yDestination=CUST_START_POS;
		deliveryState=state.givingFood;
	}
	
	public void DoGoDeliver(Point Location){
		xDestination=(int)Math.round(Location.getX());
		yDestination=(int)Math.round(Location.getY());
	}
	
	public void thisIsMe(DeliveryManRole Role){
		this.role=Role;
	}
}
