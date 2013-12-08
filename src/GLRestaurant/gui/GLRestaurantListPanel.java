package GLRestaurant.gui;

import GLRestaurant.roles.GLCustomerRole;
import GLRestaurant.roles.GLHostRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class GLRestaurantListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    private JButton pauseB = new JButton("Pause");
    private JButton zeroInventoryB = new JButton("NoFood");
    private JTextField nameInput = new JTextField();
    private JCheckBox hungryState;
    private GLRestaurantPanel restPanel;
    private String type;
    private boolean paused = false;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public GLRestaurantListPanel(GLRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        JLabel panelType = new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>");
        JPanel topPane = new JPanel();
        pauseB.addActionListener(this);
        zeroInventoryB.addActionListener(this);
        topPane.add(pauseB);
        topPane.add(zeroInventoryB);
        topPane.add(panelType);
        add(topPane);
        topPane.setMaximumSize(new Dimension(200,2));
        
        JPanel inputPane = new JPanel();
        
        addPersonB.addActionListener(this);
        add(addPersonB);
        addPersonB.setMaximumSize(new Dimension(200,2));
 
        Dimension maximumSize = new Dimension(200,1);
        nameInput.setMaximumSize(maximumSize);
        nameInput.addActionListener(this);
        inputPane.setLayout(new GridLayout(0,2));
        inputPane.add(nameInput);
        
        hungryState = new JCheckBox();
        hungryState.setText("Hungry?");
        add(hungryState);
        inputPane.add(hungryState);
        add(inputPane);
        inputPane.setMaximumSize(new Dimension(500,2));
        
        if(type == "Waiters") {
        	hungryState.setVisible(false);
        	pauseB.setVisible(false);
        } else {
        	zeroInventoryB.setVisible(false);
        }
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == addPersonB) {
//        	if(!nameInput.getText().isEmpty()) {
//        		addPerson(nameInput.getText());
//            	nameInput.setText("");
//        	}
//        } else if (e.getSource() == pauseB) {
//        	if(!paused) {
//        		System.out.println("Restaurant paused.");
//        		pauseB.setText("Resume");
//        		restPanel.pause();
//        		paused = true;
//        	} else {
//        		System.out.println("Restaurant unpaused.");
//        		pauseB.setText("Pause");
//        		restPanel.resume();
//        		paused = false;
//        	}
//        } else if (e.getSource() == zeroInventoryB) {
//        	System.out.println("Cook inventory has been zeroed out.");
//        	restPanel.emptyCookInventory();
//        } else {
//        	// Isn't the second for loop more beautiful?
//            /*for (int i = 0; i < list.size(); i++) {
//                JButton temp = list.get(i);*/
//        	for (JButton temp:list){
//                if (e.getSource() == temp)
//                    //restPanel.showInfo(type, temp.getText());
//            }
//        }
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
         
            validate();
        }
    }
}
