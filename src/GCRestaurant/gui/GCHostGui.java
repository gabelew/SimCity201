package GCRestaurant.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import city.gui.Gui;
import GCRestaurant.roles.GCCookRole;
import GCRestaurant.roles.GCHostRole;

public class GCHostGui implements Gui{

	private GCHostRole agent = null;
	private boolean isPresent = true;
	private boolean doneplating = true;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private final int DEFAULT_POSX = 200;
	private final int DEFAULT_POSY = 55;
	private enum foodstate{none, plating, cooking, served, gettingFromFridge, gotFood};

	public GCHostGui(GCHostRole c){ //HostAgent m) {
		agent = c;
		xPos = DEFAULT_POSX;
		yPos = DEFAULT_POSY;
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
		//maitreD = m;
	}

	public void updatePosition() {
		if(xPos != xDestination)
		{
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
		}
		else
		{
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
		}
	
        
		
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, 50, 50);
		g.setColor(Color.ORANGE);
		
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
}
