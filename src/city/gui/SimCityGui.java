package city.gui;

import restaurant.CustomerAgent;
import restaurant.gui.RestaurantPanel;
import restaurant.gui.Table;

import javax.swing.*;

import city.PersonAgent;
import city.animationPanels.ApartmentAnimationPanel;
import city.animationPanels.BankAnimationPanel;
import city.animationPanels.HouseAnimationPanel;
import city.animationPanels.InsideAnimationPanel;
import city.animationPanels.InsideBuildingPanel;
import city.animationPanels.MarketAnimationPanel;
import city.animationPanels.RestaurantAnimationPanel;
import agent.Agent;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class SimCityGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	/* The GUI had two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	public AnimationPanel animationPanel = new AnimationPanel(this);
    
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    

    private InfoPanel infoPanel = new InfoPanel(this);
 
    private List<Table> tables = new ArrayList<Table>();
    public List<PersonAgent> persons = new ArrayList<PersonAgent>();
    
    private JFrame bottomFrame = new JFrame();
    private JPanel topPanel = new JPanel();
    CardLayout cardLayout = new CardLayout();
    JPanel buildingsPanel = new JPanel();
    
    private int hour;
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thrusday", "Friday", "Saturday", "Sunday"};
    private String dayOfWeek = daysOfWeek[0];
    
    static final int FRAMEX = 1100;
    static final int FRAMEY = 430;
    static final int INSIDE_BUILDING_FRAME_Y = 517;
    static final int WINDOWX = 225;
    static final int OFFSETPOS = 50;
    static final int NROWS = 1;
    static final int NCOLUMNS = 0;
    static final int REST_PANEL_Y = 450;
    static final int xTABLE_WIDTH = 42;
    static final int yTABLE_WIDTH = 44;
    static final int TABLE_PADDING = 5;
	static final int xTABLE_IMG_POINT_OFFSET = 1;
	static final int yTABLE_IMG_POINT_OFFSET = 40;
	static final int xTABLE_IMG_AREA_OFFSET = 10;
	static final int yTABLE_IMG_AREA_OFFSET = 42;
	static final int xDEFAULT_NEW_TABLE_POSITION = 140;
	static final int yDEFAULT_NEW_TABLE_POSITION = 40;
	static final int STARTING_TABLES_X = 200;
	static final int STARTING_TABLE1_Y = 35;
	static final int STARTING_TABLE_Y_SPACING = 90;
    
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public SimCityGui() {
    	setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, FRAMEY);
    	setLayout(new BorderLayout());
    	topPanel.setLayout(new BorderLayout(5,0));
        
        Dimension infoDim = new Dimension(WINDOWX, (int) (REST_PANEL_Y));
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);

        topPanel.add(animationPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.CENTER);
        
        
        Dimension buildingsPanelDim = new Dimension(WINDOWX,REST_PANEL_Y);
        buildingsPanel.setLayout(cardLayout);
        buildingsPanel.setMaximumSize(buildingsPanelDim);
        buildingsPanel.setMinimumSize(buildingsPanelDim);
        buildingsPanel.setPreferredSize(buildingsPanelDim);
        
        createDefaultBuildingPanels();
        
        
        bottomFrame.setLayout(new BorderLayout());
        bottomFrame.add(buildingsPanel, BorderLayout.CENTER);
        bottomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bottomFrame.setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, INSIDE_BUILDING_FRAME_Y);
        bottomFrame.setVisible(true);
    }

    private void createDefaultBuildingPanels() {
    	List<BuildingIcon> buildings = animationPanel.buildings;
    	System.out.println(buildings.size());
        for(int i =0; i<buildings.size(); i++){
        	
        	BuildingIcon b = buildings.get(i);
        	
        	if(b.type.equals("restaurant")){
            
        	JPanel APanel = new JPanel();
            RestaurantPanel restPanel = new RestaurantPanel(this);
            InsideAnimationPanel restaurantAnimationPanel = new RestaurantAnimationPanel(this);
        	
            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
            restPanel.setPreferredSize(restDim);
            restPanel.setMinimumSize(restDim);
            restPanel.setMaximumSize(restDim);
            APanel.add(restPanel);

        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,restaurantAnimationPanel, APanel);
        	b.setInsideBuildingPanel(bp);
        	buildingsPanel.add(bp, "" + i);
        	}else if(b.type.equals("market")){
                
            	JPanel APanel = new JPanel();
                RestaurantPanel restPanel = new RestaurantPanel(this);
                InsideAnimationPanel marketAnimationPanel = new MarketAnimationPanel(this);
            	
                Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
                restPanel.setPreferredSize(restDim);
                restPanel.setMinimumSize(restDim);
                restPanel.setMaximumSize(restDim);
                APanel.add(restPanel);

            	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,marketAnimationPanel, APanel);
            	b.setInsideBuildingPanel(bp);
            	buildingsPanel.add(bp, "" + i);
	    	}else if(b.type.equals("bank")){
	            
	        	JPanel APanel = new JPanel();
	            RestaurantPanel restPanel = new RestaurantPanel(this);
	            InsideAnimationPanel bankAnimationPanel = new BankAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            restPanel.setPreferredSize(restDim);
	            restPanel.setMinimumSize(restDim);
	            restPanel.setMaximumSize(restDim);
	            APanel.add(restPanel);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,bankAnimationPanel, APanel);
	        	b.setInsideBuildingPanel(bp);
	        	buildingsPanel.add(bp, "" + i);
			}else if(b.type.equals("house")){
		        
		    	JPanel APanel = new JPanel();
		        RestaurantPanel restPanel = new RestaurantPanel(this);
		        InsideAnimationPanel houseAnimationPanel = new HouseAnimationPanel(this);
		    	
		        Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
		        restPanel.setPreferredSize(restDim);
		        restPanel.setMinimumSize(restDim);
		        restPanel.setMaximumSize(restDim);
		        APanel.add(restPanel);
		
		    	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,houseAnimationPanel, APanel);
		    	b.setInsideBuildingPanel(bp);
		    	buildingsPanel.add(bp, "" + i);
			}else if(b.type.equals("apartment")){
			    
				JPanel APanel = new JPanel();
			    RestaurantPanel restPanel = new RestaurantPanel(this);
			    InsideAnimationPanel apartmentAnimationPanel = new ApartmentAnimationPanel(this);
				
			    Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
			    restPanel.setPreferredSize(restDim);
			    restPanel.setMinimumSize(restDim);
			    restPanel.setMaximumSize(restDim);
			    APanel.add(restPanel);
			
				InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,apartmentAnimationPanel, APanel);
				b.setInsideBuildingPanel(bp);
				buildingsPanel.add(bp, "" + i);
	        }
		}
	}

	/**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {

    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */ 
    public void setCustomerEnabled(CustomerAgent c) {
    	
    	infoPanel.setCustomerEnabled(c);
    	//find customer in list
    	/*for(CustomerAgent currentPerson:customersList)
    	{
	        if (currentPerson instanceof CustomerAgent) {
	            CustomerAgent cust = (CustomerAgent) currentPerson;
	            if (c.equals(cust)) {
	                stateCB.setEnabled(true);
	                stateCB.setSelected(false);
	            }
	     }
            
        }*/
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("csci201 SimCity");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.addDefaultTables();
    }
    private void addDefaultTables() {
        //restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y);
        //restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING);
        //restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
        //restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
	}

 	public int getTablesXCoord(int i){
    	return tables.get(i).getX();
    }
    public int getTablesYCoord(int i){
    	return tables.get(i).getY();
    }

	public boolean isOnTable(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return true;
			}
		}
		return false;
	}

	public Table getTableAt(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return t;
			}
		}
		return null;
	}
	
	public Table getTableAtIndex(int i) {
		if(tables.size() < i){
			return tables.get(i);
		}
		
		return null;
	}
	public void setTableOccupied(int tableNumber) {
		tables.get(tableNumber).setOccupied();
		//restPanel.setTableDisabled(tableNumber);
	}
	public void setTableUnoccupied(int tableNumber) {
		tables.get(tableNumber).setMovable();
		//restPanel.setTableEnabled(tableNumber);
	}

	public boolean notOnExistingTable(Table newTablePos, Point placeTableHere) {
		for(Table t: tables)
		{
			if(t != newTablePos){
				if(
					(t.getX() - TABLE_PADDING <= placeTableHere.x && t.getX()+xTABLE_WIDTH + TABLE_PADDING >= placeTableHere.x &&
					t.getY() - TABLE_PADDING <= placeTableHere.y && t.getY()+yTABLE_WIDTH + TABLE_PADDING >= placeTableHere.y) ||
					(placeTableHere.x - TABLE_PADDING <= t.getX() && placeTableHere.x+xTABLE_WIDTH + TABLE_PADDING >= t.getX() &&
					placeTableHere.y - TABLE_PADDING <= t.getY() && placeTableHere.y+yTABLE_WIDTH + TABLE_PADDING >= t.getY())){
					return false;
				}
			}
		}
		return true;
	}

	public void addTable() {
    	tables.add(new Table(xDEFAULT_NEW_TABLE_POSITION, yDEFAULT_NEW_TABLE_POSITION));
    	//((RestaurantAnimationPanel) restaurantAnimationPanel).addNewTable();
    	//restPanel.getHost().addNewTable();
		
	}
	public void addTable(int x, int y) {
		
    	tables.add(new Table( x, y));
    	//((RestaurantAnimationPanel) restaurantAnimationPanel).addNewTable();
    	//restPanel.getHost().addNewTable();
		
	}

	public void setWaiterOnBreak(String name) {
		//restPanel.setWaiterOnBreak(name);
		
	}

	public void setWaiterCantBreak(String name) {
		//restPanel.setWaiterCantBreak(name);
	}

	public void setWaiterBreakable(String name) {
		//restPanel.setWaiterBreakable(name);
	}
	public void setWaiterUnbreakable(String name) {
		//restPanel.setWaiterUnbreakable(name);
	}

	public void newHour() {
		System.out.println("30 secs");
		hour++;
		if(hour == 24){
			for(int i = daysOfWeek.length; i>0;i--){
				if(dayOfWeek.equalsIgnoreCase(daysOfWeek[i-1])){
					if(i!=7){
						dayOfWeek = daysOfWeek[i];
					}else{
						dayOfWeek = daysOfWeek[0];
					}
				}
			}
			hour = 0;
		}	
	for(PersonAgent p:persons){
		p.msgNextHour(hour, dayOfWeek);
	}
	
	}

	public void displayBuildingPanel(InsideBuildingPanel ibp) {
		System.out.println(ibp.getName());
		cardLayout.show(buildingsPanel, ibp.getName());
		
	}
	
}
