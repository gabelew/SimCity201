package city.animationPanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import city.gui.Gui;
import city.gui.SimCityGui;

public class ApartmentAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private SimCityGui simCityGui;
	
    static final int WALL_LENGTH = 875;
    static final int WALL_WIDTH = 10; 
	
	public ApartmentAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        simCityGui.animationPanel.timer.addActionListener(this);
		
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
        
         //Clear the screen by painting a rectangle the size of the frame
		 g2.setColor(getBackground());     
	     g2.fillRect(0, 0, WINDOWX, WINDOWY);
	     
	     //make a hallway
	     g2.setColor(Color.BLACK);
	     g2.fillRect(0, 150, WALL_LENGTH, WALL_WIDTH);
	     g2.fillRect(0, 300, WALL_LENGTH, WALL_WIDTH);
	     
	     //create rooms for each person. Total of 8 rooms in an apartment
	     for(int i = 0; i < 5; i++){
	     g2.fillRect(217*i, 0, WALL_WIDTH, 150);
	     g2.fillRect(217*i, 300, WALL_WIDTH, 150);
	     }

		
	}
}
