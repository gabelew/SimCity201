package GHRestaurant.gui;

//import restaurant.CustomerAgent;
//import restaurant.HostAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class GHListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    //private JButton breakbutton;
    private JTextField textfield = new JTextField();
    private JCheckBox checkb;
    
    private GHRestaurantPanel restPanel;
    private String type;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public GHListPanel(GHRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;
        
        if(type.equals("Customers")){
        	checkb = new JCheckBox("Hungry?", false);
        	}
        else if(type.equals("Waiters")){
        	checkb = new JCheckBox("Break?", false);
        }

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);
        
        //if(type.equals("Customers")){
        checkb.setVisible(true);
        checkb.addActionListener(this);
        //}
        //else if(type.equals("Waiters")){
        //breakbutton.addActionListener(this);
        //}
        textfield.setMaximumSize(new Dimension(Integer.MAX_VALUE, textfield.getPreferredSize().height) );
        textfield.addActionListener(this);
        
        add(addPersonB);
        //if(type.equals("Customers")){
        add(checkb);//}
       // if(type.equals("Waiters")){add(breakbutton);};
        add(textfield);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            addPerson(textfield.getText());
        }        
       /* else{
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*//*
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }*/
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
          /*  JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            
            //if(checkb.isSelected()){checkb.setEnabled(false);}
            
            
            
            
            restPanel.addPerson(type, name, checkb.isSelected());//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();*/
        }
    }   
    }

