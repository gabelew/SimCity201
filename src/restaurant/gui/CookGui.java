package restaurant.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import city.roles.CookRole;
import city.roles.CookRole.RoleOrder;
import city.gui.Gui;
import city.gui.SimCityGui;
import restaurant.gui.HostGui.Command;
import restaurant.interfaces.Waiter;

public class CookGui implements Gui  {

	private CookRole role = null;
	private boolean isPresent = false;
	private BufferedImage cookImg = null;
	SimCityGui gui;
	List<MyFood> foods = Collections.synchronizedList(new ArrayList<MyFood>());
	public List<Semaphore> grillingSpots = new ArrayList<Semaphore>();
	public List<Semaphore> counterSpots = new ArrayList<Semaphore>();
	public List<Semaphore> pickUpSpots = new ArrayList<Semaphore>();
	private enum Command {noCommand, GoToFidge, GoToGrill, GoToCounter, GoToPlate, GoToRestPost, enterRestaurant, leaveRestaurant};
	private enum FoodState{PutFoodOnGrill, PutFoodOnCounter, FoodOnGrill, FoodOnCounter, PickUpFromGrill, PickUpFromCounter, PutOnPickUpTable, OnPickUpTable, WaiterPickedUp};
	Command command = Command.noCommand;
	int xDestination_old;
	int yDestination_old;
	Command command_old;

	private Map<Integer, Point> grillMap = new HashMap<Integer, Point>();
	private Map<Integer, Point> counterMap = new HashMap<Integer, Point>();
	private Map<Integer, Point> pickUpMap = new HashMap<Integer, Point>();
	
	class MyFood{
		FoodIcon food;
		Point point;
		//Order order;
		RoleOrder order;
		FoodState state;
		public int cookingSpot = -1;
		public int pickUpSpot = -1;
		/*
		MyFood(FoodIcon f, Point p, Order o){
			this.food = f;
			this.point = p;
			this.order = o;
			
			if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken") || order.choice.equalsIgnoreCase("burger")){
				state = FoodState.PutFoodOnGrill;
			}else{
				state = FoodState.PutFoodOnCounter;
			}
		}*/
		
		MyFood(FoodIcon f, Point p, RoleOrder o){
			this.food = f;
			this.point = p;
			this.order = o;
			
			if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken") || order.choice.equalsIgnoreCase("burger")){
				state = FoodState.PutFoodOnGrill;
			}else{
				state = FoodState.PutFoodOnCounter;
			}
		}
	}

    static final int xREST_POSITION = 383;
    static final int yREST_POSITION = 105; 
    static final int xFIDGE_POSITION = 430;
    static final int yFIDGE_POSITION = 129;
    static final int xFOOD_OFFSET = 10;
    static final int yFOOD_OFFSET = 4;
    static final int xGRILL_POSITION = 402;
    static final int yGRILL_POSITION = 85;
    static final int xKITCHEN_COUNTER_POSITION = 367;
    static final int yKITCHEN_COUNTER_POSITION = 80;
    static final int xPLATING_POSITION = 390;
    static final int yPLATING_POSITION = 142;
    public static final int N_GRILLING_SPOTS = 6;
    static final int N_GRILLING_SPOT_COLUMNS = 3;
    static final int N_GRILLING_SPOT_ROWS = 2;
    public static final int N_COUNTER_SPOTS = 3;
    static final int N_COUNTER_SPOT_COLUMNS = 0;
    static final int N_COUNTER_SPOT_ROWS = 0;
    static final int xGRILL_RIGHT_START = 392;
    static final int yGRILL_RIGHT_START = 70;
    static final int xKITCHEN_COUNTER_START = 340;
    static final int yKITCHEN_COUNTER_START = 70;
    static final int xPICKUP_SPOT_START = 365;
    static final int yPICKUP_SPOT_START = 160;
    public static final int N_PICKUP_SPOTS = 12;
    static final int N_PICKUP_SPOT_COLUMNS = 6;
    static final int N_PICKUP_SPOT_ROWS = 2;
    static final int START_POSITION = -20;
    private int xPos = START_POSITION, yPos = START_POSITION;//default waiter position
    private int xDestination = START_POSITION, yDestination = START_POSITION;//default start position
    
	public CookGui(CookRole cook) {
		try {
		    cookImg = ImageIO.read(new File("imgs/chef_v1.png"));
		} catch (IOException e) {
		}
		
		setRole(cook);
		xDestination = xREST_POSITION;
		yDestination = yREST_POSITION;
		
		for(int i = 0; i < N_GRILLING_SPOTS; i++){
			grillingSpots.add(new Semaphore(1,true));
		}				
		for(int i = 0; i < N_COUNTER_SPOTS; i++){
			counterSpots.add(new Semaphore(1,true));
		}
		for(int i = 0; i < N_PICKUP_SPOTS; i++){
			pickUpSpots.add(new Semaphore(1,true));
		}
		
        for(int i = 0; i<N_GRILLING_SPOTS/N_GRILLING_SPOT_COLUMNS; i++){
        	for(int j = 0; j < N_GRILLING_SPOTS/N_GRILLING_SPOT_ROWS; j++ ){
        		grillMap.put(j+i*N_GRILLING_SPOT_COLUMNS, new Point(xGRILL_RIGHT_START+19+10*j+(-12)*i,yGRILL_RIGHT_START+2+6*j+9*i));
        	}
        }
        
        for(int i = 0; i<N_COUNTER_SPOTS; i++){
        		counterMap.put(i, new Point(xKITCHEN_COUNTER_START+14+8*i,yKITCHEN_COUNTER_START+22+(-5)*i));
        }
        
        for(int i = 0; i<N_PICKUP_SPOTS/N_PICKUP_SPOT_COLUMNS; i++){
        	for(int j = 0; j < N_PICKUP_SPOTS/N_PICKUP_SPOT_ROWS; j++ ){
        		pickUpMap.put(j+i*N_PICKUP_SPOT_COLUMNS, new Point(xPICKUP_SPOT_START+1+12*j+10*i,yPICKUP_SPOT_START+2+6*j+(-6)*i));
        	}
        }
        
	}
	
	public CookRole getRole() {
		return role;
	}

	public void setRole(CookRole role) {
		this.role = role;
	}

	public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
   
        if(command != Command.noCommand && xPos == xDestination && yPos == yDestination){
        	if(command == Command.leaveRestaurant){
        		role.msgAnimationHasLeftRestaurant();
        		command = command_old;
        		xDestination = xDestination_old;
        		yDestination = yDestination_old;
        	}else if(command == Command.enterRestaurant){
        		command = Command.noCommand;
        	}else if(command == Command.GoToFidge){
        		command = Command.noCommand;
        		role.msgAnimationFinishedAtFidge();
        	}else if (command == Command.GoToGrill){
        		command = Command.noCommand;

        		synchronized(foods){
	        		for(MyFood f: foods){
	        			if(f.state == FoodState.PutFoodOnGrill){
	        				putFoodOnGrill(f);
	        			}else if(f.state == FoodState.PickUpFromGrill){
	            			pickUpFoodOnGrill(f);
	            		}
	        		}
        		}
        	}else if (command == Command.GoToCounter){
        		command = Command.noCommand;

        		synchronized(foods){
	        		for(MyFood f: foods){
	        			if(f.state == FoodState.PutFoodOnCounter){
	        				putFoodOnCounter(f);
	        			}else if(f.state == FoodState.PickUpFromCounter){
	            			pickUpFoodOnCounter(f);
	            		}
	        		}
        		}
        	}else if(command == Command.GoToPlate){
        		command = Command.noCommand;

        		synchronized(foods){
	        		for(MyFood f: foods){
	        			if(f.state == FoodState.PutOnPickUpTable){
	        				putFoodOnPickUpTable(f);
	        			}
	        		}
        		}
        	}
        }
	}

	public void draw(Graphics2D g) {
		g.drawImage(cookImg, xPos, yPos, null);
	}

	public void drawFood(Graphics2D g) {

		synchronized(foods){
			for(MyFood f: foods){
				if(f.food != null){
					if(f.state == FoodState.PutFoodOnGrill || f.state == FoodState.PutFoodOnCounter || f.state == FoodState.PutOnPickUpTable ){
						g.drawImage(f.food.iconImg, xPos+f.point.x, yPos+f.point.y, null);
					}else {	
						g.drawImage(f.food.iconImg, f.point.x, f.point.y, null);
					}
				}
			}
		}
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoGoToFidge() {
		xDestination = xFIDGE_POSITION;
		yDestination = yFIDGE_POSITION;
		command = Command.GoToFidge;
		
	}
/*
	public void DoCookFood(RoleOrder order) {
		// Grab food from fidge(already at fidge
		// if burger,steak,chicken put on grill and set timer
		// if salad or cookie, put on right
		if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken") || order.choice.equalsIgnoreCase("burger")){
			foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			command = Command.GoToGrill;
		}else{
			foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			command = Command.GoToCounter;
		}
		
	}
	*/
	public void DoCookFood(RoleOrder order) {
		// Grab food from fidge(already at fidge
		// if burger,steak,chicken put on grill and set timer
		// if salad or cookie, put on right
		if(order.choice.equalsIgnoreCase("steak") || order.choice.equalsIgnoreCase("chicken") || order.choice.equalsIgnoreCase("burger")){
			foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			command = Command.GoToGrill;
		}else{
			foods.add(new MyFood(new FoodIcon(order.choice+"g"), new Point(xFOOD_OFFSET, yFOOD_OFFSET), order));
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			command = Command.GoToCounter;
		}
		
	}

	private void putFoodOnGrill(MyFood f) {
		for(int i = 0; i < grillingSpots.size(); i++){
			if(f.cookingSpot  < 0){
				if(grillingSpots.get(i).tryAcquire()){
					f.cookingSpot = i;
					f.state = FoodState.FoodOnGrill;
					f.point.x = grillMap.get(i).x;
					f.point.y = grillMap.get(i).y;
				}
			}
		}
		role.msgAnimationFinishedPutFoodOnGrill();
	}
	
	private void putFoodOnCounter(MyFood f){
		for(int i = 0; i < counterSpots.size(); i++){
			if(f.cookingSpot  < 0){
				if(counterSpots.get(i).tryAcquire()){
					f.cookingSpot = i;
					f.state = FoodState.FoodOnCounter;
					f.point.x = counterMap.get(i).x;
					f.point.y = counterMap.get(i).y;
				}
			}
		}
		role.msgAnimationFinishedPutFoodOnGrill();
	}
	
	private void pickUpFoodOnGrill(MyFood f){
		f.state = FoodState.PutOnPickUpTable;
		f.point.x = xFOOD_OFFSET;
		f.point.y = yFOOD_OFFSET;
		grillingSpots.get(f.cookingSpot).release();
		f.cookingSpot = -1;	

		xDestination = xPLATING_POSITION;
		yDestination = yPLATING_POSITION;
		command = Command.GoToPlate;
	}
	
	private void pickUpFoodOnCounter(MyFood f){

		f.state = FoodState.PutOnPickUpTable;
		f.point.x = xFOOD_OFFSET;
		f.point.y = yFOOD_OFFSET;
		counterSpots.get(f.cookingSpot).release();
		f.cookingSpot = -1;	

		xDestination = xPLATING_POSITION;
		yDestination = yPLATING_POSITION;
		command = Command.GoToPlate;
	}
		
	private void putFoodOnPickUpTable(MyFood f) {

		for(int i = 0; i < pickUpSpots.size(); i++){
			if(f.pickUpSpot   < 0){
				if(pickUpSpots.get(i).tryAcquire()){
					f.pickUpSpot = i;
					f.state = FoodState.OnPickUpTable;
					f.point.x = pickUpMap.get(i).x;
					f.point.y = pickUpMap.get(i).y;
				}
			}
		}

		role.msgAnimationFinishedPutFoodOnPickUpTable(f.order);
	}
	
	/*public void DoPlateFood(Order o){
		MyFood f = findMyFood(o);

		if(f.order.choice.equalsIgnoreCase("steak") || f.order.choice.equalsIgnoreCase("chicken") || f.order.choice.equalsIgnoreCase("burger")){
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			f.state = FoodState.PickUpFromGrill;
			command = Command.GoToGrill;
		}else{
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			f.state = FoodState.PickUpFromCounter;
			command = Command.GoToCounter;
		}
	}*/
	
	public void DoPlateFood(RoleOrder o){
		MyFood f = findMyFood(o);

		if(f.order.choice.equalsIgnoreCase("steak") || f.order.choice.equalsIgnoreCase("chicken") || f.order.choice.equalsIgnoreCase("burger")){
			xDestination = xGRILL_POSITION;
			yDestination = yGRILL_POSITION;
			f.state = FoodState.PickUpFromGrill;
			command = Command.GoToGrill;
		}else{
			xDestination = xKITCHEN_COUNTER_POSITION;
			yDestination = yKITCHEN_COUNTER_POSITION;
			f.state = FoodState.PickUpFromCounter;
			command = Command.GoToCounter;
		}
	}
/*
	private MyFood findMyFood(Order o) {

		synchronized(foods){
			for(MyFood f: foods){
				if(f.order == o){
					return f;
				}
			}
		}
		return null;
	}
	*/
	private MyFood findMyFood(RoleOrder o) {

		synchronized(foods){
			for(MyFood f: foods){
				if(f.order == o){
					return f;
				}
			}
		}
		return null;
	}

	public void doGoToRestPost() {
		xDestination = xREST_POSITION;
		yDestination = yREST_POSITION;
		command = Command.GoToRestPost;
	}

	public void doGoToGrill() {
		xDestination = xGRILL_POSITION;
		yDestination = yGRILL_POSITION;
		command = Command.GoToGrill;
	}

	public void msgPickingUpMyOrder(Waiter w, String choice, int table) {
		MyFood deleteIt = null;
		synchronized(foods){
			for(MyFood f:foods){
				if(f.order.waiter == w && f.order.choice.equalsIgnoreCase(choice) && f.state == FoodState.OnPickUpTable 
						&& f.order.table == table){
					deleteIt = f;
					pickUpSpots.get(f.pickUpSpot).release();
					role.msgAnimationFinishedWaiterPickedUpFood();
				}
			}
		}
		
		if(deleteIt!=null){
			foods.remove(deleteIt);
		}
	}

	public void DoLeaveRestaurant() {
        xDestination_old = xDestination;
        yDestination_old = yDestination;
        command_old = command;
        xDestination = START_POSITION;
        yDestination = START_POSITION;
        command = Command.leaveRestaurant;
    }
    public void DoEnterRestaurant(){
        xDestination = xREST_POSITION;
        yDestination = yREST_POSITION; 
        command = Command.enterRestaurant;   	
    }
}
