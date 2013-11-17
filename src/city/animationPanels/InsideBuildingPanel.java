package city.animationPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import city.gui.BuildingIcon;
import city.gui.SimCityGui;

public class InsideBuildingPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public boolean isVisible = false;
	SimCityGui myCity;
	String myName;
	BuildingIcon buildingIcon;
	public InsideAnimationPanel insideAnimationPanel;
	JPanel guiInteractionPanel;

    static final int FRAMEX = 1100;
    static final int FRAMEY = 430;
    static final int INSIDE_BUILDING_FRAME_Y = 517;
	
	public InsideBuildingPanel(BuildingIcon b, int i, SimCityGui s, InsideAnimationPanel iap, JPanel gip) {
		buildingIcon = b;
		myName = "" + i;
		myCity = s;
		insideAnimationPanel = iap;
		guiInteractionPanel = gip;
		
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BorderLayout());
		Dimension dim = new Dimension(FRAMEX, INSIDE_BUILDING_FRAME_Y);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
		add(insideAnimationPanel,BorderLayout.CENTER);
		add(guiInteractionPanel, BorderLayout.WEST);
	}

	public String getName(){
		return myName;
	}
	public void displayBuildngPanel() {
		myCity.displayBuildingPanel(this);
		
	}
}
