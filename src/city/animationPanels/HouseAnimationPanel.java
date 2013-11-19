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

public class HouseAnimationPanel  extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private BufferedImage fridgeImg = null;
	private BufferedImage stoveImg = null;
	private BufferedImage sinkImg = null;
	private BufferedImage bedImg = null;
	private BufferedImage couchImg = null;
	private BufferedImage tvImg = null;
	
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage chairImg = null;
	private BufferedImage hostStandImg = null;
	private BufferedImage registerImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;
	
	private final int ZERO = 0;
	private SimCityGui simCityGui;
	
    static final int xCOOK_POSITION = 340;  
    static final int yCOOK_POSITION = 50;
    
    static final int yGRILL_RIGHT_OFFSET = 30;
    static final int xGRILL_RIGHT_OFFSET = 52;
    static final int yFIDGE_OFFSET = 15;
    static final int xFIDGE_OFFSET = 100;
    
	public HouseAnimationPanel(SimCityGui simCityGui)
	{
		this.simCityGui = simCityGui;
        try 
        {
			StringBuilder path = new StringBuilder("imgs/");
		    //fridgeImg = ImageIO.read(new File(path.toString() + "fridge.png"));
		    stoveImg = ImageIO.read(new File(path.toString() + "stove.png"));
		    sinkImg = ImageIO.read(new File(path.toString() + "sink.png"));
		    bedImg = ImageIO.read(new File(path.toString() + "bed.png"));
		    couchImg = ImageIO.read(new File(path.toString() + "couch.png"));
		    tvImg = ImageIO.read(new File(path.toString() + "tv.png"));
		    //tableImg = ImageIO.read(new File(path.toString() + "house_table.png"));
		    
		    //StringBuilder path = new StringBuilder("imgs/");
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    chairImg = ImageIO.read(new File(path.toString() + "customer_chair_v1.png"));
		    hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		} 
        catch (IOException e){}
		
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
        simCityGui.animationPanel.timer.addActionListener(this);	
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{

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
         g2.setColor(getBackground());
         g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );
         g2.setColor(Color.lightGray);
         
         
      // draw kitchen
 		g.drawImage(kitchenCounterImg, xCOOK_POSITION, yCOOK_POSITION, null);
 		//g.drawImage(grillLeftImg, xCOOK_POSITION, yCOOK_POSITION-ySALAD_BAR_OFFSET, null);
 		g.drawImage(grillRightImg, xCOOK_POSITION+xGRILL_RIGHT_OFFSET, yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
 		g.drawImage(fidgeImg, xCOOK_POSITION+xFIDGE_OFFSET, yCOOK_POSITION+yFIDGE_OFFSET, null);
         //kitchen stuff
         //g.drawImage(fridgeImg, 75,-10, null);
         //g.drawImage(stoveImg, 150,0, null);
         //g.drawImage(sinkImg, 0,0, null);
         
         //dining room stuff
         g.drawImage(tableImg, 100, 200,null);
         
         //bedroom stuff
         g.drawImage(bedImg, 800,0, null);
         g.drawImage(bedImg, 800,150, null);
         
         //living room stuff
         for(int i = 0; i < 2; i++)
        	 g.drawImage(couchImg, 600+ 100*i,300, null);
         g.drawImage(tvImg, 600, 400, null);
		
	}
}
