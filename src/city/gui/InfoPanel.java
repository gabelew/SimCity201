package city.gui;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.MarketAgent;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui;

import javax.swing.*;

import city.PersonAgent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class InfoPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	private static final int REST_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 1;
	private static final int NCOLUMNS = 1;
	private static final int NROWS = 2;
	private static final double PERSONS_DEFAULT_CASH = 200.00;
	private static final double NO_CASH = 0.0;
	
    //Host, cook, waiters and customers
  /*  private HostAgent host = new HostAgent("Sarah");
    private HostGui hostGui = new HostGui(host);
    private CookAgent cook = new CookAgent("David");
    private CookGui cookGui = new CookGui(cook);
    private CashierAgent cashier = new CashierAgent("Gabe");
    private CashierGui cashierGui = new CashierGui(cashier);
*/
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    private Vector<PersonAgent> persons = new Vector<PersonAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Persons");

    //private ListPanel waitersPanel = new ListPanel(this, "Waiters");
    //private ListPanel tablesPanel = new ListPanel(this, "Tables");
    
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui

    public InfoPanel(SimCityGui gui) {

        //markets.add(new MarketAgent("Vons Market"));
        //markets.add(new MarketAgent("Sprouts Market"));
        //markets.add(new MarketAgent("CostCo"));
        
        this.gui = gui;
        /*host.setGui(hostGui, gui);
        cashier.setGui(cashierGui);
        cook.setCashier(cashier);
        cook.setGui(cookGui);
        for(MarketAgent m:markets){
        	cook.addMarket(m);
        	m.startThread();
        }*/
        
       // gui.animationPanel.addGui(hostGui);
       // gui.animationPanel.addGui(cashierGui);
      //  gui.animationPanel.addGui(cookGui);
        /*host.startThread();
        cashier.startThread();
        cook.startThread();*/

        setLayout(new GridLayout(NROWS, NCOLUMNS, REST_PANEL_GAP, REST_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));

        add(customerPanel);

        //initRestLabel();
        //add(restLabel);
        //add(group);
      //  add(waitersPanel);
        //add(tablesPanel);
        

        customerPanel.getTypeNameHere().addKeyListener(this);
    //    waitersPanel.getTypeNameHere().addKeyListener(this);
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

        if (type.equals("Persons")) {

            for (PersonAgent temp: persons){
                if (temp.getName() == name){}
                	//temp.getGui().setHungry();
            }
        }
    }
    
/*    public void setWorking(String type, String name) {

        if (type.equals("Waiters")) {

            for (WaiterAgent temp: waiters) {
                if (temp.getName() == name)
                {
                	temp.getGui().setWorking();
                }
            }
        }
    }

	public void askBreak(String name) {
        for (WaiterAgent temp: waiters) {
            if (temp.getName() == name)
            	temp.getGui().askBreak();
        }
	}*/
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
       public void addPerson(String type, String name) {

    	if (type.equals("Persons")) {
    		PersonAgent p = null;
    		if(stringIsDouble(name)){
    			p = new PersonAgent(name, Double.valueOf(name));
    		}else if(name.equalsIgnoreCase("Rami") || name.equalsIgnoreCase("Mahdi") 
    				|| name.equalsIgnoreCase("ditch") || name.equalsIgnoreCase("broke")){
    			p = new PersonAgent(name, NO_CASH);   			
    		}else{
    			p = new PersonAgent(name, PERSONS_DEFAULT_CASH);
    		}
    		PersonGui g = new PersonGui(p, gui);
    		g.setPresent(true);
    		gui.animationPanel.addGui(g);// dw
    		p.setHost(gui.restPanel.host);
    		p.setCashier(gui.restPanel.cashier);
    		p.addRestaurant(gui.restPanel.host, gui.restPanel.cashier,new Point(200,100), "hi", "01");
    		//HostAgent h, Point location, String type, String name
    		p.setGui(g);
    		
    		//if(isHungry)
    			//p.getGui().setHungry();
    		
    		persons.add(p);
    		p.startThread();
    		gui.persons.add(p);
    	}
    	
    	/*if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui g = new WaiterGui(w, gui);

    		gui.animationPanel.addGui(g);// dw
    		w.setHost(host);
    		w.setCashier(cashier);
    		w.setGui(g);
    		w.setCook(cook);
    	
    		if(isHungry)
    			w.getGui().setWorking();
    		
    		waiters.add(w);
    		w.startThread();
    	}*/

    }
    public void setCustomerEnabled(CustomerAgent c){
    	customerPanel.setCustomerEnabled(c.getName());
    }
/*
	public void setTableEnabled(int tableNumber){
		tablesPanel.setTableEnabled(tableNumber);
	}

	public void setTableDisabled(int tableNumber){
		tablesPanel.setTableDisabled(tableNumber);
	}*/
	public void addTable() {
		gui.addTable();
	}
	
	public void addTable(int x,int y) {
		gui.addTable(x,y);
	}
	
    public void pauseAgents()
    {
    	/*if(tablesPanel.getPauseButtonLabel() == "Pause")
    	{
    	 for (CustomerAgent temp: customers)
    	 {
             temp.pauseAgent();
    	 }
    	 for (WaiterAgent temp: waiters) 
    	 {
             temp.pauseAgent();
    	 }
    	 for (MarketAgent temp: markets) 
    	 {
             temp.pauseAgent();
    	 }
    	 //host.pauseAgent();
    	 //cook.pauseAgent();
    	 //cashier.pauseAgent();
    	}
    	else
    	{
       	 for(CustomerAgent temp: customers)
       	 {
                temp.resumeAgent();
       	 }
       	 for (WaiterAgent temp: waiters)
       	 {
                temp.resumeAgent();
       	 }
       	 for (MarketAgent temp: markets)
       	 {
                temp.resumeAgent();
       	 }
       //	 host.resumeAgent();
       	// cook.resumeAgent();
    	 //cashier.resumeAgent();
    	}
    	 tablesPanel.changePauseButton();     
    */}
/*
	public HostAgent getHost() {
		return host;
	}*/

/*	public ListPanel getTablesPanel() {
		return tablesPanel;
	}

	public void setTablesPanel(ListPanel tablesPanel) {
		this.tablesPanel = tablesPanel;
	}*/

	public void setWaiterOnBreak(String name) {
		//waitersPanel.setWaiterOnBreak(name);
	}

	public void setWaiterCantBreak(String name) {
		//waitersPanel.setWaiterCantBreak(name);
		
	}

	public void setWaiterBreakable(String name) {
		//waitersPanel.setWaiterBreakable(name);
		
	}
	public void setWaiterUnbreakable(String name) {
		//waitersPanel.setWaiterUnbreakable(name);
		
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
		/*if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            cook.badSteaks();
        }
		
		if ((e.getKeyCode() == KeyEvent.VK_2) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            cook.cookieMonster();
        }

		if ((e.getKeyCode() == KeyEvent.VK_D) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            markets.get(0).msgTossEverythingButCookies();
        }
		
		if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			cook.setSteaksAmount(5);
        }

		if ((e.getKeyCode() == KeyEvent.VK_W) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			
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
        }

		if ((e.getKeyCode() == KeyEvent.VK_E) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				System.out.println("Cook Inventory: ");
				for(CookAgent.Food f: cook.foods){
					System.out.print("\t" + f.getChoice() + "\t" + f.getAmount() + "\t");
				}
				System.out.println(" ");
        }
	*/	
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
	@Override
	public void keyTyped(KeyEvent e) {		
	}

}

