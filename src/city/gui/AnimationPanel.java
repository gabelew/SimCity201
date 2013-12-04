package city.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import CMRestaurant.gui.CMCustomerGui;
import CMRestaurant.gui.CMWaiterGui;
import city.BusAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private final int WINDOWX = 865;
    private final int WINDOWY = 467;
    private static int timeIncrementer = 1000;

    List<BuildingIcon> buildings = Collections.synchronizedList(new ArrayList<BuildingIcon>());
    
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
	static final int BUILDING_ROWS = 4;
	static final int BUILDING_COLUMNS = 19;
    static final int BUILDING_START_X = 57;
    static final int BUILDING_START_Y = 68;
    static final int BUILDING_OFFSET_X = 40;
    static final int BUILDING_OFFSET_Y = 80;	
	static final int xBUILDING_IMG_POINT_OFFSET = 1;
	static final int yBUILDING_IMG_POINT_OFFSET = 1;
	static final int xBUILDING_IMG_AREA_OFFSET = 35;
	static final int yBUILDING_IMG_AREA_OFFSET = 33;

	public Timer timer;
    int VERT_STREET_Y_START = 35;
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    private SimCityGui simCityGui;
    public BusAgent busLeft = new BusAgent('B');
    public BusAgent busRight = new BusAgent('F');
    public boolean paused = false;
    
	public AnimationPanel(SimCityGui gui){
		this.simCityGui = gui;

		try {
			StringBuilder path = new StringBuilder("imgs/");
			ImageIO.read(new File(path.toString() + "bus_front.png"));
		    ImageIO.read(new File(path.toString() + "bus_back.png"));
		    ImageIO.read(new File(path.toString() + "carFront.png"));
		    ImageIO.read(new File(path.toString() + "carBack.png"));
		    ImageIO.read(new File(path.toString() + "carRight.png"));
		    ImageIO.read(new File(path.toString() + "carLeft.png"));
		} catch (IOException e) {
		}
		

	    BusGui bg1 = new BusGui(busLeft, simCityGui, 'B');
	    busLeft.setBusGui(bg1);
	    BusGui bg2 = new BusGui(busRight, simCityGui, 'F');
	    busRight.setBusGui(bg2);
	    bg1.setPresent(true);
	    bg2.setPresent(true);
        guis.add(bg1);
        guis.add(bg2);
        busLeft.startThread();
        busRight.startThread();
		setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	timer = new Timer(TIMERDELAY, this );
    	timer.start();	

        addMouseListener(this);	
		this.addDefaultBuildings();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().toString().contains("Timer") && !paused)
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

	        

		synchronized(guis){
		for(Gui gui : guis) {
			//if (gui.isPresent()) {
				gui.updatePosition();
	        //}
	    }
		}
		synchronized(guis){
		for(Gui gui : guis) {
			if (gui.isPresent()) {
				gui.draw(g2);
	        }
        }
		}

    	synchronized(guis){		
	        for(Gui gui : guis) {
	        	if(gui instanceof BusGui)
		            if (gui.isPresent()) {
		                gui.draw(g2);
		            }
	        }
    	}
    	
		synchronized(guis){		
	        for(Gui gui : guis) {
	        	if(gui instanceof DeliveryManDrivingGui)
		            if (gui.isPresent()) {
		                gui.draw(g2);
		            }
	        }
    	}
		
        //Here is the buildings
        for(int i = ZERO; i<buildings.size(); i++){
        	g.drawImage(getBuildingImg(i), getBuildingXCoord(i), getBuildingYCoord(i), null);
        }
    	
    }
    
	public void addGui(PersonGui gui) {
        guis.add(gui);
	}
    
	public void addGui(DeliveryManDrivingGui gui) {
        guis.add(gui);
	}
    
	@Override
	public void mouseClicked(MouseEvent me) {
		//get Building
				BuildingIcon clickedBuilding = getBuildingAt(me.getPoint());

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

	private void addDefaultBuildings(){
	    	for(int j =0; j<BUILDING_ROWS;j++){
		    	for(int i = 1; i<BUILDING_COLUMNS;i++){
		    		if(i < 5){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"house"));

		        	}else if(i < 7){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"apartment"));
		        	}else if(i < 8){
		        		if(j < 2){
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"restaurant"));
		    			}else if(j<3){
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"market"));
		    			}
		    			else{
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"restaurant"));
		    			}
		        	}else if(i<9){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"bank"));
		        	}else if(i<10){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"market"));
		        	}else if(i<11){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"bank"));
		        	}else if(i<12){
		        		if(j < 2){
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"restaurant"));
		    			}else if(j<3){
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"market"));
		    			}
		    			else{
			    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"apartment"));
		    			}
		        	}else if(i<14){
		    			buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"apartment"));
		        	}
		        	else if(i<18){
		        		buildings.add(new BuildingIcon(BUILDING_START_X+BUILDING_OFFSET_X*i,BUILDING_START_Y+BUILDING_OFFSET_Y*j,"house"));
		        	}
		    	}
	    	}
	    	
	    }
		public BuildingIcon getBuildingAt(Point i) {
			for(BuildingIcon b: buildings)
			{
				if(b.getX()+xBUILDING_IMG_POINT_OFFSET <= i.x && b.getX()+xBUILDING_IMG_AREA_OFFSET >= i.x && b.getY()+yBUILDING_IMG_POINT_OFFSET <= i.y && b.getY()+yBUILDING_IMG_AREA_OFFSET >= i.y)
				{
					return b;
				}
			}
			return null;
		}
		public int getBuildingXCoord(int i){
	    	return buildings.get(i).getX();
	    }
	    public int getBuildingYCoord(int i){
	    	return buildings.get(i).getY();
	    }
	    public BufferedImage getBuildingImg(int i){
	    	return buildings.get(i).getImg();
	    }

}
