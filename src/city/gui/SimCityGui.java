package city.gui;

import restaurant.Restaurant;
import restaurant.RevolvingStandMonitor;
import restaurant.interfaces.Cook;

import javax.swing.*;

import market.gui.MarketPanel;
import bank.BankBuilding;
import bank.gui.BankPanel;
import CMRestaurant.gui.CMCashierGui;
import CMRestaurant.gui.CMCookGui;
import CMRestaurant.gui.CMHostGui;
import CMRestaurant.gui.CMRestaurantPanel;
import CMRestaurant.roles.CMCashierRole;
import CMRestaurant.roles.CMCookRole;
import CMRestaurant.roles.CMCustomerRole;
import CMRestaurant.roles.CMHostRole;
import CMRestaurant.roles.CMNormalWaiterRole;
import EBRestaurant.gui.EBAnimationPanel;
import EBRestaurant.gui.EBCashierGui;
import EBRestaurant.gui.EBCookGui;
import EBRestaurant.gui.EBHostGui;
import EBRestaurant.gui.EBRestaurantPanel;
import EBRestaurant.roles.EBCashierRole;
import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBHostRole;
import EBRestaurant.roles.EBWaiterRole;
import atHome.city.Apartment;
import atHome.city.Home;
import city.BankAgent;
import city.MarketAgent;
import city.PersonAgent;
import city.animationPanels.ApartmentAnimationPanel;
import city.animationPanels.BankAnimationPanel;
import city.animationPanels.HouseAnimationPanel;
import city.animationPanels.InsideAnimationPanel;
import city.animationPanels.InsideBuildingPanel;
import city.animationPanels.MarketAnimationPanel;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.gui.trace.TraceControlPanel;
import city.gui.trace.TracePanel;
import city.roles.Role;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 
    List<Home> homes = new ArrayList<Home>();
    List<Apartment> apartments = new ArrayList<Apartment>();
    List<Restaurant> restaurants = new ArrayList<Restaurant>();
    List<MarketAgent> markets = new ArrayList<MarketAgent>();
    List<BankBuilding> banks = new ArrayList<BankBuilding>();
    public BankAgent bankAgent = new BankAgent("SimCityBank");
    public List<PersonAgent> persons = Collections.synchronizedList(new ArrayList<PersonAgent>());

    public TracePanel tracePanel = new TracePanel();
    public TraceControlPanel traceControlPanel = new TraceControlPanel(tracePanel);
    private JFrame traceFrame = new JFrame();
    
    private JFrame bottomFrame = new JFrame();
    private JPanel topPanel = new JPanel();
    CardLayout cardLayout = new CardLayout();
    JPanel buildingsPanel = new JPanel();
    
    private int hour;
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thrusday", "Friday", "Saturday", "Sunday"};
    String dayOfWeek = daysOfWeek[0];
    public boolean testing = false;
    
    static final int FRAMEX = 1100;
    static final int FRAMEY = 430;
    static final int INSIDE_BUILDING_FRAME_Y = 517;
    static final int TRACEFRAMEX = 800;
    static final int WINDOWX = 225;
    static final int OFFSETPOS = 50;
    static final int NROWS = 1;
    static final int NCOLUMNS = 0;
    static final int REST_PANEL_Y = 450;
    
    
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public SimCityGui(boolean test) {
    	testing = test;
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
        
        
       /* traceFrame.setLayout(new BorderLayout());
        traceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        traceFrame.setBounds(OFFSETPOS+40, OFFSETPOS+40, TRACEFRAMEX, INSIDE_BUILDING_FRAME_Y);
        Dimension traceFrameDim = new Dimension(TRACEFRAMEX,INSIDE_BUILDING_FRAME_Y);
        traceFrame.setMaximumSize(traceFrameDim);
        traceFrame.setMinimumSize(traceFrameDim);
        traceFrame.setPreferredSize(traceFrameDim);
        traceFrame.add(tracePanel, BorderLayout.CENTER);
        traceFrame.add(traceControlPanel, BorderLayout.WEST);
        traceFrame.setVisible(true);*/
        
        
        Dimension buildingsPanelDim = new Dimension(WINDOWX,REST_PANEL_Y);
        buildingsPanel.setLayout(cardLayout);
        buildingsPanel.setMaximumSize(buildingsPanelDim);
        buildingsPanel.setMinimumSize(buildingsPanelDim);
        buildingsPanel.setPreferredSize(buildingsPanelDim);
    
        
        bottomFrame.setLayout(new BorderLayout());
        bottomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bottomFrame.setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, INSIDE_BUILDING_FRAME_Y);
        Dimension bottomFrameDim = new Dimension(FRAMEX,INSIDE_BUILDING_FRAME_Y);
        bottomFrame.setMaximumSize(bottomFrameDim);
        bottomFrame.setMinimumSize(bottomFrameDim);
        bottomFrame.setPreferredSize(bottomFrameDim);
        bottomFrame.add(buildingsPanel, BorderLayout.CENTER);
        bottomFrame.setVisible(true);    
        


        createDefaultBuildingPanels();
        
        setVisible(true);
        updateBuildingsPanel();
        if(!testing){
        	bankAgent.startThread();
        	createDefaultPeople();
        	createEmployeeList();
        	setLandlordForRenters();
        	
        	infoPanel.pauseAgents();
        }
    }
    
	public SimCityGui() {
    	setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, FRAMEY);
        Dimension frameDim = new Dimension(FRAMEX,FRAMEY);
        this.setMaximumSize(frameDim);
        this.setMinimumSize(frameDim);
        this.setPreferredSize(frameDim);
        
    	setLayout(new BorderLayout());
    	topPanel.setLayout(new BorderLayout(5,0));
        
        Dimension infoDim = new Dimension(WINDOWX, (int) (REST_PANEL_Y));
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);

        topPanel.add(animationPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.CENTER);
        
   
        traceFrame.setLayout(new BorderLayout());
        traceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        traceFrame.setBounds(OFFSETPOS+600, OFFSETPOS+40, TRACEFRAMEX, INSIDE_BUILDING_FRAME_Y);
        Dimension traceFrameDim = new Dimension(TRACEFRAMEX,INSIDE_BUILDING_FRAME_Y);
        traceFrame.setMaximumSize(traceFrameDim);
        traceFrame.setMinimumSize(traceFrameDim);
        traceFrame.setPreferredSize(traceFrameDim);
        traceFrame.add(tracePanel, BorderLayout.CENTER);
        traceFrame.add(traceControlPanel, BorderLayout.WEST);
        traceFrame.setVisible(true);
        
        
        Dimension buildingsPanelDim = new Dimension(WINDOWX,REST_PANEL_Y);
        buildingsPanel.setLayout(cardLayout);
        buildingsPanel.setMaximumSize(buildingsPanelDim);
        buildingsPanel.setMinimumSize(buildingsPanelDim);
        buildingsPanel.setPreferredSize(buildingsPanelDim);
        
        
        bottomFrame.setLayout(new BorderLayout());
        bottomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bottomFrame.setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, INSIDE_BUILDING_FRAME_Y);
        Dimension bottomFrameDim = new Dimension(FRAMEX,INSIDE_BUILDING_FRAME_Y);
        bottomFrame.setMaximumSize(bottomFrameDim);
        bottomFrame.setMinimumSize(bottomFrameDim);
        bottomFrame.setPreferredSize(bottomFrameDim);
        bottomFrame.add(buildingsPanel, BorderLayout.CENTER);
        bottomFrame.setVisible(true);    
        
        createDefaultBuildingPanels();
        
        setVisible(true);
        updateBuildingsPanel();
        bankAgent.startThread();
        createDefaultPeople();
        createEmployeeList();
        setLandlordForRenters();

    	
    	infoPanel.pauseAgents();
       
    }

    private void updateBuildingsPanel() {
    	for(int i = 0; i< restaurants.size();i++){
    		infoPanel.addBuilding("Restaurant 0" + (i+1),i);
    	}
    	for(int i = 0; i< markets.size();i++){
    		infoPanel.addBuilding("Market 0" + (i+1),i);
    	}
    	for(int i = 0; i< banks.size();i++){
    		infoPanel.addBuilding("Bank 0" + (i+1),i);
    	}
	}
    
    private void createDefaultPeople() {
    	infoPanel.getPersonPanel().addPerson("landlordcarhome");
    	infoPanel.getPersonPanel().addPerson("poor01");
    	infoPanel.getPersonPanel().addPerson("poor02");
    	infoPanel.getPersonPanel().addPerson("poor03");
    	infoPanel.getPersonPanel().addPerson("poor04");
    	infoPanel.getPersonPanel().addPerson("poor05");
    	infoPanel.getPersonPanel().addPerson("poor06");
    	infoPanel.getPersonPanel().addPerson("poor07");
    	infoPanel.getPersonPanel().addPerson("poor08");
    	infoPanel.getPersonPanel().addPerson("poorhome02");
    	infoPanel.getPersonPanel().addPerson("richhome03");
    	infoPanel.getPersonPanel().addPerson("poorhome04NoFood");
    	infoPanel.getPersonPanel().addPerson("poorhome05LowSteak");
    	infoPanel.getPersonPanel().addPerson("visiterhome06LowSteak");
    	infoPanel.getPersonPanel().addPerson("visiterhomebus07LowSteak");
    	infoPanel.getPersonPanel().addPerson("visiterhomecar08LowSteak");
    	
    	infoPanel.getPersonPanel().addPerson("rmanager01carhome");
    	infoPanel.getPersonPanel().addPerson("rmanager02carhome");
    	infoPanel.getPersonPanel().addPerson("rmanager03carhome");
    	infoPanel.getPersonPanel().addPerson("rmanager04carhome");
    	infoPanel.getPersonPanel().addPerson("rmanager05carhome");
    	
    	infoPanel.getPersonPanel().addPerson("waiter01day");
    	infoPanel.getPersonPanel().addPerson("waiter01daycar");
    	infoPanel.getPersonPanel().addPerson("waiter01nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter01nightpoor");
    	infoPanel.getPersonPanel().addPerson("waiter02day");
    	infoPanel.getPersonPanel().addPerson("waiter02daycar");
    	infoPanel.getPersonPanel().addPerson("waiter02nightpoor");
    	infoPanel.getPersonPanel().addPerson("waiter02nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter03day");
    	infoPanel.getPersonPanel().addPerson("waiter03daycar");
    	infoPanel.getPersonPanel().addPerson("waiter03nightpoor");
    	infoPanel.getPersonPanel().addPerson("waiter03nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter04day");
    	infoPanel.getPersonPanel().addPerson("waiter04daycar");
    	infoPanel.getPersonPanel().addPerson("waiter04nightpoor");
    	infoPanel.getPersonPanel().addPerson("waiter04nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter05day");
    	infoPanel.getPersonPanel().addPerson("waiter05daycar");
    	infoPanel.getPersonPanel().addPerson("waiter05night");
    	infoPanel.getPersonPanel().addPerson("waiter05nightcar");
    	
    	infoPanel.getPersonPanel().addPerson("host01day");
    	infoPanel.getPersonPanel().addPerson("host01nightcar");
    	infoPanel.getPersonPanel().addPerson("host02daypoor");
    	infoPanel.getPersonPanel().addPerson("host02nightcar");
    	infoPanel.getPersonPanel().addPerson("host03daycar");
    	infoPanel.getPersonPanel().addPerson("host03night");
    	infoPanel.getPersonPanel().addPerson("host04daypoor");
    	infoPanel.getPersonPanel().addPerson("host04nightcarpoor");
    	infoPanel.getPersonPanel().addPerson("host05daycar");
    	infoPanel.getPersonPanel().addPerson("host05night");
    	
    	infoPanel.getPersonPanel().addPerson("cook01daypoor");
    	infoPanel.getPersonPanel().addPerson("cook01nightcar");
    	infoPanel.getPersonPanel().addPerson("cook02daycar");
    	infoPanel.getPersonPanel().addPerson("cook02nightpoor");
    	infoPanel.getPersonPanel().addPerson("cook03day");
    	infoPanel.getPersonPanel().addPerson("cook03nightcarpoor");
    	infoPanel.getPersonPanel().addPerson("cook04daycar");
    	infoPanel.getPersonPanel().addPerson("cook04night");
    	infoPanel.getPersonPanel().addPerson("cook05day");
    	infoPanel.getPersonPanel().addPerson("cook05nightcarpoor");
    	
    	infoPanel.getPersonPanel().addPerson("cashier01day");
    	infoPanel.getPersonPanel().addPerson("cashier01nightcar");
    	infoPanel.getPersonPanel().addPerson("cashier02daycar");
    	infoPanel.getPersonPanel().addPerson("cashier02night");
    	infoPanel.getPersonPanel().addPerson("cashier03day");
    	infoPanel.getPersonPanel().addPerson("cashier03nightcar");
    	infoPanel.getPersonPanel().addPerson("cashier04daycar");
    	infoPanel.getPersonPanel().addPerson("cashier04night");
    	infoPanel.getPersonPanel().addPerson("cashier05daycar");
    	infoPanel.getPersonPanel().addPerson("cashier05nightcar");
    	
    	infoPanel.getPersonPanel().addPerson("clerk01daycar");
    	infoPanel.getPersonPanel().addPerson("clerk02daycar");
    	infoPanel.getPersonPanel().addPerson("clerk03daycar");
    	infoPanel.getPersonPanel().addPerson("clerk04daycar");
    	infoPanel.getPersonPanel().addPerson("clerk05daycar");
    	infoPanel.getPersonPanel().addPerson("clerk06daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan01daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan02daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan03daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan04daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan05daycar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan06daycar");
    	
    	infoPanel.getPersonPanel().addPerson("clerk01nightcar");
    	infoPanel.getPersonPanel().addPerson("clerk02nightcar");
    	infoPanel.getPersonPanel().addPerson("clerk03nightcar");
    	infoPanel.getPersonPanel().addPerson("clerk04nightcar");
    	infoPanel.getPersonPanel().addPerson("clerk05nightcar");
    	infoPanel.getPersonPanel().addPerson("clerk06nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan01nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan02nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan03nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan04nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan05nightcar");
    	infoPanel.getPersonPanel().addPerson("deliveryMan06nightcar");
    	

    }
    
    private void setLandlordForRenters() {
    	PersonAgent landlord = null;
    	for(PersonAgent p: persons) {
    		if(p.job!= null && p.job.type.equals("landlord")) {
    			landlord = p;
    		}
    	}
    	for(PersonAgent r: persons) {
    		if(r.isRenter) {
    			r.landlord = landlord;
    		}
    	}
    }
    
    private void createEmployeeList() {
    	for(PersonAgent p: persons) {
    		if(p.getName().toLowerCase().contains("rmanager01") && p.isManager) {
    			for(PersonAgent e: persons) {
    				if((e.getName().toLowerCase().contains("cook01") || e.getName().toLowerCase().contains("waiter01")
    				|| e.getName().toLowerCase().contains("cashier01") || e.getName().toLowerCase().contains("host01"))
    				&& e.job != null) {
    					p.employees.add(e);
    				}
    			}
    		} else if(p.getName().toLowerCase().contains("rmanager02") && p.isManager) {
    			for(PersonAgent e: persons) {
    				if((e.getName().toLowerCase().contains("cook02") || e.getName().toLowerCase().contains("waiter02")
    				|| e.getName().toLowerCase().contains("cashier02") || e.getName().toLowerCase().contains("host02"))
    				&& e.job != null) {
    					p.employees.add(e);
    				}
    			}
    			
   			} else if(p.getName().toLowerCase().contains("rmanager03") && p.isManager) {
   				for(PersonAgent e: persons) {
    				if((e.getName().toLowerCase().contains("cook03") || e.getName().toLowerCase().contains("waiter03")
    				|| e.getName().toLowerCase().contains("cashier03") || e.getName().toLowerCase().contains("host03"))
    				&& e.job != null) {
    					p.employees.add(e);
    				}
    			}
   				
   			} else if(p.getName().toLowerCase().contains("rmanager04") && p.isManager) {
   				for(PersonAgent e: persons) {
    				if((e.getName().toLowerCase().contains("cook04") || e.getName().toLowerCase().contains("waiter04")
    				|| e.getName().toLowerCase().contains("cashier04") || e.getName().toLowerCase().contains("host04"))
    				&& e.job != null) {
    					p.employees.add(e);
    				}
    			}
   			} else if (p.getName().toLowerCase().contains("rmanager05") && p.isManager) {
   				for(PersonAgent e: persons) {
    				if((e.getName().toLowerCase().contains("cook05") || e.getName().toLowerCase().contains("waiter05")
    				|| e.getName().toLowerCase().contains("cashier05") || e.getName().toLowerCase().contains("host05"))
    				&& e.job != null) {
    					p.employees.add(e);
    				}
    			}
    				    			
    		}
    	}
    }

	private void createDefaultBuildingPanels() {
    	List<BuildingIcon> buildings = animationPanel.buildings;
    	System.out.println(buildings.size());
        for(int i =0; i<buildings.size(); i++){
        	
        	BuildingIcon b = buildings.get(i);
        	if(b.type.equals("restaurant") && i==10){
        		EBRestaurantPanel restPanel = new EBRestaurantPanel(this);
	            InsideAnimationPanel restaurantAnimationPanel = new EBAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            restPanel.setPreferredSize(restDim);
	            restPanel.setMinimumSize(restDim);
	            restPanel.setMaximumSize(restDim);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,restaurantAnimationPanel, restPanel);
	        	restPanel.setInsideBuildingPanel(bp);
	        	b.setInsideBuildingPanel(bp);
	        	restaurantAnimationPanel.setInsideBuildingPanel(bp);
	        	buildingsPanel.add(bp, "" + i);
	        	Restaurant r =null;
	        	
	        	r = new Restaurant(
	        						(restaurant.interfaces.Host)(new EBHostRole()), 
	        						(restaurant.interfaces.Cashier)(new EBCashierRole()), 
	        						(restaurant.interfaces.Cook)(new EBCookRole(1)), 
	        						new restaurant.interfaces.Waiter.Menu(), 
	        						"RestaurantEBCustomerRole", 
	        						"RestaurantEB", 
	        						restaurantAnimationPanel, 
	        						new Point(b.getX(),b.getY()), 
	        						"RestaurantEBWaiterRole");
	     
	        	getRestaurants().add(r);
	
	        	((EBHostRole)r.host).setRestaurant(r);
	        	EBHostGui hg = new EBHostGui(((EBHostRole)r.host));
	        	((EBHostRole)r.host).setGui(hg);
	        	restaurantAnimationPanel.addGui(hg);
	
	
	        	((EBCashierRole)r.cashier).setRestaurant(r);
	        	EBCashierGui cg = new EBCashierGui(((EBCashierRole)r.cashier));
	        	((EBCashierRole)r.cashier).setGui(cg);
	        	restaurantAnimationPanel.addGui(cg);
	        	
	
	        	((EBCookRole)r.cook).setRestaurant(r);
	        	EBCookGui ccg = new EBCookGui(((EBCookRole)r.cook));
	        	((EBCookRole)r.cook).setGui(ccg);
	        	RevolvingStandMonitor revolvingStand = new RevolvingStandMonitor();
	        	//((EBCookRole)r.cook).setRevolvingStand(revolvingStand);
	        	restaurantAnimationPanel.addGui(ccg);
	        	
        	}else if(b.type.equals("restaurant")){
	            
	        	CMRestaurantPanel restPanel = new CMRestaurantPanel(this);
	            InsideAnimationPanel restaurantAnimationPanel = new CMRestaurantAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            restPanel.setPreferredSize(restDim);
	            restPanel.setMinimumSize(restDim);
	            restPanel.setMaximumSize(restDim);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,restaurantAnimationPanel, restPanel);
	        	restPanel.setInsideBuildingPanel(bp);
	        	b.setInsideBuildingPanel(bp);
	        	restaurantAnimationPanel.setInsideBuildingPanel(bp);
	        	buildingsPanel.add(bp, "" + i);
	        	Restaurant r =null;
	        	
	        	r = new Restaurant(
	        						(restaurant.interfaces.Host)(new CMHostRole()), 
	        						(restaurant.interfaces.Cashier)(new CMCashierRole()), 
	        						(restaurant.interfaces.Cook)(new CMCookRole(1)), 
	        						new restaurant.interfaces.Waiter.Menu(), 
	        						"RestaurantCMCustomerRole", 
	        						"RestaurantCM", 
	        						restaurantAnimationPanel, 
	        						new Point(b.getX(),b.getY()), 
	        						"RestaurantCMWaiterRole");
	     
	        	getRestaurants().add(r);
	        	((CMRestaurantAnimationPanel) restaurantAnimationPanel).addDefaultTables();
	
	        	((CMHostRole)r.host).setRestaurant(r);
	        	CMHostGui hg = new CMHostGui(((CMHostRole)r.host));
	        	((CMHostRole)r.host).setGui(hg);
	        	restaurantAnimationPanel.addGui(hg);
	
	
	        	((CMCashierRole)r.cashier).setRestaurant(r);
	        	CMCashierGui cg = new CMCashierGui(((CMCashierRole)r.cashier));
	        	((CMCashierRole)r.cashier).setGui(cg);
	        	restaurantAnimationPanel.addGui(cg);
	        	
	
	        	((CMCookRole)r.cook).setRestaurant(r);
	        	CMCookGui ccg = new CMCookGui(((CMCookRole)r.cook));
	        	((CMCookRole)r.cook).setGui(ccg);
	        	RevolvingStandMonitor revolvingStand = new RevolvingStandMonitor();
	        	((CMCookRole)r.cook).setRevolvingStand(revolvingStand);
	        	restaurantAnimationPanel.addGui(ccg);

        	}
        	else if(b.type.equals("market"))
        	{
                
            	//RestaurantPanel restPanel = new RestaurantPanel(this);
        		MarketPanel marketPanel = new MarketPanel(this);
                InsideAnimationPanel marketAnimationPanel = new MarketAnimationPanel(this);
            	
                Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
                marketPanel.setPreferredSize(restDim);
                marketPanel.setMinimumSize(restDim);
                marketPanel.setMaximumSize(restDim);

            	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,marketAnimationPanel, marketPanel);
            	marketPanel.setInsideBuildingPanel(bp);
            	b.setInsideBuildingPanel(bp);
            	marketAnimationPanel.setInsideBuildingPanel(bp);
            	buildingsPanel.add(bp, "" + i);
            	
            	String name;
            	if(i==8){
            		name = "Vons";
            	}else if(i==25){
            		name = "Costco";
            	}else if(i==40){
            		name = "Ralphs";
            	}else if(i==42){
            		name = "Target";
            	}else if(i==44){
            		name = "Kmart";
            	}else{
            		name = "ChadMart";
            	}
	        	
            	MarketAgent marketAgent = new MarketAgent(
            			new Point(b.getX(),b.getY()),
            			name, 
            			marketAnimationPanel);
            	marketPanel.setMarket(marketAgent);
            	marketAgent.setPanel(marketPanel);
            	marketAgent.setInventory(200, 200, 200, 200, 200);
            	marketAgent.startThread();
            	markets.add(marketAgent);
            	
	    	}else if(b.type.equals("bank")){
	            
	        	BankPanel bankPanel = new BankPanel();
	            InsideAnimationPanel bankAnimationPanel = new BankAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            bankPanel.setPreferredSize(restDim);
	            bankPanel.setMinimumSize(restDim);
	            bankPanel.setMaximumSize(restDim);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,bankAnimationPanel, bankPanel);
	        	bankAnimationPanel.setInsideBuildingPanel(bp);
	        	b.setInsideBuildingPanel(bp);
	        	buildingsPanel.add(bp, "" + i);

				banks.add(new BankBuilding(bankAnimationPanel, new Point(b.getX(),b.getY())));
			}else if(b.type.equals("house")){
		        
		    	JPanel APanel = new JPanel();
		    	BankPanel bankPanel = new BankPanel();
		        InsideAnimationPanel houseAnimationPanel = new HouseAnimationPanel(this);
		    	
		        Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
		        bankPanel.setPreferredSize(restDim);
		        bankPanel.setMinimumSize(restDim);
		        bankPanel.setMaximumSize(restDim);
		        APanel.add(bankPanel);
		        
		    	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,houseAnimationPanel, bankPanel);
            	houseAnimationPanel.setInsideBuildingPanel(bp);
		    	b.setInsideBuildingPanel(bp);
		    	houseAnimationPanel.setInsideBuildingPanel(bp);
		    	buildingsPanel.add(bp, "" + i);
		    	//adds new home to list
		    	homes.add(new Home( houseAnimationPanel, new Point(b.getX(),b.getY())));
			}else if(b.type.equals("apartment")){
			    
				BankPanel aptPanel = new BankPanel();
			    InsideAnimationPanel apartmentAnimationPanel = new ApartmentAnimationPanel(this);
				
			    Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
			    aptPanel.setPreferredSize(restDim);
			    aptPanel.setMinimumSize(restDim);
			    aptPanel.setMaximumSize(restDim);
			
				InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,apartmentAnimationPanel, aptPanel);
            	apartmentAnimationPanel.setInsideBuildingPanel(bp);
				b.setInsideBuildingPanel(bp);
				apartmentAnimationPanel.setInsideBuildingPanel(bp);
				buildingsPanel.add(bp, "" + i);
				apartments.add(new Apartment(apartmentAnimationPanel, new Point(b.getX(),b.getY())));
	        }
		}
        
        //adds markets in order of closest to restaurant
        for(int i = 0; i < restaurants.size(); i++){
        	if(i<2){
        		for(int j= 0; j<markets.size(); j++){
        			((Cook)restaurants.get(i).cook).addMarket(markets.get(j));
        		}
        	}else if(i<4){
    			((Cook)restaurants.get(i).cook).addMarket(markets.get(1));
    			((Cook)restaurants.get(i).cook).addMarket(markets.get(0));
        		for(int j= 2; j<markets.size(); j++){
        			((Cook)restaurants.get(i).cook).addMarket(markets.get(j));
        		}
        	}else{
        		for(int j= markets.size()-1; j>=0; j--){
        			((Cook)restaurants.get(i).cook).addMarket(markets.get(j));
        		}
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
    /*public void setCustomerEnabled(CustomerAgent c) {
    	
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
            
        }
    }*/
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        SimCityGui gui = new SimCityGui();
        gui.setTitle("csci201 SimCity");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		System.out.println("" + hour + "\t"+ dayOfWeek.toString());
		synchronized(persons){
			for(PersonAgent p:persons){
				p.msgNextHour(hour, dayOfWeek);
			}
		}
	
	}

	public void displayBuildingPanel(InsideBuildingPanel ibp) {
		System.out.println(ibp.getName());
		for(int i = 0; i < animationPanel.buildings.size(); i++){
			animationPanel.buildings.get(i).getInsideBuildingPanel().isVisible = false;
		}
		ibp.isVisible = true;
		cardLayout.show(buildingsPanel, ibp.getName());
		
	}
	public static Role customerFactory(PersonAgent p, Restaurant r){
		if(r.customerRole.equalsIgnoreCase("RestaurantCMCustomerRole")){
			return new CMCustomerRole(p, r);
		}else if(r.customerRole.equalsIgnoreCase("RestaurantEBCustomerRole")){
			return new EBCustomerRole(p,r);
		}
		
		return null;
	}
	public static Role waiterFactory(PersonAgent p, Restaurant r){
		if(r.waiterRole.equalsIgnoreCase("RestaurantCMWaiterRole")){
			return new CMNormalWaiterRole(p, r);
		}else if(r.customerRole.equalsIgnoreCase("RestaurantEBWaiterRole")){
			return new EBWaiterRole(p,r);
		}
		
		return null;
	}

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}
	public List<MarketAgent> getMarkets() {
		return markets;
	}
	public List<BankBuilding> getBanks() {
		return banks;
	}
	
	public List<Home> getHomes() {
		return homes;
	}
	
	public boolean apartmentsAvaiable() {
		for(Apartment a: apartments){
			if(a.noVacancies == false){
				return true;
			}
		}
		return false;
	}

	public void setOpen(String name, int buildingNumber) {
		if(name.toLowerCase().contains("restaurant")){
			Restaurant r = restaurants.get(buildingNumber);
		}else if(name.toLowerCase().contains("market")){
			MarketAgent m = markets.get(buildingNumber);
		}else if(name.toLowerCase().contains("bank")){
			BankBuilding b = banks.get(buildingNumber);
			b.setIsOpen(true);
		}
	}

	public void setClosed(String name, int buildingNumber) {
		if(name.toLowerCase().contains("restaurant")){
			Restaurant r = restaurants.get(buildingNumber);
		}else if(name.toLowerCase().contains("market")){
			MarketAgent m = markets.get(buildingNumber);
		}else if(name.toLowerCase().contains("bank")){
			BankBuilding b = banks.get(buildingNumber);
			b.setIsOpen(false);
		}
		
	}

}
