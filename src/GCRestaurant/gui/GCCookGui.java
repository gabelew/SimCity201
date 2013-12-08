package GCRestaurant.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import city.gui.Gui;
import GCRestaurant.roles.GCCookRole;

public class GCCookGui implements Gui{

	private GCCookRole agent = null;
	private boolean isPresent = true;
	private boolean doneplating = true;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private final int GRILL_EDGE = 200;
	private final int DEFAULT_POSX = 200;
	private final int DEFAULT_POSY = 55;
	private final int FRIDGEX = 520;
	private final int FRIDGEY = 55;
	private final int PLATINGX = 200;
	private final int PLATINGY = 60;
	private int GRILLX = 200;
	private int GRILLY = 20;
	private final int COOK_SIZE = 20;
	private List<Food> orders = new ArrayList<Food>();
	private enum foodstate{none, plating, cooking, served, gettingFromFridge, gotFood};

	public GCCookGui(GCCookRole c){ //HostAgent m) {
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
		if (xPos == xDestination && yPos == yDestination
           		& (xDestination == FRIDGEX) & (yDestination == FRIDGEY)) 
        {
			agent.msgActionDone();
		   try
			{
				for(Food f: orders)
				{
					if(f.state == foodstate.gettingFromFridge)
					{
						f.state = foodstate.gotFood;
					}
				}
			}
			catch(Exception e){ return; }
    	   
        }
		if (xPos == xDestination && yPos == yDestination
           		& (xDestination == GRILLX) & (yDestination == GRILLY)) 
        {
			agent.msgActionDone();
		   xDestination = DEFAULT_POSX;
		   yDestination = DEFAULT_POSY;
		   try
			{
				for(Food f: orders)
				{
					if(f.state == foodstate.gotFood)
					{
						f.state = foodstate.cooking;
					}
				}
			}
			catch(Exception e){ return; }
    	   System.out.println("done~~~~~~~");
        }
		
		if(xPos == xDestination && yPos == yDestination
				& (xDestination == PLATINGX) & (yDestination == PLATINGY))
		{
			agent.msgActionDone();
			try
			{
				for(Food f: orders)
				{
					if(f.state == foodstate.plating)
					{
						doneplating = true;
						f.state = foodstate.served;
						f.x = 200;
						f.y = 85;
					}
				}
			}
			catch(Exception e){ return; }
		}
		
	}

	public void doGoHome()
	{
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
	}
	public void goToFridge(String food, int ordernum)
	{
		xDestination = FRIDGEX;
		yDestination = FRIDGEY;
		orders.add(new Food(FRIDGEX, FRIDGEY, food));
		orders.get(ordernum).state = foodstate.gettingFromFridge;
	}
	public void cookOrder(int grillLoc)
	{
		GRILLX = GRILL_EDGE + 20*grillLoc;
		try
		{
		orders.get(grillLoc).x = GRILLX;
		orders.get(grillLoc).y = GRILLY;
		}
		catch(Exception e){return;}
		
	}
	public void goToGrill(int orderNum)
	{
		try
		{
		xDestination = orders.get(orderNum).x;
		yDestination = orders.get(orderNum).y;
		GRILLX = xDestination;
		GRILLY = yDestination;
		}
		catch(Exception e)
		{
			return;
		}
		
	}
	public void plateFood(int orderNum)
	{
		doneplating = false;
		xDestination = PLATINGX;
		yDestination = PLATINGY;
		orders.get(orderNum).x = DEFAULT_POSX;
		orders.get(orderNum).y = DEFAULT_POSY;
		orders.get(orderNum).state = foodstate.plating;
	}
	public void pickedUpFood()
	{
		try
		{
			orders.remove(0);
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, COOK_SIZE, COOK_SIZE);
		g.setColor(Color.ORANGE);
		try
		{
			for(int i = 0; i < orders.size(); i++)
			{
				if(orders.get(i).state == foodstate.plating || orders.get(i).state == foodstate.gotFood)
				{
					orders.get(i).x = xPos;
					orders.get(i).y = yPos;
				}
				if(orders.get(i).state != foodstate.none)
				{
					String abrFood = (orders.get(i).choice).charAt(0) + "" + (orders.get(i).choice).charAt(1);
					g.drawString( abrFood, orders.get(i).x, orders.get(i).y);
				}
			}
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	private class Food
	{
		int x = -10;
		int y = -10;
		String choice;
		foodstate state = foodstate.none;
		public Food(int x, int y, String c)
		{
			this.x = x;
			this.y = y;
			this.choice = c;
		}
	}
	public void DoEnterRestaurant() {
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
		
	}
}
