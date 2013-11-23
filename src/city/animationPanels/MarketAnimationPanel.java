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

public class MarketAnimationPanel extends InsideAnimationPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private BufferedImage shelfImg = null;
	private BufferedImage clerkTableImg = null;
	private BufferedImage chairImg=null;
	private BufferedImage deliveryStandImg=null;
    static final int xCLERK_POSITION = 130;  
    static final int yCLERK_POSITION = 250;
    static final int xSHELF_POSITION=100;
    static final int ySHELF_POSITION=120;
    static final int xSHELF2_POSITION=220;
    static final int ySHELF2_POSITION=270;
    static final int xCHAIR_POSITION=140;
    static final int yCHAIR_POSITION=370;
    static final int xDELIVERY_POSITION=80;
    static final int yDELIVERY_POSITION=200;
    static final int NUM_SHELF=6;
    static final int NUM_CHAIR=8;
    static final int ZERO=0;
	private SimCityGui simCityGui;

	
	public MarketAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    shelfImg = ImageIO.read(new File(path.toString() + "shelf3.png"));
		    clerkTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		    chairImg= ImageIO.read(new File(path.toString()+"customer_chair_v1.png"));
		    deliveryStandImg=ImageIO.read(new File(path.toString()+"host_stand.png"));
		} catch (IOException e) {
		}
		
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
        simCityGui.animationPanel.timer.addActionListener(this);	
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		for(Gui gui : getGuis()) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }


		if(insideBuildingPanel != null && insideBuildingPanel.isVisible){
			repaint();  //Will have paintComponent called
		}
	}
	@Override
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(getBackground());
        g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );
        g2.setColor(Color.lightGray);

	     //Clerk Stand
	     g.drawImage(clerkTableImg, xCLERK_POSITION, yCLERK_POSITION, null);
	     //Delivery Man Area
	     g.drawImage(deliveryStandImg,xDELIVERY_POSITION,yDELIVERY_POSITION,null);
	     //first row of shelves
	     for (int i=ZERO;i<NUM_SHELF;i++){
	    	 g.drawImage(shelfImg, xSHELF_POSITION+i*50, ySHELF_POSITION-i*20, null);
	     }
	     //second row of shelves
	     for(int i=ZERO;i<NUM_SHELF;i++){
	    	 g.drawImage(shelfImg, xSHELF2_POSITION+i*50, ySHELF2_POSITION-i*20, null); 
	     }
	     //chiar waiting area
	     for (int i=ZERO;i<NUM_CHAIR;i++){
	    	 g.drawImage(chairImg, xCHAIR_POSITION+i*50, yCHAIR_POSITION, null); 
	     }
	     
		for(Gui gui : getGuis()) {
			if (gui.isPresent()) {
				gui.draw(g2);
	        }
		}
	}

}
