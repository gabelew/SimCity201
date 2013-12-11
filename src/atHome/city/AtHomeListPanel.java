package atHome.city;

import javax.swing.*;

import restaurant.interfaces.Host;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;
import city.roles.AtHomeRole;
import GCRestaurant.roles.GCHostRole;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class AtHomeListPanel extends JPanel implements ActionListener 
{
	private static final long serialVersionUID = 1L;
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel buttonGroup = new JPanel();

    private AtHomePanel restPanel;
    private String type;
    
    private JCheckBox setStateCB;
    private List<AtHomeRole> people = new ArrayList<AtHomeRole>();
    private List<ListItem> listItems = new ArrayList<ListItem>();

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
    public AtHomeListPanel(AtHomePanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        
        setStateCB = new JCheckBox();
        setStateCB.setVisible(true);
        setStateCB.addActionListener(this);
        setStateCB.setText("Break Appliance?");
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

    public void actionPerformed(ActionEvent e) 
    {
    	for (ListItem temp:listItems)
    	{
    		if (e.getSource() == temp.stateCB)
            {
    			if(temp.stateCB.getText().equals("Break Appliance?"))
	               {
	               	temp.stateCB.setText("has broken app");
	               	temp.stateCB.setEnabled(false);
	               	temp.stateCB.setSelected(true);
	               	
	               	//finds role
	               	for(AtHomeRole r : people)
	               	{
	               		if(r.myPerson.getName().equals(temp.label.getText()))
	               		{
	               			r.BrokenApplianceMsg("fridge");
	               		}
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
    public void addPerson(String name, AtHomeRole role) 
    {
    	people.add(role);
        if (name != null) {
            
            JPanel addNewCustView = new JPanel();

            Dimension paneSize = pane.getSize();
            
        	addNewCustView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));  
            Dimension addCustViewSize = new Dimension(paneSize.width - LIST_ITEM_VIEW_OFFSET,
                    (int) (paneSize.height / LIST_ITEM_H)*2);
            addNewCustView.setPreferredSize(addCustViewSize);
            addNewCustView.setMinimumSize(addCustViewSize);
            addNewCustView.setMaximumSize(addCustViewSize);      
            
            JCheckBox newstateCB = new JCheckBox();

            newstateCB.setText("Break Appliance?");
            newstateCB.setSelected(false);
            
            addNewCustView.add(newstateCB, BorderLayout.SOUTH);

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
	public void breakHomeApplicate(String name){
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("has broken app");
		    	temp.stateCB.setEnabled(false);
		    	temp.stateCB.setSelected(true);
			}
		}
	}
	public void notBrokenAnymore(String name) {
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("Break Appliance?");
                temp.stateCB.setEnabled(true);
                temp.stateCB.setSelected(false);
			}
		
		}
	}
}
