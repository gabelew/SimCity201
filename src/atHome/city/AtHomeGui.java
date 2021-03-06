package atHome.city;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import restaurant.gui.FoodIcon;
import city.PersonAgent;
import city.gui.Gui;
import city.gui.SimCityGui;
import city.roles.AtHomeRole;

public class AtHomeGui implements Gui{
	
	private PersonAgent agent = null;
	private AtHomeRole role = null;
	private boolean isPresent = false;
	
	private static BufferedImage personImg = null;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private int xStartPos = -20;
	private int yStartPos = 200;
	public int xHomePosition = 20;
	public int yHomePosition = 30;
	private int xFRIDGE_POSITION = 0;
	private int yFRIDGE_POSITION = 0;
	private int xGRILL_POSITION = 0;
	private int yGRILL_POSITION = 0;
	private int xTABLE_POS = 57;
	private int yTABLE_POS = 70;
	private int xKITCHEN_COUNTER_POSITION = 0;
	private int yKITCHEN_COUNTER_POSITION = 0;
	private int yTABLE_OFFSET = 300;
	private int xKITCHEN_OFFSET = 217;
	private int xFOOD_OFFSET = 10;
	private int yFOOD_OFFSET = 4;
	private int yKITCHEN_COUNTER_OFFSET = 30;
	private int yGRILL_RIGHT_OFFSET = 30;
	private int xGRILL_RIGHT_OFFSET = 52;
	private int yFIDGE_OFFSET = 15;
	private int xFIDGE_OFFSET = 100;
	private int yAPT_OFFSET = 310;
    private int xAPT_OFFSET = 30;
    private int HOUSE_TABLEPOS = 150;
    private int COOKING_OFFSET = 20;
	private int KITCHEN_OFFSET = 15;
	List<MyFood> foods = Collections.synchronizedList(new ArrayList<MyFood>());
	private enum Command {noCommand, GoHome, GoToFridge, GoToGrill, GoToCounter, GoToRestPost, EatFood, LeaveHome, GetFoodFromCounter, GetFoodFromGrill};
	private enum FoodState{PutFoodOnGrill, PutFoodOnCounter, FoodOnGrill, FoodOnCounter, PickUpFromGrill, PickUpFromCounter, PutOnPickUpTable, OnPickUpTable, WaiterPickedUp};
	Command command = Command.noCommand;
	
	public AtHomeGui(PersonAgent c, AtHomeRole r)
	{
		
		try 
		{
			StringBuilder path = new StringBuilder("imgs/");
		    personImg = ImageIO.read(new File(path.toString() + "customer_v1.png"));
		} 
		catch (IOException e) {}
		
		this.agent = c;
		this.role = r;
		if(agent.myHome instanceof Apartment)
		{
			int aptnum = ((Apartment)agent.myHome).renters.indexOf(agent);
			//System.out.println("MY APT NUMBER IS: " + aptnum);
			if(aptnum < 4)//top 4 apartments
			{
				xKITCHEN_COUNTER_POSITION = xHomePosition + aptnum*xKITCHEN_OFFSET;
				yKITCHEN_COUNTER_POSITION = yHomePosition - yKITCHEN_COUNTER_OFFSET + KITCHEN_OFFSET;
				xFRIDGE_POSITION = xHomePosition + xFIDGE_OFFSET + aptnum*xKITCHEN_OFFSET;
				yFRIDGE_POSITION = yHomePosition + yFIDGE_OFFSET- KITCHEN_OFFSET;
				xGRILL_POSITION = xHomePosition + xGRILL_RIGHT_OFFSET + aptnum*xKITCHEN_OFFSET;
				yGRILL_POSITION = yHomePosition -yGRILL_RIGHT_OFFSET+ KITCHEN_OFFSET;
				xTABLE_POS += xKITCHEN_OFFSET*aptnum;
				xHomePosition = xHomePosition + aptnum*xKITCHEN_OFFSET; 
				//xDestination = xHomePosition;
				//yDestination = yHomePosition;
			}
			else //bottom 4 apartments
			{
				xKITCHEN_COUNTER_POSITION = xAPT_OFFSET + xHomePosition + (aptnum-4)*xKITCHEN_OFFSET;
				yKITCHEN_COUNTER_POSITION = yHomePosition - yKITCHEN_COUNTER_OFFSET + yAPT_OFFSET+ KITCHEN_OFFSET;
				xFRIDGE_POSITION = xAPT_OFFSET + xHomePosition + xFIDGE_OFFSET + (aptnum-4)*xKITCHEN_OFFSET;
				yFRIDGE_POSITION = yHomePosition + yFIDGE_OFFSET + yAPT_OFFSET- KITCHEN_OFFSET;
				xGRILL_POSITION = xAPT_OFFSET + xHomePosition + xGRILL_RIGHT_OFFSET + (aptnum-4)*xKITCHEN_OFFSET;
				yGRILL_POSITION = yHomePosition -yGRILL_RIGHT_OFFSET + yAPT_OFFSET+ KITCHEN_OFFSET;
				xTABLE_POS = xAPT_OFFSET*2 + xKITCHEN_OFFSET*(aptnum-4);
				yTABLE_POS += yTABLE_OFFSET;
				
				xHomePosition = xHomePosition + (aptnum-4)*xKITCHEN_OFFSET;
				yHomePosition = yHomePosition + yAPT_OFFSET;
				//xDestination = xHomePosition;
				//yDestination = yHomePosition;
			}
		}
		
		if(agent.myHome instanceof Home)
		{
				xHomePosition = 50;
				yHomePosition = 50;
				xKITCHEN_COUNTER_POSITION = xHomePosition;
				yKITCHEN_COUNTER_POSITION = yHomePosition - yKITCHEN_COUNTER_OFFSET;
				xFRIDGE_POSITION = xHomePosition + xFIDGE_OFFSET;
				yFRIDGE_POSITION = yHomePosition + yFIDGE_OFFSET;
				xGRILL_POSITION = xHomePosition + xGRILL_RIGHT_OFFSET;
				yGRILL_POSITION = yHomePosition -yGRILL_RIGHT_OFFSET;
				xTABLE_POS = HOUSE_TABLEPOS;
				yTABLE_POS = HOUSE_TABLEPOS;
		}
		xPos = xStartPos;
		yPos = yStartPos;
	}

	
	@Override
	public void updatePosition() 
	{
		//moving within house
		if(agent.myHome instanceof Home)
		{
			if(command == Command.LeaveHome)
			{
				
				if (yPos < yDestination)
					yPos++;
				else if (yPos > yDestination)
					yPos--;
				if(yPos == yDestination)
				{
					if (xPos < xDestination)
						xPos++;
					else if (xPos > xDestination)
						xPos--;
				}
			}
			else
			{
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
			}
		}
		else //moving within apt
		{
			if(command == Command.LeaveHome)
			{
				
				if (yPos < yDestination)
					yPos++;
				else if (yPos > yDestination)
					yPos--;
				if(yPos == yDestination)
				{
					if (xPos < xDestination)
						xPos++;
					else if (xPos > xDestination)
						xPos--;
				}
			}
			else
			{
				if (xPos < xDestination)
					xPos++;
				else if (xPos > xDestination)
					xPos--;
				else if (yPos < yDestination)
					yPos++;
				else if (yPos > yDestination)
					yPos--;
			}
		}
		
		if (xPos == xDestination && yPos == yDestination) 
		{
			if(command == Command.GoHome)
			{
				command = Command.noCommand;
				role.msgAnimationFinshed();
			}
			if(command == Command.GoToFridge)
			{
				command = Command.noCommand;
				role.msgAnimationFinshed();
				xDestination = xHomePosition;
				yDestination = yHomePosition;
			}
			
			else if(command == Command.GoToGrill || command == Command.GoToCounter)
			{	
				command = Command.noCommand;
				role.msgAnimationFinshed();
				xDestination = xHomePosition;
				yDestination = yHomePosition;
				for(MyFood f: foods)
				{
					if(f.food != null)
					{
						if(f.state == FoodState.PutFoodOnGrill)
						{
							f.state = FoodState.FoodOnGrill;
							f.CookingPoint = new Point(xGRILL_POSITION + COOKING_OFFSET, yGRILL_POSITION + 0*COOKING_OFFSET);
						}
						else if (f.state == FoodState.PutFoodOnCounter)
						{
							f.state = FoodState.FoodOnCounter;
							f.CookingPoint = new Point(xKITCHEN_COUNTER_POSITION + COOKING_OFFSET,yKITCHEN_COUNTER_POSITION + 0*COOKING_OFFSET);
						}
					}
				}
			}
			else if(command == command.GetFoodFromGrill || command == command.GetFoodFromCounter)
			{
				command = Command.noCommand;
				role.msgAnimationFinshed();
				for(MyFood f : foods)
				{
					if(f.state == FoodState.FoodOnGrill)
					{
						f.state = FoodState.PickUpFromGrill;
					}
					else if (f.state == FoodState.FoodOnCounter)
					{
						f.state = FoodState.PickUpFromCounter;
					}
				}
			}
			else if(command == Command.EatFood || command == Command.LeaveHome)
			{
				command = Command.noCommand;
				role.msgAnimationFinshed();
			}
		}
		
	}

	public void doEnterHome()
	{
		command = Command.GoHome;
	    xDestination = xTABLE_POS;
	    yDestination = yTABLE_POS; 
	}
	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(personImg, xPos, yPos, null);
		drawFood(g);
	}

	public void drawFood(Graphics2D g) {

		synchronized(foods)
		{
			for(MyFood f: foods)
			{
				if(f.food != null)
				{
					if(f.state == FoodState.PutFoodOnGrill || f.state == FoodState.PutFoodOnCounter || f.state == FoodState.PickUpFromCounter || f.state == FoodState.PickUpFromGrill)
					{
						g.drawImage(f.food.iconImg, xPos+f.point.x, yPos+f.point.y, null);
					}
					else if(f.state == FoodState.FoodOnGrill || f.state == FoodState.FoodOnCounter)
					{	
						g.drawImage(f.food.iconImg, f.CookingPoint.x, f.CookingPoint.y, null);
					}
				}
			}
		}
	}
	
	public void DoLeaveHome()
	{
		command = Command.LeaveHome;
		xDestination = xStartPos;
		yDestination = yStartPos;
	}
	
/***********************************
 * Cooking at home animation calls
 **********************************/
	public void DoGoToFridge() 
	{
		xDestination = xFRIDGE_POSITION;
		yDestination = yFRIDGE_POSITION;
		command = Command.GoToFridge;	
	}
	
	public void DoCookFood(String choice) 
	{
		// Grab food from fidge(already at fidge
		// if burger,steak,chicken put on grill and set timer
		// if salad or cookie, put on right
		if(choice.equalsIgnoreCase("steak") || choice.equalsIgnoreCase("chicken")){
			foods.add(new MyFood(new FoodIcon(choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET),choice));
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			command = Command.GoToGrill;
		}else{
			foods.add(new MyFood(new FoodIcon(choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET),choice));
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			command = Command.GoToCounter;
		}
		
	}
	
	public void SitDownAndEatFood()
	{
		command = Command.EatFood;
		xDestination = xTABLE_POS;
		yDestination = yTABLE_POS;
	}
	public void PlateFood()
	{
		for(MyFood f: foods)
		{
			if(f.food != null)
			{
				if(f.state == FoodState.FoodOnGrill)
				{
					command = Command.GetFoodFromGrill;
					xDestination = xGRILL_POSITION;
					yDestination = yGRILL_POSITION;
				}
				if (f.state == FoodState.FoodOnCounter)
				{
					command = Command.GetFoodFromCounter;
					xDestination = xKITCHEN_COUNTER_POSITION;
					yDestination = yKITCHEN_COUNTER_POSITION;
				}
			}
		}
	}
	
	public void DoneEating()
	{
		foods.clear();
	}

/*****************
 * Utility Class 
 ****************/
	class MyFood
	{
		FoodIcon food;
		Point point;
		Point CookingPoint;
		FoodState state;
		String choice;
		MyFood(FoodIcon f, Point p, String c){
			this.food = f;
			this.point = p;
			this.choice = c;
			
			if(choice.equalsIgnoreCase("steak") || choice.equalsIgnoreCase("chicken"))
			{
				state = FoodState.PutFoodOnGrill;
			}
			else
			{
				state = FoodState.PutFoodOnCounter;
			}
		}
	}
}
