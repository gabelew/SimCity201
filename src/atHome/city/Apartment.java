package atHome.city;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import city.PersonAgent;
import city.animationPanels.InsideAnimationPanel;

public class Apartment extends Residence
{
	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	public List<PersonAgent> renters = new ArrayList<PersonAgent>();
	public PersonAgent owner = null;
	
	public void setOwner(PersonAgent p)
	{
		this.owner = p;
	}
	public void addRenter(PersonAgent p)
	{
		renters.add(p);
	}
	public Apartment(InsideAnimationPanel iap, Point loc)
	{
		super(iap,loc);
		this.insideAnimationPanel = iap;
		this.location = loc;
	}
}
