package city.animationPanels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import city.gui.Gui;
import city.gui.SimCityGui;

public class HouseAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private BufferedImage bedImg = null;
	
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage flooringImg = null;
	private BufferedImage bedflooringImg = null;
	private BufferedImage kitchenfloor = null;
	private BufferedImage pooltable = null;
	private BufferedImage aptwall = null;
	private BufferedImage poolfloor = null;
	private BufferedImage sidewall = null;
	
	private final int ZERO = 0;
	
    static final int xCOOK_POSITION = 50;  
    static final int yCOOK_POSITION = 50;
    static final int yKITCHEN_COUNTER_OFFSET = 30;
    static final int yGRILL_RIGHT_OFFSET = 30;
    static final int xGRILL_RIGHT_OFFSET = 52;
    static final int yFIDGE_OFFSET = 15;
    static final int xFIDGE_OFFSET = 100;
    
    static final int xTable = 150;
    static final int yTable = 150;
    
    static final int xBed = 800;
    static final int yBed = 0;
	public HouseAnimationPanel(SimCityGui simCityGui)
	{
		this.simCityGui = simCityGui;
        try 
        {
			StringBuilder path = new StringBuilder("imgs/");
		    bedImg = ImageIO.read(new File(path.toString() + "classybed.png"));
		    flooringImg = ImageIO.read(new File(path.toString() + "flooring.png"));
		    bedflooringImg = ImageIO.read(new File(path.toString() + "bedhouseflooring.png"));
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    kitchenfloor = ImageIO.read(new File(path.toString() + "housekitchenfloor.png"));
		    pooltable = ImageIO.read(new File(path.toString() + "pooltable.png"));
		    aptwall = ImageIO.read(new File(path.toString() + "aptwall.png"));
		    poolfloor = ImageIO.read(new File(path.toString() + "poolfloor.png"));
		    sidewall = ImageIO.read(new File(path.toString() + "sidewall.png"));
		} 
        catch (IOException e){}
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
        simCityGui.animationPanel.timer.addActionListener(this);	
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{

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
         
         //draws floors
         
         for(int i = 0; i < 10; i ++)
        	 for(int j = 0; j < 8; j++)
        		 g.drawImage(flooringImg, 49*i, 100+49*j, null);
         for(int i = 0; i < 9; i ++)
        	 for(int j = 0; j < 10; j++)
        		 g.drawImage(bedflooringImg, 450+49*i, 49*j, null);
         
         
         //draws kitchen floor
         for(int i = 0; i < 3; i ++)
        	 for(int j = 0; j < 2; j++)
        		 g.drawImage(kitchenfloor, 75*i, 45*j, null);
         //draws kitchen wall
         g2.drawImage(aptwall, 250, -5, null);
         //draws pool table
         g.drawImage(poolfloor,260,-2,null);
         g.drawImage(pooltable, 312, 50, null);
         g2.drawImage(aptwall, 450, -5, null);
         //draw kitchen stuff
 		g.drawImage(kitchenCounterImg, xCOOK_POSITION, yCOOK_POSITION-yKITCHEN_COUNTER_OFFSET, null);
		g.drawImage(grillRightImg, xCOOK_POSITION+xGRILL_RIGHT_OFFSET, yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
		g.drawImage(fidgeImg, xCOOK_POSITION+xFIDGE_OFFSET, yCOOK_POSITION+yFIDGE_OFFSET, null);
 		 
         //dining room stuff
 		 g.drawImage(tableImg, xTable, yTable, null);
         
         //bedroom stuff
 		 for(int j = 0; j < 2; j++)
 			 g.drawImage(bedImg, 700,300*j, null);
 		 g.drawImage(sidewall, 630,250,null);
 		 g.drawImage(sidewall, 450,250,null);
 		 g.drawImage(aptwall, 450,250,null);
 		 
         
         synchronized(guis){
				for(Gui gui : getGuis()) {
		            if (gui.isPresent()) {
		                gui.draw(g2);
		            }
		        }
			}
	}
}
