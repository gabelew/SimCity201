package city.gui.trace;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import city.gui.trace.AlertLevel;
import city.gui.trace.AlertTag;
import city.gui.trace.TracePanel;

public class TraceControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	TracePanel tp;	//Hack so I can easily call showAlertsWithLevel for this demo

	JToggleButton messagesButton = new JToggleButton("Message");
	JToggleButton errorButton = new JToggleButton("Error");
	JToggleButton warningButton = new JToggleButton("Warning");
	JToggleButton infoButton = new JToggleButton("Info");
	JToggleButton debugButton = new JToggleButton("Debug");
	JToggleButton personTagButton = new JToggleButton("Person");
	JToggleButton busTagButton = new JToggleButton("Bus");
	JToggleButton atHomeTagButton = new JToggleButton("At Home");
	JToggleButton marketCustTagButton = new JToggleButton("Market Customer");
	JToggleButton marketClerkTagButton = new JToggleButton("Market Clerk");
	JToggleButton deliveryManTagButton = new JToggleButton("Market Deliveryman");
	JToggleButton bankCustTagButton = new JToggleButton("Bank Customer");
	JToggleButton bankSystemTagButton = new JToggleButton("Bank System");
	JToggleButton restCustTagButton = new JToggleButton("Restaurant Customer");
	JToggleButton hostTagButton = new JToggleButton("Restaurant Host");
	JToggleButton cookTagButton = new JToggleButton("Restaurant Cook");
	JToggleButton cashierTagButton = new JToggleButton("Restaurant Cashier");
	JToggleButton waiterTagButton = new JToggleButton("Restaurant Waiter");

    static final int BUTTON_PADDING = 40;
    static final int GROUP_BUTTON_Y = 25;
	
	public TraceControlPanel(final TracePanel tracePanel) {
		this.tp = tracePanel;

		messagesButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithLevel(AlertLevel.MESSAGE);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithLevel(AlertLevel.MESSAGE);
				}
			}
		});
		
		errorButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithLevel(AlertLevel.ERROR);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithLevel(AlertLevel.ERROR);
				}
			}
		});
		
		warningButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithLevel(AlertLevel.WARNING);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithLevel(AlertLevel.WARNING);
				}
			}
		});
		
		infoButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithLevel(AlertLevel.INFO);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithLevel(AlertLevel.INFO);
				}
			}
		});
		
		debugButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithLevel(AlertLevel.DEBUG);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithLevel(AlertLevel.DEBUG);
				}
			}
		});
		personTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.PERSON);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.PERSON);
				}
			}
		});
        
		bankCustTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.BANK_CUSTOMER);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.BANK_CUSTOMER);
				}
			}
		});
        
		atHomeTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.AT_HOME);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.AT_HOME);
				}
			}
		});
        
		marketCustTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.MARKET_CUSTOMER);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.MARKET_CUSTOMER);
				}
			}
		});
        
		
		marketClerkTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.MARKET_CLERK);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.MARKET_CLERK);
				}
			}
		});
        
		
		deliveryManTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.MARKET_DELIVERYMAN);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.MARKET_DELIVERYMAN);
				}
			}
		});
        
		
		bankSystemTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.BANK_SYSTEM);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.BANK_SYSTEM);
				}
			}
		});
        
		
		restCustTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.REST_CUSTOMER);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.REST_CUSTOMER);
				}
			}
		});
        
		
		hostTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.REST_HOST);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.REST_HOST);
				}
			}
		});
        
		
		cookTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.REST_COOK);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.REST_COOK);
				}
			}
		});
        
		
		cashierTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.REST_CASHIER);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.REST_CASHIER);
				}
			}
		});
        
		
		waiterTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.REST_WAITER);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.REST_WAITER);
				}
			}
		});
        
		
		busTagButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					tracePanel.showAlertsWithTag(AlertTag.BUS);
				}else if(e.getStateChange()==ItemEvent.DESELECTED){
					tracePanel.hideAlertsWithTag(AlertTag.BUS);
				}
			}
		});
        
		
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setLayout(new GridLayout(0,1,10,2));
		this.add(messagesButton);
		this.add(errorButton);
		this.add(warningButton);
		this.add(infoButton);
		this.add(debugButton);
		this.add(personTagButton);
		this.add(atHomeTagButton);
		this.add(marketCustTagButton);
		this.add(marketClerkTagButton);
		this.add(deliveryManTagButton);
		this.add(bankCustTagButton);
		this.add(bankSystemTagButton);
		this.add(restCustTagButton);
		this.add(hostTagButton);
		this.add(cookTagButton);
		this.add(cashierTagButton);
		this.add(waiterTagButton);
		this.add(busTagButton);
		this.setMinimumSize(new Dimension(50, 600));
	}
}
