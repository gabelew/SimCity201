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

import restaurant.RoleOrder;
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
	private int xHomePosition = 20;
	private int yHomePosition = 30;
	private int xFRIDGE_POSITION = 0;
	private int yFRIDGE_POSITION = 0;
	private int xGRILL_POSITION = 0;
	private int yGRILL_POSITION = 0;
	private int xTABLE_POS = 0;
	private int yTABLE_POS = 0;
	private int xKITCHEN_COUNTER_POSITION = 0;
	private int yKITCHEN_COUNTER_POSITION = 0;
    static final int xFOOD_OFFSET = 10;
    static final int yFOOD_OFFSET = 4;
    static final int yKITCHEN_COUNTER_OFFSET = 30;
    static final int yGRILL_RIGHT_OFFSET = 30;
    static final int xGRILL_RIGHT_OFFSET = 52;
    static final int yFIDGE_OFFSET = 15;
    static final int xFIDGE_OFFSET = 100;
    static final int yAPT_OFFSET = 310;
	
	List<MyFood> foods = Collections.synchronizedList(new ArrayList<MyFood>());
	private enum Command {noCommand, GoToFridge, GoToGrill, GoToCounter, GoToRestPost};
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
		
		agent = c;
		role = r;
		if(agent.myHome instanceof Apartment)
		{
			int aptnum = ((Apartment)agent.myHome).renters.indexOf(agent);
			if(aptnum < 4)//top 4 apartments
			{
				xKITCHEN_COUNTER_POSITION = xHomePosition + aptnum*217;
				yKITCHEN_COUNTER_POSITION = yHomePosition - yKITCHEN_COUNTER_OFFSET;
				xFRIDGE_POSITION = xHomePosition + xFIDGE_OFFSET + aptnum*217;;
				yFRIDGE_POSITION = yHomePosition + yFIDGE_OFFSET;
				xGRILL_POSITION = xHomePosition + xGRILL_RIGHT_OFFSET + aptnum*217;
				yGRILL_POSITION = yHomePosition -yGRILL_RIGHT_OFFSET;
			}
			else //bottom 4 apartments
			{
				xKITCHEN_COUNTER_POSITION = xHomePosition + aptnum*217;
				yKITCHEN_COUNTER_POSITION = yHomePosition - yKITCHEN_COUNTER_OFFSET + yAPT_OFFSET;
				xFRIDGE_POSITION = xHomePosition + xFIDGE_OFFSET + aptnum*217;;
				yFRIDGE_POSITION = yHomePosition + yFIDGE_OFFSET + yAPT_OFFSET;
				xGRILL_POSITION = xHomePosition + xGRILL_RIGHT_OFFSET + aptnum*217;
				yGRILL_POSITION = yHomePosition -yGRILL_RIGHT_OFFSET + yAPT_OFFSET;
			}
		}
		xPos = 0;
		yPos = 200; //103+80*i
		xDestination = 200;
		yDestination = 200;
		
	}

	
	@Override
	public void updatePosition() {
			if (xPos < xDestination && (yPos - 115)%80==0)
				xPos++;
			else if (xPos > xDestination && (yPos - 115)%80==0)
				xPos--;
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
			yPos--;
		
/*
		if (xPos == xDestination && yPos == yDestination) {
			if(command == Command.walkToDestination){
				command = Command.noCommand;
				agent.msgAnimationFinshed();
			}
		}
		*/
	}

	public void doEnterHome()
	{
	        xDestination = xHomePosition;
	        yDestination = yHomePosition; 
	        //command = Command.enterHome;   	
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
	}

	public void drawFood(Graphics2D g) {

		synchronized(foods)
		{
			for(MyFood f: foods)
			{
				if(f.food != null)
				{
					if(f.state == FoodState.PutFoodOnGrill || f.state == FoodState.PutFoodOnCounter )
					{
						g.drawImage(f.food.iconImg, xPos+f.point.x, yPos+f.point.y, null);
					}
					else 
					{	
						g.drawImage(f.food.iconImg, f.point.x, f.point.y, null);
					}
				}
			}
		}
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
	public void DoCookFood(RoleOrder order) 
	{
		// Grab food from fidge(already at fidge
		// if burger,steak,chicken put on grill and set timer
		// if salad or cookie, put on right
		if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken") || order.choice.equalsIgnoreCase("burger")){
			foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			command = Command.GoToGrill;
		}else{
			//foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			command = Command.GoToCounter;
		}
		
	}
	
	public void PlateAndEatFood()
	{
		xDestination = xTABLE_POS;
		yDestination = yTABLE_POS;
	}

/*****************
 * Utility Class 
 ****************/
	class MyFood
	{
		FoodIcon food;
		Point point;
		RoleOrder order;
		FoodState state;
		MyFood(FoodIcon f, Point p, RoleOrder o){
			this.food = f;
			this.point = p;
			this.order = o;
			
			if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken"))
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
