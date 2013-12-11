package GLRestaurant.gui;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
@SuppressWarnings("serial")
public class GLRestaurantListPanel extends JPanel implements ActionListener {
	private GLRestaurantPanel restPanel;
	private JPanel view = new JPanel();
    private List<JLabel> listItems = new ArrayList<JLabel>();
	static final int LIST_ITEM_VIEW_GAP = 5;
    static final int LIST_ITEM_VIEW_X = 180;
    static final int LIST_ITEM_VIEW_Y = 25;
    static final int GROUP_BUTTON_X = 150;
    static final int GROUP_BUTTON_Y = 25;
    static final int LIST_ITEM_VIEW_OFFSET = 30;
    static final int LABEL_SIZE_OFFSET = 20;
    static final int LIST_ITEM_H = 7;
    static final int BUTTON_PADDING = 40;
    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private String type;
	public JTextField typeNameHere = new JTextField();

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
	    add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
	    
	    view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {

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
	            
	            JPanel addNewCustView = new JPanel();

	            Dimension paneSize = pane.getSize();
	            
	        	addNewCustView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));  
	            Dimension addCustViewSize = new Dimension(paneSize.width - LIST_ITEM_VIEW_OFFSET,
	                    (int) (paneSize.height / LIST_ITEM_H));
	            addNewCustView.setPreferredSize(addCustViewSize);
	            addNewCustView.setMinimumSize(addCustViewSize);
	            addNewCustView.setMaximumSize(addCustViewSize);
	            
	            JLabel label = new JLabel(name);
	            
	            label.setBackground(Color.white);
	            //label.setForeground(Color.black);
	            Dimension labelSize = new Dimension(paneSize.width - LABEL_SIZE_OFFSET,
	                    (int) (paneSize.height / LIST_ITEM_H));
	            label.setPreferredSize(labelSize);
	            label.setMinimumSize(labelSize);
	            label.setMaximumSize(labelSize);
	            listItems.add(label);
	            
	            addNewCustView.add(label);
	            view.add(addNewCustView);
		 }
	            validate();
    }
    
    public void removePerson(String name) {
		JPanel removeP = null;
		for(int i = 0; i < view.getComponentCount();i++){
			if(((JLabel) ((JPanel) view.getComponent(i)).getComponent(0)).getText().equals(name)){
				removeP = (JPanel) view.getComponent(i);
			}
		}
		if(removeP != null){
			view.remove(removeP);
		}
		JLabel removeI = null;
		for(int i = 0; i< listItems.size();i++){
			if(listItems.get(i).getText().equals(name)){
				removeI = listItems.get(i);
			}
		}
			
		if(removeI != null){
			listItems.remove(removeI);
		}
		
		invalidate();
		validate();	
		repaint();
	}
}
