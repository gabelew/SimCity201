package EBRestaurant.gui;

import javax.swing.*;

import city.MarketAgent;
import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;
import EBRestaurant.roles.EBCashierRole;
import EBRestaurant.roles.EBCookRole;
import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBHostRole;
import EBRestaurant.roles.EBWaiterRole;

import java.awt.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class EBRestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    /*private EBHostRole host = new EBHostRole();
    private EBHostGui hostGui = new EBHostGui(host);
    private EBCookRole cook= new EBCookRole();
    private EBCookGui cookGui = new EBCookGui(cook);
    private EBCashierRole Cashier=new EBCashierRole();*/
    private final int rows=1;
    private final int cols=2;
    private final int hgap=20;
    private final int vgap=20;
    private final int hgaps=10;
    private final int vgaps=10;
    private int waiterXIndex=50;
    private Vector<EBCustomerRole> customers = new Vector<EBCustomerRole>();
    private Vector<EBWaiterRole> waiters = new Vector<EBWaiterRole>();
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    private String types;
    private JPanel restLabel = new JPanel();
    private EBListPanel customerPanel = new EBListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;

    public EBRestaurantPanel(SimCityGui simCityGui) {
        this.gui = simCityGui;
        /*host.setGui(hostGui);
        gui.animationPanel.addGui(cookGui);
        gui.animationPanel.addGui(hostGui);
        cook.setGui(cookGui);*/
        setLayout(new GridLayout(rows, cols, hgap, vgap));
        group.setLayout(new GridLayout(rows, cols, hgaps, vgaps));
        group.add(customerPanel);
        //initRestLabel();
        add(restLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    /*private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }*/

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
     /*public void showInfo(String type, String name) {
    	types=type;
        if (type.equals("Customers")) {
      
            for (int i = 0; i < customers.size(); i++) {
                EBCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    restGui.updateInfoPanel(temp);
            }
        }
        else if(type.equals("waiters"))
        {
            for (int i = 0; i < waiters.size(); i++) {
                EBWaiterRole temp = waiters.get(i);
                if (temp.getName() == name)
                    restGui.updateInfoPanel(temp);
            }
        }
    }*/

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    
    /*public void addMarket(String name,int steak,int chicken,int salad,int pizza){
    	MarketAgent m = new MarketAgent(null, name, null);
    	markets.add(m);
        cook.addMarket(m);
        m.startThread();
    }*/
    
    public void addPerson(String type, String name, boolean checkHungry,String amount,boolean resp) {

    	/*if (type.equals("Customers")) {
    		EBCustomerRole c = new EBCustomerRole(name);	
    		EBCustomerGui g = new EBCustomerGui(c, gui);
    		
            float money=Float.parseFloat(amount);
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(Cashier);
    		c.setGui(g);
    		c.setAmount(money);
    		c.setResponsible(resp);
    		if (checkHungry)
    		{
    			g.setHungry();
    		}
    		customers.add(c);
    		//c.startThread();
    	}
    	else{
    		EBWaiterRole w=new EBWaiterRole(name);
    		EBWaiterGui wg = new EBWaiterGui(w, gui,waiterXIndex);
    		waiterXIndex=waiterXIndex+30;
    		if (waiterXIndex>400){
    			waiterXIndex=50;
    		}
    		gui.animationPanel.addGui(wg);
    		host.msgReadyToWork(w);
    		//waiters.add(w);
    		w.setCook(cook);
    		w.setHost(host);
    		w.setCashier(Cashier);
    		w.setGui(wg);
    		waiters.add(w);
    		//w.startThread();
    	}*/
    }

    public void pause(boolean isPaused){
    	if (isPaused==true){
    		/*host.pauseIt();
    		cook.pauseIt();
    		Cashier.pauseIt();
    		for (MarketAgent m:markets){
    			m.pauseIt();
    		}
    		for (EBWaiterRole w:waiters){
    			w.pauseIt();
    		}
    		for(EBCustomerRole c:customers){
    			c.pauseIt();
    		}*/
    	}
    	else if (isPaused==false){
    		/*host.resumeIt();
    		cook.resumeIt();
    		Cashier.resumeIt();
    		for (MarketAgent m:markets){
    			m.resumeIt();
    		}
    		for (EBWaiterRole w:waiters){
    			w.resumeIt();
    		}
    		for (EBCustomerRole c:customers){
    			c.resumeIt();
    		}*/
    	}
    }

    
	public void setInsideBuildingPanel(InsideBuildingPanel bp) {
		insideBuildingPanel = bp;
	}

}
