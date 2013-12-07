package EBRestaurant.gui;


import javax.swing.*;

import EBRestaurant.roles.EBCustomerRole;
import EBRestaurant.roles.EBWaiterRole;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class EBRestaurantGui extends JFrame implements ActionListener {
    /* The GUI has one frame, with the control frame
     * and the animation frame
     */
	JPanel animationFrame = new JPanel();
	public EBAnimationPanel animationPanel = new EBAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private EBRestaurantPanel restPanel = new EBRestaurantPanel(this);
    private int windowPos=50;
    private int infoRows=1;
    private int infoCols=2;
    private int infoHgap=30;
    private int infoVgap=0;
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JCheckBox stateWB;
    private double restXFactor=.4;
    private double animationXFactor=.5;
    private double restYFactor=.25;
    private double YFactor=.75;
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public EBRestaurantGui() {
        int WINDOWX = 1100;
        int WINDOWY = 500;

        setBounds(windowPos, windowPos, WINDOWX, WINDOWY);
        setLayout(new FlowLayout());
        
        Dimension animationDim= new Dimension((int)(WINDOWX*animationXFactor), (int)(WINDOWY*YFactor));
        animationPanel.setPreferredSize(animationDim);
    	add(animationPanel);


        Dimension restDim = new Dimension((int)(WINDOWX*restXFactor), (int)(WINDOWY*YFactor));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int)(WINDOWY*restYFactor));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        stateWB = new JCheckBox();
        stateWB.setVisible(false);
        stateWB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(infoRows, infoCols, infoHgap, infoVgap));
        
        infoLabel = new JLabel(); 
        //infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        infoPanel.add(stateWB);
        add(infoPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {

        currentPerson = person;

        if (person instanceof EBCustomerRole) {
        	stateWB.setVisible(false);
        	stateCB.setVisible(true);
            EBCustomerRole customer = (EBCustomerRole) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        if (person instanceof EBWaiterRole) {
        	stateCB.setVisible(false);
        	stateWB.setVisible(true);
            EBWaiterRole waiter = (EBWaiterRole) person;
            stateWB.setText("Break?");
            stateWB.setSelected(waiter.getGui().onBreak());
          // Hack. Should ask waiterGui
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof EBCustomerRole) {
                EBCustomerRole c = (EBCustomerRole) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        if (e.getSource()==stateWB)
        {
        	if(stateWB.isSelected())
        	{
            if (currentPerson instanceof EBWaiterRole) {
                EBWaiterRole w = (EBWaiterRole) currentPerson;
                w.getGui().setBreak();
                //stateWB.setEnabled(false);
            }
        	}
        	else{
                EBWaiterRole w = (EBWaiterRole) currentPerson;
                w.getGui().unsetBreak();
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(EBCustomerRole c) {
        if (currentPerson instanceof EBCustomerRole) {
            EBCustomerRole cust = (EBCustomerRole) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    public void setWaiterEnabled(EBWaiterRole w){
        if (currentPerson instanceof EBWaiterRole) {
            EBWaiterRole waiter = (EBWaiterRole) currentPerson;
            if (w.equals(waiter)) {
                stateWB.setEnabled(true);
                stateWB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        EBRestaurantGui gui = new EBRestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
}
