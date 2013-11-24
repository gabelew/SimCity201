package atHome.city;

import java.awt.Point;

import city.PersonAgent;
import city.animationPanels.InsideAnimationPanel;

public class Home extends Residence
{
	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	public PersonAgent owner = null;
	
	public Home(InsideAnimationPanel iap, Point loc)
	{
		super(iap,loc);
		this.insideAnimationPanel = iap;
		this.location = loc;
	}
}
