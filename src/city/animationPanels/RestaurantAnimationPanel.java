package city.animationPanels;

import javax.imageio.ImageIO;
import javax.swing.*;

import restaurant.Restaurant;
import restaurant.gui.CashierGui;
import restaurant.gui.CookGui;
import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantPanel;
import restaurant.gui.Table;
import restaurant.gui.WaiterGui;
import city.gui.Gui;
import city.gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class RestaurantAnimationPanel extends InsideAnimationPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

    private List<Table> tables = new ArrayList<Table>();
	private boolean movingTable = false;
	private Table clickedTable = null;
	private int clickedTableMouseXOffset;
	private int clickedTableMouseYOffset;

	public List<Semaphore> waitingSeatsWaiter = new ArrayList<Semaphore>();
	public List<Semaphore> waitingSeats = new ArrayList<Semaphore>();
	
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
    static final int xTABLE_WIDTH = 42;
    static final int yTABLE_WIDTH = 44;
    static final int TABLE_PADDING = 5;
	static final int xTABLE_IMG_POINT_OFFSET = 1;
	static final int yTABLE_IMG_POINT_OFFSET = 40;
	static final int xTABLE_IMG_AREA_OFFSET = 10;
	static final int yTABLE_IMG_AREA_OFFSET = 42;
	static final int xDEFAULT_NEW_TABLE_POSITION = 140;
	static final int yDEFAULT_NEW_TABLE_POSITION = 40;
	static final int STARTING_TABLES_X = 200;
	static final int STARTING_TABLE1_Y = 35;
	static final int STARTING_TABLE_Y_SPACING = 90;
	
    private SimCityGui simCityGui;
	private BufferedImage kitchenCounterImg = null;
	private BufferedImage tableImg = null;
	private BufferedImage chairImg = null;
	private BufferedImage hostStandImg = null;
	private BufferedImage registerImg = null;
	private BufferedImage fidgeImg = null;
	private BufferedImage grillRightImg = null;
	private BufferedImage platingTableImg = null;

	public RestaurantAnimationPanel(SimCityGui simCityGui){
		this.simCityGui = simCityGui;
		
		try {
			StringBuilder path = new StringBuilder("imgs/");
		    kitchenCounterImg = ImageIO.read(new File(path.toString() + "kitchen.png"));
		    tableImg = ImageIO.read(new File(path.toString() + "table.png"));
		    chairImg = ImageIO.read(new File(path.toString() + "customer_chair_v1.png"));
		    hostStandImg = ImageIO.read(new File(path.toString() + "host_stand.png"));
		    registerImg = ImageIO.read(new File(path.toString() + "register.png"));
		    fidgeImg = ImageIO.read(new File(path.toString() + "fidge.png"));
		    ImageIO.read(new File(path.toString() + "grill.png"));
		    grillRightImg = ImageIO.read(new File(path.toString() + "grill2.png"));
		    platingTableImg = ImageIO.read(new File(path.toString() + "platingTable.png"));
		} catch (IOException e) {
		}
		
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 

        simCityGui.animationPanel.timer.addActionListener(this);
		
    	for(int i = 0; i < NWAITSEATS; i++){
			waitingSeats.add(new Semaphore(1,true));
		}
    	for(int i = 0; i < NRESTSEATS; i++){
			waitingSeatsWaiter.add(new Semaphore(1,true));
		}
        addMouseListener(this);	
		
	}

	public void actionPerformed(ActionEvent e) {
		for(Gui gui : getGuis()) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
		if(insideBuildingPanel != null && insideBuildingPanel.isVisible)
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
        for(int i = ZERO; i<tables.size(); i++){
        	g.drawImage(chairImg, getTablesXCoord(i) + xCHAIR_OFFSET, getTablesYCoord(i) + yCHAIR_OFFSET, null);
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
		


        for(Gui gui : getGuis()) {
        	if(!(gui instanceof CustomerGui || gui instanceof WaiterGui))
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
        
      //Plating  tables
      		for(int i = 0; i<NPLATING_TABLES; i++){
      			g.drawImage(platingTableImg, xCOOK_POSITION+xPLATINGTABLE_OFFSET+17*i, yCOOK_POSITION+yPLATINGTABLE_OFFSET+8*i, null);
          	}

        for(Gui gui : getGuis()) {
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
    	
    	for(Gui gui : getGuis()) {
        	if(gui instanceof CashierGui)
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
        }
        
        //Here is the table
        g2.setColor(Color.ORANGE);
        for(int i = ZERO; i<tables.size(); i++){
        	g.drawImage(tableImg, getTablesXCoord(i), getTablesYCoord(i), null);
        }
        

        for(Gui gui : getGuis()) {
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
		clickedTable = getTableAt(me.getPoint());

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
				&& notOnExistingTable(clickedTable, placeTableHere)){
				clickedTable.changePos(placeTableHere);
			}

			//set table state to free
			clickedTable.setMovable();
	
		}
		clickedTable = null;
	}

    public void addDefaultTables() {
    	((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y);
    	((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING);
    	((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
    	((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).getTablesPanel().addStartingTable(STARTING_TABLES_X,STARTING_TABLE1_Y+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING+STARTING_TABLE_Y_SPACING);
	}

 	public int getTablesXCoord(int i){
    	return tables.get(i).getX();
    }
    public int getTablesYCoord(int i){
    	return tables.get(i).getY();
    }

	public boolean isOnTable(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return true;
			}
		}
		return false;
	}

	public Table getTableAt(Point i) {
		for(Table t: tables)
		{
			if(t.getX()+xTABLE_IMG_POINT_OFFSET <= i.x && t.getX()+yTABLE_IMG_POINT_OFFSET >= i.x && t.getY()+xTABLE_IMG_AREA_OFFSET <= i.y && t.getY()+yTABLE_IMG_AREA_OFFSET >= i.y)
			{
				return t;
			}
		}
		return null;
	}
	
	public Table getTableAtIndex(int i) {
		if(tables.size() < i){
			return tables.get(i);
		}
		
		return null;
	}
	public void setTableOccupied(int tableNumber) {
		System.out.println("\t\t\tsetTableOccupied " + tableNumber + "Tables size " + tables.size());
		tables.get(tableNumber).setOccupied();
		((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).setTableDisabled(tableNumber);
	}
	public void setTableUnoccupied(int tableNumber) {
		tables.get(tableNumber).setMovable();
		((RestaurantPanel) insideBuildingPanel.guiInteractionPanel).setTableEnabled(tableNumber);
	}

	public boolean notOnExistingTable(Table newTablePos, Point placeTableHere) {
		for(Table t: tables)
		{
			if(t != newTablePos){
				if(
					(t.getX() - TABLE_PADDING <= placeTableHere.x && t.getX()+xTABLE_WIDTH + TABLE_PADDING >= placeTableHere.x &&
					t.getY() - TABLE_PADDING <= placeTableHere.y && t.getY()+yTABLE_WIDTH + TABLE_PADDING >= placeTableHere.y) ||
					(placeTableHere.x - TABLE_PADDING <= t.getX() && placeTableHere.x+xTABLE_WIDTH + TABLE_PADDING >= t.getX() &&
					placeTableHere.y - TABLE_PADDING <= t.getY() && placeTableHere.y+yTABLE_WIDTH + TABLE_PADDING >= t.getY())){
					return false;
				}
			}
		}
		return true;
	}

	public void addTable() {
    	tables.add(new Table(xDEFAULT_NEW_TABLE_POSITION, yDEFAULT_NEW_TABLE_POSITION));
    	for(Restaurant r: simCityGui.getRestaurants()){
    		if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
    			r.host.addNewTable();
    		}
    	}
    	
		
	}
	public void addTable(int x, int y) {
		
    	tables.add(new Table( x, y));
    	for(Restaurant r: simCityGui.getRestaurants()){
    		if(r.insideAnimationPanel == insideBuildingPanel.insideAnimationPanel){
    			r.host.addNewTable();
    		}
    	}
		
	}


	
}
