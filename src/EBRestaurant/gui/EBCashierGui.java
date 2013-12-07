package EBRestaurant.gui;


import java.awt.*;

import city.gui.Gui;
import EBRestaurant.roles.EBCashierRole;

public class EBCashierGui implements Gui {

    private EBCashierRole agent = null;

    private int xPos = -40, yPos = -40;//default cashier position
    private int xDestination = -40, yDestination = -40;//default start position
    private final int xChange = 20;
    public String choice1="";
    public String choice2="";
    public String choice3="";

    public EBCashierGui(EBCashierRole agent) {
        this.agent = agent;
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
		xDestination=100;
		yDestination=100;
	}

	public void DoLeaveRestaurant() {
		xDestination=-40;
		yDestination=-40;
	}
}
