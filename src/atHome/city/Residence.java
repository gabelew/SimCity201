package atHome.city;

import java.awt.Point;
import city.animationPanels.InsideAnimationPanel;

public class Residence 
{
	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	
	public Residence( InsideAnimationPanel iap, Point loc)
	{
		this.insideAnimationPanel = iap;
		this.location = loc;
	}
	
}
