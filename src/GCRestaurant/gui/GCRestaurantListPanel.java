package GCRestaurant.gui;

import javax.swing.*;

import restaurant.interfaces.Host;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

import GCRestaurant.roles.GCHostRole;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class GCRestaurantListPanel extends JPanel implements ActionListener 
{
	private static final long serialVersionUID = 1L;
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    //private JPanel addListItemView = new JPanel();
    private JPanel buttonGroup = new JPanel();

    private GCRestaurantPanel restPanel;
    private String type;
    
    private JCheckBox setStateCB;
    //private List<JCheckBox> stateCBList = new ArrayList<JCheckBox>();
    private List<ListItem> listItems = new ArrayList<ListItem>();
    private Host host;

    static final int LIST_ITEM_VIEW_GAP = 5;
    static final int LIST_ITEM_VIEW_X = 180;
    static final int LIST_ITEM_VIEW_Y = 25;
    static final int GROUP_BUTTON_X = 150;
    static final int GROUP_BUTTON_Y = 25;
    static final int LIST_ITEM_VIEW_OFFSET = 30;
    static final int LABEL_SIZE_OFFSET = 20;
    static final int LIST_ITEM_H = 7;
    static final int BUTTON_PADDING = 40;
    static final int ONE = 1;
    static final String GREEN_LABEL_COLOR = "0x10721c";
    static final String RED_LABEL_COLOR = "0x971515";
    
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public GCRestaurantListPanel(GCRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        
        setStateCB = new JCheckBox();
        setStateCB.setVisible(true);
        setStateCB.addActionListener(this);
        if(type == "Waiters")
        	setStateCB.setText("Break?");
        setStateCB.setSelected(false);
        setStateCB.setEnabled(true);
        
        buttonGroup.setLayout(new BoxLayout(buttonGroup, BoxLayout.X_AXIS));
        
        Dimension buttonGroupSize = new Dimension(GROUP_BUTTON_X, GROUP_BUTTON_Y);
        buttonGroup.setPreferredSize(buttonGroupSize);
        buttonGroup.setMinimumSize(buttonGroupSize);
        buttonGroup.setMaximumSize(buttonGroupSize);      
        
        add(buttonGroup);
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
        
    }

    public void setHost(Host h)
    {
    	this.host = h;
    }
    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) 
    {
    	for (ListItem temp:listItems)
    	{
    		if (e.getSource() == temp.stateCB)
            {
    			if(type == "Waiters")
    			{
    				if(temp.stateCB.getText().equals("Break?"))
	                {
	                	temp.stateCB.setText("On Break");
	                	temp.stateCB.setEnabled(false);
	                	temp.stateCB.setSelected(true);
	                	((GCHostRole)host).msgCanIBreak(temp.stateCB.getName());
	                }
    			}
            }
    	}
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     */    	
    public void addPerson(String name) 
    {
        if (name != null) {
            
            JPanel addNewCustView = new JPanel();

            Dimension paneSize = pane.getSize();
            
        	addNewCustView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));  
            Dimension addCustViewSize = new Dimension(paneSize.width - LIST_ITEM_VIEW_OFFSET,
                    (int) (paneSize.height / LIST_ITEM_H));
            addNewCustView.setPreferredSize(addCustViewSize);
            addNewCustView.setMinimumSize(addCustViewSize);
            addNewCustView.setMaximumSize(addCustViewSize);      
            
            JCheckBox newstateCB = new JCheckBox();

            if(type == "Waiters")
            {
            	newstateCB.setText("Break?");
            }
            else
            {
            	newstateCB.setEnabled(false);
            }
            newstateCB.setSelected(false);
            
            addNewCustView.add(newstateCB, BorderLayout.EAST);

            newstateCB.addActionListener(this);
            
            JLabel label = new JLabel(name);
            label.setBackground(Color.white);
            
            Dimension labelSize = new Dimension(paneSize.width - LABEL_SIZE_OFFSET, (int) (paneSize.height / LIST_ITEM_H));
            label.setPreferredSize(labelSize);
            label.setMinimumSize(labelSize);
            label.setMaximumSize(labelSize);
            newstateCB.setName(name);
            newstateCB.addActionListener(this);
            
            listItems.add(new ListItem(label, newstateCB));
            addNewCustView.add(label);
            view.add(addNewCustView);
            
            validate();
        }
    }
    
    public void removeWaiter(String name)
    {
    			JPanel removeP = null;
    			for(int i = 0; i < view.getComponentCount();i++)
    			{
    				if(((JLabel) ((JPanel) view.getComponent(i)).getComponent(1)).getText().equals(name)){
    					removeP = (JPanel) view.getComponent(i);
    				}
    			}
    			if(removeP != null){
    				view.remove(removeP);
    			}
    			ListItem removeI = null;
    			for(int i = 0; i< listItems.size();i++)
    			{
    				if(listItems.get(i).label.getText().equals(name)){
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
	
	class ListItem
	{
		JLabel label;
		JCheckBox stateCB;
		
		ListItem(JLabel label, JCheckBox c)
		{
			this.label = label;
			stateCB = c;
		}
	}
	public void setWaiterBackFromBreak(String name){
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("Break?");
		    	temp.stateCB.setEnabled(true);
		    	temp.stateCB.setSelected(false);
			}
		}
	}
	public void setWaiterOnBreak(String name) {
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("On Break!");
                temp.stateCB.setEnabled(true);
                temp.stateCB.setSelected(false);
			}
		
		}
	}
	public void setWaiterCantBreak(String name) {
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("Break?");
                temp.stateCB.setEnabled(true);
                temp.stateCB.setSelected(false);
                
			}
		
		}
	}
}
