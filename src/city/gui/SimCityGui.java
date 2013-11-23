package city.gui;

import restaurant.Restaurant;
import restaurant.gui.CashierGui;
import restaurant.gui.CookGui;
import restaurant.gui.HostGui;
import restaurant.gui.RestaurantPanel;

import javax.swing.*;
import atHome.city.Home;
import city.MarketAgent;
import city.PersonAgent;
import city.animationPanels.ApartmentAnimationPanel;
import city.animationPanels.BankAnimationPanel;
import city.animationPanels.HouseAnimationPanel;
import city.animationPanels.InsideAnimationPanel;
import city.animationPanels.InsideBuildingPanel;
import city.animationPanels.MarketAnimationPanel;
import city.animationPanels.RestaurantAnimationPanel;
import city.roles.CashierRole;
import city.roles.CookRole;
import city.roles.CustomerRole;
import city.roles.HostRole;
import city.roles.Role;
import city.roles.WaiterRole;

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
    List<Restaurant> restaurants = new ArrayList<Restaurant>();
    List<MarketAgent> markets = new ArrayList<MarketAgent>();
    public List<PersonAgent> persons = Collections.synchronizedList(new ArrayList<PersonAgent>());
    
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
        
        
        bottomFrame.setLayout(new BorderLayout());
        bottomFrame.add(buildingsPanel, BorderLayout.CENTER);
        bottomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bottomFrame.setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, INSIDE_BUILDING_FRAME_Y);
        bottomFrame.setVisible(true);

        createDefaultBuildingPanels();
        
        setVisible(true);
        createDefaultPeople();
    }

    private void createDefaultPeople() {
    	/*infoPanel.getPersonPanel().addPerson("waiter01day");
    	infoPanel.getPersonPanel().addPerson("waiter01daycar");
    	infoPanel.getPersonPanel().addPerson("waiter01nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter01night");
    	infoPanel.getPersonPanel().addPerson("waiter02day");
    	infoPanel.getPersonPanel().addPerson("waiter02daycar");
    	infoPanel.getPersonPanel().addPerson("waiter02night");
    	infoPanel.getPersonPanel().addPerson("waiter02nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter03day");
    	infoPanel.getPersonPanel().addPerson("waiter03daycar");
    	infoPanel.getPersonPanel().addPerson("waiter03night");
    	infoPanel.getPersonPanel().addPerson("waiter03nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter04day");
    	infoPanel.getPersonPanel().addPerson("waiter04daycar");
    	infoPanel.getPersonPanel().addPerson("waiter04night");
    	infoPanel.getPersonPanel().addPerson("waiter04nightcar");
    	infoPanel.getPersonPanel().addPerson("waiter05day");
    	infoPanel.getPersonPanel().addPerson("waiter05daycar");
    	infoPanel.getPersonPanel().addPerson("waiter05night");
    	infoPanel.getPersonPanel().addPerson("waiter05nightcar");
    	
    	infoPanel.getPersonPanel().addPerson("host01day");
    	infoPanel.getPersonPanel().addPerson("host01nightcar");
    	infoPanel.getPersonPanel().addPerson("host02day");
    	infoPanel.getPersonPanel().addPerson("host02nightcar");
    	infoPanel.getPersonPanel().addPerson("host03daycar");
    	infoPanel.getPersonPanel().addPerson("host03night");
    	infoPanel.getPersonPanel().addPerson("host04day");
    	infoPanel.getPersonPanel().addPerson("host04nightcar");
    	infoPanel.getPersonPanel().addPerson("host05daycar");
    	infoPanel.getPersonPanel().addPerson("host05night");
    	
    	infoPanel.getPersonPanel().addPerson("cook01day");
    	infoPanel.getPersonPanel().addPerson("cook01nightcar");
    	infoPanel.getPersonPanel().addPerson("cook02daycar");
    	infoPanel.getPersonPanel().addPerson("cook02night");
    	infoPanel.getPersonPanel().addPerson("cook03day");
    	infoPanel.getPersonPanel().addPerson("cook03nightcar");
    	infoPanel.getPersonPanel().addPerson("cook04daycar");
    	infoPanel.getPersonPanel().addPerson("cook04night");
    	infoPanel.getPersonPanel().addPerson("cook05day");
    	infoPanel.getPersonPanel().addPerson("cook05nightcar");
    	
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
    	*/
    	/*infoPanel.getPersonPanel().addPerson("clerk01daycar");
    	infoPanel.getPersonPanel().addPerson("clerk02daycar");
    	infoPanel.getPersonPanel().addPerson("clerk03daycar");
    	infoPanel.getPersonPanel().addPerson("clerk04daycar");
    	infoPanel.getPersonPanel().addPerson("clerk05daycar");
    	infoPanel.getPersonPanel().addPerson("clerk06daycar");*/
    	infoPanel.getPersonPanel().addPerson("deliveryMan01daycar");
    	
    	
    }

	private void createDefaultBuildingPanels() {
    	List<BuildingIcon> buildings = animationPanel.buildings;
    	System.out.println(buildings.size());
        for(int i =0; i<buildings.size(); i++){
        	
        	BuildingIcon b = buildings.get(i);
        	
        	if(b.type.equals("restaurant"))
        	{
	            
	        	RestaurantPanel restPanel = new RestaurantPanel(this);
	            InsideAnimationPanel restaurantAnimationPanel = new RestaurantAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            restPanel.setPreferredSize(restDim);
	            restPanel.setMinimumSize(restDim);
	            restPanel.setMaximumSize(restDim);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,restaurantAnimationPanel, restPanel);
	        	restPanel.setInsideBuildingPanel(bp);
	        	b.setInsideBuildingPanel(bp);
	        	restaurantAnimationPanel.setInsideBuildingPanel(bp);
	        	buildingsPanel.add(bp, "" + i);
	        	Restaurant r = new Restaurant(
	        						(restaurant.interfaces.Host)(new HostRole()), 
	        						(restaurant.interfaces.Cashier)(new CashierRole()), 
	        						(restaurant.interfaces.Cook)(new CookRole()), 
	        						new restaurant.interfaces.Waiter.Menu(), 
	        						"Restaurant1CustomerRole", 
	        						"Restaurant1", 
	        						restaurantAnimationPanel, 
	        						new Point(b.getX(),b.getY()), 
	        						"Restaurant1WaiterRole");
	        	getRestaurants().add(r);
	        	((RestaurantAnimationPanel) restaurantAnimationPanel).addDefaultTables();
	
	        	((HostRole)r.host).setRestaurant(r);
	        	HostGui hg = new HostGui(((HostRole)r.host));
	        	((HostRole)r.host).setGui(hg);
	        	restaurantAnimationPanel.addGui(hg);
	
	
	        	((CashierRole)r.cashier).setRestaurant(r);
	        	CashierGui cg = new CashierGui(((CashierRole)r.cashier));
	        	((CashierRole)r.cashier).setGui(cg);
	        	restaurantAnimationPanel.addGui(cg);
	        	
	
	        	((CookRole)r.cook).setRestaurant(r);
	        	CookGui ccg = new CookGui(((CookRole)r.cook));
	        	((CookRole)r.cook).setGui(ccg);
	        	restaurantAnimationPanel.addGui(ccg);

        	}
        	else if(b.type.equals("market"))
        	{
                
            	RestaurantPanel restPanel = new RestaurantPanel(this);
                InsideAnimationPanel marketAnimationPanel = new MarketAnimationPanel(this);
            	
                Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
                restPanel.setPreferredSize(restDim);
                restPanel.setMinimumSize(restDim);
                restPanel.setMaximumSize(restDim);

            	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,marketAnimationPanel, restPanel);
            	restPanel.setInsideBuildingPanel(bp);
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
            	marketAgent.setInventory(200, 200, 200, 200, 200);
            	markets.add(marketAgent);
            	
	    	}else if(b.type.equals("bank")){
	            
	        	RestaurantPanel restPanel = new RestaurantPanel(this);
	            InsideAnimationPanel bankAnimationPanel = new BankAnimationPanel(this);
	        	
	            Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
	            restPanel.setPreferredSize(restDim);
	            restPanel.setMinimumSize(restDim);
	            restPanel.setMaximumSize(restDim);
	
	        	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,bankAnimationPanel, restPanel);
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
		        
		    	InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,houseAnimationPanel, restPanel);
            	restPanel.setInsideBuildingPanel(bp);
		    	b.setInsideBuildingPanel(bp);
		    	houseAnimationPanel.setInsideBuildingPanel(bp);
		    	buildingsPanel.add(bp, "" + i);
		    	//adds new home to list
		    	homes.add(new Home( houseAnimationPanel, new Point(b.getX(),b.getY())));
			}else if(b.type.equals("apartment")){
			    
				RestaurantPanel restPanel = new RestaurantPanel(this);
			    InsideAnimationPanel apartmentAnimationPanel = new ApartmentAnimationPanel(this);
				
			    Dimension restDim = new Dimension(WINDOWX,REST_PANEL_Y);
			    restPanel.setPreferredSize(restDim);
			    restPanel.setMinimumSize(restDim);
			    restPanel.setMaximumSize(restDim);
			
				InsideBuildingPanel bp = new InsideBuildingPanel(b, i, this,apartmentAnimationPanel, restPanel);
            	restPanel.setInsideBuildingPanel(bp);
				b.setInsideBuildingPanel(bp);
				apartmentAnimationPanel.setInsideBuildingPanel(bp);
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
		if(r.customerRole.equalsIgnoreCase("Restaurant1CustomerRole")){
			return new CustomerRole(p, r);
		}
		
		return null;
	}
	public static Role waiterFactory(PersonAgent p, Restaurant r){
		if(r.waiterRole.equalsIgnoreCase("Restaurant1WaiterRole")){
			return new WaiterRole(p, r);
		}
		
		return null;
	}

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}
	public List<MarketAgent> getMarkets() {
		return markets;
	}
	
	public List<Home> getHomes() {
		return homes;
	}

}
