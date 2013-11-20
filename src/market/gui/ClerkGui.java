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
import city.roles.ClerkRole;
import city.roles.MarketCustomerRole;

public class ClerkGui implements Gui{

	private ClerkRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage cashierImg = null;
	
	private int xPos = CUST_START_POS, yPos = CUST_START_POS;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	private Semaphore atShelf=new Semaphore(1,true);
	private enum state{waiting,gettingFood,givingFood};
	state clerkState;
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 50;
	static final int yWAITING_START = 70;
	static final int xSHELF1=100;
	static final int ySHELF1=120;
	
	public ClerkGui(ClerkRole role) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			cashierImg = ImageIO.read(new File(path.toString() + "cashier_v1.png"));
		} catch(IOException e) {
			
		}
		this.role = role;
	}
	
	public void setRole(ClerkRole Role) {
		this.role = Role;
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
		if (xPos==xDestination&&yPos==yDestination&&clerkState==state.gettingFood){
			atShelf.release();
			clerkState=state.waiting;
		}
		if(xPos==xDestination&&yPos==yDestination&&clerkState==state.givingFood){
			atShelf.release();
			clerkState=state.waiting;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(cashierImg, xPos, yPos, null);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void GoToShelf(String Food){
		if (Food=="steak"){
			xDestination=xSHELF1;
			yDestination=ySHELF1;
			clerkState=state.gettingFood;
		}
		if(Food=="chicken"){
			xDestination=xSHELF1+50;
			yDestination=ySHELF1+50;
			clerkState=state.gettingFood;
		}
		if(Food=="pizza"){
			xDestination=xSHELF1+100;
			yDestination=ySHELF1+100;
			clerkState=state.gettingFood;
		}
		if(Food=="cookie"){
			xDestination=xSHELF1+150;
			yDestination=ySHELF1+150;
			clerkState=state.gettingFood;
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
		clerkState=state.waiting;
	}
	
	public void DoGoGiveOrder(){
		xDestination=xWAITING_START;
		yDestination=yWAITING_START;
		clerkState=state.givingFood;
		try {
			atShelf.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}