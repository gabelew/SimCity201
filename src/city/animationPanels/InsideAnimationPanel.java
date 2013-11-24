package city.animationPanels;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import restaurant.gui.CustomerGui;
import restaurant.gui.WaiterGui;
import city.gui.Gui;

public abstract class InsideAnimationPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	InsideBuildingPanel insideBuildingPanel;
	protected static final int TIMERDELAY = 20;
	protected final int WINDOWX = 934;
    protected final int WINDOWY = 472;

    protected List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    
	public void setInsideBuildingPanel(InsideBuildingPanel ibp){
		insideBuildingPanel = ibp;
	}
	public InsideBuildingPanel getInsideBuildingPanel(){
		return insideBuildingPanel;
	}
	
    public abstract void paintComponent(Graphics g);
    public void addGui(Gui gui) {
		getGuis().add(gui);
	}
	public List<Gui> getGuis() {
		return guis;
	}
	public void setGuis(List<Gui> guis) {
		this.guis = guis;
	}
	public void removeGui(Gui gui) {
		guis.remove(gui);
		
	}
    
}
