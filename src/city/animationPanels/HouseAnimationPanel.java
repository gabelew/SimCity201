package city.animationPanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import city.gui.Gui;
import city.gui.SimCityGui;

public class HouseAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private SimCityGui simCityGui;
	
	public HouseAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();	
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {

		for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
		
		if(insideBuildingPanel != null && insideBuildingPanel.isVisible)
			repaint();  //Will have paintComponent called
		
	}
	@Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

		 g2.setColor(Color.pink);     
	     g2.fillRect(20, 20, 100, 100);

		
	}
}
