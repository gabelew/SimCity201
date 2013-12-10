package GCRestaurant.gui;

import javax.swing.*;

import restaurant.interfaces.Host;
import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;
import CMRestaurant.gui.CMRestaurantListPanel;
import agent.Agent;

import java.awt.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class GCRestaurantPanel extends JPanel 
{
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 2;
	private static final int NROWS = 2;


    //private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    //private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private JPanel group = new JPanel();
    public GCRestaurantListPanel waitersPanel = new GCRestaurantListPanel(this, "Waiters");
    public GCRestaurantListPanel customersPanel = new GCRestaurantListPanel(this, "Customers");
    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public GCRestaurantPanel(SimCityGui gui) 
    {
    	setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));
        this.gui = gui;
        
        add(waitersPanel);
        add(customersPanel);
    }
    
	public void setInsideBuildingPanel(InsideBuildingPanel bp)
	{
		this.insideBuildingPanel = bp;
	}
	
	//adds host to listpanel
	public void setHost(Host host) 
	{
		waitersPanel.setHost(host);
	}
	//adds waiters names to list panel
	public void addWaiterToList(String name)
	{
		waitersPanel.addPerson(name);
	}
	public void removeWaiterFromList(String name)
	{
		waitersPanel.removeWaiter(name);
	}
	
	//adds customers names to list panel
	public void addCustomerToList(String name)
	{
		customersPanel.addPerson(name);
	}
	public void removeCustomerFromList(String name)
	{
		customersPanel.removeWaiter(name);
	}

	//break functionality
	public void setWaiterCantBreak(String name) {
		waitersPanel.setWaiterCantBreak(name);
	}

	public void WaiterBackFromBreak(String name) {
		waitersPanel.setWaiterBackFromBreak(name);
	}

}
