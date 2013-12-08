package GHRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import city.animationPanels.InsideAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class GHAnimationPanel extends InsideAnimationPanel implements ActionListener {

    private Image bufferImage;
    private Dimension bufferSize;
    static final int TABLE1XLOCATION = 200, TABLE1YLOCATION = 250;
    static final int TABLE2XLOCATION = 200, TABLE2YLOCATION = 150;
    static final int TABLE3XLOCATION = 200, TABLE3YLOCATION = 50;
    static final int TABLESIZE = 50;
    
    private List<Gui> guis = new ArrayList<Gui>();
    
	private BufferedImage tableImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;

    public GHAnimationPanel(SimCityGui gui) {
    	this.simCityGui = gui;
    	
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		} catch (IOException e) {
		}
    	
    	
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


        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the tables
        //g2.setColor(Color.ORANGE);
        g2.drawImage(tableImg,TABLE1XLOCATION,TABLE1YLOCATION,null);
        
        //g3.setColor(Color.ORANGE);
        g2.drawImage(tableImg,TABLE2XLOCATION,TABLE2YLOCATION,null);
        
        //g4.setColor(Color.ORANGE);
        g2.drawImage(tableImg,TABLE3XLOCATION,TABLE3YLOCATION,null);

        //g2.setColor(Color.BLACK);
        g2.drawImage(platingTableImg,350,150,null);
        g2.drawImage(platingTableImg,350,165,null);
        g2.drawImage(platingTableImg,350,180,null);
        g2.drawImage(platingTableImg,350,195,null);
        g2.drawImage(platingTableImg,350,210,null);
        g2.drawImage(platingTableImg,350,225,null);
        g2.drawImage(platingTableImg,350,240,null);
        g2.drawImage(platingTableImg,350,255,null);

        
        //g2.setColor(Color.DARK_GRAY);
        g2.drawImage(grillRightImg,450,150,null);
        
        
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
