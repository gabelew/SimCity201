package EBRestaurant.gui;


import java.awt.*;
import java.util.HashMap;

import city.gui.Gui;
import EBRestaurant.roles.EBCookRole;

public class EBCookGui implements Gui {

    private EBCookRole agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private static int xDestination = -20;//default start position
	private static int yDestination = -20;
    private final int xChange = 20;
    private final int yChange = -20;
    public String choice1="";
    public String choice2="";
    public String choice3="";
    private int ChoiceY=210;
    private int Choice1X=300;
    private int Choice2X=330;
    private int Choice3X=360;
    private int wait1Y=250;
    private int wait2Y=280;
    private int wait3Y=310;
    private int waitX=302;
    private String wait1="";
    private String wait2="";
    private String wait3="";
    HashMap<String,String>shortChoice=new HashMap<String,String>();

    public EBCookGui(EBCookRole agent) {
        this.agent = agent;
        shortChoice.put("Steak", "St");
        shortChoice.put("Salad", "Sa");
        shortChoice.put("Chicken", "Ch");
        shortChoice.put("Pizza", "Pi");
        shortChoice.put("", "");
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

    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(xPos, yPos, xChange, xChange);
        g.drawString(choice1, Choice1X, ChoiceY);
        g.drawString(choice2, Choice2X, ChoiceY);
        g.drawString(choice3, Choice3X, ChoiceY);
        g.drawString(wait1, waitX, wait1Y);
        g.drawString(wait2, waitX, wait2Y);
        g.drawString(wait3, waitX, wait3Y);
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

	public static void DoEnterRestaurant() {
		xDestination=330;
		yDestination=240;
	}

	public static void DoLeaveRestaurant() {
		xDestination=-20;
		yDestination=-20;
	}
}
