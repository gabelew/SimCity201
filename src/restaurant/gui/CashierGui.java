package restaurant.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import restaurant.CashierAgent;

public class CashierGui implements Gui  {

	private CashierAgent agent = null;
	private boolean isPresent = true;
	private BufferedImage cashierImg = null;
	RestaurantGui gui;

	static final int xSTART_POSITION = 57;
	static final int ySTART_POSITION = 258;   

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = xSTART_POSITION, yDestination = ySTART_POSITION;//default start position
	    
	public CashierGui(CashierAgent c) {
		try {
		    cashierImg = ImageIO.read(new File("imgs/cashier_v1.png"));
		} catch (IOException e) {
		}
			
		setAgent(c);
		xDestination = xSTART_POSITION;
		yDestination = ySTART_POSITION;
	}
		
	public CashierAgent getAgent() {
		return agent;
	}

	public void setAgent(CashierAgent agent) {
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
		g.drawImage(cashierImg, xPos, yPos, null);
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
		
	public boolean isPresent() {
		return isPresent;
	}

	public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

}
