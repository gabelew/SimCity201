package market.gui;

import restaurant.Restaurant;
import restaurant.gui.RestaurantListPanel;

import javax.swing.*;

import city.MarketAgent;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 15;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 7;
	private static final int GROUP_NCOLUMNS = 1;
	private static final int NCOLUMNS = 1;
	private static final int NROWS = 8;
	private static final double CUSTOMER_DEFAULT_CASH = 200.00;
	private static final double NO_CASH = 0.0;
	
	private int steak=0;
	private int chicken=0;
	private int cookie=0;
	private int salad=0;
	private int car=0;
    //private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    //private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterRole> waiters = new Vector<WaiterRole>();
    private MarketAgent Market;
    private JLabel marketLabel = new JLabel();
    private JButton update = new JButton("Update all");
    //private ListPanel customerPanel = new ListPanel(this, "Customers");

    public MarketListPanel steakPanel = new MarketListPanel(this, "Steak",200);
    public MarketListPanel saladPanel = new MarketListPanel(this, "Salad",200);
    public MarketListPanel chickenPanel = new MarketListPanel(this, "Chicken",200);
    public MarketListPanel cookiePanel = new MarketListPanel(this, "Cookie",200);
    public MarketListPanel carPanel = new MarketListPanel(this, "Car",200);
    public JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public MarketPanel(SimCityGui gui) {

        //setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
    	setLayout(new FlowLayout(FlowLayout.CENTER,10,20));
        marketLabel.setText("<html> To change the inventory for this market <br> input the amounts below.<br> If empty will default to zero</html>");
        update.addActionListener(this);
        update.setPreferredSize(new Dimension(100,30));
        add(marketLabel);
        add(update);
        add(steakPanel);
        add(saladPanel);
        add(chickenPanel);
        add(cookiePanel);
        add(carPanel);
        
        //createWaiter("w1");
        //createWaiter("w2");

        //customerPanel.getTypeNameHere().addKeyListener(this);
        //waitersPanel.getTypeNameHere().addKeyListener(this);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
   
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == update) {
        	String steaks=steakPanel.typeNameHere.getText();
        	String chickens=chickenPanel.typeNameHere.getText();
        	String salads=saladPanel.typeNameHere.getText();
        	String cookies=cookiePanel.typeNameHere.getText();
        	String cars=carPanel.typeNameHere.getText();
        	if(!steaks.isEmpty())
        		steak=Integer.parseInt(steaks);
        	if(!chickens.isEmpty())
        		chicken=Integer.parseInt(chickens);
        	if(!cookies.isEmpty())
        		cookie=Integer.parseInt(cookies);
        	if(!salads.isEmpty())
        		salad=Integer.parseInt(salads);
        	if(!cars.isEmpty())
        		car=Integer.parseInt(cars);
        	Market.setInventory(car, chicken, steak, salad, cookie);
        	steakPanel.typeNameHere.setText("");
        	chickenPanel.typeNameHere.setText("");
        	saladPanel.typeNameHere.setText("");
        	cookiePanel.typeNameHere.setText("");
        	carPanel.typeNameHere.setText("");
        	steakPanel.labels.setText("Steak: "+steak);
        	chickenPanel.labels.setText("Chicken: "+chicken);
        	saladPanel.labels.setText("Salad: "+salad);
        	cookiePanel.labels.setText("Cookie: "+cookie);
        	carPanel.labels.setText("Car: "+car);
        }
    }

	public void setInsideBuildingPanel(InsideBuildingPanel parent){
    	insideBuildingPanel = parent;
    } 
    public InsideBuildingPanel getInsideBuildingPanel(){
    	return insideBuildingPanel;
    }
    public void setMarket(MarketAgent market){
    	Market=market;
    }
}

