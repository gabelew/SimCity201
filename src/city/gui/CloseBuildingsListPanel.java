package city.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import city.gui.ListPanel.ListItem;

public class CloseBuildingsListPanel extends JPanel implements ActionListener  {
	private static final long serialVersionUID = 1L;
	
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private InfoPanel infoPanel;
    private List<ListItem> listItems = new ArrayList<ListItem>();
    private String type;
	private long lastCheckAction = 0;
	
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
    
	class ListItem{
		JLabel label;
		JCheckBox stateCB;
		int buildingNumber;
		ListItem(JLabel label, JCheckBox c, int i){
			this.label = label;
			stateCB = c;
			buildingNumber = i;
		}
	}
	
	
	public CloseBuildingsListPanel(InfoPanel infoPanel, String type) {
        this.infoPanel = infoPanel;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
      
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
        
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
    	if(lastCheckAction != e.getWhen())
    	{
    		lastCheckAction = e.getWhen();
        	for (ListItem temp:listItems){
                if (e.getSource() == temp.stateCB)
                {
                	if(temp.stateCB.getText() == "Close?")
            		{
            			temp.stateCB.setText("Open?");
                    	temp.stateCB.setEnabled(true);
                    	temp.stateCB.setSelected(false);
            			infoPanel.gui.setClosed(temp.stateCB.getName(), temp.buildingNumber);
            		}
            		else{
            			temp.stateCB.setText("Close?");
                    	temp.stateCB.setEnabled(true);
                    	temp.stateCB.setSelected(false);
                    	infoPanel.gui.setOpen(temp.stateCB.getName(), temp.buildingNumber);
            		}
                }
            }
        }
	}


    public void addBuilding(String name, int i) {
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
            newstateCB.setVisible(true);
        	newstateCB.setText("Close?");
            newstateCB.setSelected(false);
            
            JLabel label = new JLabel(name);
            label.setBackground(Color.white);
            label.setForeground(Color.black);

            Dimension labelSize = new Dimension(paneSize.width - LABEL_SIZE_OFFSET,
                    (int) (paneSize.height / LIST_ITEM_H));
            label.setPreferredSize(labelSize);
            label.setMinimumSize(labelSize);
            label.setMaximumSize(labelSize);
            newstateCB.setName(name);
            addNewCustView.add(newstateCB, BorderLayout.EAST);
            newstateCB.addActionListener(this);
            
            listItems.add(new ListItem(label, newstateCB,i));
            addNewCustView.add(label);
            view.add(addNewCustView);

            validate();            
            
    	}
    }
}
