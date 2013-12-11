package GLRestaurant.gui;


import javax.swing.*;

import city.animationPanels.InsideBuildingPanel;
import restaurant.Restaurant;
import city.animationPanels.GLRestaurantAnimationPanel;
import city.gui.SimCityGui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

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
    
//    private JPanel restLabel = new JPanel();
    //private ListPanel customerPanel = new ListPanel(this, "Customers");
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
      
       // add(group);
    }
    
//    public void setWorking(String type, String name) {
//
//        if (type.equals("Waiters")) {
//
//    		for(Restaurant r: gui.getRestaurants()){
//    	        for (GLHostRole.MyWaiter temp: ((GLHostRole)r.host).waiters) {
//    	            if (((GLWaiterRole) temp.w).getName() == name){
//    	            	((GLWaiterRole) temp.w).getGui().setWorking();
//    	            }
//    	        }
//    		}
//        }
//    }
    
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
    
//    /**
//     * Tells the base agents to pause.
//     */
//    public void pause() {
//    	for (Agent a : agents) {
//    		a.pause();
//    	}
//    }
//    		
//    public void resume() {
//    	for (Agent a: agents) {
//    		a.resume();
//    	}
//    }

}
