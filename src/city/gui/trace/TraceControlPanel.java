package city.gui.trace;

import java.awt.Dimension;
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

	TracePanel tp;	//Hack so I can easily call showAlertsWithLevel for this demo.
	
	JButton enableMessagesButton;		//You could (and probably should) substitute a JToggleButton to replace both
	JButton disableMessagesButton;		//of these, but I split it into enable and disable for clarity in the demo.
	JButton enableErrorButton;		
	JButton disableErrorButton;		
	JButton enableBankCustTagButton;		//You could (and probably should) substitute a JToggleButton to replace both
	JButton disableBankCustTagButton;		//of these, but I split it into enable and disable for clarity in the demo.
	JToggleButton bankCustTagButton;
	
	public TraceControlPanel(final TracePanel tracePanel) {
		this.tp = tracePanel;
		enableMessagesButton = new JButton("Show Level: MESSAGE");
		disableMessagesButton = new JButton("Hide Level: MESSAGE");
		enableErrorButton = new JButton("Show Level: ERROR");
		disableErrorButton = new JButton("Hide Level: ERROR");
		enableBankCustTagButton = new JButton("Show Tag: BANK_CUSTOMER");
		disableBankCustTagButton = new JButton("Hide Tag: BANK_CUSTOMER");
		bankCustTagButton = new JToggleButton("BANK_CUSTOMER");
		
		
		enableMessagesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This is how you make messages with a certain Level (normal MESSAGE here) show up in the trace panel.
				tracePanel.showAlertsWithLevel(AlertLevel.MESSAGE);
				//================================================================================
			}
		});
		disableMessagesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This is how you make messages with a certain Level not show up in the trace panel.
				tracePanel.hideAlertsWithLevel(AlertLevel.MESSAGE);
				//================================================================================
			}
		});
		enableErrorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This is how you make messages with a level of ERROR show up in the trace panel.
				tracePanel.showAlertsWithLevel(AlertLevel.ERROR);
				//================================================================================
			}
		});
		disableErrorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This is how you make messages with a level of ERROR not show up in the trace panel.
				tracePanel.hideAlertsWithLevel(AlertLevel.ERROR);
				//================================================================================
			}
		});
		enableBankCustTagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This works the same way as AlertLevels, only you're using tags instead.
				//In this demo, I generate message with tag BANK_CUSTOMER when you click in the 
				//AnimationPanel somewhere.
				tracePanel.showAlertsWithTag(AlertTag.BANK_CUSTOMER);
				//================================================================================
			}
		});
		disableBankCustTagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//============================ TUTORIAL ==========================================
				//This works the same way as AlertLevels, only you're using tags instead.
				tracePanel.hideAlertsWithTag(AlertTag.BANK_CUSTOMER);
				//================================================================================
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
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(enableMessagesButton);
		this.add(disableMessagesButton);
		this.add(enableErrorButton);
		this.add(disableErrorButton);
		this.add(enableBankCustTagButton);
		this.add(disableBankCustTagButton);
		this.add(bankCustTagButton);
		this.setMinimumSize(new Dimension(50, 600));
	}
}
