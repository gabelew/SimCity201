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
	private long lastCheckAction = 0;
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel addListItemView = new JPanel();
    private JButton addPersonB = new JButton("Add");
    private JButton pauseAgentsB = new JButton("Pause");
    private JPanel buttonGroup = new JPanel();

    private MarketPanel marketPanel;
    private String type;
    
    private JTextField typeNameHere = new JTextField();
    private JCheckBox setStateCB;
    //private List<JCheckBox> stateCBList = new ArrayList<JCheckBox>();
    //private List<ListItem> listItems = new ArrayList<ListItem>();
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
       /* setStateCB = new JCheckBox();
        setStateCB.setVisible(true);
        setStateCB.addActionListener(this);
        if(type == "Customers")
        	setStateCB.setText("Hungry?");
        else if(type == "Waiters")
        	setStateCB.setText("Working?");
        setStateCB.setSelected(false);
        setStateCB.setEnabled(true);
        addListItemView.add(setStateCB, BorderLayout.EAST);
        
        
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
        
        if(this.type == "Tables")
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
        add(pane);*/
        
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
       
    }
}
