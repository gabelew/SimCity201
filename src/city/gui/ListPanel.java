package city.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private long lastCheckAction = 0;
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel addListItemView = new JPanel();
    private JButton addPersonB = new JButton("Add");
    private JButton pauseAgentsB = new JButton("Pause");
    private JPanel buttonGroup = new JPanel();

    private InfoPanel infoPanel;
    private String type;
    
    private JTextField typeNameHere = new JTextField();
   // private JCheckBox setStateCB;
    //private List<JCheckBox> stateCBList = new ArrayList<JCheckBox>();
    private List<ListItem> listItems = new ArrayList<ListItem>();
    private int tableCounter = 0;

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
     * @param infoPanel   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(InfoPanel infoPanel, String type) {
        this.infoPanel = infoPanel;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addListItemView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));
        Dimension addCustViewSize = new Dimension(LIST_ITEM_VIEW_X, LIST_ITEM_VIEW_Y);
        addListItemView.setPreferredSize(addCustViewSize);
        addListItemView.setMinimumSize(addCustViewSize);
        addListItemView.setMaximumSize(addCustViewSize);      

        addListItemView.add(getTypeNameHere(),BorderLayout.CENTER);
    /*    setStateCB = new JCheckBox();
        setStateCB.setVisible(true);
        setStateCB.addActionListener(this);
        if(type == "Persons")
        	setStateCB.setText("Hungry?");
        else if(type == "Waiters")
        	setStateCB.setText("Working?");
        setStateCB.setSelected(false);
        setStateCB.setEnabled(true);
        addListItemView.add(setStateCB, BorderLayout.EAST);*/
        
        
        buttonGroup.setLayout(new BoxLayout(buttonGroup, BoxLayout.X_AXIS));
        
        Dimension buttonGroupSize = new Dimension(GROUP_BUTTON_X, GROUP_BUTTON_Y);
        buttonGroup.setPreferredSize(buttonGroupSize);
        buttonGroup.setMinimumSize(buttonGroupSize);
        buttonGroup.setMaximumSize(buttonGroupSize);      
        
        FontMetrics fm = addPersonB.getFontMetrics(addPersonB.getFont());
        
        
        addPersonB.addActionListener(this);
        Dimension addPersonBSize = new Dimension(fm.stringWidth("Add") + BUTTON_PADDING, GROUP_BUTTON_Y);
        addPersonB.setPreferredSize(addPersonBSize);
        addPersonB.setMinimumSize(addPersonBSize);
        addPersonB.setMaximumSize(addPersonBSize);  
        buttonGroup.add(addPersonB);
        
        pauseAgentsB.addActionListener(this);
        Dimension pauseAgentsBSize = new Dimension(fm.stringWidth("Resume") + BUTTON_PADDING, GROUP_BUTTON_Y);
        pauseAgentsB.setPreferredSize(pauseAgentsBSize);
        pauseAgentsB.setMinimumSize(pauseAgentsBSize);
        pauseAgentsB.setMaximumSize(pauseAgentsBSize);
        
        if(this.type == "Persons")
        {
        	buttonGroup.add(pauseAgentsB);
        }
        
        add(buttonGroup);

        //view.add(addCustView);

        if(this.type != "Tables")
        {
        	add(addListItemView);
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
        if (e.getSource() == addPersonB) {
            if(type == "Persons"){
            	addPerson(getTypeNameHere().getText());
            }
           /* if(type == "Waiters"){
            	addPerson(getTypeNameHere().getText());
            }
            if(type == "Tables"){
            	addPerson("Table " + ++tableCounter);	
            }*/
            
            getTypeNameHere().setText(null);
           // setStateCB.setSelected(false);

        }
        else if(e.getSource() == pauseAgentsB)
        {
        	infoPanel.pauseAgents();	
        }
        else //if(listItems.contains(e.getSource()))
        {
        	if(lastCheckAction != e.getWhen())
        	{
        		lastCheckAction = e.getWhen();
	        	for (ListItem temp:listItems){
	                if (e.getSource() == temp.stateCB)
	                {
	                	if(type == "Persons")
	                	{
	                		infoPanel.setHungry(type, temp.stateCB.getName());
	                		temp.stateCB.setEnabled(false);
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
            
            JCheckBox newstateCB = new JCheckBox();

            if(type != "Tables"){
            	newstateCB.setVisible(true);
            }
            if(type == "Tables"){
            	newstateCB.setVisible(false);
        	}
            newstateCB.addActionListener(this);
            
            if(type == "Persons")
            	newstateCB.setText("Hungry?");
            if(type == "Waiters")
            	newstateCB.setText("Working?");
            if(type == "Tables")
            	newstateCB.setText("delete?");
            
            newstateCB.setSelected(false);
/*
            if(type == "Waiters" && setStateCB.isSelected())
            {
            	newstateCB.setText("Break?");
            	newstateCB.setEnabled(true);
                newstateCB.setSelected(false);
            }
            else if(setStateCB.isSelected())
            {
            	newstateCB.setEnabled(false);
                newstateCB.setSelected(true);
            }
            else
            {
                newstateCB.setEnabled(true);
                newstateCB.setSelected(false);
            }
            
            addNewCustView.add(newstateCB, BorderLayout.EAST);

            */
            newstateCB.addActionListener(this);
            
            JLabel label = new JLabel(name);
            label.setBackground(Color.white);
            if(type == "Tables"){
            	label.setForeground(Color.decode(GREEN_LABEL_COLOR));
        	}
        	else{
            	label.setForeground(Color.black);
            }
            
            Dimension labelSize = new Dimension(paneSize.width - LABEL_SIZE_OFFSET,
                    (int) (paneSize.height / LIST_ITEM_H));
            label.setPreferredSize(labelSize);
            label.setMinimumSize(labelSize);
            label.setMaximumSize(labelSize);
            newstateCB.setName(name);
            newstateCB.addActionListener(this);
            
            listItems.add(new ListItem(label, newstateCB));
            addNewCustView.add(label);
            view.add(addNewCustView);
            
            if(type != "Tables")
            {
            	infoPanel.addPerson(type, name);//puts customer or waiter on list
            }

            
            validate();
        }
    }
    
    public void addStartingTable(int x, int y){
    	String name = "Table " + ++tableCounter;
        
        JPanel addNewCustView = new JPanel();

        Dimension paneSize = pane.getSize();
        
    	addNewCustView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));  
        Dimension addCustViewSize = new Dimension(paneSize.width - LIST_ITEM_VIEW_OFFSET,
                (int) (paneSize.height / LIST_ITEM_H));
        addNewCustView.setPreferredSize(addCustViewSize);
        addNewCustView.setMinimumSize(addCustViewSize);
        addNewCustView.setMaximumSize(addCustViewSize);      
        
        JCheckBox newstateCB = new JCheckBox();
        newstateCB.setVisible(false);
        newstateCB.addActionListener(this);
        
        newstateCB.setText("delete?");
        
        
        newstateCB.setEnabled(true);
        newstateCB.setSelected(false);
        
        
        addNewCustView.add(newstateCB, BorderLayout.EAST);

        newstateCB.addActionListener(this);
        
        JLabel label = new JLabel(name);
        label.setBackground(Color.white);
        
        if(type == "Tables"){
        	label.setForeground(Color.decode(GREEN_LABEL_COLOR));
    	}
    	else{
        	label.setForeground(Color.black);
        }
        
        Dimension labelSize = new Dimension(paneSize.width - LABEL_SIZE_OFFSET,
                (int) (paneSize.height / LIST_ITEM_H));
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
    
	public void setCustomerEnabled(String name) {
		for(ListItem temp:listItems)
    	{
	        if (temp.stateCB.getName() == name) {
	           
	                temp.stateCB.setEnabled(true);
	                temp.stateCB.setSelected(false);
	     }
    	}
		
	}
	
	public void setTableEnabled(int tableNumber){
		String name = "Table "+ (tableNumber + ONE);
		for(ListItem temp:listItems)
    	{
	        if (temp.stateCB.getName().equals(name)) {
	           
	                temp.stateCB.setEnabled(true);
	                temp.stateCB.setSelected(false);
	                temp.label.setForeground(Color.decode(GREEN_LABEL_COLOR));
	     }
    	}	
	}
	public void setTableDisabled(int tableNumber){
		String name = "Table "+ (tableNumber + ONE);
		for(ListItem temp:listItems)
    	{
	        if (temp.stateCB.getName().equals(name)) {
	           
	                temp.stateCB.setEnabled(false);
	                temp.label.setForeground(Color.decode(RED_LABEL_COLOR));
	     }
    	}
		
	}
	public void changePauseButton() {
		if(getPauseButtonLabel() == "Pause")
			pauseAgentsB.setText("Resume");
		else
			pauseAgentsB.setText("Pause");
	}
	public void hidePauseButton() {
		pauseAgentsB.setVisible(false);
		
	}
	public void showPauseButton(){
		pauseAgentsB.setVisible(true);
	}
	public String getPauseButtonLabel(){
		return pauseAgentsB.getText();
	}
	
	class ListItem{
		JLabel label;
		JCheckBox stateCB;
		
		ListItem(JLabel label, JCheckBox c){
			this.label = label;
			stateCB = c;
		}
	}

	public void setWaiterOnBreak(String name) {
		for (ListItem temp:listItems){
			if(temp.stateCB.getName() == name)
			{
				temp.stateCB.setText("Back To Work?");
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

	public void setWaiterBreakable(String name) {
		for(ListItem temp:listItems)
    	{
	        if (temp.stateCB.getName() == name) {
	                temp.stateCB.setEnabled(true);
	                temp.stateCB.setSelected(false);
	        }
    	}	
	}
	public void setWaiterUnbreakable(String name) {
		for(ListItem temp:listItems)
    	{
	        if (temp.stateCB.getName() == (name)) {
	                temp.stateCB.setEnabled(false);
	                temp.stateCB.setSelected(false);
	        }
    	}	
	}

	public JTextField getTypeNameHere() {
		return typeNameHere;
	}

	public void setTypeNameHere(JTextField typeNameHere) {
		this.typeNameHere = typeNameHere;
	}


}
