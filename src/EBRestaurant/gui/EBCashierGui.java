package EBRestaurant.gui;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;
import EBRestaurant.roles.EBCashierRole;

public class EBCashierGui implements Gui {

    private EBCashierRole agent = null;
    private BufferedImage cashierImg = null;
    private boolean isPresent = true;
    private int xPos = -40, yPos = -40;//default cashier position
    private int xDestination = -40, yDestination = -40;//default start position
    public String choice1="";
    public String choice2="";
    public String choice3="";
    private enum guiState{none,entering,leaving};
    guiState command;

    public EBCashierGui(EBCashierRole agent) {
        this.agent = agent;
        try {
		    cashierImg = ImageIO.read(new File("imgs/cashier_v1.png"));
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
        if (xPos == xDestination&&yPos==yDestination&&command==guiState.leaving){
        	command=guiState.none;
        	agent.msgLeft();
        }
        
    }

    public void draw(Graphics2D g) {
    	g.drawImage(cashierImg, xPos, yPos, null);
    }
    

    public boolean isPresent() {
        return true;
    }


    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoEnterRestaurant() {
		xDestination=40;
		yDestination=400;
		command=guiState.entering;
	}

	public void DoLeaveRestaurant() {
		xDestination=-40;
		yDestination=-40;
		command=guiState.leaving;
	}

	public void setPresent(boolean b) {
		isPresent = b;
	}
}
