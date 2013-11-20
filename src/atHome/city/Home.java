package atHome.city;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import city.PersonAgent;
import city.animationPanels.InsideAnimationPanel;

public class Home 
{
	public Point location;
	public List<PersonAgent> people = new ArrayList<PersonAgent>();
	public InsideAnimationPanel insideAnimationPanel;
	public String type;
	
	public void addPerson(PersonAgent p)
	{
		people.add(p);
	}
	public Home( InsideAnimationPanel iap, Point loc)
	{
		this.insideAnimationPanel = iap;
		this.location = loc;
		//this.type = p.getName() + "'s House";
	}
}
