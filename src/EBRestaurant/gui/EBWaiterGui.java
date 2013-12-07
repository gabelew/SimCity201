package EBRestaurant.gui;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import EBRestaurant.roles.EBWaiterRole;
import restaurant.interfaces.*;

public class EBWaiterGui implements Gui {
	private static BufferedImage waiterImg = null;
    private EBWaiterRole agent = null;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private final int xChange = 20;
    private final int yChange = -20;
    private int xWaitingPosition=100;
    private final int yWaitingPosition=50;
    private final int xTable = 100;
    private final int yTable1 = 100;
    private final int yTable2 = 200;
    private final int yTable3 = 300;
    public String choice1;
    public String choice2;
    public String choice3;
    private int ChoiceX;
    private int Choice1Y;
    private int Choice2Y;
    private int Choice3Y;
    private boolean onBreak;

    public EBWaiterGui(EBWaiterRole w){
    	try {
		    waiterImg = ImageIO.read(new File("imgs/waiter_v1.png"));
		} catch (IOException e) {
		}
    	agent = w;
		xPos = -20;
		yPos = -20;
		xDestination = xWaitingPosition;
		yDestination = yWaitingPosition;
		ChoiceX=-5;
		Choice1Y=-5;
		Choice2Y=-5;
		Choice3Y=-5;
		choice1="";
		choice2="";
		choice3="";
		onBreak=false;
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + xChange) & ((yDestination == yTable1 + yChange)||(yDestination == yTable2 + yChange)||(yDestination == yTable3 + yChange))) {
           agent.msgAtTable();
        }
        if (xPos==xWaitingPosition && yPos==yWaitingPosition)
        {
        	agent.msgAtStart();
        }
        if (xPos==290 && yPos==220)
        {
        	agent.msgAtCook();
        }
        if (xPos==-40 && yPos==-40){
        	agent.msgLeftTheRestaurant();
        }
    }


    public void draw(Graphics2D g) {
    	g.drawImage(waiterImg, xPos, yPos, null);
        g.setColor(Color.black);
        Font textFont = new Font("Arial", Font.BOLD, 12);  
        g.setFont(textFont);
        g.drawString(choice1, ChoiceX, Choice1Y);
        g.drawString(choice2, ChoiceX, Choice2Y);
        g.drawString(choice3, ChoiceX, Choice3Y);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(Customer customer, int tableNumber) {
        if (tableNumber==1)
        {
        	xDestination = xTable + xChange;
        	yDestination = yTable1 + yChange;
        }
        else if (tableNumber==2)
        {
        	xDestination = xTable + xChange;
        	yDestination = yTable2 + yChange;
        }
        else if (tableNumber==3)
        {
        	xDestination = xTable + xChange;
        	yDestination = yTable3 + yChange;
        }
    }

    public void DoLeaveCustomer() {
        xDestination = xWaitingPosition;
        yDestination = yWaitingPosition;
    }
    
    public void DoGoToCook(){
    	xDestination = 290;
    	yDestination = 220;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void setChoice(String Choice, int tableNumber){
    	if (tableNumber==1){
    		Choice1Y=yTable1;
    		ChoiceX=xTable;
    		choice1=Choice;
    	}
    	if (tableNumber==2){
    		Choice2Y=yTable2;
    		ChoiceX=xTable;
    		choice2=Choice;
    	}
    	if (tableNumber==3){
    		Choice3Y=yTable3;
    		ChoiceX=xTable;
    		choice3=Choice;
    	}
    }
    
    
	public void setBreak() {
		onBreak=true;
		agent.msgAskForBreak();
	}
	
	public void unsetBreak(){
		onBreak=false;
		agent.backFromBreak();
	}
	
	public void breakDenied(){
		onBreak=false;
	}
    
	public boolean onBreak() {
		return onBreak;
	}

	public void DoLeaveRestaurant() {
		xDestination=-40;
		yDestination=-40;
	}
}
