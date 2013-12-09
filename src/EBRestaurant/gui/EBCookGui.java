package EBRestaurant.gui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import city.gui.Gui;
import EBRestaurant.roles.EBCookRole;

public class EBCookGui implements Gui {

    private EBCookRole role = null;
    private BufferedImage cookImg = null;
    private boolean isPresent = true;
    private int xPos = -20, yPos = -20;//default waiter position
    private static int xDestination = -20;//default start position
	private static int yDestination = -20;
    private final int xChange = 20;
    private final int yChange = -20;
    public String choice1="";
    public String choice2="";
    public String choice3="";
    private int ChoiceY=210;
    private int Choice1X=340;
    private int Choice2X=350;
    private int Choice3X=360;
    private int wait1Y=288;
    private int wait2Y=296;
    private int wait3Y=304;
    private int wait1X=317;
    private int wait2X=334;
    private int wait3X=351;
    private String wait1="";
    private String wait2="";
    private String wait3="";
    boolean leaving=false;
    HashMap<String,String>shortChoice=new HashMap<String,String>();

    public EBCookGui(EBCookRole role) {
        this.role = role;
        shortChoice.put("steak", "St");
        shortChoice.put("salad", "Sa");
        shortChoice.put("chicken", "Ch");
        shortChoice.put("cookie", "Co");
        shortChoice.put("", "");
        try {
		    cookImg = ImageIO.read(new File("imgs/chef_v1.png"));
		} catch (IOException e) {
		}
		
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if(xPos==xDestination&&yPos==yDestination&&leaving){
        	leaving=false;
        	role.msgLeft();
        }

    }

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString(choice1, Choice1X, ChoiceY);
        g.drawString(choice2, Choice2X, ChoiceY);
        g.drawString(choice3, Choice3X, ChoiceY);
        g.drawString(wait1, wait1X, wait1Y);
        g.drawString(wait2, wait2X, wait2Y);
        g.drawString(wait3, wait3X, wait3Y);
        g.drawImage(cookImg, xPos, yPos, null);
    }
    
    public void setCooking(String choice, int cookingSlot){
    	if (cookingSlot==1){
    		choice1=shortChoice.get(choice);
    	}
    	if (cookingSlot==2){
    		choice2=shortChoice.get(choice);
    	}
    	if (cookingSlot==3){
    		choice3=shortChoice.get(choice);
    	}
    }
    public void setReady(String choice, int waitingSpot){
    	if (waitingSpot==1){
    		wait1=shortChoice.get(choice);
    	}
    	if (waitingSpot==2){
    		wait2=shortChoice.get(choice);
    	}
    	if (waitingSpot==3){
    		wait3=shortChoice.get(choice);
    	}
    }

    public boolean isPresent() {
        return true;
    }


    public void DoLeaveCustomer() {
        xDestination = yChange;
        yDestination = yChange;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoEnterRestaurant() {
		xDestination=330;
		yDestination=240;
	}

	public void DoLeaveRestaurant() {
		xDestination=-20;
		yDestination=-20;
		leaving=true;
	}

	@Override
	public void setPresent(boolean b) {
		isPresent=b;
	}
}
