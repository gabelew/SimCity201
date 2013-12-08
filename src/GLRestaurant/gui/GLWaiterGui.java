package GLRestaurant.gui;

import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLWaiterRole;
import GLRestaurant.roles.GLCookRole;
import city.gui.Gui;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class GLWaiterGui implements Gui {

	private static final int PERSONWIDTH = 20;
	private static final int PERSONHEIGHT = 20;
	private int ORIGINALX, ORIGINALY;
	private int platex, platey;
	
    private GLWaiterRole agent = null;
    
    //RestaurantGui gui;
    GLCustomerGui customerGui;
    private int customerx, customery;
    private enum Command {noCommand, onBreak, ReturnFromBreak, IfAtOrigin, IfAtCustomer, IfAtPlate};
    private Command command = Command.noCommand;
    String customerChoice;
    boolean customerOrdered = false;
    boolean bringingFood = false;
    boolean goOnBreak = false;
    String foodCarried = "";
    private ArrayBlockingQueue<String> orders = new ArrayBlockingQueue<String>(10);

    private int xPos, yPos;//waiter's position
    private int xDestination, yDestination; // waiter's destination

    static class Point {
    	int x;
    	int y;
    	Point (int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    Map<Integer, Point> tableMap = new HashMap<Integer, Point>();
    
    public static final Point TableOne = new Point(100,100);
    public static final Point TableTwo = new Point(200,100);
    public static final Point TableThree = new Point(300,100);

    public GLWaiterGui(GLWaiterRole agent) {
        this.agent = agent;
        tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
    }
    
    public GLWaiterGui(GLWaiterRole w, int x, int y){ 
		this.agent = w;
		ORIGINALX = x;
		ORIGINALY = y;
		xPos = -40;
		yPos = -40;
		xDestination = ORIGINALX;
		yDestination = ORIGINALY;
		//this.gui = gui;
		tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
	}
    
    public void setCustomerGui(GLCustomerGui g) {
    	this.customerGui = g;
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
        if (xDestination == xPos && yDestination == yPos) {
        	if (((TableOne.x + 20 == xDestination) || (TableTwo.x + 20 == xDestination) || (TableThree.x + 20 == xDestination)) 
        	&& ((TableOne.y - 20 == yDestination) || (TableTwo.y - 20 == yDestination) || (TableThree.y - 20 == yDestination))) {
        		agent.msgAtTable();
        	} else if (Command.onBreak == command) {
        		//gui.setWaiterOnBreak(agent);
        	} else if (Command.ReturnFromBreak == command) {
        		//gui.setWaiterEnabled(agent);
        	} else if (Command.IfAtOrigin == command && ORIGINALX == xDestination && ORIGINALY == yDestination) {
        		agent.msgAtOrigin();
        	} else if (Command.IfAtCustomer == command && customerx == xDestination && customery == yDestination) {
        		agent.msgAtCustomer();
        	} else if (Command.IfAtPlate == command && platex == xDestination && platey == yDestination)
        		agent.msgAtPlate();
        	command = Command.noCommand;
        }
    }

    public void takenOrder(String choice) {
    	customerOrdered= true;
    	foodCarried = "";
    	for (String order : orders) {
			foodCarried += order + "? ";
		}
		if("steak".equals(choice)) {
			this.customerChoice = "ST";
		}
		else if("chicken".equals(choice)) {
			this.customerChoice = "CH";
		}
		else if("salad".equals(choice)) {
			this.customerChoice = "SD";
		}
		else if("pizza".equals(choice)){
			this.customerChoice = "PZ";
		}
		orders.add(customerChoice);
		foodCarried += customerChoice + "? ";
	}
    
    public void servingFood(String choice) {
		customerOrdered = false;
		if("steak".equals(choice)) {
			this.customerChoice = "ST";
		}
		else if("chicken".equals(choice)) {
			this.customerChoice = "CH";
		}
		else if("salad".equals(choice)) {
			this.customerChoice = "SD";
		}
		else if("pizza".equals(choice)){
			this.customerChoice = "PZ";
		}
		foodCarried = customerChoice;
		bringingFood = true;
	}
    
    public void ifAtOrigin() {
    	command = Command.IfAtOrigin;
    }
    
    public void ifAtCustomer(int x, int y) {
    	command = Command.IfAtCustomer;
    	customerx = x + 20;
    	customery = y - 20;
    }
    
    public void ifAtPlate(int x, int y) {
    	command = Command.IfAtPlate;
    	platex = x - 20;
    	platey = y;
    }
    
    public void finishedWithOrder(String choice) {
    	bringingFood = false;
    	foodCarried = "";
    	orders.remove();
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.darkGray);
        g.fillRect(xPos, yPos, PERSONWIDTH, PERSONHEIGHT);
        if(customerOrdered) {
			g.setColor(Color.BLACK);		
			g.drawString(foodCarried, xPos + 15, yPos);
		}
		else if(bringingFood) {
			g.setColor(Color.BLACK);
			g.drawString(foodCarried, xPos + 15, yPos);
		}
    }

    public boolean isPresent() {
        return true;
    }
    
    public boolean onBreak() {
    	return goOnBreak;
    }
    
    public void wantGoOnBreak() {
    	goOnBreak = true;
    	agent.msgWantToGoOnBreak();
    }
    
    public void goOnBreak() {
    	xDestination = -40;
    	yDestination = -40;
    	command = Command.onBreak;
    }
    
    public void breakDone() {
    	goOnBreak = false;
    	xDestination = ORIGINALX;
    	yDestination = ORIGINALY;
    	command = Command.ReturnFromBreak;
    	agent.msgReturnedFromBreak();
    }
    
    public void noBreak() {
    	goOnBreak = false;
    	command = Command.ReturnFromBreak;
    }

    public void GoToTable(GLCustomerRole customer, int tablenumber) {
    	Point destination = tableMap.get(tablenumber);
    	if(null != destination) {
    		xDestination = destination.x + 20;
    		yDestination = destination.y - 20;
    	}
    }

    public void DoLeaveCustomer() {
        xDestination = ORIGINALX;
        yDestination = ORIGINALY;
    }
    
    public void GoToPlate(int x, int y) {
    	xDestination = x - 20;
    	yDestination = y;
    }
    
    public void GoToCustomer(int x, int y) {
    	xDestination = x + 20;
    	yDestination = y - 20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	@Override
	public void setPresent(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
