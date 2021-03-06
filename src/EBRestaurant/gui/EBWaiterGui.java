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
    private Waiter agent = null;
    private boolean isPresent = true;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private final int xChange = 20;
    private final int yChange = -20;
    private int xWaitingPosition=100;
    private int yWaitingPosition=50;
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
    
    private enum command {none,onBreak,goToStart,goToCook};
    command commands;

    public EBWaiterGui(Waiter w){
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
		commands=command.none;
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
           ((EBWaiterRole) agent).msgAtTable();
        }
        else if (xPos==xWaitingPosition && yPos==yWaitingPosition&&commands==command.goToStart)
        {
        	((EBWaiterRole) agent).msgAtStart();
        }
        else if (xPos==290 && yPos==220&&commands==command.goToCook)
        {
        	((EBWaiterRole) agent).msgAtCook();
        }
        else if (xPos==-40 && yPos==-40){
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
        commands=command.goToStart;
    }
    
    public void DoGoToCook(){
    	xDestination = 290;
    	yDestination = 220;
    	commands=command.goToCook;
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
    
    public void setWaitingPosition(int x,int y){
    	 xWaitingPosition=xWaitingPosition+x;
    	 xDestination=xWaitingPosition;
    	 yWaitingPosition=yWaitingPosition+y;
    	 yDestination=yWaitingPosition;
    }
    
    
	public void setBreak() {
		commands=command.onBreak;
		agent.msgAskForBreak();
	}
	
	public void unsetBreak(){
		commands=command.none;
		((EBWaiterRole) agent).backFromBreak();
	}
	
	public void breakDenied(){
		commands=command.none;
	}
    
	public boolean onBreak() {
		if(commands==command.onBreak)
			return true;
		else
			return false;
	}

	public void DoLeaveRestaurant() {
		xDestination=-40;
		yDestination=-40;
	}

	@Override
	public void setPresent(boolean b) {
		isPresent=b;
	}

	public void askBreak() {
		agent.msgAskForBreak();
	}
}
