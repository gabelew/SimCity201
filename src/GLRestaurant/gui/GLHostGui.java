package GLRestaurant.gui;


import GLRestaurant.roles.GLHostRole;
import city.gui.Gui;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GLHostGui implements Gui {
	private static final int PERSONWIDTH = 20;
	private static final int PERSONHEIGHT = 20;
    private GLHostRole agent = null;

    private int xPos = -20, yPos = -20;//default host position
    private int xDestination = -20, yDestination = -20;//default start position
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

    public GLHostGui(GLHostRole agent) {
        this.agent = agent;
        tableMap.put(1, TableOne);
        tableMap.put(2, TableTwo);
        tableMap.put(3, TableThree);
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
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, PERSONWIDTH, PERSONHEIGHT);
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

	@Override
	public void setPresent(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
