package city.animationPanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import city.gui.Gui;
import city.gui.SimCityGui;

public class ApartmentAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private SimCityGui simCityGui;
	
	private static BufferedImage kitchen = null;
	private static BufferedImage fridge = null;
	private static BufferedImage table = null;
	private static BufferedImage bed = null;
    static final int WALL_LENGTH = 875;
    static final int WALL_WIDTH = 10; 
	
	public ApartmentAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        simCityGui.animationPanel.timer.addActionListener(this);
        
        try {
			StringBuilder path = new StringBuilder("imgs/");
			kitchen = ImageIO.read(new File(path.toString() + "kitchen.png"));
			fridge = ImageIO.read(new File(path.toString() + "fidge.png"));
			table = ImageIO.read(new File(path.toString() + "table.png"));
			bed = ImageIO.read(new File(path.toString() + "bed.png"));
			
		} catch (IOException e){
			
		}
		
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
	     
	     g2.setColor(Color.GRAY);
	     g2.fillRect(0, 160, WALL_LENGTH, 140);
	     
	     g2.setColor(Color.RED);
	     g2.fillRect(0, 200, WALL_LENGTH, 60);
	     
	     //create rooms for each person. Total of 8 rooms in an apartment
	     for(int i = 0; i < 5; i++){
		     g2.setColor(Color.BLACK);
	    	 g2.fillRect(217*i, 0, WALL_WIDTH, 150);
	    	 g2.fillRect(217*i, 300, WALL_WIDTH, 150);
	     }
	     
	     //creates a kitchen, fridge, table, and bed for each tenant
	     for(int i = 0; i < 5; i++){
	    	 //top 4
		     g2.drawImage(kitchen, 15+(i*217), 20, null);
		     g2.drawImage(fridge, 100+(i*217), 10, null);
		     g2.drawImage(table, 57+(i*217), 50, null);
		     g2.drawImage(bed, 150+(i*217), 70, null);
		     
		     //bottom 4
		     g2.drawImage(kitchen, 15+(i*217), 320, null);
		     g2.drawImage(fridge, 100+(i*217), 310, null);
		     g2.drawImage(table, 57+(i*217), 350, null);
		     g2.drawImage(bed, 150+(i*217), 370, null);

		 }
		
	}
}
