package atHome.city;

import java.awt.Point;
import city.animationPanels.InsideAnimationPanel;

public class Home 
{
	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	
	public Home( InsideAnimationPanel iap, Point loc)
	{
		this.insideAnimationPanel = iap;
		this.location = loc;
	}
}
