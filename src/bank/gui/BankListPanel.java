package bank.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import city.gui.trace.AlertLog;
import city.gui.trace.AlertTag;

/**
 * Subpanel of BankPanel
 * @author Gabriel
 *
 */
@SuppressWarnings("serial")
public class BankListPanel extends JPanel implements ActionListener{
	private BankPanel bankPanel;
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
	
	public BankListPanel(BankPanel bp, String type) {
		bankPanel = bp;
		this.type = type;
		
		setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
	    add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
	    
	    view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
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
