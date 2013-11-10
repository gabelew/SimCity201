package restaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

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

    private static int NTABLES = 0;
	private boolean movingTable = false;
	private Table clickedTable = null;
	private int clickedTableMouseXOffset;
	private int clickedTableMouseYOffset;
	private final int WINDOWX = 500;
    private final int WINDOWY = 467;
    
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

    private List<Gui> guis = new ArrayList<Gui>();
    private RestaurantGui restGui;
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage chairImg = null;
	private BufferedImage hostStandImg = null;
	private BufferedImage registerImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillLeftImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;

	public AnimationPanel(RestaurantGui gui){
		this.restGui = gui;
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    chairImg = ImageIO.read(new File(path.toString() + "customer_chair_v1.png"));
		    hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    grillLeftImg = ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		} catch (IOException e) {
		}
		
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();	
		
    	for(int i = 0; i < NWAITSEATS; i++){
			CustomerGui.waitingSeats.add(new Semaphore(1,true));
		}
    	for(int i = 0; i < NRESTSEATS; i++){
			WaiterGui.waitingSeats.add(new Semaphore(1,true));
		}
        addMouseListener(this);	
		
	}
   /* public AnimationPanel() {
		try {
		    kitchenImg = ImageIO.read(new File("kitchen.png"));
		    tableImg = ImageIO.read(new File("table.png"));
		    chairImg = ImageIO.read(new File("customer_chair_v1.png"));
		    hostStandImg = ImageIO.read(new File("host_stand.png"));
		} catch (IOException e) {
		}
		
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(TIMERDELAY, this );
    	timer.start();
    }*/

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(ZERO, ZERO, WINDOWX, WINDOWY );

        
        //draw table area
        g2.setColor(Color.lightGray);
        if(movingTable){
        	g2.fillRect(xTABLE_AREA, yTABLE_AREA, xTABLE_AREA_WIDTH, yTABLE_AREA_WIDTH);	// Tables
    	}
        

        //Here is the chairs
        for(int i = ZERO; i<NTABLES; i++){
        	g.drawImage(chairImg, restGui.getTablesXCoord(i) + xCHAIR_OFFSET, restGui.getTablesYCoord(i) + yCHAIR_OFFSET, null);
        }
        for(int i = ZERO; i<NWAITSEATS/NWAITSEATS_ROWS; i++){
        	for(int j = ZERO; j < NWAITSEATS/NWAITSEATS_COLUMNS; j++ )
        	g.drawImage(chairImg, i*WAITINGSEATS_X_GAP+WAITINGSEATS_X_START, j*WAITINGSEATS_Y_GAP+WAITINGSEATS_Y_START, null);
        }
        
		//draw Host Stand
    	g.drawImage(hostStandImg, xHOSTSTAND_OFFSET, yHOSTSTAND_OFFSET, null);

        // draw kitchen
		g.drawImage(kitchenCounterImg, xCOOK_POSITION, yCOOK_POSITION-yKITCHEN_COUNTER_OFFSET, null);
		//g.drawImage(grillLeftImg, xCOOK_POSITION, yCOOK_POSITION-ySALAD_BAR_OFFSET, null);
		g.drawImage(grillRightImg, xCOOK_POSITION+xGRILL_RIGHT_OFFSET, yCOOK_POSITION-yGRILL_RIGHT_OFFSET, null);
		g.drawImage(fidgeImg, xCOOK_POSITION+xFIDGE_OFFSET, yCOOK_POSITION+yFIDGE_OFFSET, null);
		
		for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
        	if(!(gui instanceof CustomerGui || gui instanceof WaiterGui))
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
        
      //Plating  tables
      		for(int i = 0; i<NPLATING_TABLES; i++){
      			g.drawImage(platingTableImg, xCOOK_POSITION+xPLATINGTABLE_OFFSET+17*i, yCOOK_POSITION+yPLATINGTABLE_OFFSET+8*i, null);
          	}

        for(Gui gui : guis) {
        	if(gui instanceof CustomerGui || gui instanceof WaiterGui)
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
        
        g2.setColor(Color.MAGENTA);       
		// draw kitchen wall
        g2.fillRect(xCOOK_POSITION-xKITCHEN_WALL_OFFSET, yCOOK_POSITION-yKITCHEN_WALL_OFFSET, xKITCHEN_WALL_WIDTH, yKITCHEN_WALL_WIDTH);

        // draw restArea wall
        g2.fillRect(xREST_POSITION-xRESTAREA_WALL_OFFSET, yREST_POSITION-yRESTAREA_WALL_OFFSET, xRESTAREA_WALL1_WIDTH, yRESTAREA_WALL1_WIDTH);
        g2.fillRect(xREST_POSITION-xRESTAREA_WALL_OFFSET, yREST_POSITION-yRESTAREA_WALL_OFFSET, xRESTAREA_WALL2_WIDTH, yRESTAREA_WALL2_WIDTH);
    	
        //draw Register Stand
    	g.drawImage(registerImg, xREGISTER_POSITION, yREGISTER_POSITION, null);
    	
    	for(Gui gui : guis) {
        	if(gui instanceof CashierGui)
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
        
        //Here is the table
        g2.setColor(Color.ORANGE);
        for(int i = ZERO; i<NTABLES; i++){
        	g.drawImage(tableImg, restGui.getTablesXCoord(i), restGui.getTablesYCoord(i), null);
        }
        

        for(Gui gui : guis) {
        	if(gui instanceof CustomerGui){
        		CustomerGui cGui = (CustomerGui) gui;
	            if (cGui.isPresent()) {
	            	cGui.drawFood(g2);
	            }
        	}else if(gui instanceof CookGui){
        		CookGui cGui = (CookGui) gui;
	            if (cGui.isPresent()) {
	            	cGui.drawFood(g2);
	            }
        	}
        }

    }

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
    }
    
	@Override
	public void mouseClicked(MouseEvent me) {
		
	}
	@Override
	public void mouseEntered(MouseEvent me) {
		
	}
	@Override
	public void mouseExited(MouseEvent me) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent me) {
		//get table
		clickedTable = restGui.getTableAt(me.getPoint());

		if(clickedTable != null && clickedTable.getState() == Table.TableState.Movable)
		{
			//set table state to moving
			clickedTable.beingMoved();
			
			//start drawing valid placement area
			movingTable = true; 
			
			//set mousePointerOffset
			clickedTableMouseXOffset = me.getPoint().x - clickedTable.getX();
			clickedTableMouseYOffset = me.getPoint().y - clickedTable.getY();
		}
		
	}
	@Override
	public void mouseReleased(MouseEvent me) {
		if(clickedTable != null && clickedTable.getState() == Table.TableState.BeingMoved)
		{
			//stop drawing valid placement area
			movingTable = false; 
			
			//place table with offset calculated if in valid area
			Point placeTableHere = new Point(me.getPoint().x - clickedTableMouseXOffset,me.getPoint().y - clickedTableMouseYOffset);

			if(xTABLE_AREA <= placeTableHere.x && xTABLE_AREA + xTABLE_AREA_WIDTH - xTABLE_WIDTH >= placeTableHere.x 
				&& yTABLE_AREA <= placeTableHere.y && yTABLE_AREA + yTABLE_AREA_WIDTH - yTABLE_WIDTH >= placeTableHere.y
				&& restGui.notOnExistingTable(clickedTable, placeTableHere)){
				clickedTable.changePos(placeTableHere);
			}

			//set table state to free
			clickedTable.setMovable();
	
		}
		clickedTable = null;
	}
	public void addNewTable() {
		NTABLES++;
	}
}
