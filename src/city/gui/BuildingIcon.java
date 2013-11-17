package city.gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import city.animationPanels.InsideBuildingPanel;

public class BuildingIcon {
	private int xPos, yPos;
	private BufferedImage buildingImg = null;
	public String type;
	InsideBuildingPanel buildingPanel;
	
	BuildingIcon(int x,int y, String type){
		xPos = x;
		yPos = y;
		try {
			StringBuilder path = new StringBuilder("imgs/" + type.toLowerCase()+".png");
			buildingImg = ImageIO.read(new File(path.toString()));
		} catch (IOException e) {
		}
		this.type = type;
	}
	public void setInsideBuildingPanel(InsideBuildingPanel bp){
		buildingPanel = bp;
	}
	public InsideBuildingPanel getInsideBuildingPanel(){
		return buildingPanel;
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
		
		buildingPanel.displayBuildngPanel();
	}

	public BufferedImage getImg() {
		return buildingImg;
	}
}
