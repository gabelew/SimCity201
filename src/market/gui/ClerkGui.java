package market.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import city.gui.Gui;
import city.roles.ClerkRole;

public class ClerkGui implements Gui{

	private ClerkRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage cashierImg = null;
	
	private int xPos = CUST_START_POS, yPos = CUST_START_POS;
	private int xDestination = xWAITING_START, yDestination = yWAITING_START;
	private enum state{gettingToStand,waiting,gettingFood,givingFood,leaving};
	state clerkState;
	
	static final int CUST_START_POS = -40;
	static final int xWAITING_START = 150;
	static final int yWAITING_START = 230;
	static final int xSHELF1=100;
	static final int ySHELF1=120;
	
	public ClerkGui(ClerkRole role) {
		try {
			StringBuilder path = new StringBuilder("imgs/");
			cashierImg = ImageIO.read(new File(path.toString() + "cashier_v1.png"));
		} catch(IOException e) {
			
		}
		this.role = role;
		clerkState=state.gettingToStand;
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
			clerkState=state.waiting;
			role.atShelf();
		}
		if(xPos==xDestination&&yPos==yDestination&&clerkState==state.givingFood){
			clerkState=state.waiting;
			role.atShelf();
		}
		if(xPos==xDestination&&yPos==yDestination&&clerkState==state.gettingToStand){
			clerkState=state.waiting;
			role.atShelf();
		}
		if(xPos==xDestination&&yPos==yDestination&&clerkState==state.leaving){
			clerkState=state.waiting;
			role.atShelf();
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
		else if(Food=="chicken"){
			xDestination=xSHELF1+60;
			yDestination=ySHELF1-10;
			clerkState=state.gettingFood;
		}
		else if(Food=="pizza"){
			xDestination=xSHELF1+100;
			yDestination=ySHELF1-20;
			clerkState=state.gettingFood;
		}
		else if(Food=="cookie"){
			xDestination=xSHELF1+150;
			yDestination=ySHELF1+150;
			clerkState=state.gettingFood;
		}
		else{
			xDestination=xSHELF1+200;
			yDestination=ySHELF1+140;
			clerkState=state.gettingFood;
		}

	}
	
	public void DoGoGetFood(Map<String,Integer> choices){
		clerkState=state.waiting;
		for (String Key:choices.keySet()){
			GoToShelf(Key);
			break;
		}
	}
	
	public void DoLeaveWork(){
		xDestination=CUST_START_POS;
		yDestination=CUST_START_POS;
		clerkState=state.leaving;
	}
	
	public void DoGoGiveOrder(){
		xDestination=xWAITING_START;
		yDestination=yWAITING_START;
		clerkState=state.givingFood;
	}

	public void DoDoneWithOrder(){
		clerkState=state.gettingToStand;
	}
	
	public void setPresent(boolean b) {
		isPresent = true;
	}

}
