package city.animationPanels;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import city.gui.Gui;

public abstract class InsideAnimationPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	InsideBuildingPanel insideBuildingPanel;
	protected static final int TIMERDELAY = 10;
	protected final int WINDOWX = 934;
    protected final int WINDOWY = 472;
    
    protected List<Gui> guis = new ArrayList<Gui>();
    
	public void setInsideBuildingPanel(InsideBuildingPanel ibp){
		insideBuildingPanel = ibp;
	}
	public InsideBuildingPanel getInsideBuildingPanel(){
		return insideBuildingPanel;
	}
	
    public abstract void paintComponent(Graphics g);
    public void addGui(Gui gui) {
		guis.add(gui);
	}
    
}
