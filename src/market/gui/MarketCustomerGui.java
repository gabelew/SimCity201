package market.gui;

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
import city.roles.BankCustomerRole;
import city.roles.MarketCustomerRole;

public class MarketCustomerGui implements Gui{

	private MarketCustomerRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage customerImg = null;
	
	private int xPos = CUST_START_POS, yPos = CUST_START_POS;
	private int xDestination = 10, yDestination = 10;
	
	public static List<Semaphore> atms = new ArrayList<Semaphore>();
	private Map<Integer, Point> atmMap = new HashMap<Integer, Point>();
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 50;
	static final int yWAITING_START = 70;
	
	public MarketCustomerGui(MarketCustomerRole role) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			customerImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		} catch(IOException e) {
			
		}
		this.role = role;
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
		g.drawImage(customerImg, xPos, yPos, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void DoLeaveMarket(){
		xDestination=-5;
		yDestination=-5;
	}

	public void DoGoToClerk() {
		xDestination=xWAITING_START-5;
		yDestination=yWAITING_START;
		
	}

	public void setPresent(boolean b) {
		isPresent = true;
	}

}
