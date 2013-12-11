package GLRestaurant.gui;


import javax.swing.*;

import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;

import java.awt.*;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
@SuppressWarnings("serial")
public class GLRestaurantPanel extends JPanel {
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 2;
	private static final int NROWS = 2;
    
    private GLRestaurantListPanel waiterPanel = new GLRestaurantListPanel(this, "Waiters");
    private GLRestaurantListPanel customerPanel = new GLRestaurantListPanel(this, "Customers");

    private JPanel group = new JPanel();

    private SimCityGui gui;
    private InsideBuildingPanel insideBuildingPanel;
    
    public GLRestaurantPanel(SimCityGui gui) {	
    	this.gui = gui;

    	setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));

        add(waiterPanel);
        add(customerPanel);
    }
    
    public void addWaiterToList(String name){
		waiterPanel.addPerson(name);
	}
	public void removeWaiterFromList(String name){
		waiterPanel.removePerson(name);
	}
	public void addCustomerToList(String name){
		customerPanel.addPerson(name);
	}
	public void removeCustomerFromList(String name){
		customerPanel.removePerson(name);
	}
	
	public void setInsideBuildingPanel(InsideBuildingPanel parent){
    	insideBuildingPanel = parent;
    } 
    public InsideBuildingPanel getInsideBuildingPanel(){
    	return insideBuildingPanel;
    }

}
