package city.animationPanels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import CMRestaurant.gui.CMRestaurantPanel;
import bank.gui.BankCustomerGui;
import bank.gui.BankPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

public class BankAnimationPanel extends InsideAnimationPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	static final int NATMS = 16;
	static final int NATMS_ROWS = 4;
	static final int NATMS_COLUMNS = 4;
	static final int ATM_X_START = 200;
	static final int ATM_Y_START = 40;
	static final int ATM_X_GAP = 100;
	static final int ATM_Y_GAP = 90;
	
	private BufferedImage atmImg = null;
	private BufferedImage fountainImg = null;
	private BufferedImage bankFlooring = null;
	private BufferedImage bankStatue = null;
	private BufferedImage bankStatueL = null;
	public List<Semaphore> atms = new CopyOnWriteArrayList<Semaphore>();
	
	public BankAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
			atmImg = ImageIO.read(new File(path.toString() + "atmMirror.png"));
			fountainImg = ImageIO.read(new File(path.toString() + "fountain.png"));
			bankFlooring = ImageIO.read(new File(path.toString() + "bankfloor.png"));
			bankStatue = ImageIO.read(new File(path.toString() + "dragon.png"));
			bankStatueL = ImageIO.read(new File(path.toString() + "dragonL.png"));
		} catch (IOException e){
			
		}

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        

        simCityGui.animationPanel.timer.addActionListener(this);
    	
    	for(int i = 0; i < NATMS; i++) {
    		atms.add(new Semaphore(1,true));
    	}
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		synchronized(guis) {
			for(Gui gui : getGuis()) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
		}
		if(insideBuildingPanel != null && insideBuildingPanel.isVisible)
			repaint();  //Will have paintComponent called
		
	}
	
	public void addCustomerToList(String name){
		((BankPanel) insideBuildingPanel.guiInteractionPanel).addCustomerToList(name);
	}
	public void removeCustomerFromList(String name){
		((BankPanel) insideBuildingPanel.guiInteractionPanel).removeCustomerFromList(name);
	}
	
	@Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY);
		
        for(int i = 0; i < 15; i ++)
          	 for(int j = 0; j < 14; j++)
          		 g.drawImage(bankFlooring, 80*i, 80*j, null);
        
		// draw ATMS
		for(int i = 0; i < NATMS/NATMS_ROWS; i++) {
			for(int j = 0; j < NATMS/NATMS_COLUMNS; j++) {
				g.drawImage(atmImg, i*ATM_X_GAP+ATM_X_START, j*ATM_Y_GAP+ATM_Y_START, null);
			} 
		}
		for(int i = 0; i < 3; i++)
		{
			g.drawImage(bankStatueL, 120 - 50*i,350,null);
			g.drawImage(bankStatue, 750 - 50*i,0,null);
			g.drawImage(bankStatue, 750 - 50*i,350,null);
		}
		g.drawImage(fountainImg, 340, 140, null);
		
		synchronized(guis) {
			for(Gui gui : getGuis()) {
				if(gui.isPresent()) {
					gui.draw(g2);;
				}
			}
		}

		
	}
}
