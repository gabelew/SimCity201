package GHRestaurant.gui;
import CMRestaurant.roles.CMCashierRole;
import GHRestaurant.gui.GHHostGui.Command;
import GHRestaurant.roles.*;
import restaurant.interfaces.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.gui.Gui;

public class GHCashierGui implements Gui {

    private GHCashierRole role = null;
    private boolean isPresent = false;

    private int xPos = -20, yPos = -20;//default waiter position
    public int xDestination = 100, yDestination = 30;//default start position
    private int tableNumber;

    public static final int xTable = 200;
    public static final int yTable = 250;
	private BufferedImage cashierImg = null;
	private enum Command {
		Enter, Leave, none}
	private Command command;


    public GHCashierGui(GHCashierRole role) {
        this.role = role;
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
        
        if(xPos == xDestination && yPos == yDestination){
        	if(command == Command.Leave){
        		role.msgAnimationHasLeftRestaurant();
        		command = Command.none;
        	}else if(command == Command.Enter){
        		command = Command.none;
        	}
        		
        }

    }

    public void draw(Graphics2D g) {
		g.drawImage(cashierImg, xPos, yPos, null);
    }

    public boolean isPresent() {
        return isPresent;
    }


    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;

    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	@Override
	public void setPresent(boolean b) {
		isPresent = b;
	}

	public void DoEnterRestaurant() {
		xDestination = 100;
		yDestination = 30;	
		command = Command.Enter;
	}

	public void DoLeaveRestaurant() {
		xDestination = -20;
		yDestination = -20;		
		command = Command.Leave;
	}
}
