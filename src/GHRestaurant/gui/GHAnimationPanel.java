package GHRestaurant.gui;

import javax.swing.*;

import city.animationPanels.InsideAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class GHAnimationPanel extends InsideAnimationPanel implements ActionListener {

    private Image bufferImage;
    private Dimension bufferSize;
    static final int G2XLOCATION = 200, G2YLOCATION = 250;
    static final int G3XLOCATION = 200, G3YLOCATION = 150;
    static final int G4XLOCATION = 200, G4YLOCATION = 50;
    static final int TABLESIZE = 50;
    
    private List<Gui> guis = new ArrayList<Gui>();

    public GHAnimationPanel(SimCityGui gui) {
    	this.simCityGui = gui;
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
        simCityGui.animationPanel.timer.addActionListener(this);

    	//Timer timer = new Timer(20, this );
    	//timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D g3 = (Graphics2D)g;
        Graphics2D g4 = (Graphics2D)g;
        Graphics2D g5 = (Graphics2D)g;
        Graphics2D g6 = (Graphics2D)g;


        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the tables
        g2.setColor(Color.ORANGE);
        g2.fillRect(G2XLOCATION, G2YLOCATION, TABLESIZE, TABLESIZE);//200 and 250 need to be table params

        g3.setColor(Color.ORANGE);
        g3.fillRect(G3XLOCATION, G3YLOCATION, TABLESIZE, TABLESIZE);//200 and 150 need to be table params

        g4.setColor(Color.ORANGE);
        g4.fillRect(G4XLOCATION, G4YLOCATION, TABLESIZE, TABLESIZE);//200 and 50 need to be table params

        g5.setColor(Color.BLACK);
        g5.fillRect(350,150 ,15 ,100 );
        
        g6.setColor(Color.DARK_GRAY);
        g6.fillRect(400,150 ,30 ,30 );
        
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
                //gui.draw(g3);
                //gui.draw(g4);
            }
        }
    }

    public void addGui(GHCustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(GHHostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(GHWaiterGui gui){
    	guis.add(gui);
    }
    
    public void addGui(GHCookGui gui){
    	guis.add(gui);
    }
}
