package bank.gui;

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;

import city.BankAgent;
import city.MarketAgent;
import city.animationPanels.InsideBuildingPanel;
import city.gui.SimCityGui;
import city.roles.BankCustomerRole;

@SuppressWarnings("serial")
public class BankPanel extends JPanel{
	private static final int BANK_PANEL_GAP = 20;
	private static final int GROUP_PANEL_GAP = 10;
	private static final int GROUP_NROWS = 1;
	private static final int GROUP_NCOLUMNS = 2;
	private static final int NCOLUMNS = 2;
	private static final int NROWS = 2;
	private Vector<BankCustomerRole> customers = new Vector<BankCustomerRole>();
	private SimCityGui gui; // reference to main gui
    private InsideBuildingPanel insideBuildingPanel;
    public BankListPanel customerPanel = new BankListPanel(this, "Customers");
    private JPanel group = new JPanel();
    public BankPanel() {
    	
    }
    public BankPanel(SimCityGui gui) {
    	setLayout(new GridLayout(NROWS, NCOLUMNS, BANK_PANEL_GAP, BANK_PANEL_GAP));
        group.setLayout(new GridLayout(GROUP_NROWS, GROUP_NCOLUMNS, GROUP_PANEL_GAP, GROUP_PANEL_GAP));
        this.gui = gui;

        add(customerPanel);
    }
	
    public void addCustomerToList(String name){
		customerPanel.addPerson(name);
	}
	public void removeCustomerFromList(String name){
		customerPanel.removePerson(name);
	}
    
	public void setInsideBuildingPanel(InsideBuildingPanel parent){
    	insideBuildingPanel = parent;
    } 
    public InsideBuildingPanel getInsideBuildingPanel(){
    	return insideBuildingPanel;
    }
 
    
}
