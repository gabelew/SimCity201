package GCRestaurant.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import restaurant.interfaces.Customer;
import city.gui.Gui;
import GCRestaurant.roles.GCCustomerRole;
import GCRestaurant.roles.GCWaiterRole;

public class GCWaiterGui implements Gui {

	private final int TABLE_SPACING = 100;
	private final int TABLE_Y = 175;
    private GCWaiterRole role = null;
    private boolean atStart = true;

    private int startPos = -20;
    private int DEFAULT_POSY = 110;
    private int DEFAULT_POSX = 0;
    private int xPos = DEFAULT_POSX, yPos = DEFAULT_POSY;//default waiter position
    private int xDestination = DEFAULT_POSX, yDestination = DEFAULT_POSY;//default start position
    private int personSize = 20;
    private int cookPosX = 200, cookPosY = 80;
    private int cashierPosX = 97, cashierPosY = 50;
    private int customerPos = 40;
    
    public int xTable;
    public int yTable;
    private boolean carryFood = false;
    private List<String> foods = Collections.synchronizedList(new ArrayList<String>());
    private BufferedImage waiterImg;

    public GCWaiterGui(GCWaiterRole r) {
        this.role = r;
        try {waiterImg = ImageIO.read(new File("imgs/waiter_v1.png"));}
        catch (IOException e) {}
        this.xTable = TABLE_SPACING;
        this.yTable = TABLE_Y;
        xPos = startPos;
        yPos = startPos;
        xDestination = startPos;
        yDestination = startPos;
        
    }
    
    public void setWaiterPosition(int homePos)
    {
    	this.DEFAULT_POSY += homePos*personSize;
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
       		& (xDestination == xTable + personSize) & (yDestination == yTable - personSize)) {
           
        	role.msgAtTable();
        }
        
        if (xPos == xDestination && yPos == yDestination
           		& (xDestination == cookPosX) & (yDestination == cookPosY)) {
             //xPos = DEFAULT_POSX;
             //yPos = DEFAULT_POSY;
             xDestination = DEFAULT_POSX;
             yDestination = DEFAULT_POSY;
        	role.msgAtTable();
            }
        if (xPos == xDestination && yPos == yDestination
           		& (xDestination == cashierPosX) & (yDestination == cashierPosY)) {
             //xPos = DEFAULT_POSX;
             //yPos = DEFAULT_POSY;
             xDestination = DEFAULT_POSX;
             yDestination = DEFAULT_POSY;
        	role.msgAtTable();
            }
       if (xPos == xDestination && yPos == yDestination
           		& (xDestination == customerPos) & (yDestination == customerPos)) 
        {
    	   role.msgAtTable();
        }
        
        if(xPos == DEFAULT_POSX && yPos == DEFAULT_POSY){atStart = true;}
        else{atStart = false;}
    }

    public void draw(Graphics2D g) {
    	try
    	{
    		g.drawImage(waiterImg, xPos, yPos, null);
	        if(carryFood)
	        {
	        	g.setColor(Color.BLACK);
				g.setFont(new Font("Arial", Font.BOLD, 18));
				if(!foods.isEmpty())
				{
					for(int i = 0; i <foods.size(); i++)
					{
						String abrFood = (foods.get(i)).charAt(0) + "" + (foods.get(i)).charAt(1);
						g.drawString( abrFood, xPos, yPos + personSize);
					}
				}
	        }
	        if(foods.size() == 0)
	        {
	        	carryFood = false;
	        }
    	}
    	catch(Exception e)
    	{
    		return;
    	}
    }
    
    public void bringFood(String food)
    {
    	carryFood = true;
    	foods.add(food);
    }
    public void servedFood()
    {
    	foods.remove(0);
    }

    public boolean isPresent() {
        return true;
    }

    public void getWaitingCustomer()
    {
    	xDestination = customerPos;
    	yDestination = customerPos;
    }
    public void DoBringToTable(Customer c, int tableNumber) {
    	xTable = tableNumber*TABLE_SPACING;
    	
        xDestination = xTable + personSize;
        yDestination = yTable - personSize;
    }
    
    public void getFood()
    {
    	
    }
    public void goToTable(int tableNumber) {
    	xTable = tableNumber*TABLE_SPACING;
    	
        xDestination = xTable + personSize;
        
        yDestination = yTable - personSize;
    }
    
    public void goToCook()
    {
    	xDestination = cookPosX;
    	yDestination = cookPosY;
    }
    
    public void goToCashier()
    {
    	xDestination = cashierPosX;
    	yDestination = cashierPosY;
    }
    
    public void DoLeaveCustomer() {
        xDestination = DEFAULT_POSX;
        yDestination = DEFAULT_POSY;
    }
    
    public boolean atStartPos()
    {
    	return atStart;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	@Override
	public void setPresent(boolean b) {
		
	}

	public void enterRestaurant() {
		xDestination = DEFAULT_POSX;
		yDestination = DEFAULT_POSY;
		
	}
}
