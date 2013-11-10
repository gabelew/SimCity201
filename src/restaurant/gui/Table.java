package restaurant.gui;

import java.awt.Point;

public class Table {

	private int xPos, yPos;

	public enum TableState {Movable,BeingMoved,Occupied};
	private TableState state = TableState.Movable;
	
	
	Table(int x,int y){
		xPos = x;
		yPos = y;
	}

	public TableState getState(){
		return state;
	}
	
	public void beingMoved(){
		state = TableState.BeingMoved;
	}
	
	public void setOccupied(){
		state = TableState.Occupied;
	}
	
	public void setMovable(){
		state = TableState.Movable;
	}

	public void changePos(int x,int y){
		xPos = x;
		yPos = y;
	}
	public void changePos(Point i){
		xPos = i.x;
		yPos = i.y;
	}
	
	public int getX(){
		return xPos;
	}
	public int getY(){
		return yPos;
	}
}
