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
	
	private static BufferedImage kitchen = null;
	private static BufferedImage grill = null;
	private static BufferedImage fridge = null;
	private static BufferedImage table = null;
	private static BufferedImage bed = null;
	private static BufferedImage rcarpet = null;
	private static BufferedImage flooring = null;
	private static BufferedImage aptwall = null;
    static final int WALL_LENGTH = 875;
    static final int WALL_WIDTH = 10; 
    //variables for kitchen
    static final int xCOOK_POSITION = 20;  
    static final int yCOOK_POSITION = 30;
    static final int yKITCHEN_COUNTER_OFFSET = 30;
    static final int yGRILL_RIGHT_OFFSET = 30;
    static final int xGRILL_RIGHT_OFFSET = 52;
    static final int yFIDGE_OFFSET = 15;
    static final int xFIDGE_OFFSET = 100;
    static final int xAPT_OFFSET = 30;
    
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
			grill = ImageIO.read(new File(path.toString() + "grill2.png"));
			rcarpet = ImageIO.read(new File(path.toString() + "aptcarpet.jpg"));
			flooring = ImageIO.read(new File(path.toString() + "flooring.png"));
			aptwall = ImageIO.read(new File(path.toString() + "aptwall.png"));
		} catch (IOException e){
			
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		synchronized(guis){
			for(Gui gui : getGuis()) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
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
	     
	     
	     g2.setColor(Color.GRAY);
	     g2.fillRect(0, 160, WALL_LENGTH, 140);
	     
	     //draws red carpet
	     for(int i = 0; i < 8; i++)
	    	 for(int j = 0; j < 2; j++)
	    		 g2.drawImage(rcarpet, 100*i, 150+ j*75, null);
	     //draws wooden floors
	     for(int i = 0; i < 8; i++)
	    	 for(int j = 0; j < 3; j++)
	    		 g2.drawImage(flooring, 120*i, j*48, null);
	     
	     for(int i = 0; i < 8; i++)
	    	 for(int j = 0; j < 4; j++)
	    		 g2.drawImage(flooring, 120*i, 300+j*53, null);
	     
	     //create rooms for each person. Total of 8 rooms in an apartment
	     for(int i = 0; i < 5; i++){
	    	 g2.drawImage(aptwall, (i*217), 0, null);
	    	 g2.drawImage(aptwall, (i*217), 300, null);
	    	 g2.drawImage(aptwall, (i*217), 400, null);
	     }
	     
	     
	     //creates a kitchen, fridge, table, and bed for each tenant
	     for(int i = 0; i < 5; i++){
	    	 //top 4
	    	 g2.drawImage(kitchen, xCOOK_POSITION+(i*217), yCOOK_POSITION-yKITCHEN_COUNTER_OFFSET, null);
	 		 g2.drawImage(grill, xCOOK_POSITION+xGRILL_RIGHT_OFFSET+(i*217), yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
	 		 g2.drawImage(fridge, xCOOK_POSITION+xFIDGE_OFFSET+(i*217), yCOOK_POSITION+yFIDGE_OFFSET, null);
		     g2.drawImage(table, 57+(i*217), 70, null);
		     g2.drawImage(bed, 150+(i*217), 100, null);
		     
		     //bottom 4
		     g2.drawImage(kitchen, xAPT_OFFSET + xCOOK_POSITION+(i*217),310+ yCOOK_POSITION-yKITCHEN_COUNTER_OFFSET, null);
	 		 g2.drawImage(grill, xAPT_OFFSET + xCOOK_POSITION+xGRILL_RIGHT_OFFSET+(i*217), 310+yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
	 		 g2.drawImage(fridge, xAPT_OFFSET + xCOOK_POSITION+xFIDGE_OFFSET+(i*217),310+ yCOOK_POSITION+yFIDGE_OFFSET, null);
		     //g2.drawImage(grill, (i*217) ,320,null);
		     //g2.drawImage(kitchen, 15+(i*217), 320, null);
		     //g2.drawImage(fridge, 100+(i*217), 310, null);
		     g2.drawImage(table, 57+(i*217), 370, null);
		     g2.drawImage(bed, 150+(i*217), 420, null);

		 }
	     
	     synchronized(guis){
				for(Gui gui : getGuis()) {
		            if (gui.isPresent()) {
		                gui.draw(g2);
		            }
		        }
			}
	}
}
