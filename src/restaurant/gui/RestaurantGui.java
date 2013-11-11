package restaurant.gui;

import restaurant.CustomerAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	/* The GUI had two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	RestaurantAnimationPanel animationPanel = new RestaurantAnimationPanel(this);
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
   // private JPanel infoPanel;
   // private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
     
    private JPanel APanel = new JPanel();
    private List<Table> tables = new ArrayList<Table>();

    static final int FRAMEX = 950;
    static final int FRAMEY = 500;
    static final int WINDOWX = 450;
    static final int OFFSETPOS = 50;
    static final int NROWS = 1;
    static final int NCOLUMNS = 0;
    static final int REST_PANEL_Y = 450;
    static final int xTABLE_WIDTH = 42;
    static final int yTABLE_WIDTH = 44;
    static final int TABLE_PADDING = 5;
	static final int xTABLE_IMG_POINT_OFFSET = 1;
	static final int yTABLE_IMG_POINT_OFFSET = 40;
	static final int xTABLE_IMG_AREA_OFFSET = 10;
	static final int yTABLE_IMG_AREA_OFFSET = 42;
	static final int xDEFAULT_NEW_TABLE_POSITION = 140;
	static final int yDEFAULT_NEW_TABLE_POSITION = 40;
	static final int STARTING_TABLES_X = 200;
	static final int STARTING_TABLE1_Y = 35;
	static final int STARTING_TABLE_Y_SPACING = 90;
    
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
    	setBounds(OFFSETPOS, OFFSETPOS, FRAMEX, FRAMEY);

        setLayout(new GridLayout(NROWS,NCOLUMNS));
        
        Dimension restDim = new Dimension(WINDOWX, (int) (REST_PANEL_Y));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        APanel.add(restPanel);
        
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        add(APanel);

        add(animationPanel);

    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    
   /* public void updateInfoPanel(Object person) {
        //stateCB.setVisible(true);
        //CustomerAgent currentPerson = (CustomerAgent) person;
    	
        if (person instanceof CustomerAgent) {
          //  CustomerAgent customer = (CustomerAgent) person;
          //  stateCB.setText("Hungry?");
          //Should checkmark be there? 
          //  stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
          //  stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
          //  infoLabel.setText(
          //     "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }*/
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        /*if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }*/
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */ 
    public void setCustomerEnabled(CustomerAgent c) {
    	
    	restPanel.setCustomerEnabled(c);
    	//find customer in list
    	/*for(CustomerAgent currentPerson:customersList)
    	{
	        if (currentPerson instanceof CustomerAgent) {
	            CustomerAgent cust = (CustomerAgent) currentPerson;
	            if (c.equals(cust)) {
	                stateCB.setEnabled(true);
	                stateCB.setSelected(false);
	            }
	     }
            
        }*/
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.addDefaultTables();
    }
    private void addDefaultTables() {
        restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y);
        restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING);
        restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
        restPanel.getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
	}

	public int getTablesXCoord(int i){
    	return tables.get(i).getX();
    }
    public int getTablesYCoord(int i){
    	return tables.get(i).getY();
    }

	public boolean isOnTable(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return true;
			}
		}
		return false;
	}

	public Table getTableAt(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return t;
			}
		}
		return null;
	}
	public Table getTableAtIndex(int i) {
		if(tables.size() < i){
			return tables.get(i);
		}
		
		return null;
	}
	public void setTableOccupied(int tableNumber) {
		tables.get(tableNumber).setOccupied();
		restPanel.setTableDisabled(tableNumber);
	}
	public void setTableUnoccupied(int tableNumber) {
		tables.get(tableNumber).setMovable();
		restPanel.setTableEnabled(tableNumber);
	}

	public boolean notOnExistingTable(Table newTablePos, Point placeTableHere) {
		for(Table t: tables)
		{
			if(t != newTablePos){
				if(
					(t.getX() - TABLE_PADDING <= placeTableHere.x && t.getX()+xTABLE_WIDTH + TABLE_PADDING >= placeTableHere.x &&
					t.getY() - TABLE_PADDING <= placeTableHere.y && t.getY()+yTABLE_WIDTH + TABLE_PADDING >= placeTableHere.y) ||
					(placeTableHere.x - TABLE_PADDING <= t.getX() && placeTableHere.x+xTABLE_WIDTH + TABLE_PADDING >= t.getX() &&
					placeTableHere.y - TABLE_PADDING <= t.getY() && placeTableHere.y+yTABLE_WIDTH + TABLE_PADDING >= t.getY())){
					return false;
				}
			}
		}
		return true;
	}

	public void addTable() {
    	tables.add(new Table(xDEFAULT_NEW_TABLE_POSITION, yDEFAULT_NEW_TABLE_POSITION));
    	animationPanel.addNewTable();
    	restPanel.getHost().addNewTable();
		
	}
	public void addTable(int x, int y) {
		
    	tables.add(new Table( x, y));
    	animationPanel.addNewTable();
    	restPanel.getHost().addNewTable();
		
	}

	public void setWaiterOnBreak(String name) {
		restPanel.setWaiterOnBreak(name);
		
	}

	public void setWaiterCantBreak(String name) {
		restPanel.setWaiterCantBreak(name);
	}

	public void setWaiterBreakable(String name) {
		restPanel.setWaiterBreakable(name);
	}
	public void setWaiterUnbreakable(String name) {
		restPanel.setWaiterUnbreakable(name);
	}

}
