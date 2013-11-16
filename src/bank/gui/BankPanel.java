package bank.gui;

import java.util.Vector;

import javax.swing.JPanel;

import city.BankAgent;
import city.gui.SimCityGui;
import city.roles.BankCustomerRole;

public class BankPanel extends JPanel{
	private Vector<BankCustomerRole> customers = new Vector<BankCustomerRole>();
	private SimCityGui gui; // reference to main gui

}
