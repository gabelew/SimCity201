package restaurant.gui;

import restaurant.*;

import restaurant.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private HostGui hostGui = new HostGui(host);
    private CookAgent cook = new CookAgent("Fred");
    private CookGui cookGui = new CookGui(cook);
    private MarketAgent market = new MarketAgent("Market1",200,360,250,250);
    private MarketAgent market2 = new MarketAgent("Market2",350,150,450,100);
    private MarketAgent market3 = new MarketAgent("market3",450,550,250,50);
    private CashierAgent cashier = new CashierAgent("Brenda");

    private Vector<Customer> customers = new Vector<Customer>();
    private Vector<Waiter> waiters = new Vector<Waiter>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);

        gui.animationPanel.addGui(hostGui);
        gui.animationPanel.addGui(cookGui);
        host.startThread();
        cashier.startThread();
        cook.setMarket(market);
        cook.setMarket(market2);
        cook.setMarket(market3);
        cook.setGui(cookGui);
        market.setCashier(cashier);
        market.startThread();
        market2.startThread();
        market3.startThread();
        cook.startThread();

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 3, 20, 20));

        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel);
        add(group);
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
               "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>Host:</td><td>" + host.getName() + "<tr><td>Cook:</td><td>" + cook.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

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
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                Customer temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        else if (type.equals("Waiters")) {

            for (int i = 0; i < waiters.size(); i++) {
                Waiter temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }     
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean ishungry_break) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    		if(ishungry_break)
    			g.setHungry();
    		
    	}
    	
    	else if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui wg = new WaiterGui(w, gui);
    		gui.animationPanel.addGui(wg);// dw
    		w.setHost(host);
    		w.setCashier(cashier);
    		w.setCook(cook);
    		w.setGui(wg);
    		waiters.add(w);
    		w.startThread();
    		host.msgSetWaiter(w);
    		wg.setWork();
    		if(ishungry_break){
    			wg.GoOnBreak();
    		}
    	}
    }

}
