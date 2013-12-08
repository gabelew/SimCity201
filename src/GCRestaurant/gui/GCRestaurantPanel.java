package GCRestaurant.gui;

import javax.swing.*;

import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;
import agent.Agent;

import java.awt.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class GCRestaurantPanel extends JPanel {
	
	private final int ROWS = 1;
	private final int COLUMNS = 1;
	private final int GAP = 10;

    //Host, cook, waiters and customers
    //private HostAgent host = new HostAgent("Sarah");
    //private CookAgent cook = new CookAgent("Joe the Cook");
    //private CashierAgent cashier = new CashierAgent("Daniel");

    //private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    //private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<Agent> allAgents = new Vector<Agent>();

    private JPanel restLabel = new JPanel();
    private JPanel group = new JPanel();
    //JButton pause = new JButton("PAUSE");

    //private RestaurantGui gui; //reference to main gui
    //AnimationPanel animationPanel = new AnimationPanel();
    
    private SimCityGui gui; //reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    
    public GCRestaurantPanel(SimCityGui gui) 
    {
    	//adds host and cook to array of agents
    	//allAgents.add(host);
    	//allAgents.add(cook);
    	//allAgents.add(cashier);
        this.gui = gui;
        
        /*
        //adds markets
       //MarketAgent m = new MarketAgent("Restaurant Depot");
        MarketAgent m1 = new MarketAgent("Sams Club");
        MarketAgent m2 = new MarketAgent("Costco");
        m.setCashier(cashier);
        m1.setCashier(cashier);
        m2.setCashier(cashier);
        allAgents.add(m);
        allAgents.add(m1);
        allAgents.add(m2);
        cook.setMarket(m);
        cook.setMarket(m1);
        cook.setMarket(m2);
        //gui.animationPanel.addGui(hostGui);
        m.startThread();
        m1.startThread();
        m2.startThread();
        host.startThread();
        cook.startThread();
        cashier.startThread();

      //makes cook
    	CookGui g = new CookGui(cook);
    	gui.animationPanel.addGui(g);
    	cook.setGui(g);
    	*/
        
        setLayout(new GridLayout(ROWS, COLUMNS, 2*GAP, 2*GAP));
        group.setLayout(new GridLayout(ROWS, COLUMNS, GAP, GAP));
        
        //Adds Pause Button to GUI
        //pause.setFont(new Font("arial",Font.BOLD,36));
        //group.add(pause);

        initRestLabel();
        add(restLabel);
        add(group);
    }
    
    //pauses all agents
    public void pauseAllAgents()
    {
    	for(Agent a : allAgents)
    	{
    		a.pauseAgent();
    	}
    }
    
    public void unpauseAllAgents()
    {
    	for(Agent a : allAgents)
    	{
    		a.pauseAgent();
    	}
    }
    
    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel("");
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" +  "</td></tr></table>wait staff:</tr><td>" +
                "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

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
    
    /*
    public void showInfo(String type, String name) {

        if (type.equals("customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if(type.equals("waiters"))
        {
        	for (int i = 0; i < waiters.size(); i++) {
                WaiterAgent temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }
*/
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    
    /*
    public void addPerson(String type, String name) {
    	
    	if (type.equals("customers")) {
    		
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		//c.setWaiter(waiters.get(0));
    		c.setGui(g);
    		customers.add(c);
    		allAgents.add(c);
    		c.startThread();
    	}
    }
    public void addWaiter(WaiterAgent w)
    {
    	//WaiterAgent w = new WaiterAgent(name);
    	WaiterGui g = new WaiterGui(w,waiters.size());
    	
    	gui.animationPanel.addGui(g);
		w.setHost(host);
		w.setCook(cook);
		w.cashier = cashier;
		w.setGui(g);
		allAgents.add(w);
		waiters.add(w);
		host.addWaiter(w);
		w.startThread();
		//WaitGui = new WaiterGui();
		//gui.animationPanel.addGui();
    }
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     *
    public void addPerson(String type, String name, boolean hungry) {

    	if (type.equals("customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);
    		
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		//c.setWaiter(waiters.get(0));
    		c.setGui(g);
    		customers.add(c);
    		allAgents.add(c);
    		c.startThread();
    		if(hungry){ g.setHungry(); }
    	}
    }*/

}
