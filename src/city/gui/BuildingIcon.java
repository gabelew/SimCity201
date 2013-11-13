package city.gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BuildingIcon {
	private int xPos, yPos;
	private BufferedImage buildingImg = null;
	public String type;
	JPanel animationPanel;
	
	BuildingIcon(int x,int y, String type, JPanel a){
		xPos = x;
		yPos = y;
		animationPanel = a;
		try {
			StringBuilder path = new StringBuilder("imgs/" + type.toLowerCase()+".png");
			buildingImg = ImageIO.read(new File(path.toString()));
		} catch (IOException e) {
		}
		this.type = type;
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
	public void activateBuilding() {
		// TODO Auto-generated method stub
		
	}
	public BufferedImage getImg() {
		return buildingImg;
	}
}
