package city.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import restaurant.gui.CashierGui;
import restaurant.gui.CustomerGui;
import restaurant.gui.WaiterGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class AnimationPanel extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

    private static int NBUILDINGS = 0;
	private final int WINDOWX = 865;
    private final int WINDOWY = 467;
    private static int timeIncrementer = 0;
    
    static final int TIMERDELAY = 20;
    static final int xREST_POSITION = 350;  
    static final int yREST_POSITION = 400; 
    static final int xCOOK_POSITION = 340;  
    static final int yCOOK_POSITION = 100;
    static final int xHOSTSTAND_OFFSET = 65;  
    static final int yHOSTSTAND_OFFSET = 70;
    static final int xCHAIR_OFFSET = 25;
    static final int yCHAIR_OFFSET = 3;
    static final int ZERO = 0; 
    static final int xTABLE_AREA = 120;
    static final int yTABLE_AREA = 20;
    static final int xTABLE_AREA_WIDTH = 180;
    static final int yTABLE_AREA_WIDTH = 425;
    static final int xTABLE_WIDTH = 42;
    static final int yTABLE_WIDTH = 44;
    static final int xKITCHEN_WALL_OFFSET = 10;
    static final int yKITCHEN_WALL_OFFSET = 25;
    static final int xKITCHEN_WALL_WIDTH = 5;
    static final int yKITCHEN_WALL_WIDTH = 105;
    static final int xRESTAREA_WALL_OFFSET = 15;
    static final int yRESTAREA_WALL_OFFSET = 25;
    static final int xRESTAREA_WALL1_WIDTH = 50;
    static final int yRESTAREA_WALL1_WIDTH = 5;
    static final int xRESTAREA_WALL2_WIDTH = 5;
    static final int yRESTAREA_WALL2_WIDTH = 50;
    static final int xREGISTER_POSITION = 65;
    static final int yREGISTER_POSITION = 265;
    static final int NWAITSEATS = 6;
    static final int NWAITSEATS_COLUMNS = 3;
    static final int NWAITSEATS_ROWS = 2;
    static final int WAITINGSEATS_X_START = 20;
    static final int WAITINGSEATS_Y_START = 10;
    static final int WAITINGSEATS_X_GAP = 30;
    static final int WAITINGSEATS_Y_GAP = 30;
    static final int yKITCHEN_COUNTER_OFFSET = 30;
    static final int yGRILL_RIGHT_OFFSET = 30;
    static final int xGRILL_RIGHT_OFFSET = 52;
    static final int yFIDGE_OFFSET = 15;
    static final int xFIDGE_OFFSET = 100;
    static final int xPLATINGTABLE_OFFSET = 25;
    static final int yPLATINGTABLE_OFFSET = 60;
    static final int NPLATING_TABLES = 4;
    static final int NRESTSEATS = 16;
	static final int THIRTYSECONDS = 1500;
	static final int SIDEWALK_WIDTH = 12;
	static final int STREET_WIDTH = 20;
    static final int VERT_STREET_X_START = 25;
    
    int VERT_STREET_Y_START = 35;
    private List<Gui> guis = new ArrayList<Gui>();
    private SimCityGui simCityGui;
	public AnimationPanel(SimCityGui gui){
		this.simCityGui = gui;
		
		setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();	

        addMouseListener(this);	
		
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().toString().contains("Timer"))
			timeIncrementer++;
    	if(timeIncrementer == THIRTYSECONDS){
    		timeIncrementer = 0;
    		simCityGui.newHour();
    	}
		repaint();  //Will have paintComponent called
	}
	
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );

        //draw grass
        Color color = new Color(0x219622);
        g2.setColor(color);
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        
        //draw the streets
        g2.setColor(Color.black);
        	g2.fillRect(VERT_STREET_X_START, 0, STREET_WIDTH, 400);			//right
        	g2.fillRect(802+VERT_STREET_X_START, 0, STREET_WIDTH, 400);		//left
        	for(int i=0; i<5;i++){
        		g2.fillRect(0, VERT_STREET_Y_START+80*(i), WINDOWX, STREET_WIDTH);
        	}
        	
       	//draw the side walk
        g2.setColor(Color.gray);
	       // g2.fillRect(20-SIDEWALK_WIDTH, 0, SIDEWALK_WIDTH, 400);			//right
	        g2.fillRect(15+30, 0, SIDEWALK_WIDTH, 35);			
	        g2.fillRect(25-SIDEWALK_WIDTH, 0, SIDEWALK_WIDTH, 35);			
	        g2.fillRect(15+30, 375, SIDEWALK_WIDTH, 35);			
	        g2.fillRect(25-SIDEWALK_WIDTH, 375, SIDEWALK_WIDTH, 35);			
	       // g2.fillRect(802+20+30, 0, SIDEWALK_WIDTH, 400);		//left
	        g2.fillRect(802+25-SIDEWALK_WIDTH, 0, SIDEWALK_WIDTH, 35);			
	        g2.fillRect(802+25-SIDEWALK_WIDTH, 375, SIDEWALK_WIDTH, 35);		
	        g2.fillRect(802+15+30, 0, SIDEWALK_WIDTH, 35);			
	        g2.fillRect(802+15+30, 375, SIDEWALK_WIDTH, 35);		
	        for(int i=0; i<4;i++){
	           	g2.fillRect(15+30, 25+30+80*i, SIDEWALK_WIDTH, 60);			//right
	           	g2.fillRect(802+25-SIDEWALK_WIDTH, 25+30+80*i, SIDEWALK_WIDTH, 60);		//left
	           	g2.fillRect(25-SIDEWALK_WIDTH, 25+30+80*i, SIDEWALK_WIDTH, 60);			//right
	           	g2.fillRect(802+15+30, 25+30+80*i, SIDEWALK_WIDTH, 60);		//left
	        }
	        for(int i=0; i<5;i++){
	        	g2.fillRect(20+30, 35+80*(i)-SIDEWALK_WIDTH, 832-30*2, SIDEWALK_WIDTH);
	        	g2.fillRect(20+30, 25+80*(i)+30, 832-30*2, SIDEWALK_WIDTH);
	        	g2.fillRect(0, 35+80*(i)-SIDEWALK_WIDTH, 20, SIDEWALK_WIDTH);
	        	g2.fillRect(0, 25+80*(i)+30, 20, SIDEWALK_WIDTH);
	        	g2.fillRect(802+30+20, 35+80*(i)-SIDEWALK_WIDTH, 20, SIDEWALK_WIDTH);
	        	g2.fillRect(802+30+20, 25+80*(i)+30, 20, SIDEWALK_WIDTH);
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
		
        //Here is the buildings
        for(int i = ZERO; i<NBUILDINGS; i++){
        	g.drawImage(simCityGui.getBuildingImg(i), simCityGui.getBuildingXCoord(i), simCityGui.getBuildingYCoord(i), null);
        }        
    }
/*
    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(CookGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    public void addGui(CashierGui gui) {
        guis.add(gui);
    }*/
    
	@Override
	public void mouseClicked(MouseEvent me) {
		//get Building
				BuildingIcon clickedBuilding = simCityGui.getBuildingAt(me.getPoint());

				if(clickedBuilding != null)
				{
					//set building active state
					clickedBuilding.activateBuilding();
					//simCityGui.switchT
				}
				
				clickedBuilding = null;
	}
	@Override
	public void mouseEntered(MouseEvent me) {
		
	}
	@Override
	public void mouseExited(MouseEvent me) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent me) {
				
	}
	@Override
	public void mouseReleased(MouseEvent me) {
	}
	public void addNewBuilding() {
		NBUILDINGS++;
	}

	public void addGui(PersonGui gui) {
        guis.add(gui);
	}
}
