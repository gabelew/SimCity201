package CMRestaurant.gui;

import restaurant.Restaurant;

import javax.swing.*;

import CMRestaurant.roles.CMCookRole;
import CMRestaurant.roles.CMCustomerRole;
import CMRestaurant.roles.CMHostRole;
import CMRestaurant.roles.CMWaiterRole;
import CMRestaurant.roles.CMCookRole.Food;
import city.animationPanels.InsideBuildingPanel;
import city.animationPanels.CMRestaurantAnimationPanel;
import city.gui.SimCityGui;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.Role;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class CMRestaurantPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 2;
	private static final int NROWS = 2;
	private static final double CUSTOMER_DEFAULT_CASH = 200.00;
	private static final double NO_CASH = 0.0;
	
    //private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    //private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    //private Vector<CMWaiterRole> waiters = new Vector<CMWaiterRole>();

    private JPanel restLabel = new JPanel();
    //private ListPanel customerPanel = new ListPanel(this, "Customers");

    public CMRestaurantListPanel waitersPanel = new CMRestaurantListPanel(this, "Waiters");
    private CMRestaurantListPanel tablesPanel = new CMRestaurantListPanel(this, "Tables");
    
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public CMRestaurantPanel(SimCityGui gui) {

        this.gui = gui;
        
       /* markets.add(new MarketAgent("Vons Market"));
        markets.add(new MarketAgent("Sprouts Market"));
        markets.add(new MarketAgent("CostCo"));
        
        host.setGui(hostGui, gui);
        cashier.setGui(cashierGui);
        cook.setCashier(cashier);
        cook.setGui(cookGui);
        for(MarketAgent m:markets){
        	cook.addMarket(m);
        	m.startThread();
        }
        
        gui.restaurantAnimationPanel.addGui(hostGui);
        gui.restaurantAnimationPanel.addGui(cashierGui);
        gui.restaurantAnimationPanel.addGui(cookGui);
        host.startThread();
        cashier.startThread();
        cook.startThread();*/

        setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));

      //  group.add(customerPanel);

        //initRestLabel();
        //add(restLabel);
        //add(group);
        add(waitersPanel);
        add(tablesPanel);
        createWaiter("w1");
        createWaiter("w2");

        //customerPanel.getTypeNameHere().addKeyListener(this);
        waitersPanel.getTypeNameHere().addKeyListener(this);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
        			//"<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u>Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Burger</td><td>$8.99</td></tr><tr><td>Cookie</td><td>$3.99</td></tr></table><br></html>");
        			"<html><h3><u>Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Burger</td><td>$8.99</td></tr><tr><td>Cookie</td><td>$3.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
   /* public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = ZERO; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
        			//temp.getGui().setHungry();
                    //gui.updateInfoPanel(temp);
            }
        }
    }*/
    
    public void setHungry(String type, String name) {

      /*  if (type.equals("Customers")) {

            for (CustomerAgent temp: customers){
                if (temp.getName() == name)
                	temp.getGui().setHungry();
            }
        }*/
    }
    
    public void setWorking(String type, String name) {

        if (type.equals("Waiters")) {

    		for(Restaurant r: gui.getRestaurants()){
    	        for (CMHostRole.MyWaiter temp: ((CMHostRole)r.host).waiters) {
    	            if (((CMWaiterRole) temp.w).getName() == name){
    	            	((CMWaiterRole) temp.w).getGui().setWorking();
    	            }
    	        }
    		}
        }
    }

	public void askBreak(String name) {
		AlertLog.getInstance().logDebug(AlertTag.REST_WAITER, "CMRestaurantPanel", "Recieved asked for break from GUI");
		for(Restaurant r: gui.getRestaurants()){
			if(((CMHostRole)r.host).waiters != null){
	        for (CMHostRole.MyWaiter temp: ((CMHostRole)r.host).waiters) {
	            if (((CMWaiterRole) temp.w).getName() == name){
	            	((CMWaiterRole) temp.w).getGui().askBreak();
	            }
	        }
			}
		}
	}

	public void createCustomer(String name){
	/*	CustomerAgent c = null;
		if(stringIsDouble(name) && Double.valueOf(name) >= 0){
			c = new CustomerAgent(name, Double.valueOf(name));
		}else if(name.equalsIgnoreCase("Rami") || name.equalsIgnoreCase("Mahdi") 
				|| name.equalsIgnoreCase("ditch") || name.equalsIgnoreCase("cheap")){
			c = new CustomerAgent(name, NO_CASH);   			
		}else{
			c = new CustomerAgent(name, CUSTOMER_DEFAULT_CASH);
		}	
		CustomerGui g = new CustomerGui(c, gui);

		gui.animationPanel.addGui(g);// dw
		c.setHost(host);
		c.setCashier(cashier);
		c.setGui(g);
		customers.add(c);
		c.startThread();
	*/}
/*	public void createPerson(String name){
		PersonAgent c = null;
		if(stringIsDouble(name) && Double.valueOf(name) >= 0){
			c = new PersonAgent(name, Double.valueOf(name));
		}else if(name.equalsIgnoreCase("Rami") || name.equalsIgnoreCase("Mahdi") 
				|| name.equalsIgnoreCase("ditch") || name.equalsIgnoreCase("cheap")){
			c = new PersonAgent(name, NO_CASH);   			
		}else{
			c = new PersonAgent(name, CUSTOMER_DEFAULT_CASH);
		}	
		PersonGui g = new PersonGui(c, gui);

		gui.animationPanel.addGui(g);// dw
		c.setHost(host);
		c.setCashier(cashier);
		c.setGui(g);
		persons.add(c);
		c.startThread();
	}*/
	public void addWaiterToList(String name){
		waitersPanel.addPerson(name);
	}
	public void removeWaiterFromList(String name){
		waitersPanel.removeWaiter(name);
	}
    public void createWaiter(String name){

		/*WaiterAgent w = new WaiterAgent(name);	
		WaiterGui g = new WaiterGui(w, gui);

		gui.restaurantAnimationPanel.addGui(g);// dw
		w.setHost(host);
		w.setCashier(cashier);
		w.setGui(g);
		w.setCook(cook);
	
		w.getGui().setWorking();
		
		waiters.add(w);
		w.startThread();*/
    }
    public void addPerson(String type, String name, Boolean isHungry) {

    	/*if (type.equals("Customers")) {
    		CustomerAgent c = null;
    		if(stringIsDouble(name)){
    			c = new CustomerAgent(name, Double.valueOf(name));
    		}else if(name.equalsIgnoreCase("Rami") || name.equalsIgnoreCase("Mahdi") 
    				|| name.equalsIgnoreCase("ditch") || name.equalsIgnoreCase("broke")){
    			c = new CustomerAgent(name, NO_CASH);   			
    		}else{
    			c = new CustomerAgent(name, CUSTOMER_DEFAULT_CASH);
    		}
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.insideAnimationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		c.setGui(g);
    		
    		if(isHungry)
    			c.getGui().setHungry();
    		
    		customers.add(c);
    		c.startThread();
    	}*/
    	
    	if (type.equals("Waiters")) {
    	/*	WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui g = new WaiterGui(w, gui);

    		gui.restaurantAnimationPanel.addGui(g);// dw
    		w.setHost(host);
    		w.setCashier(cashier);
    		w.setGui(g);
    		w.setCook(cook);
    	
    		if(isHungry)
    			w.getGui().setWorking();
    		
    		waiters.add(w);
    		w.startThread();*/
    	}

    }
    public void setCustomerEnabled(CMCustomerRole c){
    //	customerPanel.setCustomerEnabled(c.getName());
    }

	public void setTableEnabled(int tableNumber){
		tablesPanel.setTableEnabled(tableNumber);
	}

	public void setTableDisabled(int tableNumber){
		tablesPanel.setTableDisabled(tableNumber);
	}
	public void addTable() {
		((CMRestaurantAnimationPanel) insideBuildingPanel.insideAnimationPanel).addTable();
	}
	
	public void addTable(int x,int y) {
		((CMRestaurantAnimationPanel) insideBuildingPanel.insideAnimationPanel).addTable(x,y);
	}
	
	public CMRestaurantListPanel getTablesPanel() {
		return tablesPanel;
	}

	public void setTablesPanel(CMRestaurantListPanel tablesPanel) {
		this.tablesPanel = tablesPanel;
	}

	public void setWaiterOnBreak(String name) {
		waitersPanel.setWaiterOnBreak(name);
	}

	public void setWaiterCantBreak(String name) {
		waitersPanel.setWaiterCantBreak(name);
		
	}

	public void setWaiterBreakable(String name) {
		waitersPanel.setWaiterBreakable(name);
		
	}
	public void setWaiterUnbreakable(String name) {
		waitersPanel.setWaiterUnbreakable(name);
		
	}

	public boolean stringIsDouble(String myString){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
             "[+-]?(" + // Optional sign character
             "NaN|" +           // "NaN" string
             "Infinity|" +      // "Infinity" string

             // A decimal floating-point string representing a finite positive
             // number without a leading sign has at most five basic pieces:
             // Digits . Digits ExponentPart FloatTypeSuffix
             // 
             // Since this method allows integer-only strings as input
             // in addition to strings of floating-point literals, the
             // two sub-patterns below are simplifications of the grammar
             // productions from the Java Language Specification, 2nd 
             // edition, section 3.10.2.

             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

             // . Digits ExponentPart_opt FloatTypeSuffix_opt
             "(\\.("+Digits+")("+Exp+")?)|"+

       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" +
             "[fFdD]?))" +
             "[\\x00-\\x20]*");// Optional trailing "whitespace"
            
        if (Pattern.matches(fpRegex, myString))
	            return true;//Double.valueOf(myString); // Will not throw NumberFormatException
        else {
	        	return false;
        }	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			for(Restaurant r: gui.getRestaurants()){
				if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
		            ((CMCookRole) r.cook).badSteaks();
				}
			}
        }
		
		if ((e.getKeyCode() == KeyEvent.VK_2) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			for(Restaurant r: gui.getRestaurants()){
				if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
					((CMCookRole) r.cook).cookieMonster();
				}
			}
        }

		/*if ((e.getKeyCode() == KeyEvent.VK_D) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            markets.get(0).msgTossEverythingButCookies();
        }*/
		
		if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {

			for(Restaurant r: gui.getRestaurants()){
				if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
					((CMCookRole) r.cook).setSteaksAmount(5);
				}
			}
        }

		/*if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			
			for(MarketAgent m: markets){
				System.out.println("\nMarket Inventory: " + m.getName());
				for(MarketAgent.MyFood f: m.foods){
					String mstate = null;
					for(CookAgent.MyMarket mm: cook.markets){
						if(mm.getMarket() == m)
						{
							mstate = mm.foodInventoryMap.get(f.getChoice()).toString();
						}
					}
					System.out.print("\t" + f.getChoice() + " " + f.getAmount() + " "+ mstate + "\t");
				}
				System.out.println(" ");
			}
        }*/

		if ((e.getKeyCode() == KeyEvent.VK_E) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				System.out.println("Cook Inventory: ");
				for(Restaurant r: gui.getRestaurants()){
					if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
						for(Food f: ((CMCookRole)r.cook).foods){
							System.out.print("\t" + f.getChoice() + "\t" + f.getAmount() + "\t");
						}
					}
				}
				
				System.out.println(" ");
        }
		
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

