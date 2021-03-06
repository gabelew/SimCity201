package GCRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import CMRestaurant.gui.CMRestaurantPanel;
import GCRestaurant.roles.GCWaiterRole;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;
import city.animationPanels.InsideAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ArrayList;

public class GCAnimationPanel extends InsideAnimationPanel implements ActionListener {

	private final int N_TABLES = 4;
    private final int TABLEX_START = 70;
    private final int TABLEY_START = 175;
    private final int xHostStand = 70;
    private final int yHostStand = 20;
    private final int xRegister = 100;
    private final int yRegister = 80;
    private final int TABLE_SPACING = 100;
    private final int xCookOrderingStand = 180;
    private final int yCookOrderingStand = 0;
    private final int xWaitOrderingStand = 250;
    private final int yWaitOrderingStand = 80;
    private final int xGRILL = 205;
    private final int yGRILL = -2;
    private final int xFRIDGE = 265; 
    private final int yFRIDGE = 23;
    private final int platingTableDist = 30;
    private final int xPlatingTable = 184;
    private final int yPlatingTable = 80;
    private BufferedImage fridgeImg;
    private BufferedImage grillImg;
    private BufferedImage tableImg;
    private BufferedImage hostStandImg;
    private BufferedImage registerImg;
    private BufferedImage platingTableImg;
    private BufferedImage orderingStandImg;
    private Dimension bufferSize;

    private List<Waiter> waiters = new ArrayList<Waiter>();//list for waiter home positions
    private List<Gui> guis = new ArrayList<Gui>();
    SimCityGui gui;
    
    public GCAnimationPanel(SimCityGui g) {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    	this.gui = g;
    	try 
    	{
 			StringBuilder path = new StringBuilder("imgs/");
 			fridgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
 			grillImg = ImageIO.read(new File(path.toString() + "grill2.png"));
 			tableImg = ImageIO.read(new File(path.toString() + "table.png"));
 			hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		    orderingStandImg = ImageIO.read(new File(path.toString() + "orderingstand.png"));
    	}
    	catch(Exception e){}
    }

    public void addGui(Gui g)
    {
    	guis.add(g);
    }
    
	public void actionPerformed(ActionEvent e) {
		if(insideBuildingPanel != null && insideBuildingPanel.isVisible)
			repaint();
	}

    public void paintComponent(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
	
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //makes plating area
        for(int i = 0; i < 4; i++)
        {
        	g2.drawImage(platingTableImg, xPlatingTable+ (i*platingTableDist), yPlatingTable, null);
        }
        //draws orderstands
        g2.drawImage(orderingStandImg, xCookOrderingStand, yCookOrderingStand, null);
        g2.drawImage(orderingStandImg, xWaitOrderingStand, yWaitOrderingStand,null);
        //draws fridge
        g2.drawImage(fridgeImg, xFRIDGE, yFRIDGE, null);
        //draws grill
        g2.drawImage(grillImg, xGRILL, yGRILL, null);
        //draws host stand
        g2.drawImage(hostStandImg, xHostStand, yHostStand, null);
        //draws register for cashier
        g2.drawImage(registerImg, xRegister, yRegister, null);
        //draws tables
        for(int i = 0; i < N_TABLES; i++)
        {
        	g2.drawImage(tableImg, TABLEX_START+ (i*TABLE_SPACING), TABLEY_START, null);
        }
        try
        {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        
        
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
        catch(ConcurrentModificationException e) {}
    }

    /*
     * For ListView
     */
    public void addWaiterToList(String name){
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).addWaiterToList(name);
	}
	public void removeWaiterFromList(String name){
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).removeWaiterFromList(name);
	}
	public void addCustomerToList(String name){
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).addCustomerToList(name);
	}
	public void removeCustomerFromList(String name){
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).removeCustomerFromList(name);
	}
	public void setHost(Host h)
	{
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).setHost(h);	
	}
	public void setWaiterCantBreak(String name) {
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).setWaiterCantBreak(name);
	}

	public void setWaiterWorking(String name) {
		((GCRestaurantPanel) insideBuildingPanel.guiInteractionPanel).WaiterBackFromBreak(name);
	}
    /**
     * START
     * Functions to set waiterGui home positions
     */
    public int choosingWaiterHomePos(GCWaiterRole w)
    {
    	return waiters.indexOf(w);
    }
	public void addWaiter(Waiter w) {
		waiters.add(w);
	}
	public void removeWaiter(Waiter w)
	{
		waiters.remove(w);
	}
	/**
	 * END
     * Functions to set waiterGui home positions
     */

}
