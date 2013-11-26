package market.gui;

import restaurant.Restaurant;
import restaurant.gui.RestaurantListPanel;

import javax.swing.*;

import city.PersonAgent;
import city.animationPanels.InsideBuildingPanel;
import city.animationPanels.RestaurantAnimationPanel;
import city.gui.PersonGui;
import city.gui.SimCityGui;
import city.roles.CookRole;
import city.roles.CookRole.Food;
import city.roles.CustomerRole;
import city.roles.WaiterRole;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 1;
	private static final int NROWS = 8;
	private static final double CUSTOMER_DEFAULT_CASH = 200.00;
	private static final double NO_CASH = 0.0;
	
    //private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    //private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterRole> waiters = new Vector<WaiterRole>();

    private JPanel restLabel = new JPanel();
    private JButton update = new JButton("Update");
    //private ListPanel customerPanel = new ListPanel(this, "Customers");

    private MarketListPanel steakPanel = new MarketListPanel(this, "Steak");
    private MarketListPanel saladPanel = new MarketListPanel(this, "Salad");
    private MarketListPanel chickenPanel = new MarketListPanel(this, "Chicken");
    private MarketListPanel cookiePanel = new MarketListPanel(this, "Cookie");
    private MarketListPanel carPanel = new MarketListPanel(this, "Car");
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public MarketPanel(SimCityGui gui) {

        setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));

      //  group.add(customerPanel);

        //initRestLabel();
        //add(restLabel);
        //add(group);
        /*add(update);
        add(steakPanel);
        add(saladPanel);
        add(chickenPanel);
        add(cookiePanel);
        add(carPanel);*/
        
        //createWaiter("w1");
        //createWaiter("w2");

        //customerPanel.getTypeNameHere().addKeyListener(this);
        //waitersPanel.getTypeNameHere().addKeyListener(this);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
   

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
	@Override
	public void keyTyped(KeyEvent e) {		
	}
	public void setInsideBuildingPanel(InsideBuildingPanel parent){
    	insideBuildingPanel = parent;
    } 
    public InsideBuildingPanel getInsideBuildingPanel(){
    	return insideBuildingPanel;
    }
}

