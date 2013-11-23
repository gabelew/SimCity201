package atHome.city;

import java.awt.Point;

import city.PersonAgent;
import city.animationPanels.InsideAnimationPanel;

public class Home extends Residence
{
	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	public PersonAgent owner;
	
	public Home(InsideAnimationPanel iap, Point loc, PersonAgent p)
	{
		super(iap,loc);
		this.insideAnimationPanel = iap;
		this.location = loc;
		this.owner = p;
	}
}
