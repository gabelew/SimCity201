package market.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class MarketListPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel addListItemView = new JPanel();

    private MarketPanel marketPanel;
    private String type;
    
    private JTextField typeNameHere = new JTextField();
  
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
    public MarketListPanel(MarketPanel mp, String type) {
        marketPanel = mp;
        this.type = type;
        
        setLayout(new BorderLayout(5,5));
        //add(typeNameHere);
        //add(new JLabel("<html><pre>" + type + "<br></pre></html>"));
        JLabel labels = new JLabel(type+": ");
        addListItemView.setLayout(new BorderLayout(LIST_ITEM_VIEW_GAP, LIST_ITEM_VIEW_GAP));
        Dimension addCustViewSize = new Dimension(LIST_ITEM_VIEW_X, LIST_ITEM_VIEW_Y);
        addListItemView.setPreferredSize(addCustViewSize);
        addListItemView.setMinimumSize(addCustViewSize);
        addListItemView.setMaximumSize(addCustViewSize);      

        addListItemView.add(typeNameHere,BorderLayout.CENTER);
        addListItemView.add(labels,BorderLayout.LINE_END);
        
        add(addListItemView);
      
        
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
       
    }
}
