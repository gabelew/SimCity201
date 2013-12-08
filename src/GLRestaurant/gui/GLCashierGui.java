package GLRestaurant.gui;
import GLRestaurant.roles.GLCashierRole;
import city.gui.Gui;
import java.awt.*;

public class GLCashierGui implements Gui {
	private static final int PERSONWIDTH = 20;
	private static final int PERSONHEIGHT = 20;
    private GLCashierRole agent = null;

    private int xPos = -20, yPos = -20;//default position
    private int xDestination = -20, yDestination = -20;//default start position

    public GLCashierGui(GLCashierRole agent) {
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
