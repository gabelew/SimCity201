package GCRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import city.animationPanels.InsideAnimationPanel;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class GCAnimationPanel extends InsideAnimationPanel implements ActionListener {

	private final int N_TABLES = 4;
    private final int TABLEX_START = 100;
    private final int TABLEY_START = 175;
    private final int xHostStand = 70;
    private final int yHostStand = 20;
    private final int xRegister = 100;
    private final int yRegister = 40;
    private final int TABLE_SPACING = 100;
    private final int GRILL_SIZEX = 300;
    private final int GRILL_SIZEY = 20;
    private final int xGRILL = 205;
    private final int yGRILL = -2;
    private final int xFRIDGE = 265; 
    private final int yFRIDGE = 23;
    private BufferedImage fridgeImg;
    private BufferedImage grillImg;
    private BufferedImage tableImg;
    private BufferedImage hostStandImg;
    private BufferedImage registerImg;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();
    SimCityGui gui;
    
    public GCAnimationPanel(SimCityGui g) {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    	this.gui = g;
    	try 
    	{
 			StringBuilder path = new StringBuilder("imgs/");
 			fridgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
 			grillImg = ImageIO.read(new File(path.toString() + "grill2.png"));
 			tableImg = ImageIO.read(new File(path.toString() + "table.png"));
 			hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
    	}
    	catch(Exception e){}
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
	
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Borders for waiting area
        //g2.setColor(Color.BLACK);
        //g2.fillRect(80,40,5,50);
        //g2.fillRect(0,100,40,5);
        
        //makes plating
        g2.setColor(Color.lightGray);
        int x = 200;
        int y = 75;
        g2.fillRect(x, y, GRILL_SIZEX, GRILL_SIZEY);
        
        
        
        //making cooking area
        //g2.setColor(Color.BLACK);
        //g2.fillRect(200, 0, GRILL_SIZEX, GRILL_SIZEY);
        //g2.setColor(Color.RED);
        //g2.fillRect(210, 6, GRILL_SIZEX-25, 7);
        //makes fridge
        g2.setColor(Color.GRAY);
        g2.fillRect(WINDOWX-10, GRILL_SIZEY, 10, 55);
        //draws fridge
        g2.drawImage(fridgeImg, xFRIDGE, yFRIDGE, null);
        //draws grill
        g2.drawImage(grillImg, xGRILL, yGRILL, null);
        //draws host stand
        g2.drawImage(hostStandImg, xHostStand, yHostStand, null);
        //draws register for cashier
        g2.drawImage(registerImg, xRegister, yRegister, null);
        //Here is the table
        g2.setColor(Color.RED);
        for(int i = 0; i < N_TABLES; i++)
        {
        	//g2.fillRect(TABLEX_START+ (i*TABLE_SPACING), TABLEY_START, CUST_SIZE, CUST_SIZE);
        	g2.drawImage(tableImg, TABLEX_START+ (i*TABLE_SPACING), TABLEY_START, null);
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(Gui g)
    {
    	guis.add(g);
    }
}
