package GCRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import city.gui.Gui;
import GCRestaurant.gui.GCCashierGui.Command;
import GCRestaurant.roles.GCCookRole;
import GCRestaurant.roles.GCCookRole.Order;

public class GCCookGui implements Gui{

	private GCCookRole role = null;
	private boolean isPresent = true;
	private boolean doneplating = true;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private final int startPos = -20;
	private final int DEFAULT_POSX = 200;
	private final int DEFAULT_POSY = 35;
	private final int FRIDGEX = 265;
	private final int FRIDGEY = 20;
	private final int PLATINGX = 200;
	private final int PLATINGY = 60;
	private final int foodOffset = 30;
	private int GRILLX = 225;
	private int GRILLY = 20;
	private List<FoodIcon> orders = Collections.synchronizedList(new ArrayList<FoodIcon>());
	private enum foodstate{none, plating, cooking, served, gettingFromFridge, gotFood, readyToPickUp};
	private BufferedImage cookImg;
	enum Command {none,enterRestaurant, leaveRestaurant, goToFridge, putFoodOnGrill, plateFood, getCookedFood };
	Command command = Command.none;

	public GCCookGui(GCCookRole r){
		role = r;
		try {cookImg = ImageIO.read(new File("imgs/chef_v1.png"));}
		catch (IOException e) {}
		
		xPos = startPos;
		yPos = startPos;
		xDestination = startPos;
		yDestination = startPos;
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
		
		if (xPos == xDestination && yPos == yDestination && command == Command.goToFridge) 
        {
			command = Command.none;
			role.msgActionDone();
			for(FoodIcon f: orders)
			{
				if(f.state == foodstate.gettingFromFridge)
				{
					f.state = foodstate.gotFood;
				}
			}
        }
		
		if (xPos == xDestination && yPos == yDestination && command == Command.putFoodOnGrill) 
        {
			command = Command.none;
			role.msgActionDone();
			for(FoodIcon f: orders)
			{
				if(f.state == foodstate.gotFood)
				{
					f.state = foodstate.cooking;
				}
			}
			xDestination = DEFAULT_POSX;
			yDestination = DEFAULT_POSY;
        }
		
		if (xPos == xDestination && yPos == yDestination && command == Command.getCookedFood) 
        {
			command = Command.none;
			role.msgActionDone();
			for(FoodIcon f: orders)
			{
				if(f.state == foodstate.cooking)
				{
					f.state = foodstate.plating;
				}
			}
        }
		
		if(xPos == xDestination && yPos == yDestination && command == Command.plateFood)
		{
			command = Command.none;
			role.msgActionDone();
			for(FoodIcon f: orders)
			{
				if(f.state == foodstate.plating)
				{
					f.state = foodstate.readyToPickUp;
					f.yLoc = yPos+foodOffset;
				}
			}
			xDestination = DEFAULT_POSX;
			yDestination = DEFAULT_POSY;
		}
		
	}

	public void doGoHome()
	{
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
	}
	public void goToFridge(Order o)
	{
		xDestination = FRIDGEX;
		yDestination = FRIDGEY;
		orders.add(new FoodIcon(o, o.choice));
		command = Command.goToFridge;
	}
	
	public void cookFoodOnGrill(Order ordr, int grillLoc)
	{
		command = Command.putFoodOnGrill;
		for(FoodIcon f: orders)
		{
			if(f.order == ordr)
			{
				xDestination = GRILLX;
				yDestination = GRILLY;
				break;
			}
		}
	}
	public void getFoodFromGrill(Order ordr)
	{
		command = Command.getCookedFood;
		for(FoodIcon f: orders)
		{
			if(f.order == ordr)
			{
				xDestination = f.xLoc;
				yDestination = f.yLoc;
				break;
			}
		}
	}
	public void plateFood(Order o)
	{
		for(FoodIcon f: orders)
		{
			if(f.order == o)
			{
				command = Command.plateFood;
				xDestination = PLATINGX;
				yDestination = PLATINGY;
				f.state = foodstate.plating;
			}
		}
		doneplating = false;
	}
	public void pickedUpFood(Order o)
	{
		synchronized(orders)
		{
			for(FoodIcon f: orders)
			{
				if(f.order == o)
				{
					orders.remove(f);
				}
			}
		}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(cookImg, xPos, yPos, null);
		synchronized(orders)
		{
		for(FoodIcon f : orders)
		{
			if(f.state == foodstate.gotFood || f.state == foodstate.plating)
			{
				f.xLoc = xPos;
				f.yLoc = yPos-10;
				String abrFood = f.choice.charAt(0) + "" + f.choice.charAt(1);
				g.drawString( abrFood, f.xLoc, f.yLoc);
			}
			if(f.state == foodstate.cooking || f.state == foodstate.readyToPickUp)
			{
				String abrFood = f.choice.charAt(0) + "" + f.choice.charAt(1);
				g.drawString( abrFood, f.xLoc, f.yLoc);
			}
		}
		}
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	private class FoodIcon
	{
		public String choice;
		public Order order;
		public int xLoc = GRILLX;
		public int yLoc = GRILLY;
		public foodstate state = foodstate.gettingFromFridge;
		public FoodIcon(Order o, String c)
		{
			this.order = o;
			this.choice = c;
		}
	}
	public void DoEnterRestaurant() {
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
		
	}
}
