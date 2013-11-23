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
import city.roles.DeliveryManRole;

public class DeliveryManGui implements Gui{

	private DeliveryManRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage deliveryImg = null;
	
	private int xPos = CUST_START_POS, yPos = CUST_START_POS;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	
	public static List<Semaphore> atms = new ArrayList<Semaphore>();
	private Map<Integer, Point> atmMap = new HashMap<Integer, Point>();
	private enum state{waiting,gettingFood,givingFood,delivering,delivered};
	state deliveryState;
	private Semaphore atShelf=new Semaphore(1,true);
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 50;
	static final int yWAITING_START = 50;
	static final int xSHELF1=100;
	static final int ySHELF1=120;
	
	public DeliveryManGui(DeliveryManRole role) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			deliveryImg = ImageIO.read(new File(path.toString() + "host_v1.png"));
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
		if (xPos==xDestination&&yPos==yDestination&&deliveryState==state.gettingFood){
			atShelf.release();
			deliveryState=state.waiting;
		}
		if(xPos==xDestination&&yPos==yDestination&&deliveryState==state.givingFood){
			atShelf.release();
			deliveryState=state.delivering;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(deliveryImg, xPos, yPos, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void GoToShelf(String Food){
		if (Food=="steak"){
			xDestination=xSHELF1;
			yDestination=ySHELF1;
			deliveryState=state.gettingFood;
		}
		if(Food=="chicken"){
			xDestination=xSHELF1+50;
			yDestination=ySHELF1+50;
			deliveryState=state.gettingFood;
		}
		if(Food=="pizza"){
			xDestination=xSHELF1+100;
			yDestination=ySHELF1+100;
			deliveryState=state.gettingFood;
		}
		if(Food=="cookie"){
			xDestination=xSHELF1+150;
			yDestination=ySHELF1+150;
			deliveryState=state.gettingFood;
		}
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
		}
	}
	
	public void DoGoGetFood(Map<String,Integer> choices){
		for (String Key:choices.keySet()){
			GoToShelf(Key);
		}
		deliveryState=state.waiting;
	}
	
	public void DoGoPutOnTruck(){
		xDestination=xWAITING_START;
		yDestination=yWAITING_START;
		deliveryState=state.givingFood;
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void DoGoDeliver(Point Location){
		
	}

	public void setPresent(boolean b) {
		isPresent = b;
	}

}
