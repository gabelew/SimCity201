package city.animationPanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import bank.gui.BankCustomerGui;
import city.gui.Gui;
import city.gui.SimCityGui;

public class BankAnimationPanel extends InsideAnimationPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	static final int NATMS = 9;
	static final int NATMS_ROWS = 3;
	static final int NATMS_COLUMNS = 3;
	static final int ATM_X_START = 300;
	static final int ATM_Y_START = 40;
	static final int ATM_X_GAP = 100;
	static final int ATM_Y_GAP = 90;
	public boolean isVisible = true;
	
	private SimCityGui simCityGui;
	private BufferedImage atmImg = null;
	
	public BankAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
			atmImg = ImageIO.read(new File(path.toString() + "atmMirror.png"));
		} catch (IOException e){
			
		}

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();	
    	
    	for(int i = 0; i < NATMS; i++) {
    		BankCustomerGui.atms.add(new Semaphore(1,true));
    	}
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		for(Gui gui : guis) {
			if (gui.isPresent()) {
				gui.updatePosition();
			}
		}
		if(isVisible)
			repaint();
		
	}
	@Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY);
		
		// draw ATMS
		for(int i = 0; i < NATMS/NATMS_ROWS; i++) {
			for(int j = 0; j < NATMS/NATMS_COLUMNS; j++) {
				g.drawImage(atmImg, i*ATM_X_GAP+ATM_X_START, j*ATM_Y_GAP+ATM_Y_START, null);
			} 
		}
		
		for(Gui gui : guis) {
			if(gui.isPresent()) {
				gui.draw(g2);;
			}
		}

		
	}
}
