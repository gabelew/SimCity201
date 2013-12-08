package GHRestaurant.gui;

import restaurant.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class GHRestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Restaurant Animation");
	GHAnimationPanel animationPanel = new GHAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private GHRestaurantPanel restPanel = new GHRestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    //private JCheckBox stateCB;//part of infoLabel
    private JButton button;

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public GHRestaurantGui() {
        int WINDOWX = 500;
        int WINDOWY = 600;

        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        animationFrame.setVisible(true);
    	animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);

        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.Y_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .25));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));


        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        
        /*if (currentPerson instanceof Customer) {
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        infoPanel.add(stateCB);
        }
        else if(currentPerson instanceof Waiter) {*/
        button = new JButton();
        button.setVisible(false);
        button.addActionListener(this);
        infoPanel.add(button);
       // }
        
 
        //infoPanel.add(stateCB);
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
        button.setVisible(true);

        if (person instanceof Customer) {
            Customer customer = (Customer) person;
            button.setVisible(true);
            button.setText("Hungry?");
          //Should checkmark be there? 
            //button.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            button.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
      
        else if (person instanceof Waiter) {
            Waiter waiter = (Waiter) person;
            button.setVisible(true);
            //button.setText("Break?");
          //Should checkmark be there? 
            //button.setSelected(waiter.getGui().OnBreak());
          //Is waiter Break? Hack. Should ask waiterGui
            if(!waiter.getGui().OnBreak()){
                button.setText("Break?");

            button.setEnabled(!waiter.getGui().OnBreak());
            }
            else{
            	button.setText("Back-To-Work");
            	button.setEnabled(true);
            }
          // Hack. Should ask customerGui
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
        if (e.getSource() == button) {
        	
        	
            if (currentPerson instanceof Customer) {
                Customer c = (Customer) currentPerson;
                c.getGui().setHungry();
                button.setEnabled(false);
            }
        
            if (currentPerson instanceof Waiter) {
                Waiter w = (Waiter) currentPerson;
                if(button.getText().equals("Break?")){
                	w.getGui().GoOnBreak();
                	button.setEnabled(false);
                }
                else if(button.getText().equals("Back-To-Work")){
                	w.getGui().GoBackToWork();
                	button.setEnabled(false);
                }
            }   
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(Customer c) {
        if (currentPerson instanceof Customer) {
            Customer cust = (Customer) currentPerson;
            if (c.equals(cust)) {
                button.setEnabled(true);
                //button.setSelected(false);
            }
        }
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        GHRestaurantGui gui = new GHRestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
