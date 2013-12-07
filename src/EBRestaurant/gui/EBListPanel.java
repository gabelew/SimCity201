package EBRestaurant.gui;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class EBListPanel extends JPanel implements ActionListener {

    /*public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);*/
    public JScrollPane paneWaiter =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    static final int PaneX=170;
    static final int PaneY=100;
    static final int textbox=15;
    private JPanel view = new JPanel();
    private JPanel viewWaiter = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private List<JButton> listWaiter=new ArrayList<JButton>();
    //private JButton addMarket = new JButton("Add Market");
    private JButton addPersonB = new JButton("Add");
    //private JButton addPersonA = new JButton("Add");
    //private JButton pauseButton = new JButton("Pause");
    private JTextField NameField;
    //private JTextField WaiterField;
    private EBRestaurantPanel restPanel;
    private String type;
    private JLabel DisplayHungry;
    private JLabel waiters;
    private int decreaseButtonWidth=20;
    private int decreaseButtonHeight=7;
    //boolean pause=false;
    private String amount="0";

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public EBListPanel(EBRestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new FlowLayout());
        addPersonB.addActionListener(this);
        waiters= new JLabel("Waiters");

        view.setLayout(new BoxLayout(view, BoxLayout.PAGE_AXIS));
        viewWaiter.setLayout(new BoxLayout(viewWaiter, BoxLayout.PAGE_AXIS));
        paneWaiter.setViewportView(viewWaiter);
        paneWaiter.setPreferredSize(new Dimension(PaneX,PaneY));
        add(waiters);
        add(paneWaiter);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            addPerson(NameField.getText());
        }
        /*else if (e.getSource()==addPersonA){
        	addWaiter(WaiterField.getText());
        }*/
        /*else if (e.getSource()==pauseButton){
        	if(pause==false){
        		pause=true;
        	}
        	else if (pause==true){
        		pause=false;
        	}
        	restPanel.pause(pause);
        }*/
        /*else if (e.getSource()==addMarket){
        	numMarket=numMarket+1;
        	JTextField steakField = new JTextField(5);
            JTextField chickenField = new JTextField(5);
            JTextField saladField = new JTextField(5);
            JTextField pizzaField = new JTextField(5);
        	 JPanel myPanel = new JPanel();
             myPanel.add(new JLabel("Steak:"));
             myPanel.add(steakField);
             myPanel.add(Box.createHorizontalStrut(15)); // a spacer
             myPanel.add(new JLabel("Chicken:"));
             myPanel.add(chickenField);
             myPanel.add(new JLabel("Salad:"));
             myPanel.add(saladField);
             myPanel.add(Box.createHorizontalStrut(15)); // a spacer
             myPanel.add(new JLabel("Pizza:"));
             myPanel.add(pizzaField);
        	int result=JOptionPane.showConfirmDialog(null, myPanel, 
                    "Please Enter Inventory Values", JOptionPane.OK_CANCEL_OPTION);
        	if (result==JOptionPane.OK_OPTION)
        	{
                String steak=steakField.getText();
                String chicken=chickenField.getText();
                String salad=saladField.getText();
                String pizza=pizzaField.getText();
        		restPanel.addMarket("market"+numMarket,steak,chicken,salad,pizza);
        	}
        }*/
        else {
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo("Customers", temp.getText());
            }
        	for (JButton temp:listWaiter){
                if (e.getSource() == temp)
                    restPanel.showInfo("waiters", temp.getText());
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
            /*JTextField moneyField = new JTextField(5);
            JCheckBox respBox = new JCheckBox();
            JLabel responsible = new JLabel("Responsible?");
            JPanel moneyPanel = new JPanel();
            moneyPanel.add(new JLabel("Amount: "));
            moneyPanel.add(moneyField);
            moneyPanel.add(responsible);
            moneyPanel.add(respBox);
            int result=JOptionPane.showConfirmDialog(null, moneyPanel, 
                    "Please enter amount of money", JOptionPane.OK_CANCEL_OPTION);
        	if (result==JOptionPane.OK_OPTION)
        	{
               amount=moneyField.getText();
               if(respBox.isSelected())
            	   response=true;
               else
            	   response=false;
        	}
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - decreaseButtonWidth,
                    (int) (paneSize.height / decreaseButtonHeight));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson("Customers", name, StartedHungry.isSelected(),amount,response);//puts customer on list
            restPanel.showInfo("Customers", name);//puts hungry button on panel
            validate();*/
        }
    }
    public void addWaiter(String name) {
        if (name != null) {
        	this.type="waiters";
            JButton buttonWaiter = new JButton(name);
            buttonWaiter.setBackground(Color.white);

            Dimension paneSize = paneWaiter.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - decreaseButtonWidth,
                    (int) (paneSize.height / decreaseButtonHeight));
            buttonWaiter.setPreferredSize(buttonSize);
            buttonWaiter.setMinimumSize(buttonSize);
            buttonWaiter.setMaximumSize(buttonSize);
            buttonWaiter.addActionListener(this);
            listWaiter.add(buttonWaiter);
            viewWaiter.add(buttonWaiter);
        	restPanel.addPerson("Waiters", name, false,amount,false);
        	restPanel.showInfo("waiters", name);
        	validate();
        }
    }
    
}
