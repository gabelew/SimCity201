package atHome.city;

import javax.swing.*;

import restaurant.interfaces.Host;
import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;
import city.roles.AtHomeRole;
import CMRestaurant.gui.CMRestaurantListPanel;
import agent.Agent;

import java.awt.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class AtHomePanel extends JPanel 
{
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 2;
	private static final int NROWS = 2;
	private String type;
    private JPanel group = new JPanel();
    public AtHomeListPanel aptPanel = new AtHomeListPanel(this, "Apartment");
    public AtHomeListPanel homePanel = new AtHomeListPanel(this, "Home");
    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public AtHomePanel(SimCityGui gui, String type) 
    {
    	setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));
        this.gui = gui;
        this.type = type;
        if(type.equals("Home"))
        	add(homePanel);
        else
        	add(aptPanel);
        
    }
    
	public void setInsideBuildingPanel(InsideBuildingPanel bp)
	{
		this.insideBuildingPanel = bp;
	}
	
	
	//adds waiters names to list panel
	public void addAptPersonToList(String name, AtHomeRole role)
	{
		aptPanel.addPerson(name, role);
	}
	
	public void addHomePersonToList(String name, AtHomeRole role)
	{
		homePanel.addPerson(name, role);
	}
	//break functionality

	public void removePersonFromList(String name, AtHomeRole role) 
	{
		//System.out.println("!@#$%^&*(");
		homePanel.notBrokenAnymore(role);
	}
	

}
