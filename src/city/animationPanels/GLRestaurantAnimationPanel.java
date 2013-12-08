package city.animationPanels;

import javax.imageio.ImageIO;
import javax.swing.*;

import GLRestaurant.gui.GLCashierGui;
import GLRestaurant.gui.GLCookGui;
import GLRestaurant.gui.GLCustomerGui;
import GLRestaurant.gui.GLWaiterGui;
import GLRestaurant.gui.GLRestaurantPanel;
import GLRestaurant.roles.GLHostRole;
import restaurant.Restaurant;
import restaurant.gui.Table;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@SuppressWarnings("serial")
public class GLRestaurantAnimationPanel extends InsideAnimationPanel implements ActionListener{

    private List<Table> tables = new ArrayList<Table>();

//	public List<Semaphore> waitingSeatsWaiter = new ArrayList<Semaphore>();
//	public List<Semaphore> waitingSeats = new ArrayList<Semaphore>();
	
	public final int TABLE1X = 100;
	public final int TABLE2X = 200;
	public final int TABLE3X = 300;
	public final int TABLEY = 100;
	public final int CHAIR_OFFSET = 22;
	public static final int ZERO = 0;
	
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage chairImg = null;
	private BufferedImage hostStandImg = null;
	private BufferedImage registerImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;

	public GLRestaurantAnimationPanel(SimCityGui simCityGui){
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
 

        simCityGui.animationPanel.timer.addActionListener(this);
		
//    	for(int i = 0; i < NWAITSEATS; i++){
//			waitingSeats.add(new Semaphore(1,true));
//		}
//    	for(int i = 0; i < NRESTSEATS; i++){
//			waitingSeatsWaiter.add(new Semaphore(1,true));
//		}	
		
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
        g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );

      //draw chairs
        g.drawImage(chairImg, TABLE1X+CHAIR_OFFSET, TABLEY, null);
        g.drawImage(chairImg, TABLE2X+CHAIR_OFFSET, TABLEY, null);
        g.drawImage(chairImg, TABLE3X+CHAIR_OFFSET, TABLEY, null);
        
        //draw table area
        g.drawImage(tableImg, TABLE1X, TABLEY, null);
        g.drawImage(tableImg, TABLE2X, TABLEY, null);
        g.drawImage(tableImg, TABLE3X, TABLEY, null);
        

        
        
		//draw Host Stand
    	g.drawImage(hostStandImg, 50, 89, null);

        // draw kitchen
		g.drawImage(kitchenCounterImg, 500, 120, null);
		g.drawImage(grillRightImg, 560,120, null);
		g.drawImage(fidgeImg, 630, 160, null);
		g.drawImage(platingTableImg, 460, 160, null);
        g.drawImage(platingTableImg, 460, 190, null);
        g.drawImage(platingTableImg, 460, 220, null);
        
        //draw Register Stand
    	g.drawImage(registerImg, 70, 200, null);

    	synchronized(getGuis()){
	    	for(Gui gui : getGuis()) {
	        	if (gui.isPresent()) {
	        		gui.updatePosition();
		        }
	        }
    	}
    	
    	synchronized(getGuis()){
	    	for(Gui gui : getGuis()) {
	        	if (gui.isPresent()) {
	        		gui.draw(g2);
		        }
	        }
    	}

    }	
	
	public void addWaiterToList(String name){
		((GLRestaurantPanel) insideBuildingPanel.guiInteractionPanel).addWaiterToList(name);
	}
	public void removeWaiterFromList(String name){
		((GLRestaurantPanel) insideBuildingPanel.guiInteractionPanel).removeWaiterFromList(name);
	}

	
}