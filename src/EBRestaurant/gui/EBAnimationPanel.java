package EBRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import CMRestaurant.gui.CMCashierGui;
import CMRestaurant.gui.CMCookGui;
import CMRestaurant.gui.CMCustomerGui;
import CMRestaurant.gui.CMWaiterGui;
import city.animationPanels.InsideAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class EBAnimationPanel extends InsideAnimationPanel implements ActionListener {

    private final int RectX=100;
    private final int Rect1Y=100;
    private final int Rect2Y=200;
    private final int Rect3Y=300;
    private final int RectWH=45;
    private final int RectPos=0;
    private final int time=15;
    private Dimension bufferSize;
    private List<Gui> guis = new ArrayList<Gui>();
    
    private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage chairImg = null;
	private BufferedImage hostStandImg = null;
	private BufferedImage registerImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;

    public EBAnimationPanel(SimCityGui simCityGui) {
    	this.simCityGui = simCityGui;
    	try {
			StringBuilder path = new StringBuilder("imgs/");
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    chairImg = ImageIO.read(new File(path.toString() + "customer_chair_v1.png"));
		    hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		} catch (IOException e) {
		}
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        bufferSize = this.getSize();
        simCityGui.animationPanel.timer.addActionListener(this);
    	Timer timer = new Timer(time, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		synchronized(getGuis()){
			for(Gui gui : getGuis()) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
    	}
		if(insideBuildingPanel != null && insideBuildingPanel.isVisible)
			repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(RectPos, RectPos, WINDOWX, WINDOWY );
        //Here is the table
        g2.setColor(Color.ORANGE);
        g.drawImage(tableImg, RectX, Rect1Y, null);
        g.drawImage(tableImg, RectX, Rect2Y, null);
        g.drawImage(tableImg, RectX, Rect3Y, null);
        g.drawImage(grillRightImg, 340, 190, null);
        g.drawImage(fidgeImg, 390, 210, null);
        g.drawImage(kitchenCounterImg,280,200,null);
        g.drawImage(hostStandImg,40,100,null);
        g.drawImage(registerImg,40,400,null);
        g.drawImage(kitchenCounterImg,280,200,null);
        for(int i = 0; i<4; i++){
  			g.drawImage(platingTableImg, 300+17*i, 280+8*i, null);
      	}
        //g2.fillRect(300, 230, 20, 100);
        //g2.fillRect(300, 200, 80, 20);

        for(Gui gui : getGuis()) {
        	if(!(gui instanceof EBCustomerGui || gui instanceof EBWaiterGui))
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }

        for(Gui gui : getGuis()) {
        	if(gui instanceof EBCustomerGui || gui instanceof EBWaiterGui)
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
       for(Gui gui : getGuis()) {
        	if(gui instanceof EBCashierGui)
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
    }

    public void addGui(EBCustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(EBHostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(EBWaiterGui gui){
    	guis.add(gui);
    }
    
    public void addGui(EBCookGui gui){
    	guis.add(gui);
    }

    
}
