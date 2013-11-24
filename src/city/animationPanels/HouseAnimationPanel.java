package city.animationPanels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import city.gui.Gui;
import city.gui.SimCityGui;

public class HouseAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private BufferedImage bedImg = null;
	private BufferedImage couchImg = null;
	private BufferedImage tvImg = null;
	
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage flooringImg = null;
	private final int ZERO = 0;
	private SimCityGui simCityGui;
	
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
		    bedImg = ImageIO.read(new File(path.toString() + "bed.png"));
		    couchImg = ImageIO.read(new File(path.toString() + "couch.png"));
		    tvImg = ImageIO.read(new File(path.toString() + "tv.png"));
		    //tableImg = ImageIO.read(new File(path.toString() + "house_table.png"));
		    flooringImg = ImageIO.read(new File(path.toString() + "flooring.png"));
		    //StringBuilder path = new StringBuilder("imgs/");
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
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
         g2.setColor(getBackground());
         g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );
         for(int i = 0; i < 8; i ++)
        	 for(int j = 0; j < 10; j++)
        		 g.drawImage(flooringImg, 115*i, 50*j, null);
         //draw kitchen stuff
 		g.drawImage(kitchenCounterImg, xCOOK_POSITION, yCOOK_POSITION-yKITCHEN_COUNTER_OFFSET, null);
		g.drawImage(grillRightImg, xCOOK_POSITION+xGRILL_RIGHT_OFFSET, yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
		g.drawImage(fidgeImg, xCOOK_POSITION+xFIDGE_OFFSET, yCOOK_POSITION+yFIDGE_OFFSET, null);
 		 
         //dining room stuff
 		 g.drawImage(tableImg, xTable, yTable, null);
         
         //bedroom stuff
 		 for(int j = 0; j < 2; j++)
 			 g.drawImage(bedImg, 800,150*j, null);
 		 
         //living room stuff
         for(int i = 0; i < 2; i++)
        	 g.drawImage(couchImg, 600+ 100*i,300, null);
         g.drawImage(tvImg, 600, 400, null);
         
         synchronized(guis){
				for(Gui gui : getGuis()) {
		            if (gui.isPresent()) {
		                gui.draw(g2);
		            }
		        }
			}
	}
}
