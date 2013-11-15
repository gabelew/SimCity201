package city.animationPanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import city.gui.SimCityGui;

public class MarketAnimationPanel extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private SimCityGui simCityGui;
	
	public MarketAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();	
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

		 g2.setColor(Color.MAGENTA);     
	     g2.fillRect(20, 20, 100, 100);

		
	}

}
