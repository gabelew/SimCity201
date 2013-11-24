package bank;

import java.awt.Point;
import city.animationPanels.InsideAnimationPanel;

public class BankBuilding{

	public Point location;
	public InsideAnimationPanel insideAnimationPanel;
	
	public BankBuilding(InsideAnimationPanel iap, Point loc)
	{
		this.insideAnimationPanel = iap;
		this.location = loc;
	}
}
